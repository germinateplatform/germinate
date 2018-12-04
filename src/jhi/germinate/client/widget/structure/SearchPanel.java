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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextBox;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class SearchPanel extends Composite
{
	interface SearchPanelUiBinder extends UiBinder<InputGroup, SearchPanel>
	{
	}

	private static SearchPanelUiBinder ourUiBinder = GWT.create(SearchPanelUiBinder.class);

	private static boolean isInitialized = false;

	@UiField
	TextBox searchBox;

	@UiField
	Button searchButton;

	public static void init()
	{
		if (!isInitialized && GerminateSettingsHolder.isPageAvailable(Page.SEARCH))
		{
			isInitialized = true;

			SearchPanel searchPanel = new SearchPanel();

			RootPanel p = RootPanel.get(Id.STRUCTURE_SEARCH_PANEL);
			p.setVisible(true);
			p.add(searchPanel);
		}
	}

	public SearchPanel()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@UiHandler("searchBox")
	void onSearchKeyPress(KeyPressEvent e)
	{
		if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
		{
			doSearch();
		}
	}

	@UiHandler("searchBox")
	void onSearchBoxFocus(FocusEvent e)
	{
		searchBox.selectAll();
	}

	@UiHandler("searchButton")
	void onSearchButtonClicked(ClickEvent e)
	{
		doSearch();
	}

	public void clear()
	{
		if (searchBox != null && searchBox.isAttached())
			searchBox.clear();
	}

	/**
	 * Initiates the search. Will navigate to the search result page if the search box contains a valid string
	 */
	private void doSearch()
	{
		String searchString = searchBox.getText();

		if (searchString.equals(Text.LANG.searchPrompt()))
			searchString = "";

		if (searchString.equals("wakka wakka wakka"))
		{
			new Pacman();
		}
		else
		{

        	/* Save the parameter in the parameter store */
			StringParameterStore.Inst.get().put(Parameter.searchString, searchString);
			/* Clear the focus */
			searchBox.setFocus(false);

        	/* Either update the search page of navigate to it */
			String historyToken = History.getToken();
			if (Page.SEARCH.name().equals(historyToken))
				History.fireCurrentHistoryState();
			else
				History.newItem(Page.SEARCH.name());
		}

		clear();
	}
}