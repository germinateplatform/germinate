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

package jhi.germinate.client.page.geography;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.search.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class MegaEnvironmentsPage extends Composite implements ParallaxBannerPage, HasLibraries
{
	interface MegaEnvironmentsPageUiBinder extends UiBinder<HTMLPanel, MegaEnvironmentsPage>
	{
	}

	private static MegaEnvironmentsPageUiBinder ourUiBinder = GWT.create(MegaEnvironmentsPageUiBinder.class);

	@UiField
	FlowPanel mapWrapperPanel;
	@UiField
	FlowPanel resultWrapperPanel;

	@UiField
	SimplePanel   megaEnvironmentsPanel;
	@UiField
	Heading       heading;
	@UiField
	SimplePanel   mapPanel;
	@UiField
	SearchSection locationSection;
	@UiField
	SearchSection accessionSection;
	@UiField
	SimplePanel   downloadPanel;

	private MegaEnvironment megaEnvironment;
	private LocationTable   locationTable;
	private AccessionTable  accessionTable;

	private LeafletUtils.ClusteredMarkerCreator map;

	public MegaEnvironmentsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		megaEnvironmentsPanel.add(new MegaEnvironmentTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MegaEnvironment>>> callback)
			{
				return LocationService.Inst.get().getMegaEnvs(Cookie.getRequestProperties(), pagination, callback);
			}

			@Override
			protected void onItemSelected(NativeEvent event, MegaEnvironment object, int column)
			{
				super.onItemSelected(event, object, column);

				megaEnvironment = object;
				setData();
			}
		});

		locationTable = new LocationTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected void createColumns()
			{
				super.createColumns();

				/* Add the elevation column */
				TextColumn column = new TextColumn()
				{
					@Override
					public String getValue(Location object)
					{
						return Long.toString(object.getSize());
					}

					@Override
					public Class getType()
					{
						return Long.class;
					}
				};
				column.setDataStoreName(Location.COUNT);
				addColumn(column, Text.LANG.generalCount(), true);
			}

			@Override
			public boolean supportsFullIdMarking()
			{
				return true;
			}

			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				if (megaEnvironment != null)
					LocationService.Inst.get().getIdsForMegaEnv(Cookie.getRequestProperties(), megaEnvironment.getId(), callback);
				else
					callback.onSuccess(new ServerResult<>(null, new ArrayList<>()));
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Location>>> callback)
			{
				if (megaEnvironment != null)
					return LocationService.Inst.get().getForMegaEnv(Cookie.getRequestProperties(), megaEnvironment.getId(), pagination, new SearchCallback<>(locationSection, callback));
				else
				{
					callback.onSuccess(new PaginatedServerResult<>(null, new ArrayList<>(), 0));
					return null;
				}
			}
		};
		locationSection.add(locationTable);

		accessionTable = new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
			{
				/* Call the correct server class here */
				if (megaEnvironment != null)
					return AccessionService.Inst.get().getForMegaEnv(Cookie.getRequestProperties(), megaEnvironment.getId(), pagination, new SearchCallback<>(accessionSection, callback));
				else
				{
					callback.onSuccess(new PaginatedServerResult<>(null, new ArrayList<>(), 0));
					return null;
				}
			}

			@Override
			public boolean supportsFullIdMarking()
			{
				return true;
			}

			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				if (megaEnvironment != null)
					AccessionService.Inst.get().getIdsForMegaEnv(Cookie.getRequestProperties(), megaEnvironment.getId(), callback);
				else
					callback.onSuccess(new ServerResult<>(null, new ArrayList<>()));
			}

			@Override
			protected boolean supportsFiltering()
			{
				return false;
			}
		};
		accessionSection.add(accessionTable);

		downloadPanel.add(new OnDemandFileDownloadWidget((index, callback) ->
		{
			/* Request the file creation on the server */
			LocationService.Inst.get().exportToKml(Cookie.getRequestProperties(), KmlType.megaEnvironment, megaEnvironment.getId(), callback);
		}, true)
				.setIconStyle(FileDownloadWidget.IconStyle.MDI)
				.addFile(Text.LANG.downloadGoogleEarth())
				.addType(FileType.kmz));
	}

	private void setData()
	{
		/* Get the name of the mega environment */
		String megaEnvName = megaEnvironment.getName();
		if (megaEnvironment.getId() == null || megaEnvironment.getId() == -1L)
			megaEnvName = Text.LANG.megaEnvUnknown();

		heading.setText(Text.LANG.megaEnvHeadingTable(HTMLUtils.stripHtmlTags(megaEnvName)));

		/* Set up the callback object for the mega environment data */
		LocationService.Inst.get().getForMegaEnv(Cookie.getRequestProperties(), megaEnvironment.getId(), Pagination.getDefault(), new DefaultAsyncCallback<PaginatedServerResult<List<Location>>>()
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				mapWrapperPanel.setVisible(false);

				super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(PaginatedServerResult<List<Location>> result)
			{
				mapWrapperPanel.setVisible(true);

				if (map == null)
					map = new LeafletUtils.ClusteredMarkerCreator(mapPanel, result.getServerResult(), null);
				else
					map.updateData(result.getServerResult());
			}
		});

		resultWrapperPanel.setVisible(true);
		accessionTable.refreshTable();
		locationTable.refreshTable();
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxEnvironment();
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.LEAFLET_COMPLETE};
	}
}