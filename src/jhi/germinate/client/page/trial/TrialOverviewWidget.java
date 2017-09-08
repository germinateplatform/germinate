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

package jhi.germinate.client.page.trial;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.text.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;
import java.util.Map;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.client.widget.table.basic.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class TrialOverviewWidget extends Composite
{
	interface TrialOverviewWidgetUiBinder extends UiBinder<HTMLPanel, TrialOverviewWidget>
	{
	}

	private static TrialOverviewWidgetUiBinder ourUiBinder = GWT.create(TrialOverviewWidgetUiBinder.class);

	@UiField
	PhenotypeListBox                                 phenotypeBox;
	@UiField
	IntegerListBox                                   yearBox;
	@UiField
	FlowPanel                                        results;
	@UiField(provided = true)
	GerminateValueListBox<TrialsRow.TrialsAttribute> trialAttributeBox;
	@UiField
	FlowPanel                                        tablePanel;
	@UiField
	FlowPanel                                        chartPanel;

	private List<Long>                             selectedDatasets;
	private List<Phenotype>                        phenotypes;
	private List<Integer>                          years;
	private Map<TrialsRow.TrialsAttribute, String> attributeToFile;
	private TrialsCellTable                        table;
	private TrialsRow.TrialsAttribute trialsAttribute = TrialsRow.TrialsAttribute.COUNT;

	public TrialOverviewWidget(List<Phenotype> p, List<Integer> y)
	{
		this.phenotypes = p;
		this.years = y;

		addBox();

		initWidget(ourUiBinder.createAndBindUi(this));

		selectedDatasets = LongListParameterStore.Inst.get().get(Parameter.trialsDatasetIds);

		ParallelAsyncCallback<ServerResult<List<Phenotype>>> phenotypeCallback = null;
		ParallelAsyncCallback<ServerResult<List<Integer>>> yearsCallback = null;

		if (phenotypes == null)
		{
			phenotypeCallback = new ParallelAsyncCallback<ServerResult<List<Phenotype>>>()
			{
				@Override
				protected void start()
				{
					PhenotypeService.Inst.get().get(Cookie.getRequestProperties(), selectedDatasets, ExperimentType.trials, true, this);
				}
			};
		}
		if (years == null)
		{
			yearsCallback = new ParallelAsyncCallback<ServerResult<List<Integer>>>()
			{
				@Override
				protected void start()
				{
					TrialService.Inst.get().getTrialYears(Cookie.getRequestProperties(), selectedDatasets, this);
				}
			};
		}

		new ParallelParentAsyncCallback(phenotypeCallback, yearsCallback)
		{

			@Override
			public void handleSuccess()
			{
				ServerResult<List<Phenotype>> phenotypeData = getCallbackData(0);
				ServerResult<List<Integer>> yearData = getCallbackData(1);

				if (phenotypes == null)
					phenotypes = phenotypeData.getServerResult();
				if (years == null)
					years = yearData.getServerResult();

				phenotypeBox.setValue(phenotypes.get(0), false);
				phenotypeBox.setAcceptableValues(phenotypes);

				yearBox.setValue(years.get(0), false);
				yearBox.setAcceptableValues(years);
			}

			@Override
			public void handleFailure(Exception reason)
			{
				getChild(0).onFailureImpl(reason);
			}
		};
	}

	private void addBox()
	{
		 /* Add the attribute selector */
		trialAttributeBox = new GerminateValueListBox<>(new Renderer<TrialsRow.TrialsAttribute>()
		{
			@Override
			public String render(TrialsRow.TrialsAttribute attribute)
			{
				switch (attribute)
				{
					case COUNT:
						return Text.LANG.generalCount();
					case MIN:
						return Text.LANG.generalMinimum();
					case AVG:
						return Text.LANG.generalAverage();
					case MAX:
						return Text.LANG.generalMaximum();
					default:
						return "UNKNOWN";
				}
			}

			@Override
			public void render(TrialsRow.TrialsAttribute object, Appendable appendable) throws java.io.IOException
			{
				String s = render(object);
				appendable.append(s);
			}
		});
		trialAttributeBox.addValueChangeHandler(event ->
		{
			trialsAttribute = event.getValue().get(0);
			table.redraw(trialsAttribute);
			redrawChart();
		});

		trialAttributeBox.setValue(trialsAttribute, false);
		trialAttributeBox.setAcceptableValues(TrialsRow.TrialsAttribute.values());
	}

	@UiHandler("continueButton")
	void onContinueButtonClicked(ClickEvent e)
	{
		tablePanel.clear();

		List<Phenotype> selectedPhenotypes = phenotypeBox.getSelections();
		List<Long> phenotypeIds = DatabaseObject.getIds(selectedPhenotypes);

		TrialService.Inst.get().getPhenotypeOverviewTable(Cookie.getRequestProperties(), selectedDatasets, phenotypeIds, yearBox.getSelections(),
				new DefaultAsyncCallback<ServerResult<Tuple.Triple<List<String>, List<TrialsRow>, Map<TrialsRow.TrialsAttribute, String>>>>(true)
				{
					@Override
					protected void onFailureImpl(Throwable caught)
					{
						tablePanel.clear();
						results.setVisible(false);
						super.onFailureImpl(caught);
					}

					@Override
					protected void onSuccessImpl(ServerResult<Tuple.Triple<List<String>, List<TrialsRow>, Map<TrialsRow.TrialsAttribute, String>>> result)
					{
						final List<TrialsRow> data = result.getServerResult().getSecond();

						if (!CollectionUtils.isEmpty(data))
						{
							results.setVisible(true);
							attributeToFile = result.getServerResult().getThird();

                            /* Set up the redirect to the passport page */
							TrialsCellTable.AccessionClickHandler handler = GerminateSettingsHolder.isPageAvailable(Page.PASSPORT) ? id ->
							{
								try
								{
									LongParameterStore.Inst.get().putAsString(Parameter.accessionId, id);
									History.newItem(Page.PASSPORT.name());
								}
								catch (UnsupportedDataTypeException e)
								{
								}
							} : null;

                            /* Create the fancy table */
							table = new TrialsCellTable(result.getServerResult().getFirst(), result.getServerResult().getSecond(), trialsAttribute, handler);

							redrawChart();

							tablePanel.add(table);
							tablePanel.insert(GradientUtils.createHorizontalGradientLegend(data.get(0).getGradient(), GradientUtils.HorizontalLegendPosition.TOP), tablePanel.getWidgetIndex(table));
							tablePanel.add(GradientUtils.createHorizontalGradientLegend(data.get(0).getGradient()));
						}
						else
						{
							Notification.notify(Notification.Type.INFO, Text.LANG.notificationNoDataFound());
							tablePanel.removeFromParent();
						}
					}
				});
	}

	protected void redrawChart()
	{
		AbstractChart.removeD3();
		chartPanel.clear();
		chartPanel.getElement().removeAllChildren();

		String filePath = new ServletConstants.Builder()
				.setUrl(GWT.getModuleBaseURL())
				.setPath(ServletConstants.SERVLET_FILES)
				.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
				.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
				.setParam(ServletConstants.PARAM_FILE_PATH, attributeToFile.get(trialsAttribute))
				.build();

		TrialsOverviewChart chart = new TrialsOverviewChart(filePath, trialsAttribute.getI18nName());
		chartPanel.add(chart);
	}
}