/*
 *  Copyright 2018 Information and Computational Sciences,
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

import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetListWidget extends GerminateComposite
{
	private List<Dataset> selectedDatasets = new ArrayList<>();

	public DatasetListWidget()
	{
	}

	public void setType(ExperimentType type)
	{
		switch (type)
		{
			case trials:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.trialsDatasets);
				break;
			case compound:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.compoundDatasets);
				break;
			case genotype:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.genotypeDatasets);
				break;
			case allelefreq:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.allelefreqDatasets);
				break;
			case climate:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.climateDatasets);
				break;
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
		panel.clear();
		ULPanel ulPanel = new ULPanel();

		for (Dataset dataset : selectedDatasets)
			ulPanel.add(new Label(dataset.getId() + " - " + dataset.getDescription()));

		panel.add(new Heading(HeadingSize.H3, Text.LANG.selectedDatasets()));
		panel.add(ulPanel);
	}
}
