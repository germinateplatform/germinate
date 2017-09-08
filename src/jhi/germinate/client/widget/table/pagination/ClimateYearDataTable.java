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
import com.google.gwt.dom.client.*;
import com.google.gwt.safecss.shared.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
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
public abstract class ClimateYearDataTable extends DatabaseObjectPaginationTable<ClimateYearData>
{
	private Gradient gradient = new Gradient(Gradient.getTemplateGradient(), 0, 10);

	public ClimateYearDataTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(selectionMode, sortingEnabled);
	}

	@Override
	protected void onPostLoad()
	{
		super.onPostLoad();

		panel.add(GradientUtils.createHorizontalGradientLegend(gradient));
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
		return ClimateYearDataTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<ClimateYearData, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(ClimateYearData object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.ACCESSIONS_FOR_COLLSITE))
						return TableUtils.getHyperlinkValuePadded(object.getId(), "#" + Page.ACCESSIONS_FOR_COLLSITE);
					else
						return SimpleHtmlTemplate.INSTANCE.textPadded(Long.toString(object.getId()));
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyle()
				{
					return Style.combine(Style.LAYOUT_WHITE_SPACE_NO_WRAP, Style.LAYOUT_NO_PADDING);
				}
			};
			column.setDataStoreName(Location.ID);
			addColumn(column, Text.LANG.collectingsiteColumnId(), true);
		}

		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(ClimateYearData object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.ACCESSIONS_FOR_COLLSITE))
					return TableUtils.getHyperlinkValuePadded(object.getName(), "#" + Page.ACCESSIONS_FOR_COLLSITE);
				else
					return SimpleHtmlTemplate.INSTANCE.textPadded(object.getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.combine(Style.LAYOUT_WHITE_SPACE_NO_WRAP, Style.LAYOUT_NO_PADDING);
			}
		};
		column.setDataStoreName(Location.SITE_NAME);
		addColumn(column, Text.LANG.collectingsiteCollsite(), true);

		SafeHtmlCell cell = new SafeHtmlCell();

		for (int i = 1; i <= 12; i++)
		{
			final int theYear = i;
			Column<ClimateYearData, SafeHtml> c = new Column<ClimateYearData, SafeHtml>(cell)
			{
				@Override
				public SafeHtml getValue(ClimateYearData object)
				{
					Double doubleValue = object.getYearToValues().get(theYear);
					String value = "";

					try
					{
						value = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(doubleValue);
					}
					catch (NumberFormatException e)
					{
					}
					return SimpleHtmlTemplate.INSTANCE.text(value);
				}

				@Override
				public void render(Cell.Context context, ClimateYearData object, SafeHtmlBuilder sb)
				{
					Double doubleValue = object.getYearToValues().get(theYear);

					SafeStyles textColor = SafeStylesUtils.forTrustedColor(gradient.getTextColor(doubleValue).toRGBValue());
					SafeStyles backgroundColor = SafeStylesUtils.forTrustedBackgroundColor(gradient.getColor(doubleValue).toRGBValue());

					sb.append(SimpleHtmlTemplate.INSTANCE.color(backgroundColor, textColor, getValue(object).asString()));
				}

				@Override
				public String getCellStyleNames(Cell.Context context, ClimateYearData object)
				{
					return Style.combine(Style.LAYOUT_WHITE_SPACE_NO_WRAP, Style.LAYOUT_NO_PADDING);
				}
			};
			c.setDataStoreName("m" + i);
			addColumn(c, DateUtils.getLocalizedMonthAbbr(theYear), true);
		}
	}

	@Override
	protected void onItemSelected(NativeEvent event, ClimateYearData object, int column)
	{
		/* Get the id */
		if (GerminateSettingsHolder.isPageAvailable(Page.ACCESSIONS_FOR_COLLSITE))
			LongParameterStore.Inst.get().put(Parameter.collectingsiteId, object.getId());
	}

	/**
	 * Determines the minimal and maximal value of the given data for the {@link Gradient}.
	 *
	 * @param table The list of {@link ClimateYearData}s containing the data
	 */
	public void updateGradient(List<ClimateYearData> table)
	{
		/* Keep track of min and max values */
		/* The minimal value of the color coded table */
		double minValue = Float.MAX_VALUE;
		/* The maximal value of the color coded table */
		double maxValue = -Float.MAX_VALUE;

        /* For each row */
		for (ClimateYearData row : table)
		{
			/* And each column */
			for (int i = 1; i <= 12; i++)
			{
				double value = row.getYearToValues().get(i);

                /* Check for min/max */
				if (value < minValue)
					minValue = value;
				if (value > maxValue)
					maxValue = value;
			}
		}

        /* Update the gradient */
		gradient.setMin(minValue);
		gradient.setMax(maxValue);
	}
}
