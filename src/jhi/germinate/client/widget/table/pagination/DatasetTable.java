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
import com.google.gwt.i18n.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.rpc.*;

import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
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
	protected void createColumns()
	{
		Column<Dataset, ?> column;

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
				public String getCellStyleNames(Cell.Context context, Dataset object)
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Dataset.ID);
			addColumn(column, Text.LANG.datasetsColumnDatasetId(), true);
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
					if (linkToExportPage)
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
		addColumn(column, Text.LANG.datasetsColumnExperimentType(), true);


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
		addColumn(column, Text.LANG.datasetsColumnExperimentName(), true);



		/* Add the dataset description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Dataset object)
			{
				/* Check if we want to link to the export page */
				if (linkToExportPage)
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
		addColumn(column, Text.LANG.datasetsColumnDatasetDescription(), true);


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
		addColumn(column, Text.LANG.datasetsColumnContact(), true);

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
		addColumn(column, Text.LANG.datasetsColumnDatasetDate(), true);

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
		addColumn(column, Text.LANG.datasetsColumnDatasetDateEnd(), true);

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
		addColumn(column, Text.LANG.datasetsColumnDatasetSize(), true);

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
		addColumn(column, Text.LANG.datasetsColumnDatasetDataPoints(), true);

		/* Add the style for the dataset state column and the column itself */
		column = new Column<Dataset, SafeHtml>(new SafeHtmlCell())
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
		};
		addColumn(column, "", false, false);

		if (showDownload)
		{
			/*
			 * Add the style for the dataset download column and the column
             * itself
             */
			SafeHtmlCell cell = new SafeHtmlCell()
			{
				@Override
				public Set<String> getConsumedEvents()
				{
					Set<String> events = new HashSet<>();
					events.add(BrowserEvents.CLICK);
					return events;
				}
			};

			Column<Dataset, SafeHtml> downloadColumn = new Column<Dataset, SafeHtml>(cell)
			{
				@Override
				public String getCellStyleNames(Cell.Context context, Dataset row)
				{
					return Style.TEXT_CENTER_ALIGN;
				}

				@Override
				public SafeHtml getValue(Dataset row)
				{
					String sourceFile = row.getSourceFile();

					if (downloadCallback != null)
					{
						String fa = Style.FA_DOWNLOAD;
						String title = Text.LANG.generalDownload();
						return SimpleHtmlTemplate.INSTANCE.fontAwesomeAnchor(fa, title, UriUtils.fromString(""), "");
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

				@Override
				public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
				{
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

			addColumn(downloadColumn, "", false, false);
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

	protected void onSelectionChanged(NativeEvent event, Dataset object, int column)
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
			case genotype:
				parameter = Parameter.genotypeDatasetIds;
				break;
			case phenotype:
				parameter = Parameter.phenotypeDatasetIds;
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
			case phenotype:
				if (GerminateSettingsHolder.isPageAvailable(Page.CATEGORICAL_EXPORT))
					page = Page.CATEGORICAL_EXPORT;
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
