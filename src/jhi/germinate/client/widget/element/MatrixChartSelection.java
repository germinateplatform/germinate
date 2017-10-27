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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class MatrixChartSelection<T extends DatabaseObject> extends Composite
{
	interface MatrixChartSelectionUiBinder extends UiBinder<HTMLPanel, MatrixChartSelection>
	{
	}

	private static MatrixChartSelectionUiBinder ourUiBinder = GWT.create(MatrixChartSelectionUiBinder.class);

	private static final int MAX_NR_OF_OBJECTS = 5;

	@UiField
	Heading heading;
	@UiField
	HTML    html;
	@UiField
	Label   text;

	@UiField(provided = true)
	GerminateValueListBox objectBox;

	@UiField
	HTML         groupHtml;
	@UiField
	GroupListBox groupBox;

	@UiField
	StringListBox colors;

	public MatrixChartSelection(ExperimentType type, List<T> objects, List<Group> groups)
	{
		switch (type)
		{
			case compound:
				objectBox = new CompoundListBox();
				break;
			case trials:
				objectBox = new PhenotypeListBox();
				break;
		}

		initWidget(ourUiBinder.createAndBindUi(this));

		switch (type)
		{
			case compound:
				heading.setText(Text.LANG.compoundExportHeadingCompounds());
				text.setText(Text.LANG.compoundMatrixAtMost(MAX_NR_OF_OBJECTS));
				break;
			case trials:
				heading.setText(Text.LANG.phenotypeExportHeadingPhenotypes());
				text.setText(Text.LANG.phenotypeMatrixAtMost(MAX_NR_OF_OBJECTS));
				break;
		}

		html.setHTML(Text.LANG.phenotypeExportSubtitlePhenotypes());

		objectBox.setSelectAllVisible(false);
		objectBox.setValue(objects.get(0), true);
		objectBox.setAcceptableValues(objects);

		groupHtml.setHTML(Text.LANG.phenotypeExportSubtitleAccessionGroups());
		groupBox.setAcceptableValues(groups);

		String[] coloringValues;

		switch (type)
		{
			case trials:
				coloringValues = new String[]{Text.LANG.trialsPByPColorByNone(), Text.LANG.trialsPByPColorByDataset(), Text.LANG.trialsPByPColorByYear(), Text.LANG.trialsPByPColorByTreatment()};
				break;
			case compound:
			default:
				coloringValues = new String[]{Text.LANG.trialsPByPColorByNone(), Text.LANG.trialsPByPColorByDataset(), Text.LANG.trialsPByPColorByYear()};
				break;
		}
		colors.setValue(coloringValues[0], false);
		colors.setAcceptableValues(coloringValues);
	}

	public String getColor()
	{
		return colors.getSelection();
	}

	public List<T> getObjects()
	{
		return objectBox.getSelections();
	}

	public List<Group> getGroups()
	{
		return groupBox.getSelections();
	}
}