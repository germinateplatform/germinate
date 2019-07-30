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

package jhi.germinate.client.page;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.googlecode.gwt.charts.client.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.Notification.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;

/**
 * An {@link Enum} of all the available {@link Library}s that can be loaded
 *
 * @author Sebastian Raubach
 */
public enum Library
{
	LEAFLET_COMPLETE(""),
	LEAFLET("./js/leaflet/leaflet.js", "./css/leaflet/leaflet.css"),
	LEAFLET_DRAW("./js/leaflet/leaflet-draw.js", "./css/leaflet/leaflet-draw.css"),
	LEAFLET_MINIMAP("./js/leaflet/leaflet-minimap.js", "./css/leaflet/leaflet-minimap.css"),
	LEAFLET_PRUNE_CLUSTER("./js/leaflet/leaflet-prunecluster.js", "./css/leaflet/leaflet-prunecluster.css"),
	LEAFLET_HEATMAP("./js/leaflet/leaflet-heat.js"),
	LEAFLET_SYNC("./js/leaflet/leaflet-sync.js"),
	LEAFLET_GEODESIC("./js/leaflet/leaflet-geodesic.js"),

	HUTTON_BANNER("./js/hutton-banner.js", "./css/hutton-banner.css"),
	GOOGLE_CHARTS(ChartPackage.GEOCHART.name()),
	HTML_2_CANVAS("./js/html2canvas.js"),
	ZXCVBN("./js/zxcvbn.js"),
	PLOTLY("./js/plotly-latest.min.js"),
	PLOTLY_ALLELE_FREQ_CHART("./js/d3/plotly-allelefreq-chart.js"),
	PLOTLY_MAP_CHART("./js/d3/plotly-map-chart.js"),
	PLOTLY_BAR("./js/d3/plotly-bar-chart.js"),
	PLOTLY_SCATTER_PLOT("./js/d3/plotly-scatter-plot.js"),
	PLOTLY_SCATTER_MATRIX("./js/d3/plotly-scatter-matrix.js"),
	PLOTLY_LINE_CHART("./js/d3/plotly-line-chart.js"),
	D3_V3("./js/d3/d3.v3.min.js"),
	D3_TOPOJSON("./js/d3/d3-topojson.js"),
	D3_SAVE_AS("./js/d3/d3-save-as-png.js"),
	D3_DOWNLOAD(""),
	D3_DAGRE("./js/d3/dagre-d3.min.js"),
	D3_PEDIGREE_CHART("./js/d3/d3-pedigree-chart.js"),
	D3_HISTOGRAM("./js/d3/d3-histogram.js"),
	D3_TOOLTIP("./js/d3/d3-tip.js"),
	D3_LEGEND("./js/d3/d3-legend.js"),
	D3_LASSO("./js/d3/d3-lasso.js"),
	D3_PIE("./js/d3/d3-pie.js"),
	D3_MAP_CANVAS("./js/d3/d3-map-canvas.js"),
	D3_SCATTER_PLOT("./js/d3/d3-scatter-plot.js"),
	D3_SCATTER_MATRIX("./js/d3/d3-scatter-matrix.js"),
	D3_GROUPED_BAR_CHART("./js/d3/d3-grouped-bar-chart.js"),
	D3_BAR_CHART("./js/d3/d3-bar-chart.js"),
	D3_MULTI_LINE_CHART("./js/d3/d3-multi-line-chart.js"),
	D3_HORIZONTAL_GROUPED_BAR_CHART("./js/d3/d3-horizontal-grouped-bar-chart.js"),
	D3_BAR_CHART_FAKE_X("./js/d3/d3-bar-chart-fake-x.js"),
	D3_TREEMAP("./js/d3/d3-treemap.js"),
	D3_FLAPJACK_BINNING("./js/d3/d3-flapjack-binning.js");

	private String  jsPath;
	private String  cssPath = null;
	private boolean loaded  = false;

	Library(String jsPath)
	{
		this.jsPath = jsPath;
	}

	Library(String jsPath, String cssPath)
	{
		this.jsPath = jsPath;
		this.cssPath = cssPath;
	}

	/**
	 * Returns the path to the library
	 *
	 * @return The path to the library
	 */
	public String getJsPath()
	{
		return jsPath;
	}

	public String getCssPath()
	{
		return cssPath;
	}

	/**
	 * Returns <code>true</code> if the library has already been loaded
	 *
	 * @return <code>true</code> if the library has already been loaded
	 */
	public boolean isLoaded()
	{
		return loaded;
	}

	/**
	 * Sets the loaded status of the {@link Library}
	 *
	 * @param loaded Set to <code>true</code> to indicate that the {@link Library} has already been loaded
	 */
	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}

	/**
	 * Returns a {@link ParallelCallback} whose {@link ParallelCallback#onSuccess(Object)} method is called once the library was successfully loaded.
	 * This will return <code>null</code> if the library is already
	 *
	 * @return A {@link ParallelCallback} whose {@link ParallelCallback#onSuccess(Object)} method is called once the library was successfully loaded.
	 * This will return <code>null</code> if the library is already loaded.
	 */
	public ParallelCallback<Void, Exception> getCallback()
	{
		switch (this)
		{
			case GOOGLE_CHARTS:
				if (isLoaded())
				{
					return null;
				}
				else
				{
					return new ParallelCallback<Void, Exception>()
					{
						@Override
						public void onSuccess(Void result)
						{
							Library.this.setLoaded(true);
							super.onSuccess(result);
						}

						@Override
						public void onFailure(Exception caught)
						{
							Notification.notify(Type.ERROR, Text.LANG.notificationLibraryError() + " " + Library.this.name());

							super.onFailure(caught);
						}

						@Override
						protected void start()
						{
							ChartLoader chartLoader = new ChartLoader(ChartPackage.valueOf(jsPath));
							chartLoader.loadApi(() -> this.onSuccess(null));
						}
					};
				}

			case D3_DOWNLOAD:

				/* This one is a bit tricky. We load all at once in an iterative
				 * fashion. */
				if (isLoaded() || (D3_SAVE_AS.isLoaded() && HTML_2_CANVAS.isLoaded()))
				{
					return null;
				}
				else
				{
					return new ParallelCallback<Void, Exception>()
					{
						@Override
						public void onSuccess(Void result)
						{
							Library.this.setLoaded(true);
							D3_SAVE_AS.setLoaded(true);
							HTML_2_CANVAS.setLoaded(true);
							super.onSuccess(result);
						}

						@Override
						public void onFailure(Exception caught)
						{
							Notification.notify(Type.ERROR, Text.LANG.notificationLibraryError() + " " + Library.this.name());

							super.onFailure(caught);
						}

						@Override
						protected void start()
						{
							final ParallelCallback<Void, Exception> that = this;

							List<ParallelCallback<?, ?>> callbacks = new ArrayList<>();
							if (!D3_SAVE_AS.loaded)
								callbacks.add(D3_SAVE_AS.getCallback());
							if (!HTML_2_CANVAS.loaded)
								callbacks.add(HTML_2_CANVAS.getCallback());

							new IterativeParentCallback(callbacks)
							{
								@Override
								public void handleSuccess()
								{
									that.onSuccess(null);
								}

								@Override
								public void handleFailure(Exception reason)
								{
									that.onFailure(reason);
								}
							};
						}
					};
				}

			case LEAFLET_COMPLETE:
			{
				/* This one is a bit tricky. We load all at once in an iterative
				 * fashion. GoogleMaps first, then the others. We need to make
				 * sure that we mark all sub-items as loaded as well. */
				if (isLoaded() || (LEAFLET.isLoaded() && LEAFLET_MINIMAP.isLoaded() && LEAFLET_HEATMAP.isLoaded()) && LEAFLET_PRUNE_CLUSTER.isLoaded() && LEAFLET_DRAW.isLoaded() && LEAFLET_GEODESIC.isLoaded() && LEAFLET_SYNC.isLoaded())
				{
					return null;
				}
				else
				{
					return new ParallelCallback<Void, Exception>()
					{
						@Override
						public void onSuccess(Void result)
						{
							Library.this.setLoaded(true);
							LEAFLET.setLoaded(true);
							LEAFLET_MINIMAP.setLoaded(true);
							LEAFLET_HEATMAP.setLoaded(true);
							LEAFLET_PRUNE_CLUSTER.setLoaded(true);
							LEAFLET_DRAW.setLoaded(true);
							LEAFLET_GEODESIC.setLoaded(true);
							LEAFLET_SYNC.setLoaded(true);
							super.onSuccess(result);
						}

						@Override
						public void onFailure(Exception caught)
						{
							Notification.notify(Type.ERROR, Text.LANG.notificationLibraryError() + " " + Library.this.name());

							super.onFailure(caught);
						}

						@Override
						protected void start()
						{
							final ParallelCallback<Void, Exception> that = this;

							List<ParallelCallback<?, ?>> callbacks = new ArrayList<>();
							if (!LEAFLET.loaded)
								callbacks.add(LEAFLET.getCallback());
							if (!LEAFLET_HEATMAP.loaded)
								callbacks.add(LEAFLET_HEATMAP.getCallback());
							if (!LEAFLET_MINIMAP.loaded)
								callbacks.add(LEAFLET_MINIMAP.getCallback());
							if (!LEAFLET_PRUNE_CLUSTER.loaded)
								callbacks.add(LEAFLET_PRUNE_CLUSTER.getCallback());
							if (!LEAFLET_DRAW.loaded)
								callbacks.add(LEAFLET_DRAW.getCallback());
							if (!LEAFLET_GEODESIC.loaded)
								callbacks.add(LEAFLET_GEODESIC.getCallback());
							if (!LEAFLET_SYNC.loaded)
								callbacks.add(LEAFLET_SYNC.getCallback());

							new IterativeParentCallback(callbacks)
							{
								@Override
								public void handleSuccess()
								{
									that.onSuccess(null);
								}

								@Override
								public void handleFailure(Exception reason)
								{
									that.onFailure(reason);
								}
							};
						}
					};
				}
			}

			default:
				if (isLoaded())
				{
					return null;
				}
				else
				{
					return new ParallelCallback<Void, Exception>()
					{
						@Override
						public void onSuccess(Void result)
						{
							Library.this.setLoaded(true);
							super.onSuccess(result);
						}

						@Override
						public void onFailure(Exception e)
						{
							Notification.notify(Type.ERROR, Text.LANG.notificationLibraryError() + " " + Library.this.name());

							super.onFailure(e);
						}

						@Override
						public void start()
						{
							ScriptInjector.fromUrl(jsPath)
										  .setCallback(this)
										  .setWindow(ScriptInjector.TOP_WINDOW)
										  .inject();

							if (!StringUtils.isEmpty(cssPath))
								loadCss(cssPath);
						}
					};
				}
		}
	}

	public static void loadCss(String url)
	{
		LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setHref(url);
		nativeAttachToHead(link);
	}

	/**
	 * Attach element to head
	 */
	protected static native void nativeAttachToHead(JavaScriptObject scriptElement) /*-{
		var element = $doc.getElementsByTagName("head")[0];
		element.insertBefore(scriptElement, element.firstChild);
	}-*/;

	/**
	 * {@link Queue} is a class holding a {@link LinkedList} of {@link Library} s. It is used to keep track of requested {@link Library}s that need to
	 * be loaded.
	 *
	 * @author Sebastian Raubach
	 */
	public static class Queue
	{
		/**
		 * We are using a simple {@link LinkedList} here because it implements {@link Deque}. It's not synchronized but that doesn't matter, since the
		 * client/javascript part of GWT is single threaded.
		 */
		private static final LinkedList<Library> QUEUE = new LinkedList<>();

		/**
		 * Checks if the {@link Queue} is empty
		 *
		 * @return <code>true</code> if the {@link Queue} is empty.
		 */
		public static boolean isEmpty()
		{
			return QUEUE.isEmpty();
		}

		/**
		 * Attempts to add the new {@link Library} to the {@link Queue}. <p/> Will return <code>true</code> if the {@link Library} was added
		 * successfully and <code>false</code> if the {@link Library} either has already been loaded or if the {@link Queue} already contains it.
		 *
		 * @param lib The {@link Library} to add been loaded or if the {@link Queue} already contains it.
		 */
		public static void push(Library lib)
		{
			if (!lib.isLoaded() && !QUEUE.contains(lib))
				QUEUE.add(lib);
		}

		/**
		 * Returns the head of the {@link Queue} or <code>null</code>
		 *
		 * @return The head of the {@link Queue} or <code>null</code>
		 */
		public static Library pop()
		{
			return QUEUE.poll();
		}

		/**
		 * Returns all {@link ParallelCallback}s in the same get as their {@link Library}s in the {@link Queue}.
		 *
		 * @return All {@link ParallelCallback}s
		 */
		public static ParallelCallback<?, ?>[] popAllCallbacks()
		{
			ParallelCallback<?, ?>[] result = new ParallelCallback<?, ?>[QUEUE.size()];

			for (int i = 0; i < result.length; i++)
				result[i] = QUEUE.poll().getCallback();

			return result;
		}

		/**
		 * Calls all the {@link ParallelCallback}s of the {@link Queue}. Will notify the given {@link Callback} on success or failure
		 *
		 * @param callback The {@link Callback} that will be notified if the internal {@link ParallelCallback}s succeed or fail.
		 */
		public static void callCallbacks(final Callback<Void, Exception> callback)
		{
			/* Create a parent that holds all the sub-callbacks */
			new IterativeParentCallback(popAllCallbacks())
			{
				@Override
				public void handleSuccess()
				{
					callback.onSuccess(null);
				}

				@Override
				public void handleFailure(Exception reason)
				{
					callback.onFailure(reason);
				}
			};
		}

		public static void load(DefaultAsyncCallback<Void> callback, Library... library)
		{
			if (library == null)
			{
				callback.onSuccess(null);
				return;
			}

			Arrays.stream(library)
				  .filter(l -> !l.isLoaded())
				  .forEachOrdered(Queue::push);

			Library.Queue.callCallbacks(new Callback<Void, Exception>()
			{
				@Override
				public void onFailure(Exception reason)
				{
					Notification.notify(Notification.Type.ERROR, reason.getLocalizedMessage());

					if (GerminateSettingsHolder.get().loadPageOnLibraryError.getValue())
					{
						callback.onSuccess(null);
					}
					else
					{
						callback.onFailure(reason);
					}
				}

				@Override
				public void onSuccess(Void result)
				{
					/* Once they all successfully loaded, set up the child content */
					callback.onSuccess(null);
				}
			});
		}
	}
}
