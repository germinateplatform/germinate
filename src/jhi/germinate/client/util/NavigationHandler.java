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

package jhi.germinate.client.util;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.management.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.about.*;
import jhi.germinate.client.page.accession.*;
import jhi.germinate.client.page.admin.*;
import jhi.germinate.client.page.allelefreq.*;
import jhi.germinate.client.page.climate.*;
import jhi.germinate.client.page.compound.*;
import jhi.germinate.client.page.dataset.*;
import jhi.germinate.client.page.genotype.*;
import jhi.germinate.client.page.geography.*;
import jhi.germinate.client.page.groups.*;
import jhi.germinate.client.page.image.*;
import jhi.germinate.client.page.login.*;
import jhi.germinate.client.page.markeditemlist.*;
import jhi.germinate.client.page.search.*;
import jhi.germinate.client.page.statistics.*;
import jhi.germinate.client.page.trial.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.news.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class NavigationHandler
{
	/**
	 * Redirects the user to the page with the given name.
	 *
	 * @param event The new {@link PageNavigationEvent}
	 */
	public static void onPageNavigation(PageNavigationEvent event)
	{
		final Page page = event.getPage();

		JavaScript.GoogleAnalytics.trackPageview(page.name());

		// ABOUT GERMINATE
		if (Page.ABOUT_GERMINATE.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new AboutGerminatePage()));
		}
		// ABOUT PROJECT
		else if (Page.ABOUT_PROJECT.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new HTMLPage(Text.LANG.aboutProjectTitle(), Text.LANG.aboutProjectText())
			{
				@Override
				public String getParallaxStyle()
				{
					return ParallaxResource.INSTANCE.css().parallaxAboutProject();
				}
			}));
		}
		// DATA STATISTICS
		else if (Page.DATA_STATISTICS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new StatisticsOverviewPage()));
		}
		// ACKNOWLEDGEMENTS
		else if (Page.ACKNOWLEDGEMENTS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.ABOUT_GERMINATE, new HTMLPage(Text.LANG.acknowledgementsTitle(), Text.LANG.acknowledgementsText())
			{
				@Override
				public String getParallaxStyle()
				{
					return ParallaxResource.INSTANCE.css().parallaxAcknowledgements();
				}
			}));
		}
		// COOKIE
		else if (Page.COOKIE.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) CookieModal::show);
		}
		// ACCESSIONS FOR COLLSITE
		else if (Page.ACCESSIONS_FOR_COLLSITE.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.ACCESSION_OVERVIEW, new AccessionsAtCollsitePage()));
		}
		// BROWSE ACCESSIONS
		else if (Page.ACCESSION_OVERVIEW.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new AccessionOverviewPage()));
		}
		// CLIMATE DATASETS
		else if (Page.CLIMATE_DATASETS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () ->
			{
				DatasetWidget widget = new DatasetWidget(new DatasetWidget.DatasetCallback()
				{
					@Override
					public boolean isContinueButtonAvailable()
					{
						return GerminateSettingsHolder.isPageAvailable(Page.CLIMATE);
					}

					@Override
					public void onContinuePressed()
					{
						History.newItem(Page.CLIMATE.name());
					}
				}, ExperimentType.climate, true)
				{
					@Override
					public String getParallaxStyle()
					{
						return ParallaxResource.INSTANCE.css().parallaxClimate();
					}
				};
				widget.setShowMap(true);
				widget.setLinkToExportPage(false);
				widget.setHeaderText(Text.LANG.climateDatasetHeader());

				ContentHolder.getInstance().setContent(page, page, widget);
			});
		}
		// CLIMATE
		else if (Page.CLIMATE.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.CLIMATE, new ClimateDataPage()));
		}
		// IMAGE_GALLERY
		else if (Page.IMAGE_GALLERY.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new ImagePage()));
		}
		// GENOTYPE DATASETS
		else if (Page.GENOTYPE_DATASETS.is(page))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					final DatasetWidget widget = new DatasetWidget(new DatasetWidget.DatasetCallback()
					{
						@Override
						public boolean isContinueButtonAvailable()
						{
							return GerminateSettingsHolder.isPageAvailable(Page.GENOTYPE_EXPORT);
						}

						@Override
						public void onContinuePressed()
						{
							History.newItem(Page.GENOTYPE_EXPORT.name());
						}
					}, ExperimentType.genotype, true);
					widget.setShowMap(true);
					widget.setLinkToExportPage(false);
					widget.setHeaderText(Text.LANG.genotypeDatasetHeader());
					widget.setShowDownload(true, new SimpleCallback<Dataset>()
					{
						@Override
						public void onSuccess(Dataset result)
						{
							if (!StringUtils.isEmpty(result.getSourceFile()))
							{
								/* If we're dealing with an hdf5 file, convert it to tab-delimited */
								if (result.getSourceFile().endsWith(".hdf5"))
								{
									/* Start the export process */
									GenotypeService.Inst.get().convertHdf5ToFlapjack(Cookie.getRequestProperties(), result.getId(), new DefaultAsyncCallback<ServerResult<String>>(true)
									{
										@Override
										protected void onFailureImpl(Throwable caught)
										{
											if (caught instanceof InvalidArgumentException)
												Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInsufficientPermissions());
											else
												super.onFailureImpl(caught);
										}

										@Override
										protected void onSuccessImpl(ServerResult<String> result)
										{
											ExperimentDetailsPage.clickDownloadLink(result);
										}
									});
								}
								else
								{
									/* Else just download the file */
									String href = new ServletConstants.Builder()
											.setUrl(GWT.getModuleBaseURL())
											.setPath(ServletConstants.SERVLET_FILES)
											.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
											.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
											.setParam(ServletConstants.PARAM_FILE_LOCATION, FileLocation.data.name())
											.setParam(ServletConstants.PARAM_FILE_PATH, (ReferenceFolder.genotype.name() + "/") + result.getSourceFile())
											.build();

									JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), result.getSourceFile());

            						/* Click it */
									JavaScript.invokeDownload(href);
								}
							}
						}
					});

					ContentHolder.getInstance().setContent(page, page, widget);
				}
			});

		}
		// GENOTYPE EXPORT
		else if (Page.GENOTYPE_EXPORT.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.GENOTYPE_DATASETS, new GenotypeExportPage()));
		}
		// GEOGRAPHIC SEARCH
		else if (Page.GEOGRAPHIC_SEARCH.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new GeographicSearchPage()));
		}
		// LOCATIONS
		else if (Page.LOCATIONS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new LocationsPage()));
		}
		// GROUPS
		else if (Page.GROUPS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new GroupsPage()));
		}
		// HOME
		else if (Page.HOME.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new Home()));
		}
		// NEWS
		else if (Page.NEWS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.HOME, new NewsPage()));
		}
		//MAP DETAILS
		else if (Page.MAP_DETAILS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new MapsPage()));
		}
		// MARKER DETAILS
		else if (Page.MARKER_DETAILS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.MAP_DETAILS, new MarkerPage()));
		}
		// MEGA ENVIRONMENTS
		else if (Page.MEGA_ENVIRONMENT.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new MegaEnvironmentsPage()));
		}
		// PASSPORT
		else if (Page.PASSPORT.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.ACCESSION_OVERVIEW, new PassportPage()));
		}
		// SEARCH
		else if (Page.SEARCH.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.ACCESSION_OVERVIEW, new SearchPage()));
		}
		// TRIALS
		else if (Page.TRIALS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.TRIALS_DATASETS, new TrialPage()));
		}
		// TRIALS DATASETS
		else if (Page.TRIALS_DATASETS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () ->
			{
				DatasetWidget widget = new DatasetWidget(new DatasetWidget.DatasetCallback()
				{
					@Override
					public boolean isContinueButtonAvailable()
					{
						return GerminateSettingsHolder.isPageAvailable(Page.TRIALS);
					}

					@Override
					public void onContinuePressed()
					{
						History.newItem(Page.TRIALS.name());
					}
				}, ExperimentType.trials, false)
				{
					@Override
					public String getParallaxStyle()
					{
						return ParallaxResource.INSTANCE.css().parallaxTrial();
					}
				};
				widget.setShowMap(true);
				widget.setLinkToExportPage(false);
				widget.setHeaderText(Text.LANG.trialsDatasetHeader());

				ContentHolder.getInstance().setContent(page, page, widget);
			});
		}
//		// COLLECTINGSITES TREEMAP
//		else if (Page.LOCATION_TREEMAP.is(page))
//		{
//			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new LocationTreemapPage()));
//		}
		// ALLELE FREQ DATASETS
		else if (Page.ALLELE_FREQUENCY_DATASET.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () ->
			{
				DatasetWidget widget = new DatasetWidget(new DatasetWidget.DatasetCallback()
				{
					@Override
					public boolean isContinueButtonAvailable()
					{
						return GerminateSettingsHolder.isPageAvailable(Page.ALLELE_FREQUENCY_EXPORT);
					}

					@Override
					public void onContinuePressed()
					{
						History.newItem(Page.ALLELE_FREQUENCY_EXPORT.name());
					}
				}, ExperimentType.allelefreq, true);
				widget.setShowMap(true);
				widget.setLinkToExportPage(false);
				widget.setHeaderText(Text.LANG.allelefreqDatasetHeader());

				ContentHolder.getInstance().setContent(page, page, widget);
			});
		}
		// ALLELE FREQ EXPORT
		else if (Page.ALLELE_FREQUENCY_EXPORT.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.ALLELE_FREQUENCY_DATASET, new AlleleFreqExportPage()));
		}
		// ALLELE FREQ RESULT
		else if (Page.ALLELE_FREQUENCY_RESULT.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.ALLELE_FREQUENCY_DATASET, new AlleleFreqResultsPage()));
		}
		// SHOPPING CART
		else if (Page.MARKED_ITEMS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.GROUPS, new MarkedItemListPage()));
		}
		// DATASET OVERVIEW
		else if (Page.DATASET_OVERVIEW.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new DatasetPage()));
		}
		// EXPERIMENT DETAILS
		else if (Page.EXPERIMENT_DETAILS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new ExperimentDetailsPage()));
		}
		// INSTITUTIONS PAGE
		else if (Page.INSTITUTIONS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.LOCATIONS, new InstitutionsPage()));
		}
		// GROUP PREVIEW PAGE
		else if (Page.GROUP_PREVIEW.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.GROUPS, new GroupPreviewPage()));
		}
		// ADMIN CONFIG PAGE
		else if (Page.ADMIN_CONFIG.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.HOME, new AdminConfigPage()));
		}
		// COMPOUNDS PAGE
		else if (Page.COMPOUNDS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, page, new CompoundPage()));
		}
		// COMPOUND DETAILS PAGE
		else if (Page.COMPOUND_DETAILS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.COMPOUNDS, new CompoundDetailsPage()));
		}
		// TRIALS DATASETS
		else if (Page.COMPOUND_DATASETS.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () ->
			{
				DatasetWidget widget = new DatasetWidget(new DatasetWidget.DatasetCallback()
				{
					@Override
					public boolean isContinueButtonAvailable()
					{
						return GerminateSettingsHolder.isPageAvailable(Page.COMPOUND_DATA);
					}

					@Override
					public void onContinuePressed()
					{
						History.newItem(Page.COMPOUND_DATA.name());
					}
				}, ExperimentType.compound, false);
				widget.setShowMap(true);
				widget.setLinkToExportPage(false);
				widget.setHeaderText(Text.LANG.compoundDatasetHeader());

				ContentHolder.getInstance().setContent(page, page, widget);
			});
		}
		// COMPOUND DATA
		else if (Page.COMPOUND_DATA.is(page))
		{
			GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.COMPOUND_DATASETS, new CompoundDataPage()));
		}
		// FALLBACK
		else
		{
			if (ModuleCore.getUseAuthentication() && !ModuleCore.isLoggedIn())
				GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.HOME, new LoginPage()));
			else
				GWT.runAsync((RunAsyncNotifyCallback) () -> ContentHolder.getInstance().setContent(page, Page.HOME, new Home()));
		}
	}
}
