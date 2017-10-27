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

package jhi.germinate.client.page.accession;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class AccessionDataDownloadWidget extends Composite
{
	interface AccessionDataDownloadWidgetUiBinder extends UiBinder<FlowPanel, AccessionDataDownloadWidget>
	{
	}

	private static AccessionDataDownloadWidgetUiBinder ourUiBinder = GWT.create(AccessionDataDownloadWidgetUiBinder.class);

	@UiField
	HTML text;

	@UiField
	ListBox columnBox;

	@UiField
	GroupListBox groupBox;

	@UiField
	ToggleSwitch includeAttributes;

	@UiField
	Button pedigree;

	public AccessionDataDownloadWidget(List<String> columns, List<Group> groups, Boolean hasPedigree)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		text.setHTML(Text.LANG.browseAccessionsDownloadText());

		columns.forEach(columnBox::addItem);

		if (CollectionUtils.isEmpty(groups))
			groups = new ArrayList<>();

		Group dummy = new Group(-2L).setDescription(Text.LANG.accessionsDownloadSelectedAccessions());
		groups.add(0, dummy);

		groupBox.setValue(dummy, false);
		groupBox.setAcceptableValues(true, groups);

		includeAttributes.setOnText(Text.LANG.generalYes());
		includeAttributes.setOffText(Text.LANG.generalNo());

		if (!hasPedigree)
			pedigree.removeFromParent();
	}

	@UiHandler("download")
	void onDownload(ClickEvent e)
	{
		long groupId = groupBox.getSelection().getId();

		FileDownloadCallback callback = new FileDownloadCallback(true);

		if (groupId == -2L)
		{
			Set<String> markedIds = MarkedItemList.get(MarkedItemList.ItemType.ACCESSION);

			if (CollectionUtils.isEmpty(markedIds))
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationAccessionExportMarkAtLeastOne());
				callback.forceClose();
			}
			else
			{
				AccessionService.Inst.get().export(Cookie.getRequestProperties(), columnBox.getItemText(columnBox.getSelectedIndex()), markedIds, includeAttributes.getValue(), callback);
			}
		}
		else
		{
			AccessionService.Inst.get().export(Cookie.getRequestProperties(), columnBox.getItemText(columnBox.getSelectedIndex()), groupBox.getSelection().getId(), includeAttributes.getValue(), callback);
		}
	}

	@UiHandler("pedigree")
	void onPedigreeDownload(ClickEvent e)
	{
		long groupId = groupBox.getSelection().getId();

		FileDownloadCallback callback = new FileDownloadCallback(true);

		if (groupId == -2L)
		{
			Set<Long> markedIds = MarkedItemList.getAsLong(MarkedItemList.ItemType.ACCESSION);

			if (CollectionUtils.isEmpty(markedIds))
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationAccessionExportMarkAtLeastOne());
				callback.forceClose();
			}
			else
			{
				PedigreeService.Inst.get().exportToHelium(Cookie.getRequestProperties(), markedIds, Pedigree.PedigreeQuery.UP_DOWN, callback);
			}
		}
		else
		{
			PedigreeService.Inst.get().exportToHelium(Cookie.getRequestProperties(), groupId, callback);
		}
	}
}