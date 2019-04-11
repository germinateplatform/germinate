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

package jhi.germinate.client.page.dataset;

import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetAttributeDownloadDialog extends AlertDialog
{
	private Dataset dataset;

	public DatasetAttributeDownloadDialog(String heading, Widget widget, Dataset dataset)
	{
		super(heading, widget);
		this.dataset = dataset;
	}

	@Override
	protected void setUp()
	{
		super.setUp();

		ButtonGroup downloadGroup = new ButtonGroup();
		downloadGroup.setDropUp(true);
		Button button = new Button(Text.LANG.generalSave());
		button.addStyleName(Style.combine(Style.MDI, Style.MDI_LG, Style.MDI_DOWNLOAD));
		button.setDataToggle(Toggle.DROPDOWN);
		button.setToggleCaret(true);
		DropDownMenu menu = new DropDownMenu();
		downloadGroup.add(button);
		downloadGroup.add(menu);

		MdiAnchorListItem attr = new MdiAnchorListItem(Text.LANG.datasetAttributesDownloadAttributes());
		menu.add(attr);
		attr.setMdi(Style.MDI_PLAYLIST_PLUS);
		attr.addClickHandler((event) -> {
			DatasetService.Inst.get().exportAttributes(Cookie.getRequestProperties(), DatabaseObject.getIds(dataset), null, new DefaultAsyncCallback<ServerResult<String>>(true)
			{
				@Override
				protected void onSuccessImpl(ServerResult<String> result)
				{
					if (result.hasData())
					{
						GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, "datasetAttributes", "txt", dataset.getId().intValue());
						JavaScript.invokeGerminateDownload(result.getServerResult());
					}
				}
			});
		});

		if (!StringUtils.isEmpty(dataset.getDublinCore()))
		{
			MdiAnchorListItem json = new MdiAnchorListItem(Text.LANG.datasetAttributesDownloadDublinCore());
			menu.add(json);
			json.setMdi(Style.MDI_JSON);
			json.addClickHandler((event) -> {
				DatasetService.Inst.get().getDublinCoreJson(Cookie.getRequestProperties(), dataset.getId(), new DefaultAsyncCallback<ServerResult<String>>(){
					@Override
					protected void onSuccessImpl(ServerResult<String> result)
					{
						if(result.hasData())
						{
							GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, "datasetAttributes", "json", dataset.getId().intValue());
							JavaScript.invokeGerminateDownload(result.getServerResult());
						}
					}
				});
			});
		}

		buttonGroup.insert(downloadGroup, 0);
	}
}
