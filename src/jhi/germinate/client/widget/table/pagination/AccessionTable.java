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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.Locale;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.datatype.*;

/**
 * @author Sebastian Raubach
 */
public abstract class AccessionTable extends MarkableDatabaseObjectPaginationTable<Accession>
{
	public AccessionTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(MarkedItemList.ItemType.ACCESSION, selectionMode, sortingEnabled);
	}

	@Override
	public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	protected boolean supportsDownload()
	{
		return false;
	}

	@Override
	protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	protected String getClassName()
	{
		return AccessionTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Accession, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Accession object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(Long.toString(object.getId()));
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyle()
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Accession.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), sortingEnabled);
		}

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Accession object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
					return TableUtils.getHyperlinkValue(object.getGeneralIdentifier(), "#" + Page.PASSPORT);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getGeneralIdentifier());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.GENERAL_IDENTIFIER);
		addColumn(column, Text.LANG.accessionsColumnGeneralIdentifier(), sortingEnabled);

		/* Add the name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Accession object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
					return TableUtils.getHyperlinkValue(object.getName(), "#" + Page.PASSPORT);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.NAME);
		addColumn(column, Text.LANG.accessionsColumnName(), sortingEnabled);

		/* Add the number column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Accession object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
					return TableUtils.getHyperlinkValue(object.getNumber(), "#" + Page.PASSPORT);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getNumber());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.NUMBER);
		addColumn(column, Text.LANG.accessionsColumnNumber(), sortingEnabled);

		/* Add the puid column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return object.getPuid();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.PUID);
		addColumn(column, new HeaderConfig(Text.LANG.accessionsColumnPuid(), Text.LANG.accessionColumnHelpPuid()), sortingEnabled);

		/* Add the entity type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return object.getEntityType().getName();
			}

			@Override
			public Class getType()
			{
				return EntityType.class;
			}
		};
		column.setDataStoreName(EntityType.NAME);
		addColumn(column, new HeaderConfig(Text.LANG.accessionsColumnEntityType(), Text.LANG.accessionsColumnHelpEntityType()), sortingEnabled);

		/* Add the biological status column */
		column = new SafeHtmlColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public SafeHtml getValue(Accession row)
			{
				if (row.getBiologicalStatus() != null)
				{
					String status = row.getBiologicalStatus().getSampStat();
					int index = status.indexOf(" (");
					String toDisplay = index != -1 ? status.substring(0, index) : status;
					return SimpleHtmlTemplate.INSTANCE.popoverText(toDisplay, status, Placement.TOP.getCssName());
				}
				else
				{
					return SimpleHtmlTemplate.INSTANCE.empty();
				}
			}
		};
		column.setDataStoreName(BiologicalStatus.SAMPSTAT);
		addColumn(column, Text.LANG.accessionColumnBiologicalStatus(), sortingEnabled);

		/* Add the synonyms column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return object.getSynonyms();
			}

			@Override
			public Class getType()
			{
				return Json.class;
			}
		};
		column.setDataStoreName(Synonym.SYNONYM);
		addColumn(column, Text.LANG.accessionsColumnSynonym(), false);

		/* Add the collector column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return object.getCollNumb();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.COLLNUMB);
		addColumn(column, Text.LANG.accessionsColumnCollNumber(), sortingEnabled);

		/* Add the genus column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getTaxonomy() != null)
					return object.getTaxonomy().getGenus();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.TEXT_ITALIC;
			}
		};
		column.setDataStoreName(Taxonomy.GENUS);
		addColumn(column, Text.LANG.passportColumnGenus(), sortingEnabled);

		/* Add the species column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getTaxonomy() != null)
					return object.getTaxonomy().getSpecies();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.TEXT_ITALIC;
			}
		};
		column.setDataStoreName(Taxonomy.SPECIES);
		addColumn(column, Text.LANG.passportColumnSpecies(), sortingEnabled);

		/* Add the subtaxa column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getTaxonomy() != null)
					return object.getTaxonomy().getSubtaxa();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.TEXT_ITALIC;
			}
		};
		column.setDataStoreName(Taxonomy.SUBTAXA);
		addColumn(column, Text.LANG.passportColumnSubtaxa(), sortingEnabled);

		/* Add the elevation column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getLocation() != null && object.getLocation().getElevation() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getLocation().getElevation()));
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.ELEVATION);
		addColumn(column, Text.LANG.collectingsiteElevation(), sortingEnabled);

		/* Add the country column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getLocation() != null && object.getLocation().getCountry() != null)
					return object.getLocation().getCountry().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
			}

			@Override
			public void render(Cell.Context context, Accession object, SafeHtmlBuilder sb)
			{
				String value = getValue(object);
				if (value != null)
				{
					sb.appendHtmlConstant("<span class=\"" + Style.COUNTRY_FLAG + " " + object.getLocation().getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "\"></span>");
					sb.append(SafeHtmlUtils.fromString(value));
				}
				else
				{
					super.render(context, object, sb);
				}
			}
		};
		column.setDataStoreName(Country.COUNTRY_NAME);
		addColumn(column, Text.LANG.passportColumnCountry(), sortingEnabled);

		/* Add the collection date column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return DateUtils.getLocalizedDate(object.getCollDate());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Accession.COLLDATE);
		addColumn(column, Text.LANG.passportColumnColldate(), sortingEnabled);

		/* Add the has image column */
		column = new SafeHtmlColumn()
		{
			@Override
			public Class getType()
			{
				return Boolean.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
			}

			@Override
			public SafeHtml getValue(Accession row)
			{
				String count = row.getExtra(Accession.IMAGE_COUNT);
				String path = row.getExtra(Accession.FIRST_IMAGE_PATH);
				if (count != null && !StringUtils.areEqual(count, "0"))
					return SimpleHtmlTemplate.INSTANCE.popoverImage(SimpleHtmlTemplate.INSTANCE.materialIconFixedWidthWithText(Style.MDI_CAMERA, Text.LANG.accessionColumnHasImage(), count), getUrl(path), Placement.BOTTOM.getCssName());
				else
					return SimpleHtmlTemplate.INSTANCE.empty();
			}

			private String getUrl(String imagePath)
			{
				if (!imagePath.startsWith("http://") && !imagePath.startsWith("https://"))
				{
					imagePath = new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
															  .setPath(ServletConstants.SERVLET_IMAGES)
															  .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
															  .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
															  .setParam(ServletConstants.PARAM_SIZE, ImageService.SIZE_SMALL)
															  .setParam(ServletConstants.PARAM_IMAGE_PATH, imagePath)
															  .build();
				}

				return imagePath;
			}
		};
		column.setDataStoreName(Accession.IMAGE_COUNT);
		addColumn(column, new HeaderConfig(""), true, false);

		if (GerminateSettingsHolder.get().pdciEnabled.getValue())
		{
			column = new SafeHtmlColumn()
			{
				@Override
				public String getCellStyle()
				{
					return Style.combine(Style.TEXT_CENTER_ALIGN, Style.LAYOUT_V_ALIGN_MIDDLE, Style.LAYOUT_WHITE_SPACE_NO_WRAP);
				}

				@Override
				public SafeHtml getValue(Accession row)
				{
					if (row.getPdci() != null)
						return SimpleHtmlTemplate.INSTANCE.peityDonut(NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(row.getPdci()), row.getPdci(), 10);
					else
						return SimpleHtmlTemplate.INSTANCE.empty();
				}

				@Override
				public void render(Cell.Context context, Accession object, SafeHtmlBuilder sb)
				{
					super.render(context, object, sb);

					if (GerminateSettingsHolder.get().pdciEnabled.getValue())
					{
						Scheduler.get().scheduleDeferred(() -> {
							TableRowElement row = getTable().getRowElement(context.getIndex() - getTable().getPageStart());
							jsniPeity(row);
						});
					}
				}

				@Override
				public Class getType()
				{
					return Double.class;
				}
			};
			column.setDataStoreName(Accession.PDCI);
			addColumn(column, new HeaderConfig(Text.LANG.passportColumnPDCI(), Text.LANG.passportColumnHelpPDCI()), true);
		}
	}

	@Override
	protected void onPostLoad()
	{
		super.onPostLoad();

		jsniImagePreview(getId());
	}

	private native void jsniPeity(Element element)/*-{
		var color = @jhi.germinate.client.util.GerminateSettingsHolder::getCategoricalColor(*)(0);
		$wnd.$(element).find('.donut').peity('donut', {
			fill: [color, "#cccccc"],
			radius: 9
		});
	}-*/;

	private native void jsniImagePreview(String id)/*-{
		$wnd.$('#' + id).on('mouseenter', '.img-popover[data-toggle="popover"]', function () {
			var e = $wnd.$(this);
			e.off('mouseenter');

			$wnd.$.ajax({
				mimeType: "text/plain; charset=x-user-defined",
				url: e.data('img')
			}).done(function (d, textStatus, jqXHR) {
				e.popover({
					html: true,
					placement: e.data('placement') ? e.data('placement') : 'bottom',
					trigger: 'hover',
					content: function() {
						return '<img src="data:image/jpg;base64,'+ $wnd.base64Encode(d) + '" style="max-width: 100%; width: 300px; height: auto;"/>';
					}
				});

				e.popover('show');
			});
		});
	}-*/;

	@Override
	protected void onItemSelected(NativeEvent event, Accession object, int column)
	{
		/* Get the id */
		LongParameterStore.Inst.get().put(Parameter.accessionId, object.getId());
	}

	@Override
	protected MenuItem[] getAdditionalItems(Accession row, PopupPanel popupPanel, MarkedItemListCallback callback)
	{
		MenuItem[] menuItems = new MenuItem[4];
		int i = 0;

		if (row == null)
		{
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_UP_BOX, Text.LANG.cartAddEntityParents()), () -> getEntityParentIds(getSearchFilter(false), new MarkingCallback(true, true, popupPanel, callback)));
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_UP_BOX_OUTLINE, Text.LANG.cartRemoveEntityParents()), () -> getEntityParentIds(getSearchFilter(false), new MarkingCallback(true, false, popupPanel, callback)));
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_DOWN_BOX, Text.LANG.cartAddEntityChildren()), () -> getEntityChildrenIds(getSearchFilter(false), new MarkingCallback(true, true, popupPanel, callback)));
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_DOWN_BOX_OUTLINE, Text.LANG.cartRemoveEntityChildren()), () -> getEntityChildrenIds(getSearchFilter(false), new MarkingCallback(true, false, popupPanel, callback)));
		}
		else
		{
			List<String> id = new ArrayList<>();
			id.add(Long.toString(row.getId()));

			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_UP_BOX, Text.LANG.cartAddEntityParents()), () -> AccessionService.Inst.get().getEntityParentIds(Cookie.getRequestProperties(), id, new MarkingCallback(true, true, popupPanel, callback)));
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_UP_BOX_OUTLINE, Text.LANG.cartRemoveEntityParents()), () -> AccessionService.Inst.get().getEntityParentIds(Cookie.getRequestProperties(), id, new MarkingCallback(true, false, popupPanel, callback)));
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_DOWN_BOX, Text.LANG.cartAddEntityChildren()), () -> AccessionService.Inst.get().getEntityChildIds(Cookie.getRequestProperties(), id, new MarkingCallback(true, true, popupPanel, callback)));
			menuItems[i++] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHEVRON_DOWN_BOX_OUTLINE, Text.LANG.cartRemoveEntityChildren()), () -> AccessionService.Inst.get().getEntityChildIds(Cookie.getRequestProperties(), id, new MarkingCallback(true, false, popupPanel, callback)));
		}

		return menuItems;
	}

	private void getEntityParentIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		getIds(filter, new AsyncCallback<ServerResult<List<String>>>()
		{
			@Override
			public void onFailure(Throwable throwable)
			{
				callback.onFailure(throwable);
			}

			@Override
			public void onSuccess(ServerResult<List<String>> ids)
			{
				if (ids.hasData())
					AccessionService.Inst.get().getEntityParentIds(Cookie.getRequestProperties(), ids.getServerResult(), callback);
				else
					callback.onSuccess(new ServerResult<>(null, new ArrayList<>()));
			}
		});
	}

	protected void getEntityChildrenIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		getIds(filter, new AsyncCallback<ServerResult<List<String>>>()
		{
			@Override
			public void onFailure(Throwable throwable)
			{
				callback.onFailure(throwable);
			}

			@Override
			public void onSuccess(ServerResult<List<String>> ids)
			{
				if (ids.hasData())
					AccessionService.Inst.get().getEntityChildIds(Cookie.getRequestProperties(), ids.getServerResult(), callback);
				else
					callback.onSuccess(new ServerResult<>(null, new ArrayList<>()));
			}
		});
	}

	private class MarkingCallback extends DefaultAsyncCallback<ServerResult<List<String>>>
	{
		private boolean                add;
		private PopupPanel             popupPanel;
		private MarkedItemListCallback callback;

		public MarkingCallback(boolean longRunning, boolean add, PopupPanel popupPanel, MarkedItemListCallback callback)
		{
			super(longRunning);
			this.add = add;
			this.popupPanel = popupPanel;
			this.callback = callback;
		}

		@Override
		protected void onSuccessImpl(ServerResult<List<String>> result)
		{
			if (result.hasData())
			{
				if (add)
					MarkedItemList.add(MarkedItemList.ItemType.ACCESSION, result.getServerResult());
				else
					MarkedItemList.remove(MarkedItemList.ItemType.ACCESSION, result.getServerResult());
				popupPanel.hide();
				callback.updateTable(result.getServerResult());
			}
		}
	}
}
