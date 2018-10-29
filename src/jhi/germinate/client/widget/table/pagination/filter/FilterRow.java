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
import com.google.gwt.resources.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.datepicker.client.ui.*;

import java.util.*;
import java.util.stream.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class FilterRow extends Composite
{
	private static ComparisonRowUiBinder ourUiBinder = GWT.create(ComparisonRowUiBinder.class);
	@UiField
	Style                                   style;
	@UiField(provided = true)
	DropdownInputButton<Column>             column;
	@UiField(provided = true)
	DropdownInputButton<ComparisonOperator> operator;
	@UiField
	FlowPanel                               input;
	@UiField
	TextBox                                 firstInput;
	@UiField
	TextBox                                 secondInput;
	@UiField
	FlowPanel                               date;
	@UiField
	DatePicker                              firstDate;
	@UiField
	DatePicker                              secondDate;
	@UiField
	FlowPanel                               dbObject;
	@UiField
	SimplePanel                             firstDbObjectPanel;
	@UiField
	SimplePanel                             secondDbObjectPanel;

	@UiField
	Button deleteButton;
	private List<Column> columns;

	private GerminateValueListBox firstDbObject;
	private GerminateValueListBox secondDbObject;

	public FilterRow(List<Column> columns, boolean canDelete)
	{
		this.columns = columns;

		column = new DropdownInputButton<Column>()
		{
			@Override
			protected String getLabel(Column item)
			{
				return item.displayName;
			}

			@Override
			protected void onValueChange(Column item)
			{
				if (item.getDataType().equals(Date.class))
				{
					date.setVisible(true);
					input.setVisible(false);
					dbObject.setVisible(false);
				}
				else if (item.getDataType().equals(EntityType.class) || item.getDataType().equals(LocationType.class))
				{
					date.setVisible(false);
					input.setVisible(false);
					dbObject.setVisible(true);
				}
				else
				{
					date.setVisible(false);
					input.setVisible(true);
					dbObject.setVisible(false);
				}

				updateDbObjects();
			}
		};
		operator = new DropdownInputButton<ComparisonOperator>()
		{
			@Override
			protected String getLabel(ComparisonOperator item)
			{
				return getOperatorString(item);
			}

			@Override
			protected void onValueChange(ComparisonOperator value)
			{
				update(value);
			}
		};


		initWidget(ourUiBinder.createAndBindUi(this));

		column.setData(columns, true);

		operator.setData(Arrays.asList(new Like(), new Equal(), new GreaterThan(), new GreaterThanEquals(), new LessThan(), new LessThanEquals(), new Between(), new InSet()), true);
		deleteButton.setEnabled(canDelete);

		updateDbObjects();
	}

	private void updateDbObjects()
	{
		firstDbObjectPanel.clear();
		secondDbObjectPanel.clear();

		Class<?> type = column.getSelection().getDataType();

		if (Objects.equals(type, EntityType.class))
		{
			firstDbObject = new EntityTypeListBox(false);
			secondDbObject = new EntityTypeListBox(false);
			firstDbObjectPanel.add(firstDbObject);
			secondDbObjectPanel.add(secondDbObject);
		}
		else if (Objects.equals(type, LocationType.class))
		{
			firstDbObject = new LocationTypeListBox(false);
			secondDbObject = new LocationTypeListBox(false);
			firstDbObjectPanel.add(firstDbObject);
			secondDbObjectPanel.add(secondDbObject);
		}
	}

	public static String getOperatorString(ComparisonOperator item)
	{
		if (item instanceof Like)
			return Text.LANG.operatorsLike();
		else if (item instanceof Equal)
			return Text.LANG.operatorsEqual();
		else if (item instanceof GreaterThan)
			return Text.LANG.operatorsGreaterThan();
		else if (item instanceof GreaterThanEquals)
			return Text.LANG.operatorsGreaterThanEquals();
		else if (item instanceof LessThan)
			return Text.LANG.operatorsLessThan();
		else if (item instanceof LessThanEquals)
			return Text.LANG.operatorsLessThanEquals();
		else if (item instanceof Between)
			return Text.LANG.operatorsBetween();
		else if (item instanceof InSet)
			return Text.LANG.operatorInSet();
		else
			return item.getClass().getSimpleName();
	}

	public void setEnterKeyListener(KeyPressHandler handler)
	{
		firstInput.addKeyPressHandler(handler);
		secondInput.addKeyPressHandler(handler);
	}

	private void update(ComparisonOperator value)
	{
		boolean firstVisible;
		boolean secondVisible;
		boolean add;

		switch (value.getRequiredNumberOfvalues())
		{
			case ONE:
			case MANY:
				firstVisible = true;
				secondVisible = false;
				add = false;
				break;
			case TWO:
				firstVisible = true;
				secondVisible = true;
				add = true;
				break;
			default:
				firstVisible = false;
				secondVisible = false;
				add = false;
				break;
		}

		firstInput.setVisible(firstVisible);
		secondInput.setVisible(secondVisible);
		firstDate.setVisible(firstVisible);
		secondDate.setVisible(secondVisible);
		firstDbObjectPanel.setVisible(firstVisible);
		secondDbObjectPanel.setVisible(secondVisible);

		if (add)
		{
			firstInput.addStyleName(style.inputDual());
			secondInput.addStyleName(style.inputDual());
			firstDate.addStyleName(style.inputDual());
			secondDate.addStyleName(style.inputDual());
			firstDbObjectPanel.addStyleName(style.inputDual());
			secondDbObjectPanel.addStyleName(style.inputDual());
		}
		else
		{
			firstInput.removeStyleName(style.inputDual());
			secondInput.removeStyleName(style.inputDual());
			firstDate.removeStyleName(style.inputDual());
			secondDate.removeStyleName(style.inputDual());
			firstDbObjectPanel.removeStyleName(style.inputDual());
			secondDbObjectPanel.removeStyleName(style.inputDual());
		}
	}

	public void setValue(String col, List<String> values, ComparisonOperator op)
	{
		columns.stream()
			   .filter(c -> c.databaseColumn.equalsIgnoreCase(col))
			   .findFirst()
			   .ifPresent(c -> {
				   column.setSelection(c, true);
				   operator.setSelection(op, true);

				   if (c.getDataType().equals(EntityType.class))
				   {
					   try
					   {
						   if (values.size() > 0)
							   firstDbObject.setValue(EntityType.valueOf(values.get(0)), true);
					   }
					   catch (IllegalArgumentException e)
					   {
						   if (values.size() > 0)
							   firstInput.setValue(values.get(0));
					   }
				   }
				   else if (c.getDataType().equals(LocationType.class))
				   {
					   try
					   {
						   if (values.size() > 0)
							   firstDbObject.setValue(LocationType.valueOf(values.get(0)), true);
					   }
					   catch (IllegalArgumentException e)
					   {
						   if (values.size() > 0)
							   firstInput.setValue(values.get(0));
					   }
				   }
				   else
				   {
					   switch (op.getRequiredNumberOfvalues())
					   {
						   case TWO:
							   firstInput.setValue(values.get(0));
							   secondInput.setValue(values.get(1));
							   break;
						   case MANY:
							   firstInput.setValue(values.stream().collect(Collectors.joining(", ")));
							   break;
						   case ONE:
						   default:
							   firstInput.setValue(values.get(0));
					   }
				   }
			   });
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClicked(ClickEvent e)
	{
	}

	/**
	 * Creates and returns the {@link PartialSearchQuery} represented by this {@link FilterRow}
	 *
	 * @return The created {@link PartialSearchQuery}
	 * @throws InvalidArgumentException    Thrown if the value is illegal in some way, the wrong type or not injection safe
	 * @throws InvalidSearchQueryException Thrown if values are being added and the PhenotypeDef and ComparisonOperators being used have not already
	 *                                     been set (they are required for value validation)
	 */
	public SearchCondition getSearchCondition() throws InvalidArgumentException, InvalidSearchQueryException
	{
		ComparisonOperator comparator = operator.getSelection();

		if (comparator == null)
			return null;

		SearchCondition value = new SearchCondition(column.getSelection().databaseColumn, comparator, getFirst(), String.class);

		if (secondInput.isVisible())
			value.addConditionValue(getSecond());

		return value;
	}

	private String getFirst()
	{
		if (input.isVisible())
			return firstInput.getValue();
		else if (date.isVisible())
			return DateUtils.getDatabaseDate(firstDate.getValue().getTime());
		else if (dbObject.isVisible())
		{
			Class<?> type = column.getSelection().getDataType();

			if (Objects.equals(type, EntityType.class))
			{
				return ((EntityType) firstDbObject.getSelection()).getName();
			}
			else if (Objects.equals(type, LocationType.class))
			{
				return ((LocationType) firstDbObject.getSelection()).name();
			}

			return null;
		}
		else
			return "";
	}

	private String getSecond()
	{
		if (input.isVisible())
			return secondInput.getValue();
		else if (date.isVisible())
			return DateUtils.getDatabaseDate(secondDate.getValue().getTime());
		else if (dbObject.isVisible())
		{
			Class<?> type = column.getSelection().getDataType();

			if (Objects.equals(type, EntityType.class))
			{
				return ((EntityType) secondDbObject.getSelection()).getName();
			}
			else if (Objects.equals(type, LocationType.class))
			{
				return ((LocationType) secondDbObject.getSelection()).getName();
			}

			return null;
		}
		else
			return "";
	}

	public String getSearchConditionString()
	{
		return "'" + column.getSelection().displayName + "' " + getOperatorString(operator.getSelection()) + " '" + getFirst() + (secondInput.isVisible() || secondDate.isVisible() || secondDbObjectPanel.isVisible() ? ", " + getSecond() : "") + "'";
	}

	public boolean isEmpty()
	{
		if (StringUtils.isEmpty(getFirst()))
			return true;
		else if (secondInput.isVisible() || secondDate.isVisible() || secondDbObjectPanel.isVisible())
			return StringUtils.isEmpty(getSecond());
		else
			return false;
	}

	interface Style extends CssResource
	{
		String inputDual();

		String input();

		String group();

		String margin();

		String clear();
	}

	interface ComparisonRowUiBinder extends UiBinder<InputGroup, FilterRow>
	{
	}

	public static class Column
	{
		private String   databaseColumn;
		private String   displayName;
		private Class<?> dataType;

		public Column(String databaseColumn, String displayName, Class<?> dataType)
		{
			this.databaseColumn = databaseColumn;
			this.displayName = displayName;
			this.dataType = dataType;
		}

		public String getDatabaseColumn()
		{
			return databaseColumn;
		}

		public Column setDatabaseColumn(String databaseColumn)
		{
			this.databaseColumn = databaseColumn;
			return this;
		}

		public String getDisplayName()
		{
			return displayName;
		}

		public Column setDisplayName(String displayName)
		{
			this.displayName = displayName;
			return this;
		}

		public Class<?> getDataType()
		{
			return dataType;
		}

		public Column setDataType(Class<?> dataType)
		{
			this.dataType = dataType;
			return this;
		}
	}
}