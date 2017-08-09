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

package jhi.germinate.client.widget.map;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;
import java.util.Map;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.gwt.leaflet.client.basic.*;
import jhi.gwt.leaflet.client.control.*;
import jhi.gwt.leaflet.client.layer.library.*;
import jhi.gwt.leaflet.client.layer.other.*;
import jhi.gwt.leaflet.client.map.*;

/**
 * @author Sebastian Raubach
 */
public class LeafletUtils
{
	private static final LeafletLatLng CENTER             = LeafletLatLng.newInstance(22.4749, 2.1703);
	public static final  int           DEFAULT_MAP_HEIGHT = 500;

	public static class MapCreator<T>
	{
		private   Panel            parent;
		protected LeafletMap       map;
		protected LeafletMiniMap   miniMap;
		private   OnMapLoadHandler handler;
		protected SimplePanel mapPanel = new SimplePanel();

		public MapCreator(Panel parent, OnMapLoadHandler handler)
		{
			this.parent = parent;
			this.handler = handler;

			mapPanel.addStyleName(Style.MAPS_PANEL);
			mapPanel.setHeight(DEFAULT_MAP_HEIGHT + "px");

			parent.add(mapPanel);

			Scheduler.get().scheduleDeferred(() ->
			{
				map = LeafletMap.newInstance(mapPanel.getElement());
				map.postponeZoomUntilFocus();
				map.setView(CENTER, 1);

				miniMap = LeafletMiniMap.newInstance(map, LeafletMiniMap.Options.newInstance()
																				.setToggleDisplay(true)
																				.setPosition(LeafletControlPosition.BOTTOM_LEFT)
																				.setZoomLevelFixed(0)
																				.setCenterFixed(CENTER)
																				.setWidth(240)
																				.setHeight(120));

				onMapLoad();
			});
		}

		public LeafletMap getMap()
		{
			return map;
		}

		public Panel getParent()
		{
			return parent;
		}

		protected void onMapLoad()
		{
			if (handler != null)
				handler.onMapLoaded(mapPanel, map);
		}

		public void updateData(T data)
		{
			// Nothing to update
		}
	}

	public static class HeatmapCreator extends MapCreator<Collection<Location>>
	{
		private LeafletHeatmap       heatmap;
		private Collection<Location> locations;

		public HeatmapCreator(Panel parent, Collection<Location> locations, OnMapLoadHandler handler)
		{
			super(parent, handler);
			this.locations = locations;
		}

		@Override
		protected void onMapLoad()
		{
			updateData(locations);

			super.onMapLoad();
		}

		@Override
		public void updateData(Collection<Location> data)
		{
			this.locations = data;

			if (heatmap != null)
				map.removeLayer(heatmap);

			if (!CollectionUtils.isEmpty(data))
			{
				JsArray<LeafletLatLng> points = JsArray.createArray().cast();

				for (Location location : locations)
				{
					Double latitude = location.getLatitude();
					Double longitude = location.getLongitude();

					if (latitude == null || longitude == null)
						continue;

					points.push(LeafletLatLng.newInstance(latitude, longitude, 1));
				}

				List<String> colors = GerminateSettingsHolder.get().templateGradientColors.getValue();

				LeafletHeatmap.GradientOptions o = LeafletHeatmap.GradientOptions.newInstance();

				float start = 0f;

				for (int i = 0; i < colors.size(); i++)
					o.add(start + (0.6f * i) / (colors.size() - 1), colors.get(i));

				heatmap = LeafletHeatmap.newInstance(map, LeafletHeatmap.Options.newInstance()
																				.setGradient(o)
																				.setMaxZoom(12)
																				.setMinOpacity(0.3)
																				.setRadius(10)
																				.setMax(1)
																				.setBlur(10), points);
			}
			else
			{
				heatmap = null;
			}
		}
	}

	public static class ClusteredMarkerCreator extends MapCreator<Collection<Location>>
	{
		private LeafletPruneCluster  clusterer;
		private Collection<Location> locations;
		private OnMarkerClickHandler clickHandler = null;

		public ClusteredMarkerCreator(Panel parent, Collection<Location> locations, OnMapLoadHandler handler)
		{
			super(parent, handler);
			this.locations = locations;
		}

		public ClusteredMarkerCreator(Panel parent, Collection<Location> locations, OnMarkerClickHandler clickHandler, OnMapLoadHandler handler)
		{
			this(parent, locations, handler);
			this.clickHandler = clickHandler;
		}

		@Override
		protected void onMapLoad()
		{
			updateData(locations);

			super.onMapLoad();
		}

		public void setMarkerClickHandler(OnMarkerClickHandler clickHandler)
		{
			this.clickHandler = clickHandler;

			updateData(locations);
		}

		@Override
		public void updateData(Collection<Location> data)
		{
			addMarkerLinkFunction(clickHandler);

			this.locations = data;

			if (clusterer == null)
				clusterer = LeafletPruneCluster.newInstance(map);
			else
				clusterer.removeMarkers();

			if (!CollectionUtils.isEmpty(data))
			{
				for (Location location : locations)
				{
					Double latitude = location.getLatitude();
					Double longitude = location.getLongitude();

					if (latitude == null || longitude == null)
						continue;

					StringBuilder title = getLocationInfoContent(location, clickHandler != null);

					title.append("</div>");

					LeafletPruneMarker marker = LeafletPruneMarker.newInstance(latitude, longitude)
																  .bindPopup(title.toString());

					clusterer.registerMarker(marker);
				}
			}

			clusterer.processView();
		}

		protected StringBuilder getLocationInfoContent(Location location, boolean addLink)
		{
			return getLocationInfoWindowContent(location, addLink);
		}
	}

	public static class DatasetMarkerCreator extends ClusteredMarkerCreator
	{
		private Map<Location, Dataset> mapping;

		public DatasetMarkerCreator(Panel parent, Map<Location, Dataset> locations, OnMapLoadHandler handler)
		{
			super(parent, locations.keySet(), handler);
			this.mapping = locations;
		}

		public DatasetMarkerCreator(Panel parent, Map<Location, Dataset> locations, OnMarkerClickHandler clickHandler, OnMapLoadHandler handler)
		{
			super(parent, locations.keySet(), clickHandler, handler);
			this.mapping = locations;
		}

		@Override
		protected StringBuilder getLocationInfoContent(Location location, boolean addLink)
		{
			StringBuilder builder = getLocationInfoWindowContent(location, addLink);
			addToTooltip(builder, location, Text.LANG.locationMapDataset(), mapping.get(location).getDescription(), false);

			builder.append("</div>");

			return builder;
		}
	}

	public static class IndividualMarkerCreator extends MapCreator<Collection<Location>>
	{
		private List<LeafletMarker> markers = new ArrayList<>();
		private Collection<Location> locations;

		public IndividualMarkerCreator(Panel parent, Collection<Location> locations, OnMapLoadHandler handler)
		{
			super(parent, handler);
			this.locations = locations;
		}

		@Override
		protected void onMapLoad()
		{
			updateData(locations);

			super.onMapLoad();
		}

		@Override
		public void updateData(Collection<Location> data)
		{
			this.locations = data;

			for (LeafletMarker marker : markers)
				map.removeLayer(marker);

			if (!CollectionUtils.isEmpty(locations))
			{
				for (Location location : locations)
				{
					Double latitude = location.getLatitude();
					Double longitude = location.getLongitude();

					if (latitude == null || longitude == null)
						continue;

					LeafletLatLng latLng = LeafletLatLng.newInstance(latitude, longitude);

					markers.add(LeafletMarker.newInstance(latLng)
											 .addTo(map)
											 .bindPopup(location.getName()));
				}

				if (markers.size() == 1)
					markers.get(0).openPopup();
			}
		}
	}

	public static StringBuilder getLocationInfoWindowContent(Location location, boolean addLink)
	{
		StringBuilder title = new StringBuilder("<div style='width: 250px'>");

		addToTooltip(title, location, Text.LANG.locationMapLocation(), location.getName(), addLink);
		if (location.getCountry() != null)
			addToTooltip(title, location, Text.LANG.passportColumnCountry(), location.getCountry().getName(), false);
		addToTooltip(title, location, Text.LANG.collectingsiteLatitude(), location.getLatitude());
		addToTooltip(title, location, Text.LANG.collectingsiteLongitude(), location.getLongitude());
		addToTooltip(title, location, Text.LANG.collectingsiteElevation(), location.getElevation());

		if (location.getType() != null)
			addToTooltip(title, location, Text.LANG.collectingsiteType(), location.getType().getName(), false);

		return title;
	}

	private static void addToTooltip(StringBuilder title, Location location, String key, Double value)
	{
		String formattedValue = "";
		try
		{
			formattedValue = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(value);
		}
		catch (Exception e)
		{
		}

		addToTooltip(title, location, key, formattedValue, false);
	}

	private static void addToTooltip(StringBuilder title, Location location, String key, String value, boolean addMarkerLink)
	{
		if (addMarkerLink)
		{
			/*
			 * Add a link which will call the newly defined
			 * javascript function
			 */
			title.append("<p><b>")
				 .append(key)
				 .append(":</b> ")
				 .append("<a href='javascript:void(0);' onclick=\"javascript:markerLinkFunction('")
				 .append(location.getId())
				 .append("','")
				 .append(value)
				 .append("');\">")
				 .append(value)
				 .append("</a>")
				 .append("</p>");
		}
		else
		{
			title.append("<p><b>")
				 .append(key)
				 .append(":</b> ")
				 .append(value)
				 .append("</p>");
		}
	}

	/**
	 * Defines the javascript function <code>markerLinkFunction(id, collsite)</code>, calling this function from javascript will invoke the method
	 * {@link OnMarkerClickHandler#onMarkerClicked(String, String)} )}.
	 *
	 * @param handler The {@link OnMarkerClickHandler} to call
	 */
	private static native void addMarkerLinkFunction(OnMarkerClickHandler handler)/*-{
		// Define the function
		if (handler) {
			$wnd.markerLinkFunction = function (id, collsite) {
				handler.@jhi.germinate.client.widget.map.LeafletUtils.OnMarkerClickHandler::onMarkerClicked(*)(id, collsite);
			}
		}
		else {
			$wnd.markerLinkFunction = undefined;
		}
	}-*/;

	public static ImageOverlayWrapper addClimateOverlays(LeafletMap map, List<ClimateOverlay> overlays)
	{
		ImageOverlayWrapper result = new ImageOverlayWrapper();
		for (ClimateOverlay overlay : overlays)
		{
			String overlayUrl = new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
															  .setPath(ServletConstants.SERVLET_IMAGES)
															  .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
															  .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
															  .setParam(ServletConstants.PARAM_SIZE, ImageService.SIZE_LARGE)
															  .setParam(ServletConstants.PARAM_REFERENCE_FOLDER, ReferenceFolder.climate.name())
															  .setParam(ServletConstants.PARAM_IMAGE_PATH, overlay.getPath())
															  .build();

			/* If the current overlay is a legend image */
			if (overlay.isLegend())
			{
				Element parent = map.getContainer();
				DivElement div = Document.get().createDivElement();
				div.addClassName(Style.WIDGET_MAP_STATIC_OVERLAY);

				ImageElement image = Document.get().createImageElement();
				image.setSrc(overlayUrl);

				div.appendChild(image);
				parent.appendChild(div);
				result.legendOverlay = div;
			}
			else
			{
				try
				{
					LeafletLatLngBounds bounds = LeafletLatLngBounds.newInstance(LeafletLatLng.newInstance(overlay.getBottomLeftLatitude(), overlay.getBottomLeftLongitude()),
							LeafletLatLng.newInstance(overlay.getTopRightLatitude(), overlay.getTopRightLongitude()));

					LeafletImageOverlay image = LeafletImageOverlay.newInstance(overlayUrl, bounds)
																   .addTo(map);

					result.overlays.add(image);
				}
				catch (NullPointerException e)
				{
					// Do nothing here
				}
			}
		}

		return result;
	}

	public static class ImageOverlayWrapper
	{
		private List<LeafletImageOverlay> overlays = new ArrayList<>();
		private Element legendOverlay;

		public void clear(LeafletMap map)
		{
			overlays.forEach(map::removeLayer);
			if (legendOverlay != null)
				legendOverlay.removeFromParent();

			overlays.clear();
			legendOverlay = null;
		}
	}

	public interface OnMapLoadHandler
	{
		void onMapLoaded(SimplePanel mapPanel, LeafletMap map);
	}

	/**
	 * An interface to listen for click events on markers
	 *
	 * @author Sebastian Raubach
	 */
	public interface OnMarkerClickHandler
	{
		/**
		 * Called when a marker has been clicked
		 *
		 * @param id   The id of the item represented by this marker
		 * @param name The content of the marker
		 */
		void onMarkerClicked(String id, String name);
	}
}
