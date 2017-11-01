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

package jhi.germinate.client.widget.table.pagination.filter;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.*;

import java.util.*;
import java.util.stream.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class FilterPanel implements KeyPressHandler
{
	private AlertDialog dialog;
	private List<ToggleSwitch> switches = new ArrayList<>();
	private List<FilterRow>    rows     = new ArrayList<>();
	private final FlowPanel content;
	private final FlowPanel body;

	private List<FilterRow.Column> columns;
	private FilterPanelHandler     handler;

	public FilterPanel()
	{
		body = new FlowPanel();
		content = new FlowPanel();
		Button addButton = new Button(Text.LANG.generalAdd(), e -> addRow());
		addButton.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));

		body.add(new HTML(Text.LANG.tableFilterInfo()));
		body.add(content);
		body.add(addButton);
	}

	public void open()
	{
		if (dialog == null)
		{
			dialog = new AlertDialog(Text.LANG.generalFilter(), body)
					.setRemoveOnHide(false)
					.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClear(), Style.MDI_CANCEL, e -> {
						clear();
						if (handler != null)
							handler.onClearClicked();
					}))
					.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalSearch(), Style.MDI_MAGNIFY, ButtonType.PRIMARY, e -> {
						if (handler != null)
							handler.onSearchClicked();
					}));
		}

		dialog.open();
	}

	public void onUnload()
	{
		if (dialog != null)
			dialog.remove();
	}

	public void update(List<FilterRow.Column> columns)
	{
		this.columns = columns;

		clear();
	}

	public void addFilterPanelHandler(FilterPanelHandler handler)
	{
		this.handler = handler;
	}

	public void clear()
	{
		if (rows.size() > 0)
			rows.forEach(r -> r.removeFromParent());

		rows.clear();

		if (switches.size() > 0)
			switches.forEach(s -> s.removeFromParent());

		switches.clear();
	}

	public void add(Map<String, String> mapping, boolean isAnd, ComparisonOperator operator)
	{
		clear();

		for (Map.Entry<String, String> entry : mapping.entrySet())
		{
			addRow();

			FilterRow row = rows.get(rows.size() - 1);
			row.setValue(entry.getKey(), entry.getValue(), operator);

			if (switches.size() > 0)
				switches.get(switches.size() - 1).setValue(isAnd);
		}
	}

	public int getSize()
	{
		return rows.size();
	}

	public void setVisible(boolean visible)
	{
		if (visible && rows.size() < 1)
			addRow();

		if (visible)
		{
			open();
		}
		else
		{
			clear();
			if (dialog != null)
				dialog.close();
		}
	}

	private void addRow()
	{
		FilterRow row = new FilterRow(columns, rows.size() > 0)
		{
			@Override
			void onDeleteButtonClicked(ClickEvent e)
			{
				int index = rows.indexOf(this);
				if (index >= 0)
				{
					if (getSize() > 1)
					{
						ToggleSwitch toggleSwitch = switches.get(Math.max(0, index - 1));
						switches.remove(toggleSwitch);
						toggleSwitch.removeFromParent();
					}
					rows.remove(this);
					this.removeFromParent();

					if (handler != null)
						handler.onRowDeleted();
				}
			}
		};

		row.setEnterKeyListener(this);

		if (rows.size() > 0)
		{
			ToggleSwitch toggleSwitch = new ToggleSwitch();
			toggleSwitch.setOnText(Text.LANG.operatorsAnd());
			toggleSwitch.setOffText(Text.LANG.operatorsOr());
			toggleSwitch.setSize(SizeType.MINI);
			toggleSwitch.setOnColor(ColorType.PRIMARY);
			toggleSwitch.setOffColor(ColorType.PRIMARY);
			toggleSwitch.setValue(true);

			content.add(toggleSwitch);
			switches.add(toggleSwitch);
		}

		content.add(row);
		rows.add(row);

		if (handler != null)
			handler.onRowAdded();

		Scheduler.get().scheduleDeferred(() -> JavaScript.scrollToBottom(content.getElement()));
	}

	public PartialSearchQuery getQuery()
	{
		/* Assemble the query */
		PartialSearchQuery query = new PartialSearchQuery();

		List<FilterRow> valid = rows.stream()
									.filter(r -> !r.isEmpty())
									.collect(Collectors.toList());

        /* For each of the rows */
		for (int i = 0; i < valid.size(); i++)
		{
			FilterRow row = valid.get(i);

            /* Otherwise add it and possibly the operator in between */
			if (i > 0)
			{
				boolean value = switches.get(i - 1).getValue();
				query.addLogicalOperator(value ? new And() : new Or());
			}

			try
			{
				query.add(row.getSearchCondition());
			}
			catch (InvalidArgumentException | InvalidSearchQueryException e)
			{
				Notification.notify(Notification.Type.ERROR, e.getLocalizedMessage());
				return null;
			}
		}

		return query;
	}

	public FlowPanel getQueryHtml()
	{
		FlowPanel result = new FlowPanel();

		List<FilterRow> valid = rows.stream()
									.filter(r -> !r.isEmpty())
									.collect(Collectors.toList());

		/* For each of the rows */
		for (int i = 0; i < valid.size(); i++)
		{
			FilterRow row = valid.get(i);

            /* Otherwise add it and possibly the operator in between */
			if (i > 0)
			{
				boolean value = switches.get(i - 1).getValue();
				Label label = new Label(value ? Text.LANG.operatorsAnd() : Text.LANG.operatorsOr());
				label.addStyleName(Style.LAYOUT_DISPLAY_INLINE_BLOCK);
				result.add(label);
			}

			result.add(new Label(LabelType.SUCCESS, row.getSearchConditionString()));
		}

		return result;
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
		{
			if (handler != null)
				handler.onSearchClicked();
			dialog.close();
		}
	}

	public interface FilterPanelHandler
	{
		void onRowAdded();

		void onRowDeleted();

		void onSearchClicked();

		void onClearClicked();
	}
}