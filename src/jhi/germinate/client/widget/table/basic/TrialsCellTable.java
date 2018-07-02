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
import com.google.gwt.safecss.shared.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class TrialsCellTable extends GerminateComposite
{
	private List<String>              years;
	private List<TrialsRow>           data;
	private TrialsRow.TrialsAttribute trialsAttribute;
	private AdvancedTable<TrialsRow>  table;

	public TrialsCellTable(List<String> years, List<TrialsRow> data, TrialsRow.TrialsAttribute trialsAttribute, AccessionClickHandler handler)
	{
		this.years = years;
		this.data = data;
		this.trialsAttribute = trialsAttribute;

		if (handler != null)
			addAccessionLinkFunction(handler);
	}

	/**
	 * Determines the minimal and maximal value of the given data for the {@link Gradient}.
	 *
	 * @param table The list of {@link TrialsRow}s containing the data
	 * @param years The years
	 */
	private void determineMinMaxValues(List<TrialsRow> table, List<String> years)
	{
		/* For each row */
		for (TrialsRow row : table)
		{
			/* Keep track of min and max values */
			float minValue = Float.MAX_VALUE;
			float maxValue = -Float.MAX_VALUE;

            /* And each column */
			for (String year : years)
			{
				if (year.equals("phenotype"))
					continue;

				float value;
				try
				{
					/* Get the value from the GerminateTable */
					value = Float.parseFloat(row.getYearToValues().get(year).getAttribute(trialsAttribute));
				}
				catch (Exception e)
				{
					continue;
				}

                /* Check for min/max */
				if (value < minValue)
					minValue = value;
				if (value > maxValue)
					maxValue = value;
			}

			row.setGradient(new Gradient(Gradient.getPrimaryColorGradient(), minValue, maxValue));
		}
	}

	protected SafeHtml formatTrialsCellContent(TrialsRow.TrialsCell cell, String textColor)
	{
		if (cell == null)
			return SimpleHtmlTemplate.INSTANCE.empty();
		else
		{
			/* Format to two decimal places */
			String min = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(cell.getMin());
			String avg = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(cell.getAvg());
			String max = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(cell.getMax());

			StringBuilder builder = new StringBuilder();

//			builder.append("<div class='").append(Style.combine(StyleConstants.FONT_80_PERCENT, StyleConstants.TRIALS_CELL)).append("'>");
			builder.append("<div>");

			String minAnchor = "<a href='javascript:void(0);' style='color: " + textColor + ";' onclick=\"javascript:accessionLinkFunction('" + cell.getMinAccessionId() + "');\">" + cell.getMinAccessionName() + "</a>";
			String maxAnchor = "<a href='javascript:void(0);' style='color: " + textColor + ";' onclick=\"javascript:accessionLinkFunction('" + cell.getMaxAccessionId() + "');\">" + cell.getMaxAccessionName() + "</a>";

			builder.append("<p>").append(Text.LANG.generalCount()).append(": ").append(cell.getAttribute(TrialsRow.TrialsAttribute.COUNT)).append("</p>");
			builder.append("<p>").append(Text.LANG.generalMinimum()).append(": ").append(minAnchor).append(" (").append(min).append(")</p>");
			builder.append("<p>").append(Text.LANG.generalAverage()).append(": ").append(avg).append("</p>");
			builder.append("<p>").append(Text.LANG.generalMaximum()).append(": ").append(maxAnchor).append(" (").append(max).append(")</p>");

			builder.append("</div>");

			return SafeHtmlUtils.fromTrustedString(builder.toString());
		}
	}

	@Override
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		table = new AdvancedTable<>(data);

		Column<TrialsRow, String> column = new Column<TrialsRow, String>(new TextCell())
		{
			@Override
			public String getValue(TrialsRow object)
			{
				String phenotype = object.getPhenotype().getName();

				if (object.getPhenotype().getUnit() != null)
					phenotype += " [" + object.getPhenotype().getUnit().getAbbreviation() + "]";

				return phenotype;
			}

			@Override
			public String getCellStyleNames(Cell.Context context, TrialsRow object)
			{
				return Style.TEXT_BOLD;
			}
		};
		column.setSortable(true);
		table.addStringColumn(column, Text.LANG.phenotypeColumnName());

		Collections.sort(years);

		SafeHtmlCell cell = new SafeHtmlCell();

		for (String year : years)
		{
			final String theYear = year;
			Column<TrialsRow, SafeHtml> c = new Column<TrialsRow, SafeHtml>(cell)
			{
				@Override
				public SafeHtml getValue(TrialsRow row)
				{
					TrialsRow.TrialsCell cell = row.getYearToValues().get(theYear);

					if (cell == null)
						return SimpleHtmlTemplate.INSTANCE.empty();

					String origValue = cell.getAttribute(TrialsCellTable.this.trialsAttribute);

					String c = row.getGradient().getTextColor(origValue).toRGBValue();
					SafeStyles textColor = SafeStylesUtils.forTrustedColor(c);
					SafeStyles backgroundColor = SafeStylesUtils.forTrustedBackgroundColor(row.getGradient().getColor(origValue).toRGBValue());

					SafeHtml value = formatTrialsCellContent(cell, c);

					return SimpleHtmlTemplate.INSTANCE.color(backgroundColor, textColor, value);
				}

				@Override
				public String getCellStyleNames(Cell.Context context, TrialsRow object)
				{
					return Style.combine(Style.LAYOUT_WHITE_SPACE_NO_WRAP, Style.LAYOUT_NO_PADDING);
				}
			};
			table.addSafeHtmlColumn(c, theYear);
		}

        /* Determine min and max values and set the gradients */
		determineMinMaxValues(data, years);

		/* Wrap everything in a scrollable composite */
		FlowPanel scrollPanel = new FlowPanel();
		scrollPanel.setStyleName(Style.combine(Style.LAYOUT_CLEAR_BOTH, Style.LAYOUT_OVERFLOW_X_AUTO));
		scrollPanel.add(table);
		panel.add(scrollPanel);
	}

	public void redraw(TrialsRow.TrialsAttribute trialsAttribute)
	{
		this.trialsAttribute = trialsAttribute;

		if (table != null)
		{
			determineMinMaxValues(data, years);
			table.getTable().redraw();
		}
	}

	/**
	 * Defines the javascript function <code>markerLinkFunction(id, collsite)</code>, calling this function from javascript will invoke the method
	 * {@link jhi.germinate.client.widget.map.LeafletUtils.OnMarkerClickHandler#onMarkerClicked(String, String)}.
	 *
	 * @param handler The {@link AccessionClickHandler} to call
	 */
	private static native void addAccessionLinkFunction(AccessionClickHandler handler)/*-{
		// Define the function
		$wnd.accessionLinkFunction = function (id) {
			if (handler != null) {
				handler.@jhi.germinate.client.widget.table.basic.TrialsCellTable.AccessionClickHandler::onAccessionClicked(*)(id);
			}
		}
	}-*/;

	public interface AccessionClickHandler
	{
		void onAccessionClicked(String id);
	}
}
