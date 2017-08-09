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

package jhi.germinate.client.widget.table.pagination.cell;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.client.*;
import com.google.gwt.safehtml.shared.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.shared.Style;

/**
 * @author Sebastian Raubach
 */
public class DummyInputCell extends AbstractCell<String>
{
	interface Templates extends SafeHtmlTemplates
	{
		@Template("<div class='" + jhi.germinate.shared.Style.LAYOUT_VERTICAL_INPUT_GROUP + "'><input class='" + Styles.FORM_CONTROL + "' type='text' style='border-top: 0; width: 0px; visibility: hidden; display: block; padding: 3px;' value='{0}' disabled ></input></div>")
		SafeHtml filterDummy(String value);

		@Template("<div class='" + Style.LAYOUT_VERTICAL_INPUT_GROUP + "'><input class='" + Styles.FORM_CONTROL + "' type='text' style='border-top: 0; width: 0px; visibility: hidden; display: none; padding: 3px;' value='{0}' disabled ></input></div>")
		SafeHtml filterDummyInvisible(String value);
	}

	private static final Templates templates = GWT.create(Templates.class);

	private FilterCell.VisibilityCallback visibilityCallback;

	public DummyInputCell(FilterCell.VisibilityCallback visibilityCallback)
	{
		super(BrowserEvents.FOCUS);

		this.visibilityCallback = visibilityCallback;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb)
	{
		if (visibilityCallback != null && !visibilityCallback.isFilterVisible())
		{
			sb.append(templates.filterDummyInvisible(""));
		}
		else
		{
			sb.append(templates.filterDummy(""));
		}
	}
}
