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
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class ScatterChartSelection<T extends DatabaseObject> extends Composite
{
	interface ScatterChartSelectionUiBinder extends UiBinder<HTMLPanel, ScatterChartSelection>
	{
	}

	private static ScatterChartSelectionUiBinder ourUiBinder = GWT.create(ScatterChartSelectionUiBinder.class);

	@UiField
	FormLabel             firstObjectLabel;
	@UiField(provided = true)
	GerminateValueListBox firstObject;
	@UiField
	FormLabel             secondObjectLabel;
	@UiField(provided = true)
	GerminateValueListBox secondObject;
	@UiField
	GroupListBox          group;
	@UiField
	StringListBox         color;

	public ScatterChartSelection(ExperimentType type, List<T> objects, List<Group> groups)
	{
		switch (type)
		{
			case phenotype:
			case trials:
				firstObject = new PhenotypeListBox();
				secondObject = new PhenotypeListBox();
				break;
			case compound:
				firstObject = new CompoundListBox();
				secondObject = new CompoundListBox();
		}

		initWidget(ourUiBinder.createAndBindUi(this));

		switch (type)
		{
			case phenotype:
			case trials:
				firstObjectLabel.setText(Text.LANG.phenotypeFirstPhenotype());
				secondObjectLabel.setText(Text.LANG.phenotypeSecondPhenotype());
				break;
			case compound:
				firstObjectLabel.setText(Text.LANG.compoundFirstCompound());
				secondObjectLabel.setText(Text.LANG.compoundSecondCompound());
		}


		firstObject.setValue(objects.get(0), false);
		firstObject.setAcceptableValues(objects);
		secondObject.setValue(objects.get(0), false);
		secondObject.setAcceptableValues(objects);

		Group dummy = new Group(-1L)
				.setDescription(Text.LANG.accessionsDownloadCompleteDataset());

		groups.add(0, dummy);
		group.setValue(dummy, false);
		group.setAcceptableValues(groups);

		String[] coloringValues;

		switch (type)
		{
			case phenotype:
			case trials:
				coloringValues = new String[]{Text.LANG.trialsPByPColorByNone(), Text.LANG.trialsPByPColorByDataset(), Text.LANG.trialsPByPColorByYear(), Text.LANG.trialsPByPColorByTreatment()};
				break;
			case compound:
			default:
				coloringValues = new String[]{Text.LANG.trialsPByPColorByNone(), Text.LANG.trialsPByPColorByDataset(), Text.LANG.trialsPByPColorByYear()};
				break;
		}
		color.setValue(coloringValues[0], false);
		color.setAcceptableValues(coloringValues);
	}

	public List<T> getFirstObject()
	{
		return firstObject.getSelections();
	}

	public List<T> getSecondObject()
	{
		return secondObject.getSelections();
	}

	public Group getGroup()
	{
		return group.getSelection();
	}

	public String getColor()
	{
		return color.getSelection();
	}
}