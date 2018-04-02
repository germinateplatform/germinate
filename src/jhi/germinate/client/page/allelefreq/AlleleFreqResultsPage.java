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

package jhi.germinate.client.page.allelefreq;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.ListBox;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.allelefreq.binning.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class AlleleFreqResultsPage extends Composite implements HasLibraries
{

	private final FlapjackAllelefreqBinningResult exportResult;

	interface AlleleFreqResultsPageUiBinder extends UiBinder<HTMLPanel, AlleleFreqResultsPage>
	{
	}

	private static AlleleFreqResultsPageUiBinder ourUiBinder = GWT.create(AlleleFreqResultsPageUiBinder.class);

	@UiField
	HTMLPanel panel;
	@UiField
	FlowPanel result;
	@UiField
	HTML      warning;

	@UiField
	FlowPanel            flapjackWrapper;
	@UiField
	HTML                 flapjackText;
	@UiField
	Well                 flapjackOutput;
	@UiField
	AlleleFrequencyChart alleleFreqChart;

	@UiField
	TabListItem equalWidthTab;
	@UiField
	TabListItem splitPointTab;
	@UiField
	TabListItem automaticTab;

	@UiField
	TabPane equalWidthTabContent;
	@UiField
	TabPane splitPointTabContent;
	@UiField
	TabPane automaticTabContent;

	@UiField
	EqualWidthBinningWidget equalWidthBinningWidget;
	@UiField
	SplitPointBinningWidget splitPointBinningWidget;
	@UiField
	AutomaticBinningWidget  automaticBinningWidget;

	@UiField
	FlowPanel resultWrapper;
	@UiField
	FlowPanel resultPanel;
	@UiField
	FlowPanel markerWrapper;
	@UiField
	FlowPanel markerPanel;

	public AlleleFreqResultsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		flapjackText.setHTML(Text.LANG.allelefreqResultFlapjack());

		exportResult = FlapjackAllelefreqBinningResultParameterStore.Inst.get().get(Parameter.flapjackExportResult);

		if (exportResult == null)
		{
			warning.setVisible(true);
			warning.setHTML(Text.LANG.genotypeResultNoData());
			return;
		}

		result.setVisible(true);

		if (!StringUtils.isEmpty(exportResult.getDebugOutput()))
		{
			flapjackWrapper.setVisible(true);
			flapjackOutput.add(new HTML(exportResult.getDebugOutput()));
		}
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		alleleFreqChart.setParent(this);
		alleleFreqChart.setFilePath(new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
																  .setPath(ServletConstants.SERVLET_FILES)
																  .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
																  .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
																  .setParam(ServletConstants.PARAM_FILE_PATH, exportResult.getHistogramFile())
																  .build());
	}

	/**
	 * Called from d3. Will pass in the position and width of the actual chart area containing the bars. These are then used to request the binning
	 * images from flapjack
	 *
	 * @param left  The absolute left of the first bar
	 * @param width The width of the actual chart area
	 */
	public void notifyChartPositionAndWidth(int left, double width)
	{
		int leftInt = left - panel.getAbsoluteLeft();
		int widthInt = (int) Math.round(width);

		equalWidthBinningWidget.updatePosition(leftInt, widthInt);
		splitPointBinningWidget.updatePosition(leftInt, widthInt);
		automaticBinningWidget.updatePosition(leftInt, widthInt);
	}

	/**
	 * Called from d3. Will pass in the position of the bar the user clicked on. The split point binning will be updated with this value, i.e. making
	 * it the new split point
	 *
	 * @param position The position of the bar the user clicked on
	 */
	public void notifyChartBarClicked(double position)
	{
		splitPointBinningWidget.updateSplitPosition(position);
	}

	@UiHandler("splitPointTab")
	void onSplitPointTabClicked(ClickEvent event)
	{
		splitPointBinningWidget.refresh();
	}

	private AlleleFrequencyService.HistogramParams getHistogramParams()
	{
		if (equalWidthTab.isActive())
			return equalWidthBinningWidget.getParams();
		else if (splitPointTab.isActive())
			return splitPointBinningWidget.getParams();
		else if (automaticTab.isActive())
			return automaticBinningWidget.getParams();
		else
			return null;
	}

	@UiHandler("continueButton")
	void onContinueClicked(ClickEvent event)
	{
		AlleleFrequencyService.HistogramParams params = getHistogramParams();
		List<Dataset> selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.allelefreqDatasets);
		params.datasetIds = DatabaseObject.getIds(selectedDatasets);

		AlleleFrequencyService.Inst.get().createProject(Cookie.getRequestProperties(), getHistogramParams(), new DefaultAsyncCallback<Tuple.Pair<String, FlapjackProjectCreationResult>>(true)
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				resultWrapper.setVisible(false);
				resultPanel.clear();

				if (caught instanceof MissingPropertyException)
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationUnspecifiedServerError());
				else if (caught instanceof IOException)
					Notification.notify(Notification.Type.ERROR, caught.getLocalizedMessage());
				else
					super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(Tuple.Pair<String, FlapjackProjectCreationResult> result)
			{
				resultWrapper.setVisible(true);
				resultPanel.clear();

				List<DownloadWidget.FileConfig> files = new ArrayList<>();
				files.add(new DownloadWidget.FileConfig(FileLocation.temporary, Text.LANG.allelefreqResultDownloadBinned(), result.getSecond().getRawDataFile()).setStyle(FileType.IconStyle.IMAGE));
				files.add(new DownloadWidget.FileConfig(FileLocation.temporary, Text.LANG.allelefreqResultDownloadMap(), result.getSecond().getMapFile()).setStyle(FileType.IconStyle.IMAGE));
				files.add(new DownloadWidget.FileConfig(FileLocation.temporary, Text.LANG.allelefreqResultDownloadFlapjack(), result.getSecond().getProjectFile()).setStyle(FileType.IconStyle.IMAGE));

				resultPanel.add(new DownloadWidget().addAll(files));
				markerPanel.clear();

				/* Show the deleted markers in a list */
				if (!CollectionUtils.isEmpty(result.getSecond().getDeletedMarkers()))
				{
					markerWrapper.setVisible(true);

					resultPanel.add(new Heading(HeadingSize.H3, Text.LANG.genotypeResultDeletedMarkers()));

					ListBox box = new ListBox();
					box.setVisibleItemCount(Math.min(20, result.getSecond().getDeletedMarkers().size()));

					exportResult.getDeletedMarkers().forEach(box::addItem);

					markerPanel.add(box);

					DownloadWidget widget = new DownloadWidget()
					{
						@Override
						protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
						{
							MarkerService.Inst.get().export(Cookie.getRequestProperties(), result.getSecond().getDeletedMarkers(), callback);
						}
					};
					widget.add(new DownloadWidget.FileConfig(Text.LANG.downloadDeletedMarkersAsTxt()));
					markerPanel.add(widget);
				}
				else
				{
					markerWrapper.setVisible(false);
				}
			}
		});
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_FLAPJACK_BINNING};
	}
}