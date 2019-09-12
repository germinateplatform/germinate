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

package jhi.germinate.client.widget.table;

import com.google.gwt.safehtml.shared.*;
import com.google.gwt.text.shared.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.shared.*;

public class ButtonCell extends com.google.gwt.cell.client.ButtonCell implements HasEnabled
{

	private String icon;

	private ButtonType type = ButtonType.DEFAULT;

	private ButtonSize size = ButtonSize.DEFAULT;

	private boolean enabled = true;

	public ButtonCell()
	{
		super(SimpleSafeHtmlRenderer.getInstance());
	}

	public ButtonCell(ButtonType type)
	{
		this();
		this.type = type;
	}

	public ButtonCell(String icon)
	{
		this();
		this.icon = icon;
	}

	public ButtonCell(ButtonSize size)
	{
		this();
		this.size = size;
	}

	public ButtonCell(ButtonType type, String icon)
	{
		this();
		this.type = type;
		this.icon = icon;
	}

	public ButtonCell(ButtonType type, ButtonSize size)
	{
		this();
		this.type = type;
		this.size = size;
	}

	public ButtonCell(String icon, ButtonSize size)
	{
		this();
		this.icon = icon;
		this.size = size;
	}

	public ButtonCell(String icon, ButtonType type, ButtonSize size)
	{
		this();
		this.icon = icon;
		this.type = type;
		this.size = size;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb)
	{
		String cssClasses = "btn " + type.getCssName() + " " + size.getCssName();

		String disabled = "";
		if (!enabled)
		{
			disabled = " disabled='disabled'";
		}

		sb.appendHtmlConstant("<button type='button' class='" + cssClasses + "' tabindex='-1'" + disabled + ">");
		if (!StringUtils.isEmpty(icon))
		{
			String iconHtml = "<i class='" + Style.mdiLg(icon) + "'></i> ";
			sb.appendHtmlConstant(iconHtml);
		}
		if (data != null)
		{
			sb.append(data);
		}
		sb.appendHtmlConstant("</button>");
	}

}
