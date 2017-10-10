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

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
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
	private boolean linkToExportPage = false;
	private boolean showDownload     = false;
	private ReferenceFolder         referenceFolder;
	private SimpleCallback<Dataset> downloadCallback;

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

		/* Add the experiment type column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				if (object.getExperiment().getType() != ExperimentType.unknown)
				{
					/* Check if we want to link to the export page */
					if (linkToExportPage && object.hasLicenseBeenAccepted(ModuleCore.getUserAuth().getId()))
						return getExportPageLink(object, object.getExperiment().getType().name());
					else
						return DatasetTable.this.getValue(object, object.getExperiment().getType().name());
				}
				else
				{
					return SimpleHtmlTemplate.INSTANCE.text("");
				}
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
		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				if (object.getExperiment() != null)
				{
					return object.getExperiment().getName();
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

		/* Add the dataset description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				/* Check if we want to link to the export page */
				if (linkToExportPage && object.hasLicenseBeenAccepted(ModuleCore.getUserAuth().getId()))
					return getExportPageLink(object, object.getDescription());
				else
					return DatasetTable.this.getValue(object, object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Dataset.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnDatasetDescription(), sortingEnabled);

		/* Add the dataset datatype column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				return DatasetTable.this.getValue(object, object.getDatatype());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Dataset.DATATYPE);
		addColumn(column, Text.LANG.datasetsColumnDatasetDatatype(), sortingEnabled);

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

					if (object.hasLicenseBeenAccepted(ModuleCore.getUserAuth().getId()))
						icon = Style.MDI_CHECK;

					if (data != null)
						return TableUtils.getHyperlinkValueWithIcon(object.getLicense().getName(), null, Style.combine(Style.MDI, Style.FA_LG, Style.FA_FIXED_WIDTH, Emphasis.PRIMARY.getCssName(), icon));
					else
						return TableUtils.getCellValueWithIcon(object.getLicense().getName(), Style.combine(Style.MDI, Style.FA_LG, Style.FA_FIXED_WIDTH, Emphasis.PRIMARY.getCssName(), icon));
				}
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
			{
				if (BrowserEvents.CLICK.equals(event.getType()) && object.getLicense() != null && data != null)
				{
					event.preventDefault();

					if (object.hasLicenseBeenAccepted(ModuleCore.getUserAuth().getId()))
					{
						new AlertDialog(Text.LANG.licenseWizardTitle(), new HTML(data.getContent()))
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), IconType.BAN, null))
								.open();
					}
					else
					{
						HTML html = new HTML(data.getContent());
						html.getElement().getStyle().setProperty("maxHeight", "70vh");
						html.getElement().getStyle().setOverflowY(com.google.gwt.dom.client.Style.Overflow.AUTO);
						new AlertDialog(Text.LANG.licenseWizardTitle(), html)
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalAccept(), IconType.CHECK, ButtonType.SUCCESS, e ->
								{
									LicenseLog log = new LicenseLog(-1L)
											.setLicense(object.getLicense().getId())
											.setUser(ModuleCore.getUserAuth().getId())
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
								.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDecline(), IconType.BAN, ButtonType.DANGER, null))
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
		addColumn(column, Text.LANG.datasetsColumnDatasetSize(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Dataset object)
			{
				Long count = object.getDataPoints();

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
		column.setDataStoreName(Dataset.NR_OF_DATA_POINTS);
		addColumn(column, Text.LANG.datasetsColumnDatasetDataPoints(), sortingEnabled);

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
							.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), IconType.BAN, null))
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

					if (!StringUtils.isEmpty(object.getDublinCore()))
						content.add(new DublinCoreWidget(object.getDublinCore()));
					if (!CollectionUtils.isEmpty(object.getAttributeData()))
						content.add(new AttributeDataWidget(object.getAttributeData()));

					if (content.getWidgetCount() > 0)
					{
						new AlertDialog(Text.LANG.datasetAttributesTitle(), content)
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), IconType.BAN, null))
								.open();
					}
				}
				else
				{
					super.onBrowserEvent(context, elem, object, event);
				}
			}
		}, "", false);

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

		if (showDownload)
		{
			/*
			 * Add the style for the dataset download column and the column
             * itself
             */
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
					if (ModuleCore.getUseAuthentication() && !row.hasLicenseBeenAccepted(ModuleCore.getUserAuth().getId()))
					{
						return SimpleHtmlTemplate.INSTANCE.text("");
					}
					else
					{
						String sourceFile = row.getSourceFile();

						if (downloadCallback != null)
						{
							String fa = Style.MDI_DOWNLOAD;
							String title = Text.LANG.generalDownload();
							return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(fa, title, UriUtils.fromString(""), "");
						}
						else if (!StringUtils.isEmpty(sourceFile) && !sourceFile.equals("NA"))
						{
							String href = new ServletConstants.Builder()
									.setUrl(GWT.getModuleBaseURL())
									.setPath(ServletConstants.SERVLET_FILES)
									.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
									.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
									.setParam(ServletConstants.PARAM_FILE_LOCATION, FileLocation.data.name())
									.setParam(ServletConstants.PARAM_FILE_PATH, (referenceFolder == null ? "" : (referenceFolder.name() + "/")) + sourceFile)
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
					if (ModuleCore.getUseAuthentication() && !object.hasLicenseBeenAccepted(ModuleCore.getUserAuth().getId()))
					{
						event.preventDefault();
						return;
					}

					if (BrowserEvents.CLICK.equals(event.getType()) && downloadCallback != null)
					{
						event.preventDefault();
						downloadCallback.onSuccess(object);
					}
					else
					{
						super.onBrowserEvent(context, elem, object, event);
					}
				}
			};

			addColumn(downloadColumn, "", false);
		}
	}

	public void setShowDownload(boolean showDownload, ReferenceFolder referenceFolder)
	{
		this.showDownload = showDownload;
		this.referenceFolder = referenceFolder;
	}

	public void setShowDownload(boolean showDownload, SimpleCallback<Dataset> downloadCallback)
	{
		this.showDownload = showDownload;
		this.downloadCallback = downloadCallback;
	}

	protected void onItemSelected(NativeEvent event, Dataset object, int column)
	{
		/* Get their ids */
		List<Long> ids = new ArrayList<>();
		ids.add(object.getId());

		Parameter parameter = null;

		switch (object.getExperiment().getType())
		{
			case allelefreq:
				parameter = Parameter.allelefreqDatasetIds;
				break;
			case climate:
				parameter = Parameter.climateDatasetIds;
				break;
			case compound:
				parameter = Parameter.compoundDatasetIds;
				break;
			case genotype:
				parameter = Parameter.genotypeDatasetIds;
				break;
			case trials:
				parameter = Parameter.trialsDatasetIds;
				break;
		}

		if (parameter != null)
			LongListParameterStore.Inst.get().put(parameter, ids);
	}

	/**
	 * Returns the {@link SafeHtml} representing the link to the export page of the {@link ExperimentType} of this {@link Dataset}
	 *
	 * @param dataset The {@link Dataset}
	 * @param text    The text to display
	 * @return The {@link SafeHtml} instance representing the link to the export page of the {@link ExperimentType} of this {@link Dataset}
	 */
	public SafeHtml getExportPageLink(Dataset dataset, String text)
	{
		/* If there's not text to display, just return */
		if (dataset.getExperiment() == null || dataset.getExperiment().getType() == null)
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

		/* If we found a Page, link to it */
		if (page != null)
		{
			SafeUri uri = UriUtils.fromString("#" + page.name());
			return SimpleHtmlTemplate.INSTANCE.anchor(uri, text);
		}
		/* Otherwise just show the text */
		else
			return SimpleHtmlTemplate.INSTANCE.text(text);
	}

	/**
	 * Gets the html cell value
	 *
	 * @param dataset The {@link Dataset}
	 * @param text    The text to display
	 * @return The generated {@link SafeHtml}
	 */
	public SafeHtml getValue(Dataset dataset, String text)
	{
		if (StringUtils.isEmpty(text))
		{
			return SimpleHtmlTemplate.INSTANCE.text("");
		}
		else if (StringUtils.isEmpty(dataset.getHyperlink()))
		{
			return SimpleHtmlTemplate.INSTANCE.text(text);
		}
		else
		{
			SafeUri href = UriUtils.fromString(dataset.getHyperlink());
			return SimpleHtmlTemplate.INSTANCE.anchorNewTab(href, text);
		}
	}
}
