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

import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class PhenotypeDataTable extends MarkableDatabaseObjectPaginationTable<PhenotypeData>
{
	public PhenotypeDataTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(MarkedItemList.ItemType.ACCESSION, selectionMode, sortingEnabled);
	}

	@Override
	public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	public boolean supportsFullIdMarking()
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
		return PhenotypeDataTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<PhenotypeData, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(PhenotypeData object)
				{
					if (object.getAccession() != null)
						return Long.toString(object.getAccession().getId());
					else return null;
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
			column.setDataStoreName(Accession.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), sortingEnabled);
		}

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(PhenotypeData object)
			{
				if (object.getAccession() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getAccession().getGeneralIdentifier(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getAccession().getGeneralIdentifier());
				}
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.GENERAL_IDENTIFIER);
		addColumn(column, Text.LANG.accessionsColumnGeneralIdentifier(), sortingEnabled);

		/* Add the accession name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(PhenotypeData object)
			{
				if (object.getAccession() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getAccession().getName(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getAccession().getName());
				}
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.NAME);
		addColumn(column, Text.LANG.accessionsColumnName(), sortingEnabled);

		/* Add the dataset description column */
		column = new SafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(PhenotypeData object)
			{
				if (object.getDataset() == null)
					return null;
				else
					return DatasetTable.getValueTruncated(object.getDataset(), object.getDataset().getDescription());
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
			public String getValue(PhenotypeData object)
			{
				if (object.getDataset() == null || object.getDataset().getExperiment() == null || object.getDataset().getExperiment().getType() == null)
					return null;
				else
					return object.getDataset().getExperiment().getType().name();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(ExperimentType.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnExperimentType(), sortingEnabled);

		/* Add the phenotype name column */
		column = new TextColumn()
		{
			@Override
			public String getValue(PhenotypeData object)
			{
				if (object.getPhenotype() == null)
					return null;
				else
					return object.getPhenotype().getName();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Phenotype.NAME);
		addColumn(column, Text.LANG.phenotypeColumnName(), sortingEnabled);

		/* Add the phenotype short name column */
		column = new TextColumn()
		{
			@Override
			public String getValue(PhenotypeData object)
			{
				if (object.getPhenotype() == null)
					return null;
				else
					return object.getPhenotype().getShortName();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Phenotype.SHORT_NAME);
		addColumn(column, Text.LANG.phenotypeColumnShortName(), sortingEnabled);

		/* Add the unit name column */
		column = new TextColumn()
		{
			@Override
			public String getValue(PhenotypeData object)
			{
				if (object.getPhenotype() == null || object.getPhenotype().getUnit() == null)
					return null;
				else
					return object.getPhenotype().getUnit().getName();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Unit.NAME);
		addColumn(column, Text.LANG.unitColumnName(), sortingEnabled);

		/* Add the recording date column */
		column = new TextColumn()
		{
			@Override
			public String getValue(PhenotypeData object)
			{
				return DateUtils.getLocalizedDateTime(object.getRecordingDate());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(PhenotypeData.RECORDING_DATE);
		addColumn(column, Text.LANG.phenotypeColumnRecordingDate(), sortingEnabled);

		/* Add the phenotype value column */
		column = new TextColumn()
		{
			@Override
			public String getValue(PhenotypeData object)
			{
				return object.getValue();
			}

			@Override
			public Class getType()
			{
				return Object.class;
			}
		};
		column.setDataStoreName(PhenotypeData.PHENOTYPE_VALUE);
		addColumn(column, Text.LANG.phenotypeColumnValue(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, PhenotypeData object, int column)
	{
		/* Get the id */
		if (object.getAccession() != null)
			LongParameterStore.Inst.get().put(Parameter.accessionId, object.getAccession().getId());
	}
}
