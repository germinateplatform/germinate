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

import java.util.*;

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
import jhi.germinate.client.page.phenotype.*;
import jhi.germinate.client.page.search.*;
import jhi.germinate.client.page.shoppingcart.*;
import jhi.germinate.client.page.statistics.*;
import jhi.germinate.client.page.trial.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.news.*;
import jhi.germinate.client.widget.structure.*;
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
		if (page.equals(Page.ABOUT_GERMINATE))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new AboutGerminatePage());
				}
			});
		}
		// ABOUT PROJECT
		else if (page.equals(Page.ABOUT_PROJECT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new HTMLPage(Text.LANG.aboutProjectTitle(), Text.LANG.aboutProjectText())
					{
						@Override
						public String getParallaxStyle()
						{
							return ParallaxResource.INSTANCE.css().parallaxAboutProject();
						}
					});
				}
			});
		}
		else if (page.equals(Page.DATA_STATISTICS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new StatisticsOverviewPage());
				}
			});
		}
		else if (page.equals(Page.ACKNOWLEDGEMENTS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.ABOUT_GERMINATE, new HTMLPage(Text.LANG.acknowledgementsTitle(), Text.LANG.acknowledgementsText()));
				}
			});
		}
		// COOKIE
		else if (page.equals(Page.COOKIE))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					CookieModal.show();
				}
			});
		}
		// ACCESSIONS FOR COLLSITE
		else if (page.equals(Page.ACCESSIONS_FOR_COLLSITE))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.BROWSE_ACCESSIONS, new AccessionsAtCollsitePage());
				}
			});
		}
		// BROWSE ACCESSIONS
		else if (page.equals(Page.BROWSE_ACCESSIONS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new AccessionOverviewPage());
				}
			});
		}
		// CATEGORICAL DATASETS
		else if (page.equals(Page.CATEGORICAL_DATASETS))
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
							return GerminateSettingsHolder.isPageAvailable(Page.CATEGORICAL_EXPORT);
						}

						@Override
						public void onContinuePressed()
						{
							History.newItem(Page.CATEGORICAL_EXPORT.name());
						}
					}, ExperimentType.phenotype, false);
					widget.setLinkToExportPage(false);
					widget.setShowMap(true);
					widget.setHeaderText(Text.LANG.phenotypeDatasetHeader());
					widget.setShowDownload(true, new SimpleCallback<Dataset>()
					{
						@Override
						public void onSuccess(Dataset result)
						{
							/* Get the id of the selected dataset */
							List<Long> ids = new ArrayList<>();
							ids.add(result.getId());

							/* Start the export process */
							PhenotypeService.Inst.get().export(Cookie.getRequestProperties(), ids, null, null, false, new DefaultAsyncCallback<ServerResult<String>>(true)
							{
								@Override
								protected void onSuccessImpl(ServerResult<String> result)
								{
									clickDownloadLink(result);
								}
							});
						}
					});
					ContentHolder.getInstance().setContent(page, page, widget);
				}
			});

		}
		// CATEGORICAL EXPORT
		else if (page.equals(Page.CATEGORICAL_EXPORT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.CATEGORICAL_DATASETS, new PhenotypePage());
				}
			});

		}

		// CLIMATE DATASETS
		else if (page.equals(Page.CLIMATE_DATASETS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
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
					}, ExperimentType.climate, true);
					widget.setShowMap(true);
					widget.setLinkToExportPage(false);
					widget.setHeaderText(Text.LANG.climateDatasetHeader());

					ContentHolder.getInstance().setContent(page, page, widget);
				}
			});
		}

		// CLIMATE
		else if (page.equals(Page.CLIMATE))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.CLIMATE, new ClimateDataPage());
				}
			});

		}

		// GALLERY
		else if (page.equals(Page.GALLERY))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new ImagePage());
				}
			});
		}

		// GENOTYPE DATASETS
		else if (page.equals(Page.GENOTYPE_DATASETS))
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
											clickDownloadLink(result);
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
		else if (page.equals(Page.GENOTYPE_EXPORT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.GENOTYPE_DATASETS, new GenotypeExportPage());
				}
			});

		}

		// GEOGRAPHIC SEARCH
		else if (page.equals(Page.GEOGRAPHIC_SEARCH))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new GeographicSearchPage());
				}
			});

		}

		// GEOGRAPHY
		else if (page.equals(Page.GEOGRAPHY))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new LocationsPage());
				}
			});

		}

		// GROUPS
		else if (page.equals(Page.GROUPS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new GroupsPage());
				}
			});

		}

		// HELP
		else if (page.equals(Page.HELP))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					HelpWidget.show();
				}
			});
		}

		// HOME
		else if (page.equals(Page.HOME))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new Home());
				}
			});

		}

		// NEWS
		else if (page.equals(Page.NEWS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.HOME, new NewsPage());
				}
			});
		}

		// LOGOUT
		else if (page.equals(Page.LOGOUT))
		{
			/* If we use user authentication */
			if (ModuleCore.getUseAuthentication())
			{
				/* Track information using Google Analytics */
				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.LOGOUT, "logout");

                /* Clear the parameter store and show the login page */
				GerminateEventBus.BUS.fireEvent(new LogoutEvent());
			}
			else
			{
				/* Else just show the start page */
				ContentHolder.getInstance().setContent(page, Page.HOME, new Home());
			}
		}

		//MAP DETAILS
		else if (page.equals(Page.MAP_DETAILS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new MapsPage());
				}
			});

		}

		// MARKER DETAILS
		else if (page.equals(Page.MARKER_DETAILS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.MAP_DETAILS, new MarkerPage());
				}
			});

		}

		// MEGA ENVIRONMENTS
		else if (page.equals(Page.MEGA_ENVIRONMENT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new MegaEnvironmentsPage());
				}
			});

		}

		// PASSPORT
		else if (page.equals(Page.PASSPORT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.BROWSE_ACCESSIONS, new PassportPage());
				}
			});

		}

		// PASSPORT (OSTEREI)
		else if (page.equals(Page.OSTEREI))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.BROWSE_ACCESSIONS, new OsterPassportPage());
				}
			});

		}

		// SEARCH
		else if (page.equals(Page.SEARCH))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.BROWSE_ACCESSIONS, new SearchPage());
				}
			});

		}

		// TRIALS
		else if (page.equals(Page.TRIALS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.TRIALS_DATASETS, new TrialPage());
				}
			});

		}
//
//		// TRIALS INDIVIDUAL
//		else if (page.equals(Page.TRIALS_INDIVIDUAL))
//		{
//			GWT.runAsync(new RunAsyncNotifyCallback()
//			{
//				@Override
//				public void onSuccess()
//				{
//					ContentHolder.getInstance().setContent(page, Page.TRIALS_DATASETS, new TrialsIndividualPage());
//				}
//			});
//
//		}
//
		// TRIALS DATASETS
		else if (page.equals(Page.TRIALS_DATASETS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
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
					}, ExperimentType.trials, false);
					widget.setShowMap(true);
					widget.setLinkToExportPage(false);
					widget.setHeaderText(Text.LANG.trialsDatasetHeader());

					ContentHolder.getInstance().setContent(page, page, widget);
				}
			});

		}
//
//		// GBS EXPORT PAGE
//		else if (page.equals(Page.GBS_EXPORT))
//		{
//			GWT.runAsync(new RunAsyncNotifyCallback()
//			{
//				@Override
//				public void onSuccess()
//				{
//					ContentHolder.getInstance().setContent(page, page, new GBSExportPage());
//				}
//			});
//
//		}
//
//		// PCO COORDINATES PAGE
//		else if (page.equals(Page.PCO_COORDINATES))
//		{
//			GWT.runAsync(new RunAsyncNotifyCallback()
//			{
//				@Override
//				public void onSuccess()
//				{
//					ContentHolder.getInstance().setContent(page, page, new PCOCoordinatesPage());
//				}
//			});
//
//		}
//
		// COLLECTINGSITES TREEMAP
		else if (page.equals(Page.COLLSITE_TREEMAP))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new LocationTreemapPage());
				}
			});

		}

		// ALLELE FREQ DATASETS
		else if (page.equals(Page.ALLELE_FREQUENCY_DATASET))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
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
//					widget.setShowDownload(true, ReferenceFolder.allelefreq);
					widget.setHeaderText(Text.LANG.allelefreqDatasetHeader());

					ContentHolder.getInstance().setContent(page, page, widget);
				}
			});

		}

		// ALLELE FREQ EXPORT
		else if (page.equals(Page.ALLELE_FREQUENCY_EXPORT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.ALLELE_FREQUENCY_DATASET, new AlleleFreqExportPage());
				}
			});

		}

		// ALLELE FREQ RESULT
		else if (page.equals(Page.ALLELE_FREQUENCY_RESULT))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.ALLELE_FREQUENCY_DATASET, new AlleleFreqResultsPage());
				}
			});

		}
		// SHOPPING CART
		else if (page.equals(Page.SHOPPING_CART))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.GROUPS, new ShoppingCartPage());
				}
			});
		}
		// DATASET OVERVIEW
		else if (page.equals(Page.DATASET_OVERVIEW))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new DatasetPage());
				}
			});
		}
		// INSTITUTIONS PAGE
		else if (page.equals(Page.INSTITUTIONS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.GEOGRAPHY, new InstitutionsPage());
				}
			});
		}

		else if (page.equals(Page.GROUP_PREVIEW))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.GROUPS, new GroupPreviewPage());
				}
			});
		}
		else if (page.equals(Page.ADMIN_CONFIG))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.HOME, new AdminConfigPage());
				}
			});
		}
		else if (page.equals(Page.COMPOUNDS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, page, new CompoundPage());
				}
			});
		}
		else if (page.equals(Page.COMPOUND_DETAILS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.COMPOUNDS, new CompoundDetailsPage());
				}
			});
		}
		// TRIALS DATASETS
		else if (page.equals(Page.COMPOUND_DATASETS))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
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
				}
			});

		}
		// COMPOUND DATA
		else if (page.equals(Page.COMPOUND_DATA))
		{
			GWT.runAsync(new RunAsyncNotifyCallback()
			{
				@Override
				public void onSuccess()
				{
					ContentHolder.getInstance().setContent(page, Page.COMPOUND_DATASETS, new CompoundDataPage());
				}
			});
		}

		// FALLBACK
		else
		{
			if (ModuleCore.getUseAuthentication() && !ModuleCore.isLoggedIn())
				GWT.runAsync(new RunAsyncNotifyCallback()
				{
					@Override
					public void onSuccess()
					{
						ContentHolder.getInstance().setContent(page, Page.HOME, new LoginPage());
					}
				});
			else
				GWT.runAsync(new RunAsyncNotifyCallback()
				{
					@Override
					public void onSuccess()
					{
						ContentHolder.getInstance().setContent(page, Page.HOME, new Home());
					}
				});
		}
	}

	private static void clickDownloadLink(ServerResult<String> result)
	{
		/* If there is a result */
		if (result != null && result.getServerResult() != null)
		{
			/* Get the filename from the result */
			String filename = result.getServerResult();

			/* Create a new invisible dummy link on the page */
			String path = new ServletConstants.Builder()
					.setUrl(GWT.getModuleBaseURL())
					.setPath(ServletConstants.SERVLET_FILES)
					.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
					.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
					.setParam(ServletConstants.PARAM_FILE_PATH, filename).build();

			JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), filename);

            /* Click it */
			JavaScript.invokeDownload(path);
		}
		else
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
		}
	}
}
