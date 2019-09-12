/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.util.ui.alert;

import java.awt.*;

import javax.swing.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.util.ui.util.*;

/**
 * @author Sebastian Raubach
 */
public class DatabasePage extends JWizardPage
{
	private static DatabaseProperties properties = null;

	private final JTextField     server;
	private final JTextField     database;
	private final JTextField     port;
	private final JTextField     username;
	private final JPasswordField password;

	public DatabasePage()
	{
		super();

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;

		JLabel explanation = new JLabel("<html>Please provide the credentials for the Germinate database you want to import this file to.</html>");
		c.gridwidth = 2;
		add(explanation, c);
		c.gridwidth = 1;

		server = new JTextField();
		database = new JTextField();
		port = new JTextField();
		username = new JTextField();
		password = new JPasswordField();

		c.weightx = 1;
		c.gridy++;
		add(new JLabel("Server"), c);

		c.weightx = 3;
		c.gridx = (c.gridx + 1) % 2;
		add(server, c);

		c.weightx = 1;
		c.gridy++;
		c.gridx = (c.gridx + 1) % 2;
		add(new JLabel("Database"), c);

		c.weightx = 3;
		c.gridx = (c.gridx + 1) % 2;
		add(database, c);

		c.weightx = 1;
		c.gridy++;
		c.gridx = (c.gridx + 1) % 2;
		add(new JLabel("Port"), c);

		c.weightx = 3;
		c.gridx = (c.gridx + 1) % 2;
		add(port, c);

		c.weightx = 1;
		c.gridy++;
		c.gridx = (c.gridx + 1) % 2;
		add(new JLabel("Username"), c);

		c.weightx = 3;
		c.gridx = (c.gridx + 1) % 2;
		add(username, c);

		c.weightx = 1;
		c.gridy++;
		c.gridx = (c.gridx + 1) % 2;
		add(new JLabel("Password"), c);

		c.weightx = 3;
		c.gridx = (c.gridx + 1) % 2;
		add(password, c);

		if (properties == null)
			properties = DatabaseProperties.readProperties();
		server.setText(properties.getServer());
		database.setText(properties.getDatabase());
		username.setText(properties.getUsername());
		password.setText(properties.getPassword());
		port.setText(properties.getPort());
	}

	@Override
	String getTitle()
	{
		return "Database Setup";
	}

	@Override
	boolean canContinue()
	{
		boolean goOn;

		goOn = !StringUtils.isEmpty(server.getText());
		goOn &= !StringUtils.isEmpty(database.getText());
		goOn &= !StringUtils.isEmpty(username.getText());

		if (goOn)
			goOn = canConnect();

		if (goOn)
		{
			properties.setServer(server.getText())
					  .setDatabase(database.getText())
					  .setUsername(username.getText())
					  .setPassword(new String(password.getPassword()))
					  .setPort(port.getText());

			DatabaseProperties.writeProperties(properties);
		}

		return goOn;
	}

	private boolean canConnect()
	{
		try
		{
			Database.connect(Database.DatabaseType.MYSQL_DATA_IMPORT, server.getText() + (StringUtils.isEmpty(port.getText()) ? "" : (":" + port.getText())) + "/" + database.getText(), username.getText(), new String(password.getPassword()));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public String getServer()
	{
		return server.getText();
	}

	public String getDatabase()
	{
		return database.getText();
	}

	public String getPort()
	{
		return port.getText();
	}

	public String getUsername()
	{
		return username.getText();
	}

	public String getPassword()
	{
		return new String(password.getPassword());
	}
}
