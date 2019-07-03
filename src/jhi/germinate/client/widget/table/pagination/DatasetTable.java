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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.Locale;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.dataset.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DatasetTable extends DatabaseObjectPaginationTable<Dataset>
{
	private static final int TRUNCATION_LIMIT = 150;

	private boolean                 linkToExportPage;
	private boolean                 showDownload = false;
	private DatasetDownloadCallback downloadCallback;

	public DatasetTable(SelectionMode selectionMode, boolean sortingEnabled, boolean linkToExportPage)
	{
		super(selectionMode, sortingEnabled);
		this.linkToExportPage = linkToExportPage;
	}

	@Override
	protected boolean supportsFiltering()
	{
		return false;
	}

	@Override
	protected boolean supportsDownload()
	{
		return false;
	}

	@Override
	protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	protected String getClassName()
	{
		return DatasetTable.class.getSimpleName();
	}

	public static String getTextTruncated(String text)
	{
		if (StringUtils.isEmpty(text))
			return "";
		else
			return StringUtils.getWordsUntil(text, TRUNCATION_LIMIT);
	}

	public void setShowDownload(boolean showDownload)
	{
		this.showDownload = showDownload;
		this.downloadCallback = new DatasetDownloadCallback();
	}

	public void setShowDownload(DatasetDownloadCallback downloadCallback)
	{
		this.showDownload = true;
		this.downloadCallback = downloadCallback;
	}

	@Override
	protected void onItemSelected(NativeEvent event, Dataset object, int column)
	{
		if (Objects.equals(table.getColumn(column).getDataStoreName(), Experiment.EXPERIMENT_NAME))
		{
			LongParameterStore.Inst.get().put(Parameter.experimentId, object.getExperiment().getId());
		}
		else
		{
			/* Get their ids */
			List<Dataset> ids = new ArrayList<>();
			ids.add(object);

			Parameter parameter = null;

			switch (object.getExperiment().getType())
			{
				case allelefreq:
					parameter = Parameter.allelefreqDatasets;
					break;
				case climate:
					parameter = Parameter.climateDatasets;
					break;
				case compound:
					parameter = Parameter.compoundDatasets;
					break;
				case genotype:
					parameter = Parameter.genotypeDatasets;
					break;
				case trials:
					parameter = Parameter.trialsDatasets;
					break;
			}

			if (parameter != null)
				DatasetListParameterStore.Inst.get().put(parameter, ids);

			GerminateEventBus.BUS.fireEvent(new DatasetSelectionEvent(ids));
		}
	}

	/**
	 * Gets the html cell value
	 *
	 * @param dataset The {@link Dataset}
	 * @param text    The text to display
	 * @return The generated {@link SafeHtml}
	 */
	public static SafeHtml getValueTruncated(Dataset dataset, String text)
	{
		if (StringUtils.isEmpty(text))
		{
			return SimpleHtmlTemplate.INSTANCE.text("");
		}
		else
		{
			String truncated = StringUtils.getWordsUntil(text, TRUNCATION_LIMIT);

			if (StringUtils.isEmpty(dataset.getHyperlink()))
			{
				return SimpleHtmlTemplate.INSTANCE.textTruncated(text, truncated);
			}
			else
			{
				SafeUri href = UriUtils.fromString(dataset.getHyperlink());
				return SimpleHtmlTemplate.INSTANCE.anchorNewTabTruncated(href, text, truncated);
			}
		}
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Dataset, ?> column;
		SafeHtmlCell clickCell = new SafeHtmlCell()
		{
			@Override
			public Set<String> getConsumedEvents()
			{
				Set<String> events = new HashSet<>();
				events.add(BrowserEvents.CLICK);
				return events;
			}
		};

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(Dataset object)
				{
					return Long.toString(object.getId());
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyle()
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Dataset.ID);
			addColumn(column, Text.LANG.datasetsColumnDatasetId(), sortingEnabled);
		}

		/* Add the dataset name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				/* Check if we want to link to the export page */
				if (linkToExportPage && canAccess(object) && !object.isExternal())
					return getExportPageLinkTruncated(object, object.getName());
				else
					return DatasetTable.getValueTruncated(object, object.getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Dataset.NAME);
		addColumn(column, Text.LANG.datasetsColumnDatasetName(), sortingEnabled);

		/* Add the dataset description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				/* Check if we want to link to the export page */
				if (linkToExportPage && canAccess(object) && !object.isExternal())
					return getExportPageLinkTruncated(object, object.getDescription());
				else
					return DatasetTable.getValueTruncated(object, object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Dataset.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnDatasetDescription(), sortingEnabled);

		/* Add the experiment type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				if (object.getExperiment().getType() != ExperimentType.unknown)
					return object.getExperiment().getType().name();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(ExperimentType.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnExperimentType(), sortingEnabled);

		/* Experiment name */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				if (object.getExperiment() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.EXPERIMENT_DETAILS))
						return TableUtils.getHyperlinkValue(object.getExperiment().getName(), "#" + Page.EXPERIMENT_DETAILS);
					else
						return DatasetTable.getValueTruncated(object, object.getExperiment().getName());
				}

				return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Experiment.EXPERIMENT_NAME);
		addColumn(column, Text.LANG.datasetsColumnExperimentName(), sortingEnabled);

		/* Add the dataset datatype column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				return getValueTruncated(object, object.getDatatype());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Dataset.DATATYPE);
		addColumn(column, Text.LANG.datasetsColumnDatasetDatatype(), sortingEnabled);

		// Add the site name column
		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset dataset)
			{
				if (dataset.getLocation() != null)
					return dataset.getLocation().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Location.SITE_NAME);
		addColumn(column, Text.LANG.datasetsColumnSiteName(), sortingEnabled);

		/* Add the country column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				if (object.getLocation() != null && object.getLocation().getCountry() != null)
					return object.getLocation().getCountry().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public void render(Cell.Context context, Dataset object, SafeHtmlBuilder sb)
			{
				String value = getValue(object);
				if (value != null)
				{
					sb.appendHtmlConstant("<span class=\"" + Style.COUNTRY_FLAG + " " + object.getLocation().getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "\"></span>");
					sb.append(SafeHtmlUtils.fromString(value));
				}
				else
				{
					super.render(context, object, sb);
				}
			}
		};
		column.setDataStoreName(Country.COUNTRY_NAME);
		addColumn(column, Text.LANG.datasetsColumnCountry(), sortingEnabled);

		/* Add the license description column */
		column = new ClickableSafeHtmlColumn()
		{
			private LicenseData data = null;

			@Override
			public String getCellStyle()
			{
				return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
			}

			@Override
			public SafeHtml getValue(Dataset object)
			{
				if (object.getLicense() != null)
				{
					data = object.getLicense().getLicenseData(LocaleInfo.getCurrentLocale().getLocaleName());

					String icon = Style.MDI_NEW_BOX;

					if (canAccess(object))
						icon = Style.MDI_CHECK;

					if (data != null)
						return TableUtils.getHyperlinkValueWithIcon(object.getLicense().getName(), null, Style.combine(Style.mdiLgFw(icon), Emphasis.PRIMARY.getCssName()));
					else
						return TableUtils.getCellValueWithIcon(object.getLicense().getName(), Style.combine(Style.mdiLgFw(icon), Emphasis.PRIMARY.getCssName()));
				}
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			private String getLicenseFileName(Dataset dataset)
			{
				String filename = dataset.getLicense().getName();
				filename = dataset.getId() + "-" + filename.replace(' ', '-') + ".html";
				return filename;
			}

			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
			{
				if (BrowserEvents.CLICK.equals(event.getType()) && object.getLicense() != null && data != null)
				{
					event.preventDefault();

					License license = object.getLicense();
					license.setExtra(Dataset.ID, object.getId());

					LicenseWizardPage page = new LicenseWizardPage(license, null);
					if (!ModuleCore.getUseAuthentication() || object.hasLicenseBeenAccepted(ModuleCore.getUserAuth()))
					{
						new AlertDialog(Text.LANG.licenseWizardTitle(), page)
								.setPrintable(page.getId(), getLicenseFileName(object))
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), Style.MDI_CANCEL, null))
								.open();
					}
					else
					{
						new AlertDialog(Text.LANG.licenseWizardTitle(), page)
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalAccept(), Style.MDI_CHECK, ButtonType.SUCCESS, e ->
								{
									LicenseLog log = new LicenseLog(-1L)
											.setLicense(object.getLicense().getId())
											.setUser(ModuleCore.getUseAuthentication() ? ModuleCore.getUserAuth().getId() : -1L)
											.setAcceptedOn(System.currentTimeMillis());

									DatasetService.Inst.get().updateLicenseLogs(Cookie.getRequestProperties(), Collections.singletonList(log), new AsyncCallback<ServerResult<Boolean>>()
									{
										@Override
										public void onFailure(Throwable caught)
										{
										}

										@Override
										public void onSuccess(ServerResult<Boolean> result)
										{
											DatasetTable.this.refreshTable();
										}
									});
								}))
								.setPrintable(page.getId(), getLicenseFileName(object))
								.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDecline(), Style.MDI_CANCEL, ButtonType.DANGER, null))
								.open();
					}
				}
				else
				{
					super.onBrowserEvent(context, elem, object, event);
				}
			}
		};
		column.setDataStoreName(License.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnLicenseDescription(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				return object.getContact();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Dataset.CONTACT);
		addColumn(column, Text.LANG.datasetsColumnContact(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				return DateUtils.getLocalizedDate(object.getDateStart());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Dataset.DATE_START);
		addColumn(column, Text.LANG.datasetsColumnDatasetDate(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				return DateUtils.getLocalizedDate(object.getDateEnd());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Dataset.DATE_END);
		addColumn(column, Text.LANG.datasetsColumnDatasetDateEnd(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				Long count = object.getSize();

				if (count == null || count == 0L)
					return null;
				else
					return Long.toString(count);
			}

			@Override
			public Class getType()
			{
				return Long.class;
			}
		};
		column.setDataStoreName(Dataset.NR_OF_DATA_OBJECTS);
		addColumn(column, new HeaderConfig(Text.LANG.datasetsColumnDatasetSize(), Text.LANG.datasetsColumnHelpDatasetSize()), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				Long count = object.getDataPoints();

				if (count == null || count == 0L)
					return null;
				else
				{
					if (object.getExperiment().getType() == ExperimentType.genotype || object.getExperiment().getType() == ExperimentType.allelefreq)
						return "≤" + Long.toString(count);
					else
						return Long.toString(count);
				}
			}

			@Override
			public Class getType()
			{
				return Long.class;
			}
		};
		column.setDataStoreName(Dataset.NR_OF_DATA_POINTS);
		addColumn(column, new HeaderConfig(Text.LANG.datasetsColumnDatasetDataPoints(), Text.LANG.datasetsColumnHelpDatasetDataPoints()), sortingEnabled);

		/* Add the collaborator column */
		addColumn(new Column<Dataset, SafeHtml>(clickCell)
		{
			@Override
			public String getCellStyleNames(Cell.Context context, Dataset row)
			{
				return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
			}

			@Override
			public SafeHtml getValue(Dataset row)
			{
				if (!CollectionUtils.isEmpty(row.getCollaborators()))
					return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(Style.MDI_ACCOUNT_MULTIPLE, Text.LANG.datasetCollaboratorsTitle(), UriUtils.fromString(""), "");
				else
					return SimpleHtmlTemplate.INSTANCE.empty();
			}

			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
			{
				if (BrowserEvents.CLICK.equals(event.getType()) && !CollectionUtils.isEmpty(object.getCollaborators()))
				{
					event.preventDefault();

					new AlertDialog(Text.LANG.datasetCollaboratorsTitle(), new CollaboratorTable(SelectionMode.NONE, false)
					{
						@Override
						protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Collaborator>>> callback)
						{
							List<Collaborator> page = object.getCollaborators().subList(pagination.getStart(), Math.min(object.getCollaborators().size(), pagination.getStart() + pagination.getLength()));
							callback.onSuccess(new PaginatedServerResult<>(null, page, object.getCollaborators().size()));
							return null;
						}
					})
							.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), Style.MDI_CANCEL, null))
							.setSize(ModalSize.LARGE)
							.open();
				}
				else
				{
					super.onBrowserEvent(context, elem, object, event);
				}
			}
		}, "", false);

		/* Add the attribute data column */
		addColumn(new Column<Dataset, SafeHtml>(clickCell)
		{
			@Override
			public String getCellStyleNames(Cell.Context context, Dataset row)
			{
				return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
			}

			@Override
			public SafeHtml getValue(Dataset row)
			{
				if (!CollectionUtils.isEmpty(row.getAttributeData()) || !StringUtils.isEmpty(row.getDublinCore()))
					return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(Style.MDI_FILE_PLUS, Text.LANG.datasetAttributesTitle(), UriUtils.fromString(""), "");
				else
					return SimpleHtmlTemplate.INSTANCE.empty();
			}

			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
			{
				if (BrowserEvents.CLICK.equals(event.getType()) && (!CollectionUtils.isEmpty(object.getAttributeData()) || !StringUtils.isEmpty(object.getDublinCore())))
				{
					event.preventDefault();

					FlowPanel content = new FlowPanel();

					if (!CollectionUtils.isEmpty(object.getAttributeData()))
						content.add(new AttributeDataWidget(object.getAttributeData()));
					if (!StringUtils.isEmpty(object.getDublinCore()))
						content.add(new DublinCoreWidget(object.getDublinCore()));

					if (content.getWidgetCount() > 0)
					{
						new DatasetAttributeDownloadDialog(Text.LANG.datasetAttributesTitle(), content, object)
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), Style.MDI_CANCEL, null))
								.open();
					}
				}
				else
				{
					super.onBrowserEvent(context, elem, object, event);
				}
			}
		}, "", false);

		if (showDownload)
		{
			// Add the style for the dataset download column and the column itself
			Column<Dataset, SafeHtml> downloadColumn = new Column<Dataset, SafeHtml>(clickCell)
			{
				@Override
				public String getCellStyleNames(Cell.Context context, Dataset row)
				{
					return Style.TEXT_CENTER_ALIGN;
				}

				@Override
				public SafeHtml getValue(Dataset row)
				{
					// If no authentication, but license available OR authentication but user hasn't accepted
					if (!canAccess(row) || row.isExternal())
					{
						return SimpleHtmlTemplate.INSTANCE.text("");
					}
					else
					{
						String sourceFile = row.getSourceFile();

						if (downloadCallback != null)
						{
							if (downloadCallback.isSupported(row.getExperiment().getType()))
							{
								String fa = Style.MDI_DOWNLOAD;
								String title = Text.LANG.generalDownload();
								return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(fa, title, UriUtils.fromString(""), "");
							}
							else
							{
								return SimpleHtmlTemplate.INSTANCE.text("");
							}
						}
						else if (!StringUtils.isEmpty(sourceFile) && !sourceFile.equals("NA"))
						{
							String href = new ServletConstants.Builder()
									.setUrl(GWT.getModuleBaseURL())
									.setPath(ServletConstants.SERVLET_FILES)
									.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
									.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
									.setParam(ServletConstants.PARAM_FILE_LOCATION, FileLocation.data.name())
									.build();

							String fa = Style.MDI_DOWNLOAD;
							String title = Text.LANG.generalDownload();
							return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(fa, title, UriUtils.fromString(href), "blank");
						}
						else
						{
							return SimpleHtmlTemplate.INSTANCE.text("");
						}
					}
				}

				@Override
				public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
				{
					if (!canAccess(object) || object.isExternal())
					{
						event.preventDefault();
						return;
					}

					if (BrowserEvents.CLICK.equals(event.getType()) && downloadCallback != null)
					{
						if (downloadCallback.isSupported(object.getExperiment().getType()))
						{
							event.preventDefault();
							downloadCallback.onSuccess(object);
						}
						else
						{
							super.onBrowserEvent(context, elem, object, event);
						}
					}
					else
					{
						super.onBrowserEvent(context, elem, object, event);
					}
				}
			};

			addColumn(downloadColumn, "", false);
		}

		/* Add the style for the dataset state column and the column itself */
		addColumn(new Column<Dataset, SafeHtml>(new SafeHtmlCell())
		{
			@Override
			public String getCellStyleNames(Cell.Context context, Dataset row)
			{
				String style = Style.TEXT_CENTER_ALIGN;
				if (row.getDatasetState() == DatasetState.HIDDEN)
					style = Style.combine(style, Emphasis.PRIMARY.getCssName());

				return style;
			}

			@Override
			public SafeHtml getValue(Dataset row)
			{
				String mdi = "";
				String title = "";

				switch (row.getDatasetState())
				{
					case HIDDEN:
						mdi = Style.MDI_EYE_OFF;
						title = Text.LANG.datasetStateHidden();
						break;
					case PUBLIC:
						mdi = Style.MDI_LOCK_OPEN;
						title = Text.LANG.datasetStatePublic();
						break;
					case PRIVATE:
						mdi = Style.MDI_LOCK;
						title = Text.LANG.datasetStatePrivate();
						break;
				}

				return SimpleHtmlTemplate.INSTANCE.materialIconFixedWidth(mdi, title);
			}
		}, "", false);
	}

	private boolean canAccess(Dataset dataset)
	{
		return dataset.hasLicenseBeenAccepted(ModuleCore.getUserAuth());
	}

	/**
	 * Returns the {@link SafeHtml} representing the link to the export page of the {@link ExperimentType} of this {@link Dataset}
	 *
	 * @param dataset The {@link Dataset}
	 * @param text    The text to display
	 * @return The {@link SafeHtml} instance representing the link to the export page of the {@link ExperimentType} of this {@link Dataset}
	 */
	public SafeHtml getExportPageLinkTruncated(Dataset dataset, String text)
	{
		/* If there's not text to display, just return */
		if (StringUtils.isEmpty(text) || dataset.getExperiment() == null || dataset.getExperiment().getType() == null)
			return SimpleHtmlTemplate.INSTANCE.text("");

		ExperimentType type = dataset.getExperiment().getType();

		Page page = null;

		/* Then switch the type and get the associated Page */
		switch (type)
		{
			case allelefreq:
				if (GerminateSettingsHolder.isPageAvailable(Page.ALLELE_FREQUENCY_EXPORT))
					page = Page.ALLELE_FREQUENCY_EXPORT;
				break;
			case climate:
				if (GerminateSettingsHolder.isPageAvailable(Page.CLIMATE))
					page = Page.CLIMATE;
				break;
			case genotype:
				if (GerminateSettingsHolder.isPageAvailable(Page.GENOTYPE_EXPORT))
					page = Page.GENOTYPE_EXPORT;
				break;
			case trials:
				if (GerminateSettingsHolder.isPageAvailable(Page.TRIALS))
					page = Page.TRIALS;
				break;
			case compound:
				if (GerminateSettingsHolder.isPageAvailable(Page.COMPOUND_DATA))
					page = Page.COMPOUND_DATA;
				break;
		}

		String truncated = StringUtils.getWordsUntil(text, TRUNCATION_LIMIT);

		/* If we found a Page, link to it */
		if (page != null)
		{
			SafeUri uri = UriUtils.fromString("#" + page.name());
			return SimpleHtmlTemplate.INSTANCE.anchorTruncated(uri, text, truncated);
		}
		/* Otherwise just show the text */
		else
			return SimpleHtmlTemplate.INSTANCE.textTruncated(text, truncated);
	}
}
