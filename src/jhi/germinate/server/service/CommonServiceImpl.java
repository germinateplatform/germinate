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

package jhi.germinate.server.service;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.*;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.Session;
import jhi.germinate.server.util.xml.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Group;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;

/**
 * {@link CommonServiceImpl} is the implementation of {@link CommonService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/common"})
public class CommonServiceImpl extends BaseRemoteServiceServlet implements CommonService
{
	private static final long serialVersionUID = -2599538621272643710L;

	private static final String QUERY_COLUMNS       = "SELECT * FROM %s LIMIT 1";
	private static final String QUERY_COUNTRY_STATS = "SELECT `countries`.*, count(1) AS count FROM `germinatebase` LEFT JOIN `locations` ON `germinatebase`.`location_id` = `locations`.`id` LEFT JOIN `countries` ON `countries`.`id` = `locations`.`country_id` GROUP BY `countries`.`id` ORDER BY count(1) DESC";

	@Override
	public ServerResult<List<Synonym>> getSynonyms(RequestProperties properties, GerminateDatabaseTable table, Long id) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return SynonymManager.getAllForTable(userAuth, table, id);
	}

	@Override
	public ServerResult<List<String>> getColumnsOfTable(RequestProperties properties, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		ValueQuery.ExecutedValueQuery query = new ValueQuery(String.format(QUERY_COLUMNS, table.name()), userAuth)
				.run(null);

		ServerResult<List<String>> result = query.getColumnNames();

		query.close();

		return result;
	}

	@Override
	public ServerResult<GerminateSettings> getAdminSettings(RequestProperties properties) throws DatabaseException, InvalidSessionException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		try
		{
			GatekeeperUserWithPassword details = GatekeeperUserManager.getByIdWithPasswordForSystem(userAuth, userAuth.getId());

			if (details == null || !details.isAdmin())
				throw new InsufficientPermissionsException();

			GerminateSettings baseSettings = getBaseSettings();

			baseSettings.cookieLifespanMinutes = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_COOKIE_LIFESPAN_MINUTES, PropertyWatcher.getInteger(ServerProperty.GERMINATE_COOKIE_LIFESPAN_MINUTES));
			baseSettings.externalDataFolder = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_EXTERNAL_DATA_FOLDER, PropertyWatcher.get(ServerProperty.GERMINATE_EXTERNAL_DATA_FOLDER));
			baseSettings.gatekeeperRegistrationNeedsApproval = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_NEEDS_APPROVAL, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_NEEDS_APPROVAL));
			baseSettings.googleAnalyticsTrackingId = new GerminateSettings.ClientProperty<>(ServerProperty.GOOGLE_ANALYTICS_TRACKING_ID, PropertyWatcher.get(ServerProperty.GOOGLE_ANALYTICS_TRACKING_ID));
			baseSettings.keepTempFilesForHours = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_KEEP_TEMPORARY_FILES_FOR_HOURS, PropertyWatcher.getDouble(ServerProperty.GERMINATE_KEEP_TEMPORARY_FILES_FOR_HOURS));
			baseSettings.serverLoggingEnabled = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_SERVER_LOGGING_ENABLED, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_SERVER_LOGGING_ENABLED));
			baseSettings.templateDatabaseName = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_DATABASE_NAME, PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_DATABASE_NAME));
			baseSettings.templateCustomMenu = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_MENU, PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_MENU));

			return new ServerResult<>(null, baseSettings);
		}
		catch (NullPointerException | NumberFormatException e)
		{
			throw new InsufficientPermissionsException();
		}
	}

	public static List<String> getColors(String colorString)
	{
		List<String> parsedColors = new ArrayList<>();

		/* Parse the categorical colors */
		if (!StringUtils.isEmpty(colorString))
		{
			parsedColors.addAll(Arrays.stream(colorString.split(",")) /* Split the string */
									  .filter(color -> !StringUtils.isEmpty(color)) /* Ignore empty strings */
									  .map(String::trim) /* Trim white spaces */
									  .map(s -> s.startsWith("#") ? s : "#" + s) /* Prepend hash if necessary */
									  .filter(Color::isHexColor) /* Check if it's a valid color */
									  .collect(Collectors.toList()));
		}

		/* If there is no color, add at least one default one */
		if (parsedColors.isEmpty())
			parsedColors.add("#1f77b4");

		return parsedColors;
	}

	@Override
	public GerminateSettings getSettings()
	{
		return getBaseSettings();
	}

	@Override
	public void setAdminSettings(RequestProperties properties, GerminateSettings settings) throws DatabaseException, InvalidSessionException, InsufficientPermissionsException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		try
		{
			GatekeeperUserWithPassword details = GatekeeperUserManager.getByIdWithPasswordForSystem(userAuth, userAuth.getId());

			if (details == null || !details.isAdmin())
				throw new InsufficientPermissionsException();

			PropertyWatcher.set(settings.gatekeeperUrl.getServerProperty(), settings.gatekeeperUrl.getValue());
			PropertyWatcher.setBoolean(settings.gatekeeperRegistrationEnabled.getServerProperty(), settings.gatekeeperRegistrationEnabled.getValue());
			PropertyWatcher.setBoolean(settings.gatekeeperRegistrationNeedsApproval.getServerProperty(), settings.gatekeeperRegistrationNeedsApproval.getValue());

			PropertyWatcher.set(settings.templateTitle.getServerProperty(), settings.templateTitle.getValue());
			PropertyWatcher.set(settings.templateDatabaseName.getServerProperty(), settings.templateDatabaseName.getValue());
			PropertyWatcher.setBoolean(settings.templateUseToggleSwitches.getServerProperty(), settings.templateUseToggleSwitches.getValue());
			PropertyWatcher.set(settings.templateCategoricalColors.getServerProperty(), CollectionUtils.join(settings.templateCategoricalColors.getValue(), ","));
			PropertyWatcher.set(settings.templateGradientColors.getServerProperty(), CollectionUtils.join(settings.templateGradientColors.getValue(), ","));
			PropertyWatcher.set(settings.templateContactEmail.getServerProperty(), settings.templateContactEmail.getValue());
			PropertyWatcher.setBoolean(settings.templateLogoContainsLink.getServerProperty(), settings.templateLogoContainsLink.getValue());
			PropertyWatcher.setBoolean(settings.templateShowParallaxBanner.getServerProperty(), settings.templateShowParallaxBanner.getValue());
			PropertyWatcher.set(settings.templateCustomMenu.getServerProperty(), settings.templateCustomMenu.getValue());
			PropertyWatcher.set(settings.templateMarkedAccessionUrl.getServerProperty(), settings.templateMarkedAccessionUrl.getValue());

			PropertyWatcher.setBoolean(settings.googleAnalyticsEnabled.getServerProperty(), settings.googleAnalyticsEnabled.getValue());
			PropertyWatcher.set(settings.googleAnalyticsTrackingId.getServerProperty(), settings.googleAnalyticsTrackingId.getValue());

			PropertyWatcher.setBoolean(settings.downloadTrackingEnabled.getServerProperty(), settings.downloadTrackingEnabled.getValue());

			PropertyWatcher.setBoolean(settings.socialShowFacebook.getServerProperty(), settings.socialShowFacebook.getValue());
			PropertyWatcher.setBoolean(settings.socialShowTwitter.getServerProperty(), settings.socialShowTwitter.getValue());
			PropertyWatcher.setBoolean(settings.socialShowGooglePlus.getServerProperty(), settings.socialShowGooglePlus.getValue());

			PropertyWatcher.setBoolean(settings.hideIdColumn.getServerProperty(), settings.hideIdColumn.getValue());
			PropertyWatcher.setBoolean(settings.pdciEnabled.getServerProperty(), settings.pdciEnabled.getValue());
			PropertyWatcher.setBoolean(settings.serverLoggingEnabled.getServerProperty(), settings.serverLoggingEnabled.getValue());
			PropertyWatcher.setBoolean(settings.debug.getServerProperty(), settings.debug.getValue());
			PropertyWatcher.setBoolean(settings.isReadOnlyMode.getServerProperty(), settings.isReadOnlyMode.getValue());
			PropertyWatcher.setBoolean(settings.cookieNotifierEnabled.getServerProperty(), settings.cookieNotifierEnabled.getValue());

			PropertyWatcher.setDouble(settings.keepTempFilesForHours.getServerProperty(), settings.keepTempFilesForHours.getValue());
			PropertyWatcher.setDouble(settings.uploadSizeLimitMB.getServerProperty(), settings.uploadSizeLimitMB.getValue());
			PropertyWatcher.setInteger(settings.cookieLifespanMinutes.getServerProperty(), settings.cookieLifespanMinutes.getValue());
			PropertyWatcher.setInteger(settings.galleryImagesPerPage.getServerProperty(), settings.galleryImagesPerPage.getValue());
			PropertyWatcher.set(settings.externalDataFolder.getServerProperty(), settings.externalDataFolder.getValue());
			PropertyWatcher.setSet(settings.availablePages.getServerProperty(), settings.availablePages.getValue(), Page.class);

			try
			{
				PropertyWatcher.store();
			}
			catch (java.io.IOException | NullPointerException e)
			{
				throw new IOException(e);
			}

			ApplicationListener.togglePdciEnabled();
		}
		catch (NullPointerException | NumberFormatException e)
		{
			throw new InsufficientPermissionsException();
		}
	}

	private GerminateSettings getBaseSettings()
	{
		GerminateSettings settings = new GerminateSettings();

		settings.debug = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_DEBUG, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_DEBUG));
		settings.googleAnalyticsEnabled = new GerminateSettings.ClientProperty<>(ServerProperty.GOOGLE_ANALYTICS_ENABLED, PropertyWatcher.getBoolean(ServerProperty.GOOGLE_ANALYTICS_ENABLED));
		settings.availablePages = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_AVAILABLE_PAGES, PropertyWatcher.getSet(ServerProperty.GERMINATE_AVAILABLE_PAGES, Page.class));
		settings.cookieNotifierEnabled = new GerminateSettings.ClientProperty<>(ServerProperty.COOKIE_NOTIFIER_ENABLED, PropertyWatcher.getBoolean(ServerProperty.COOKIE_NOTIFIER_ENABLED));
		settings.gatekeeperUrl = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_GATEKEEPER_URL, PropertyWatcher.get(ServerProperty.GERMINATE_GATEKEEPER_URL));
		settings.showHomeOnLogin = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_SHOW_HOME_ON_LOGIN, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_SHOW_HOME_ON_LOGIN));
		settings.uploadSizeLimitMB = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_UPLOAD_SIZE_LIMIT_MB, PropertyWatcher.getDouble(ServerProperty.GERMINATE_UPLOAD_SIZE_LIMIT_MB));
		settings.gatekeeperRegistrationEnabled = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_ENABLED, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_ENABLED));
		settings.templateUseToggleSwitches = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_USE_TOGGLE_SWITCHES, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_USE_TOGGLE_SWITCHES));
		settings.socialShowFacebook = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_SOCIAL_SHOW_FACEBOOK, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_SOCIAL_SHOW_FACEBOOK));
		settings.socialShowTwitter = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_SOCIAL_SHOW_TWITTER, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_SOCIAL_SHOW_TWITTER));
		settings.socialShowGooglePlus = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_SOCIAL_SHOW_GOOGLE_PLUS, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_SOCIAL_SHOW_GOOGLE_PLUS));
		settings.hideIdColumn = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_HIDE_ID_COLUMNS, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_HIDE_ID_COLUMNS));
		settings.pdciEnabled = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_PDCI_ENABLED, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_PDCI_ENABLED));
		settings.isReadOnlyMode = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_IS_READ_ONLY, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY));
		settings.templateContactEmail = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_EMAIL_ADDRESS, PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_EMAIL_ADDRESS));
		settings.templateLogoContainsLink = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_LOGO_CONTAINS_LINK, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_LOGO_CONTAINS_LINK));
		settings.galleryImagesPerPage = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_GALLERY_IMAGES_PER_PAGE, PropertyWatcher.getInteger(ServerProperty.GERMINATE_GALLERY_IMAGES_PER_PAGE));
		settings.templateShowParallaxBanner = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_SHOW_PARALLAX_BANNER, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_SHOW_PARALLAX_BANNER));
		settings.templateTitle = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_TITLE, PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_TITLE));
		settings.downloadTrackingEnabled = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_DOWNLOAD_TRACKING_ENABLED, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_DOWNLOAD_TRACKING_ENABLED));
		settings.templateMarkedAccessionUrl = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_MARKED_ACCESSIONS_URL, PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_MARKED_ACCESSIONS_URL));

		settings.supportsAdvancedGeography = checkDatabaseVersion();


		settings.loadPageOnLibraryError = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_HIDDEN_LOAD_PAGE_ON_LIBRARY_ERROR, PropertyWatcher.getBoolean(ServerProperty.GERMINATE_HIDDEN_LOAD_PAGE_ON_LIBRARY_ERROR));


		String menuXml = PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_MENU);
		if (!StringUtils.isEmpty(menuXml))
		{
			try
			{
				Serializer serializer = new Persister();
				CustomMenuServer menu = serializer.read(CustomMenuServer.class, menuXml);
				settings.customMenu = CustomMenuToClientConverter.convertCustomMenu(menu);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		settings.templateCategoricalColors = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_CATEGORICAL_COLORS, getColors(PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_CATEGORICAL_COLORS)));
		settings.templateGradientColors = new GerminateSettings.ClientProperty<>(ServerProperty.GERMINATE_TEMPLATE_GRADIENT_COLORS, getColors(PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_GRADIENT_COLORS)));

		return settings;
	}

	@Override
	public ServerResult<List<Link>> getExternalLinks(RequestProperties properties, Long referenceId, GerminateDatabaseTable referenceTable) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		ServerResult<List<Link>> placeholderLinks = LinkManager.getPlaceholderLinksForTable(userAuth, referenceId, referenceTable);
		ServerResult<List<Link>> staticLinks = LinkManager.getStaticLinksForTable(userAuth, referenceId, referenceTable);

		placeholderLinks.getDebugInfo().addAll(staticLinks.getDebugInfo());

		placeholderLinks.setServerResult(CollectionUtils.combineList(placeholderLinks.getServerResult(), staticLinks.getServerResult()));

		return placeholderLinks;
	}

	@Override
	public ServerResult<String> getTaxonomyStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);

		try
		{
			return new ServerResult<>(null, StatisticsServlet.getStatistics(getThreadLocalRequest(), ViewInitializer.View.ACCESSIONS_PER_TAXONOMY).getName());
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public ServerResult<List<Country>> getCountryStats(RequestProperties properties) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return new DatabaseObjectQuery<Country>(QUERY_COUNTRY_STATS, userAuth)
				.run()
				.getObjects(Country.CountParser.Inst.get());
	}

	@Override
	public ServerResult<Map<String, Long>> getOverviewStats(RequestProperties properties) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		ServerResult<Map<String, Long>> result = new ServerResult<>();
		result.setDebugInfo(DebugInfo.create(userAuth));

		Map<String, Long> count = new TreeMap<>();

		ServerResult<Long> accessions = AccessionManager.getCount(userAuth);
		result.getDebugInfo().addAll(accessions.getDebugInfo());
		count.put(Accession.class.getName(), accessions.getServerResult());

		ServerResult<Long> marker = MarkerManager.getCount(userAuth);
		result.getDebugInfo().addAll(marker.getDebugInfo());
		count.put(Marker.class.getName(), marker.getServerResult());

		ServerResult<Long> location = LocationManager.getCount(userAuth);
		result.getDebugInfo().addAll(location.getDebugInfo());
		count.put(Location.class.getName(), location.getServerResult());

		ServerResult<Long> group = GroupManager.getCount(userAuth);
		result.getDebugInfo().addAll(group.getDebugInfo());
		count.put(Group.class.getName(), group.getServerResult());

		result.setServerResult(count);

		return result;
	}

	@Override
	public Void makeFilesAvailablePublically(RequestProperties properties, ExperimentType experimentType) throws InvalidSessionException
	{
		switch (experimentType)
		{
			case genotype:
				synchronized (FileServlet.PUBLICLY_AVAILABLE_FILES)
				{
					HttpSession session = getRequest().getSession();

					String map = (String) session.getAttribute(Session.GENOTYPE_MAP);

					if (!StringUtils.isEmpty(map))
						FileServlet.PUBLICLY_AVAILABLE_FILES.put(map, System.currentTimeMillis());

					String data = (String) session.getAttribute(Session.GENOTYPE_DATA);

					if (!StringUtils.isEmpty(data))
						FileServlet.PUBLICLY_AVAILABLE_FILES.put(data, System.currentTimeMillis());

					session.removeAttribute(Session.GENOTYPE_MAP);
					session.removeAttribute(Session.GENOTYPE_DATA);
				}
				break;
			default:
				break;
		}

		return null;
	}

	private boolean checkDatabaseVersion()
	{
		try
		{
			new ValueQuery("SELECT ST_CONTAINS (ST_PolygonFromText('POLYGON((0 0, 2 0, 2 2, 0 2, 0 0))'), ST_GeomFromText ('POINT(1 1)'))")
					.execute();

			return true;
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public static class ExportResult
	{
		public File   subsetWithFlapjackLinks;
		public String flapjackLinks;

		@Override
		public String toString()
		{
			return "ExportResult{" +
					", subsetWithFlapjackLinks=" + subsetWithFlapjackLinks +
					", flapjackLinks='" + flapjackLinks + '\'' +
					'}';
		}
	}
}
