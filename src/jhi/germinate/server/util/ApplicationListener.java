/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.server.util;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import javax.servlet.*;
import javax.servlet.annotation.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.enums.*;

/**
 * The {@link ApplicationListener} is the main {@link ServletContextListener} of the application. It's started when the application is loaded by
 * Tomcat. It contains {@link #contextInitialized(ServletContextEvent)} which is executed on start and {@link #contextDestroyed(ServletContextEvent)}
 * which is executed when the application terminates.
 *
 * @author Sebastian Raubach
 */
@WebListener
public class ApplicationListener implements ServletContextListener
{
	private static ScheduledExecutorService scheduler;

	private static ScheduledFuture<?> pdciFuture = null;

	/**
	 * Enabled/disables PDCI calculation depening on the setting of {@link ServerProperty#GERMINATE_PDCI_ENABLED}
	 */
	public static void togglePdciEnabled()
	{
		boolean enabled = PropertyWatcher.getBoolean(ServerProperty.GERMINATE_PDCI_ENABLED);

		if (enabled && pdciFuture == null)
			pdciFuture = scheduler.scheduleAtFixedRate(new PDCIRunnable(), 0, 4, TimeUnit.HOURS);

		if (!enabled && pdciFuture != null)
		{
			pdciFuture.cancel(false);
			pdciFuture = null;
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		// Start reading the properties file and watch for changes
		PropertyWatcher.initialize();
		// Watch the image folder and resize images as required
		ImageWatcher.initialize();
		// Watch external data folder for changes to template files
		TemplateWatcher.initialize(sce.getServletContext());

		Database.initialize();

		scheduler = Executors.newSingleThreadScheduledExecutor();
		// Every hour, update the dataset sizes
		scheduler.scheduleAtFixedRate(new DatasetMetaJob(), 0, 1, TimeUnit.HOURS);

		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_PDCI_ENABLED))
		{
			// Every foud hours, re-calculate the PDCI
			pdciFuture = scheduler.scheduleAtFixedRate(new PDCIRunnable(), 0, 4, TimeUnit.HOURS);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		// Remember to stop the property and image file watcher
		PropertyWatcher.stopFileWatcher();
		ImageWatcher.stopFileWatcher();
		TemplateWatcher.stopFileWatcher();

		try
		{
			// Stop the schedular
			scheduler.shutdownNow();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Remove temporary log files
		File file = new File(System.getProperty("java.io.tmpdir"), "logs");
		String context = sce.getServletContext().getContextPath().replace("/", "");

		if (file.exists())
		{
			File[] files = file.listFiles((dir, name) -> name.matches(context + "-log-.*\\.txt.*"));

			for (File f : files)
				f.delete();
		}

		// Now deregister JDBC drivers in this context's ClassLoader: Get the webapp's ClassLoader
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		// Loop through all drivers
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements())
		{
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl)
			{
				// This driver was registered by the webapp's ClassLoader, so deregister it
				try
				{
					Logger.getLogger("").log(Level.INFO, "Deregistering JDBC driver '" + driver + "'");
					DriverManager.deregisterDriver(driver);
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				// Driver was not registered by the webapp's ClassLoader and may be in use elsewhere
				Logger.getLogger("").log(Level.INFO, "Not deregistering JDBC driver '" + driver + "' as it does not belong to this webapp's ClassLoader");
			}
		}
	}
}
