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

package jhi.germinate.client.widget.d3js;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.resource.*;
import jhi.germinate.shared.*;

/**
 * AbstractChart is a basic composite containing a d3.js chart. It handles a lot of auxiliary bits and bobs that are important when handling charts.
 *
 * @author Sebastian Raubach
 */
public abstract class AbstractChart extends GerminateComposite
{
	/** Keep track of the created {@link PopupPanel}s */
	private static final Map<Widget, PopupPanel>          popupPanels = new HashMap<>();
	/** Keep track of the created {@link HandlerRegistration}s */
	private static final Map<Widget, HandlerRegistration> handlers    = new HashMap<>();

	protected String panelId = "";
	protected String filePath;

	private String   title;
	private SafeHtml message;
	private HTML     messageHtml;

	private FlowPanel   chartPanel;
	private ButtonGroup buttonGroup;
	private Button      button;

	protected AbstractChart()
	{
	}

	public AbstractChart(String title, SafeHtml message)
	{
		this.title = title;
		this.message = message;
	}

	public String getFilePath()
	{
		return filePath;
	}

	/**
	 * Child classes need to create their content here.
	 *
	 * @param chartPanel The parent to which the content needs to be added
	 */
	protected abstract void createContent(FlowPanel chartPanel);

	/**
	 * Update the chart with the given width
	 *
	 * @param width The new width
	 */
	protected abstract void updateChart(int width);

	/**
	 * Get the file name of the image that's about to be downloaded
	 *
	 * @return The filename of the image that's about to be downloaded
	 */
	protected abstract String getPhotoExportFilename();

	/**
	 * Returns any {@link MenuItem}s that need to be created for this chart.
	 *
	 * @return The {@link MenuItem}s that need to be created for this chart.
	 */
	protected abstract MenuItem[] getAdditionalMenuItems();

	protected boolean canDownloadSvg()
	{
		return true;
	}

	/**
	 * Removes d3 from the current page.
	 */
	public static void removeD3()
	{
		JavaScript.remove("." + Id.CHART_D3_TOOLTIP);
		JavaScript.remove("." + Id.CHART_D3_LEGEND);

		removeD3JS();

		popupPanels.values().forEach(Widget::removeFromParent);
		popupPanels.clear();

		handlers.values().forEach(HandlerRegistration::removeHandler);
		handlers.clear();
	}

	@Override
	protected void setUpContent()
	{
		panelId = "chart" + RandomUtils.RANDOM.nextLong();

		if (!StringUtils.isEmpty(title))
		{
			panel.add(new Heading(HeadingSize.H3, title));
		}

		messageHtml = new HTML();
		if (message != null)
		{
			messageHtml.setHTML(message);
		}

		panel.add(messageHtml);

		chartPanel = new FlowPanel();
		chartPanel.getElement().setId(panelId);
		chartPanel.getElement().getStyle().setTextAlign(com.google.gwt.dom.client.Style.TextAlign.CENTER);

		createContent(chartPanel);
	}

	protected void setMessage(SafeHtml message)
	{
		this.message = message;
		if (messageHtml != null && message != null)
			messageHtml.setHTML(message);
	}

	@Override
	public void onResize(boolean containerResize, boolean force)
	{
		if (containerResize || force)
		{
			/* If the chart needs to update, then notify it */
			removeD3(panelId);
			clear();
			if (chartPanel != null)
				updateChart(chartPanel.getOffsetWidth());

			if (!StringUtils.isEmpty(getPhotoExportFilename()))
				addDownloadButton();
		}
	}

	public void clear()
	{
		removeD3(panelId);
		chartPanel.getElement().removeAllChildren();
		chartPanel.clear();

		if (this instanceof PlotlyChart && chartPanel != null && chartPanel.isAttached())
			jsniClear(panelId);
	}

	private native void jsniClear(String id) /*-{
		var chart = $wnd.document.getElementById(id);

		if ($wnd.Plotly && chart) {
			$wnd.Plotly.purge(chart);
		}
	}-*/;

	@Override
	protected void onUnload()
	{
		clear();
		super.onUnload();
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;

		if (chartPanel != null && chartPanel.isAttached() && filePath != null)
			onResize(true, false);
	}

	public void forceRedraw()
	{
		if (chartPanel != null && chartPanel.isAttached())
		{
			clear();
			onResize(true, false);
		}
	}

	protected Button[] getAdditionalButtons()
	{
		return null;
	}

	/**
	 * Adds download buttons to the panel with the given id to download its svg child to the given filename. <p/> <b>IMPORTANT:</b> {@link
	 * Library#D3_DOWNLOAD} has to be loaded for this to work.
	 */
	private void addDownloadButton()
	{
		// Get information from extending class
		final String filename = getPhotoExportFilename();
		final MenuItem[] additionalItems = getAdditionalMenuItems();

		// If the download library has been loaded
		if (Library.D3_DOWNLOAD.isLoaded())
		{
			// Remove potentially existing old occurrences of the panel
			if (popupPanels.containsKey(chartPanel))
				popupPanels.get(chartPanel).removeFromParent();

			if (handlers.containsKey(chartPanel))
				handlers.get(chartPanel).removeHandler();

			// Get the button text
			SafeHtml downloadImage = Text.LANG.d3DownloadImageButton();
			SafeHtml downloadSvg = Text.LANG.d3DownloadSvgButton();
			SafeHtml downloadData = Text.LANG.d3DownloadFileButton();

			// Create the context menu
			final PopupPanel popup = new PopupPanel(true);
			popup.getElement().getStyle().setZIndex(9999);
			popup.setAutoHideOnHistoryEventsEnabled(true);
			popup.setPreviewingAllNativeEvents(true);

			popupPanels.put(chartPanel, popup);

			// Create a new menu bar
			MenuBar menuBar = new MenuBar(true);

			MenuItem menuItem;

			/* IE doesn't support (or rather allow) the operation required to save the files */
			if (!Misc.isIE())
			{
				/* Add the image download item */
				menuItem = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_FILE_IMAGE, downloadImage.asString()), (Command) () ->
				{
					// Get the initial filename
					String finalFilename = DateUtils.getFilenameForDate(System.currentTimeMillis()) + "-" + filename + ".png";
					// Then ask the user for a preference
					getFilename(finalFilename, new Callback<String, Exception>()
					{
						@Override
						public void onFailure(Exception reason)
						{
						}

						@Override
						public void onSuccess(String result)
						{
							// Track the download
							GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, "png", result);

							int index = result.indexOf(".png");

							final String name;

							if (index != -1)
								name = result.substring(0, index);
							else
								name = result;

							if (AbstractChart.this instanceof PlotlyChart)
							{
								int[] size = ((PlotlyChart)AbstractChart.this).getDownloadSize();
								downloadJsni(panelId, name + ".png", size[0], size[1]);
							}
							else
							{
								Element chart = chartPanel.getElement();
								Element svg = null;
								Element legend = null;
								for (int i = 0; i < chart.getChildCount(); i++)
								{
									Element el = DOM.getChild(chart, i);
									if (el.getTagName().equals("svg"))
									{
										svg = el;
									}
									else if (el.hasClassName(Id.CHART_D3_LEGEND))
									{
										legend = el;
									}
								}

								// Download the png
								downloadImage(svg, name + ".png");

								if (legend != null)
								{
									final Element el = legend;
									Scheduler.get().scheduleDeferred(() -> downloadLegend(el, name + "-legend.png"));
								}
							}

							popup.hide();
						}
					});
				});
				menuItem.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Emphasis.PRIMARY.getCssName()));
				menuBar.addItem(menuItem);

				if (canDownloadSvg())
				{
					/* Add the svg download item */
					menuItem = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_FILE_XML, downloadSvg.asString()), (Command) () ->
					{
						// Get the initial filename
						String finalFilename = DateUtils.getFilenameForDate(System.currentTimeMillis()) + "-" + filename + ".svg";
						// Then ask the user for a preference
						getFilename(finalFilename, new Callback<String, Exception>()
						{
							@Override
							public void onFailure(Exception reason)
							{
							}

							@Override
							public void onSuccess(String result)
							{
								// Track the download
								GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, "svg", result);

								final String name;

								int index = result.indexOf(".svg");
								if (index != -1)
									name = result.substring(0, index);
								else
									name = result;

								if (AbstractChart.this instanceof PlotlyChart)
								{
									Element[] svgs = JavaScript.find(chartPanel.getElement(), "svg:not(.icon):not(:last-child)");

									downloadSvgs(svgs, name + ".svg");
								}
								else
								{
									Element chart = chartPanel.getElement();
									Element svg = null;
									Element legend = null;
									for (int i = 0; i < chart.getChildCount(); i++)
									{
										Element el = DOM.getChild(chart, i);
										if (el.getTagName().equals("svg"))
										{
											svg = el;
										}
										else if (el.hasClassName(Id.CHART_D3_LEGEND))
										{
											legend = el;
										}
									}

									// Download the svg
									downloadSvg(svg, name + ".svg");

									if (legend != null)
									{
										final Element el = legend;
										Scheduler.get().scheduleDeferred(() -> downloadLegend(el, name + "-legend.png"));
									}
								}

								popup.hide();
							}
						});
					});
					menuItem.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Emphasis.PRIMARY.getCssName()));
					menuBar.addItem(menuItem);
				}
			}

			/* Add the file download item */
			menuItem = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_FILE_DOCUMENT, downloadData.asString()), (Command) () ->
			{
				// Invoke the download based on the path
				JavaScript.invokeDownload(filePath);
				// Track the event
				GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, "txt", filename);

				popup.hide();
			});
			menuItem.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Emphasis.PRIMARY.getCssName()));
			menuBar.addItem(menuItem);

			// If there are other items to add
			if (!ArrayUtils.isEmpty(additionalItems))
			{
				// Add a separator
				menuBar.addSeparator();

				// For each item
				for (MenuItem m : additionalItems)
				{
					final Scheduler.ScheduledCommand old = m.getScheduledCommand();
					m.setScheduledCommand(() ->
					{
						old.execute();
						popup.hide();
					});
					m.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Emphasis.PRIMARY.getCssName()));
					menuBar.addItem(m);
				}
			}

			menuBar.setVisible(true);
			menuBar.setStyleName(TooltipPanelResource.INSTANCE.css().panel());
			menuBar.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);

			popup.add(menuBar);

			if (button == null)
			{
				// Create the button that opens the menu
				button = new Button("", event -> handleEvent(event, button, popup));
				button.addStyleName(Style.mdiLg(Style.MDI_DOTS_VERTICAL));
				button.setTitle(Text.LANG.widgetChartOptions());

				SimplePanel buttonGroupContainer = new SimplePanel();
				buttonGroupContainer.addStyleName(Alignment.RIGHT.getCssName());
				buttonGroup = new ButtonGroup();
				buttonGroup.getElement().getStyle().setZIndex(10);
				panel.insert(buttonGroupContainer, panel.getWidgetIndex(chartPanel));
				buttonGroup.add(button);
				buttonGroupContainer.add(buttonGroup);

				Button[] additionalButtons = getAdditionalButtons();

				if (!ArrayUtils.isEmpty(additionalButtons))
				{
					Arrays.stream(additionalButtons)
						  .forEach(b -> buttonGroup.add(b));
				}
			}
		}
	}

	private native void downloadJsni(String id, String name, int width, int height) /*-{
		$wnd.Plotly.downloadImage(id, {format: 'png', width: width, height: height, filename: name});
	}-*/;

	/**
	 * Asks the user for a file name preference
	 *
	 * @param suggestion The initial suggestion
	 * @param callback   The callback that is called when the user interacts with the dialog
	 */
	private void getFilename(String suggestion, final Callback<String, Exception> callback)
	{
		FlowPanel panel = new FlowPanel();
		final TextBox textBox = new TextBox();
		textBox.setWidth("100%");
		textBox.setValue(suggestion);
		panel.add(textBox);

		// Delay the focus event
		Scheduler.get().scheduleDeferred(() ->
		{
			textBox.setFocus(true);
			textBox.selectAll();
		});

		// Create a new dialog
		ModalBody body = new ModalBody();
		body.add(new Label(Text.LANG.d3AlertSelectFilename()));
		body.add(textBox);

		final AlertDialog dialog = new AlertDialog(Text.LANG.generalFilename(), body)
				.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalContinue(), Style.MDI_ARROW_RIGHT_BOLD, ButtonType.PRIMARY, (e) -> callback.onSuccess(textBox.getValue())))
				.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalCancel(), Style.MDI_CANCEL, (e) -> callback.onFailure(null)));

		// Add an enter key listener
		textBox.addKeyUpHandler(event ->
		{
			// If enter is pressed, invoke the positive button click
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
				dialog.positiveClick();
		});

		dialog.open();
	}

	/**
	 * Starts the download of the legend
	 *
	 * @param div      The legend div
	 * @param filename The file name to use
	 */
	private native void downloadLegend(Element div, String filename)/*-{
		if ($wnd.saveLegendAsPng)
			$wnd.saveLegendAsPng(div, filename);
		else
			console.error("Load D3_DOWNLOAD library to download d3 charts");
	}-*/;

	/**
	 * Starts the download of the image
	 *
	 * @param svg      The svg element
	 * @param filename The file name to use
	 */
	private native void downloadImage(Element svg, String filename)/*-{
		if ($wnd.saveSvgAsPng)
			$wnd.saveSvgAsPng(svg, filename, 1);
		else
			console.error("Load D3_DOWNLOAD library to download d3 charts");
	}-*/;

	/**
	 * Starts the download of the svg
	 *
	 * @param svg The svg element
	 */
	private native void downloadSvg(Element svg, String filename)/*-{
		var that = this;
		if ($wnd.svgAsBlob) {
			$wnd.svgAsBlob(svg, 1, function (uri) {
				that.@jhi.germinate.client.widget.d3js.AbstractChart::onDownloadSvg(*)(uri, filename);
			});
		}
		else {
			console.error("Load D3_DOWNLOAD library to download d3 charts");
		}
	}-*/;

	/**
	 * Starts the download of the svg
	 *
	 * @param svgs The svg elements
	 */
	private native void downloadSvgs(Element[] svgs, String filename)/*-{
		var that = this;
		if ($wnd.svgsAsBlob) {
			$wnd.svgsAsBlob(svgs, 1, function (uri) {
				that.@jhi.germinate.client.widget.d3js.AbstractChart::onDownloadSvg(*)(uri, filename);
			});
		}
		else {
			console.error("Load D3_DOWNLOAD library to download d3 charts");
		}
	}-*/;

	private void handleEvent(DomEvent event, Widget relativeTo, PopupPanel popup)
	{
		event.preventDefault();
		event.stopPropagation();

		popup.setPopupPositionAndShow((offsetWidth, offsetHeight) ->
		{
			int popupX = event.getNativeEvent().getClientX() + Window.getScrollLeft();
			if (popupX + offsetWidth > Window.getClientWidth() + Window.getScrollLeft())
				popupX = Window.getClientWidth() + Window.getScrollLeft() - offsetWidth;

			int popupY;
			if (relativeTo != null)
				popupY = relativeTo.getAbsoluteTop() + relativeTo.getOffsetHeight();
			else
				popupY = event.getNativeEvent().getClientY() + Window.getScrollTop();

			popup.setPopupPosition(popupX, popupY);
		});
	}

	/**
	 * Opens the svg in the form a uri in a new tab
	 *
	 * @param uri The svg in the form <code>data:image/svg+xml;base64,[...]</code>
	 */
	private void onDownloadSvg(String uri, String filename)
	{
		/* Click it */
		JavaScript.invokeDownload(uri, filename);

		GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, "svg", filename);
	}

	/**
	 * Removes d3 from the current page.
	 */
	protected void removeD3(String panelId)
	{
		if (StringUtils.isEmpty(panelId))
			return;

		JavaScript.remove("#" + panelId + " ." + Id.CHART_D3_TOOLTIP);
		JavaScript.remove("#" + panelId + " ." + Id.CHART_D3_LEGEND);

		removeD3JS(panelId);

		for (Iterator<Map.Entry<Widget, PopupPanel>> it = popupPanels.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<Widget, PopupPanel> item = it.next();

			if (panelId.equals(item.getKey().getElement().getId()))
			{
				item.getValue().removeFromParent();
				it.remove();
			}
		}

		for (Iterator<Map.Entry<Widget, HandlerRegistration>> it = handlers.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<Widget, HandlerRegistration> item = it.next();

			if (panelId.equals(item.getKey().getElement().getId()))
			{
				item.getValue().removeHandler();
				it.remove();
			}
		}
	}

	private native void removeD3JS(String panelId)/*-{
		if (typeof $wnd.d3 !== "undefined")
			$wnd.d3.selectAll("#" + panelId + " svg").remove();
	}-*/;

	private static native void removeD3JS()/*-{
		if (typeof $wnd.d3 !== "undefined")
			$wnd.d3.selectAll("svg").remove();
	}-*/;
}
