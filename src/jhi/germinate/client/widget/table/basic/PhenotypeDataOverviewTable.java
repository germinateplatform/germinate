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

package jhi.germinate.client.widget.table.basic;

import com.google.gwt.cell.client.*;
import com.google.gwt.user.cellview.client.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class PhenotypeDataOverviewTable extends AdvancedTable<DataStats>
{

	public PhenotypeDataOverviewTable(List<DataStats> data)
	{
		super(data);

		Column<DataStats, String> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new Column<DataStats, String>(new TextCell())
			{
				@Override
				public String getValue(DataStats object)
				{
					return Long.toString(object.getId());
				}
			};
			column.setSortable(true);
			addStringColumn(column, Text.LANG.phenotypeColumnId());
		}

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return object.getName();
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.phenotypeColumnName());

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return object.getDescription();
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.phenotypeColumnDescription());

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				String result = null;

				if (object.getUnit() != null)
				{
					result = object.getUnit().getName();

					if (!StringUtils.isEmpty(object.getUnit().getAbbreviation()))
						result += " [" + object.getUnit().getAbbreviation() + "]";
				}

				return result;
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.unitColumnName());

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return TableUtils.getCellValueAsString(Double.toString(object.getMin()));
			}

			@Override
			public String getCellStyleNames(Cell.Context context, DataStats object)
			{
				return Style.TEXT_RIGHT_ALIGN;
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.generalMinimum());
		getTable().getHeader(getTable().getColumnCount() - 1).setHeaderStyleNames(Style.TEXT_RIGHT_ALIGN);

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return TableUtils.getCellValueAsString(Double.toString(object.getAvg()));
			}

			@Override
			public String getCellStyleNames(Cell.Context context, DataStats object)
			{
				return Style.TEXT_RIGHT_ALIGN;
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.generalAverage());
		getTable().getHeader(getTable().getColumnCount() - 1).setHeaderStyleNames(Style.TEXT_RIGHT_ALIGN);

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return TableUtils.getCellValueAsString(Double.toString(object.getMax()));
			}

			@Override
			public String getCellStyleNames(Cell.Context context, DataStats object)
			{
				return Style.TEXT_RIGHT_ALIGN;
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.generalMaximum());
		getTable().getHeader(getTable().getColumnCount() - 1).setHeaderStyleNames(Style.TEXT_RIGHT_ALIGN);

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return TableUtils.getCellValueAsString(Double.toString(object.getStd()));
			}

			@Override
			public String getCellStyleNames(Cell.Context context, DataStats object)
			{
				return Style.TEXT_RIGHT_ALIGN;
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.generalStandardDeviation());
		getTable().getHeader(getTable().getColumnCount() - 1).setHeaderStyleNames(Style.TEXT_RIGHT_ALIGN);

		column = new Column<DataStats, String>(new TextCell())
		{
			@Override
			public String getValue(DataStats object)
			{
				return DatasetTable.getTextTruncated(object.getDataset());
			}
		};
		column.setSortable(true);
		addStringColumn(column, Text.LANG.datasetsColumnDatasetDescription());
	}
}
