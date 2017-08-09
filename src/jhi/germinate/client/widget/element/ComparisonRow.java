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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextBox;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class ComparisonRow extends Composite
{
	interface ComparisonRowUiBinder extends UiBinder<InputGroup, ComparisonRow>
	{
	}

	private static ComparisonRowUiBinder ourUiBinder = GWT.create(ComparisonRowUiBinder.class);

	@UiField(provided = true)
	DropdownInputButton<String> column;
	@UiField(provided = true)
	DropdownInputButton<String> operator;
	@UiField
	TextBox                     input;
	@UiField
	Button                      deleteButton;

	private ComparisonType comparisonType;

	public ComparisonRow(boolean canDelete)
	{
		column = new DropdownInputButton<String>()
		{
			@Override
			protected String getLabel(String item)
			{
				return item;
			}
		};
		operator = new DropdownInputButton<String>()
		{
			@Override
			protected String getLabel(String item)
			{
				return item;
			}
		};

		initWidget(ourUiBinder.createAndBindUi(this));

		deleteButton.setEnabled(canDelete);
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		update();
	}

	private void update()
	{
		if (isAttached() && comparisonType != null)
		{
			column.setData(Arrays.asList(comparisonType.items), true);
			operator.setData(Arrays.asList("=", ">", "<"), true);
		}
	}

	public void setComparisonType(ComparisonType comparisonType)
	{
		this.comparisonType = comparisonType;

		update();
	}

	private ComparisonOperator getComparator()
	{
		switch (operator.getSelection())
		{
			case "=":
				return new Equal();
			case ">":
				return new GreaterThan();
			case "<":
				return new LessThan();
			default:
				return null;
		}
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClicked(ClickEvent e)
	{
		this.removeFromParent();
	}

	/**
	 * Creates and returns the {@link PartialSearchQuery} represented by this {@link ComparisonRow}
	 *
	 * @return The created {@link PartialSearchQuery}
	 * @throws InvalidArgumentException    Thrown if the value is illegal in some way, the wrong type or not injection safe
	 * @throws InvalidSearchQueryException Thrown if values are being added and the PhenotypeDef and ComparisonOperators being used have not already
	 *                                     been set (they are required for value validation)
	 */
	public SearchCondition getSearchCondition() throws InvalidArgumentException, InvalidSearchQueryException
	{
		SearchCondition value = new SearchCondition();

		ComparisonOperator comparator = getComparator();

		if (comparator == null)
			return null;

		value.setColumnName(column.getSelection());
		value.setComp(comparator);
		value.addConditionValue(input.getText());

		return value;
	}

	public enum ComparisonType
	{
		accession(Accession.ID, Accession.NAME, Accession.GENERAL_IDENTIFIER, Location.SITE_NAME, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Country.COUNTRY_NAME, Taxonomy.GENUS, Taxonomy.SPECIES, Subtaxa.TAXONOMY_IDENTIFIER),
		marker(Marker.ID, Marker.MARKER_NAME, Map.ID, Map.DESCRIPTION, MapFeatureType.DESCRIPTION, MapDefinition.CHROMOSOME, MapDefinition.DEFINITION_START),
		location(Location.ID, Location.SITE_NAME, Location.STATE, Location.REGION, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Country.COUNTRY_NAME, LocationType.NAME);

		private String[] items;

		ComparisonType(String... items)
		{
			this.items = items;
		}

		public String[] getItems()
		{
			return items;
		}
	}
}