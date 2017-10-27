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

package jhi.germinate.client.widget.element;

import org.gwtbootstrap3.client.ui.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class NonAutoCloseDropDownMenu extends DropDownMenu
{
	private final String id;

	public NonAutoCloseDropDownMenu()
	{
		super();
		id = "menu-" + RandomUtils.RANDOM.nextLong();
		getElement().setId(id);
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		jsniOn("#" + id);
	}

	@Override
	protected void onUnload()
	{
		jsniOff("#" + id);

		super.onUnload();
	}

	private native void jsniOn(String id)/*-{
		$wnd.$($doc).on('click', id, function (e) {
			e.stopPropagation();
		});
	}-*/;

	private native void jsniOff(String id)/*-{
		$wnd.$($doc).off('click', id);
	}-*/;
}
