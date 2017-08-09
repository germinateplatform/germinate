/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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

package jhi.germinate.shared.enums;

import com.google.gwt.i18n.shared.*;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;

public enum ServerProperty
{
	DATABASE_USERNAME("Germinate.Database.Username", null, true),
	DATABASE_PASSWORD("Germinate.Database.Password", null, true),
	DATABASE_SERVER("Germinate.Database.Server", null, true),
	DATABASE_NAME("Germinate.Database.Name", null, true),
	DATABASE_PORT("Germinate.Database.Port", null, false),
	GERMINATE_GATEKEEPER_URL("Gatekeeper.URL", null, false),
	GERMINATE_GATEKEEPER_SERVER("Gatekeeper.Database.Server", null, false),
	GERMINATE_GATEKEEPER_NAME("Gatekeeper.Database.Name", null, false),
	GERMINATE_GATEKEEPER_PORT("Gatekeeper.Database.Port", null, false),
	GERMINATE_GATEKEEPER_BCRYPT_ROUNDS("Gatekeeper.BCrypt.Rounds", "10", false),
	GERMINATE_GATEKEEPER_REGISTRATION_ENABLED("Gatekeeper.Registration.Enabled", "false", false),
	GERMINATE_GATEKEEPER_REGISTRATION_NEEDS_APPROVAL("Gatekeeper.Registration.Needs.Approval", "true", false),
	GERMINATE_USE_AUTHENTICATION("Germinate.UseAuthentication", "false", false),
	GERMINATE_COOKIE_LIFESPAN_MINUTES("Germinate.CookieLifespanMinutes", "1440", false),
	GERMINATE_DEBUG("Germinate.Debug", "false", false),
	GERMINATE_AUTO_UPDATE_DATABASE("Germinate.AutoUpdateDatabase", "true", false),
	GERMINATE_KEEP_TEMPORARY_FILES_FOR_HOURS("Germinate.KeepTemporaryFileForHours", "24", false),
	GERMINATE_UPLOAD_SIZE_LIMIT_MB("Germinate.UploadSizeLimitMB", "0.5", false),
	GOOGLE_ANALYTICS_ENABLED("GoogleAnalytics.Enabled", "false", false),
	GOOGLE_ANALYTICS_TRACKING_ID("GoogleAnalytics.TrackingId", null, false),
	GOOGLE_MAPS_API_KEY("GoogleMaps.Api.Key", null, false),
	COOKIE_NOTIFIER_ENABLED("CookieNotifier.Enabled", "false", false),
	GERMINATE_AVAILABLE_PAGES("Germinate.AvailablePages", null, true),
	GERMINATE_ACCESSION_DISPLAY_COLUMN("Germinate.AccessionDisplayColumn", "name", false),
	GERMINATE_BASE_SEARCH_COLUMN("Germinate.BaseSearchColumn", "name", false),
	GERMINATE_COLLECTINGSITE_TREEMAP_COLUMN("Germinate.CollectingsiteTreemapColumn", Country.COUNTRY_NAME, false),
	GERMINATE_SHOW_HOME_ON_LOGIN("Germinate.ShowHomeOnLogin", "false", false),
	GERMINATE_EXTERNAL_DATA_FOLDER("Germinate.ExternalDataFolder", null, false),
	GERMINATE_HIDE_ID_COLUMNS("Germinate.HideIdColumns", "false", false),
	GERMINATE_GALLERY_IMAGES_PER_PAGE("Germinate.Gallery.Images.Per.Page", "16", false),
	GERMINATE_GALLERY_MAKE_THUMBNAILS_SQUARE("Germinate.Gallery.Make.Thumbnails.Square", "false", false),
	GERMINATE_TEMPLATE_MENU("Germinate.Template.CustomMenu", null, false),
	GERMINATE_TEMPLATE_SHOW_SEARCH("Germinate.Template.Show.Search", "false", false),
	GERMINATE_TEMPLATE_TITLE("Germinate.Template.Title", "Germinate", false),
	GERMINATE_TEMPLATE_LOGO_CONTAINS_LINK("Germinate.Template.Logo.Contains.Link", "false", false),
	GERMINATE_TEMPLATE_CATEGORICAL_COLORS("Germinate.Template.CategoricalColors", null, true),
	GERMINATE_TEMPLATE_GRADIENT_COLORS("Germinate.Template.GradientColors", "#000000,#570000,#ff0000,#ffc800,#ffff00,#FFFFFF", true),
	GERMINATE_TEMPLATE_SOCIAL_SHOW_FACEBOOK("Germinate.Template.Social.ShowFacebook", "false", false),
	GERMINATE_TEMPLATE_SOCIAL_SHOW_TWITTER("Germinate.Template.Social.ShowTwitter", "false", false),
	GERMINATE_TEMPLATE_SOCIAL_SHOW_GOOGLE_PLUS("Germinate.Template.Social.ShowGooglePlus", "false", false),
	GERMINATE_TEMPLATE_USE_TOGGLE_SWITCHES("Germinate.Template.UseToggleSwitches", "true", false),
	GERMINATE_TEMPLATE_EMAIL_ADDRESS("Germinate.Template.EmailAddress", null, false),
	GERMINATE_TEMPLATE_DATABASE_NAME("Germinate.Template.DatabaseName", "Germinate Database", false),
	GERMINATE_TEMPLATE_TWITTER_LINK("Germinate.Template.TwitterLink", null, false),
	GERMINATE_TEMPLATE_SHOW_PARALLAX_BANNER("Germinate.Template.Show.Parallax.Banner", "true", false),
	GERMINATE_SERVER_LOGGING_ENABLED("Germinate.Server.Logging.Enabled", "false", false),
	GERMINATE_IS_UNDER_MAINTENANCE("Germinate.IsUnderMaintenance", "false", false),
	GERMINATE_IS_READ_ONLY("Germinate.IsReadOnly", "false", false),

	GERMINATE_HIDDEN_LOAD_PAGE_ON_LIBRARY_ERROR("Germinate.Hidden.Load.Page.On.Library.Error", "false", false),

	PATH_JAVA("Path.Java", null, false),
	PATH_R("Path.R", null, false);

	String  key;
	String  defaultValue;
	boolean required;

	ServerProperty(String key, String defaultValue, boolean required)
	{
		this.key = key;
		this.defaultValue = defaultValue;
		this.required = required;
	}

	public String getKey()
	{
		return key;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public boolean isRequired()
	{
		return required;
	}

	public static ServerProperty getForKey(String key)
	{
		for (ServerProperty property : values())
		{
			if (Objects.equals(key, property.key))
				return property;
		}

		return null;
	}

	private static String getYear()
	{
		return new DateTimeFormat("yyyy", new DefaultDateTimeFormatInfo())
		{
		}.format(new Date());
	}
}