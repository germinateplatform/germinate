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

package jhi.germinate.shared.datastructure;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.enums.*;

/**
 * {@link GerminateSettings} is a very simple container for settings sent from the server to the client
 *
 * @author Sebastian Raubach
 */
public class GerminateSettings implements Serializable
{
	private static final long serialVersionUID = -6091119469212552829L;

	public ClientProperty<String>  gatekeeperUrl;
	public ClientProperty<Boolean> gatekeeperRegistrationEnabled;
	public ClientProperty<Boolean> gatekeeperRegistrationNeedsApproval;

	public ClientProperty<Integer> cookieLifespanMinutes;
	public ClientProperty<Integer> galleryImagesPerPage;

	public ClientProperty<Double> keepTempFilesForHours;
	public ClientProperty<Double> uploadSizeLimitMB;

	public ClientProperty<Boolean> debug;
	public ClientProperty<Boolean> isReadOnlyMode;
	public ClientProperty<Boolean> cookieNotifierEnabled;
	public ClientProperty<Boolean> showHomeOnLogin;
	public ClientProperty<Boolean> serverLoggingEnabled;
	public ClientProperty<Boolean> hideIdColumn;

	public ClientProperty<Boolean> socialShowFacebook;
	public ClientProperty<Boolean> socialShowTwitter;
	public ClientProperty<Boolean> socialShowGooglePlus;

	public ClientProperty<Boolean> googleAnalyticsEnabled;
	public ClientProperty<String>  googleAnalyticsTrackingId;

	public ClientProperty<Set<Page>> availablePages;

	public ClientProperty<String> accessionDisplayColumn;
	public ClientProperty<String> baseSearchColumn;
	public ClientProperty<String> externalDataFolder;

	public ClientProperty<String>       templateContactEmail;
	public ClientProperty<Boolean>      templateUseToggleSwitches;
	public ClientProperty<Boolean>      templateShowSearchInMenu;
	public ClientProperty<Boolean>      templateShowParallaxBanner;
	public ClientProperty<String>       templateTitle;
	public ClientProperty<String>       templateDatabaseName;
	public ClientProperty<String>       templateTwitterUrl;
	public ClientProperty<List<String>> templateCategoricalColors;
	public ClientProperty<List<String>> templateGradientColors;
	public ClientProperty<Boolean>      templateLogoContainsLink;
	public ClientProperty<String>       templateCustomMenu;

	public ClientProperty<Boolean> loadPageOnLibraryError;

	public boolean    supportsAdvancedGeography = false;
	public CustomMenu customMenu                = null;

	public GerminateSettings()
	{

	}

	public static class ClientProperty<T> implements Serializable
	{
		private static final long serialVersionUID = 3188153629656453019L;

		ServerProperty serverProperty;
		T              value;

		protected ClientProperty()
		{
		}

		public ClientProperty(ServerProperty prop, T value)
		{
			this.serverProperty = prop;
			this.value = value;
		}

		public T getValue()
		{
			return value;
		}

		public ClientProperty setValue(T value)
		{
			this.value = value;
			return this;
		}

		public ServerProperty getServerProperty()
		{
			return serverProperty;
		}

		public boolean isRequired()
		{
			return serverProperty.isRequired();
		}

		@Override
		public String toString()
		{
			return "ClientProperty{" +
					"serverProperty=" + serverProperty +
					", value=" + value +
					'}';
		}
	}
}
