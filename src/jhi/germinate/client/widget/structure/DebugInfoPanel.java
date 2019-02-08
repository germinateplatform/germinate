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

package jhi.germinate.client.widget.structure;

import com.google.gwt.core.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class DebugInfoPanel extends Composite
{
	interface DebugInfoPanelUiBinder extends UiBinder<FlowPanel, DebugInfoPanel>
	{
	}

	private static DebugInfoPanelUiBinder ourUiBinder = GWT.create(DebugInfoPanelUiBinder.class);

	@UiField
	FlowPanel content;

	public DebugInfoPanel()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	private static boolean        isInitialized = false;
	private static DebugInfoPanel INSTANCE;

	public static void init()
	{
		if (!isInitialized)
		{
			isInitialized = true;
			INSTANCE = new DebugInfoPanel();

			RootPanel p = RootPanel.get(Id.STRUCTURE_DEBUG_INFO);
			p.add(INSTANCE);
			p.removeFromParent();
		}
	}

	public static void clear()
	{
		if (INSTANCE != null)
			INSTANCE.content.clear();
	}

	private native static void callPrintPretty() /*-{
		$wnd.prettyPrint();
	}-*/;

	public static void addDebugInfo(String description, String sqlDebugMessage)
	{
		if (!StringUtils.isEmpty(sqlDebugMessage))
		{
			SafeHtml message = CommonHtmlTemplates.INSTANCE.preCode(Style.PRETTY_PRINT_SQL, sqlDebugMessage);

			INSTANCE.content.insert(new HeaderAlert(description, message), 0);

			callPrintPretty();
		}
	}

	public static void addDebugInfo(String description, DebugInfo sqlDebugMessages)
	{
		if (sqlDebugMessages != null)
		{
			for (int i = 0; i < sqlDebugMessages.size(); i++)
			{
				addDebugInfo(description, sqlDebugMessages.get(i));
			}
		}
	}
}