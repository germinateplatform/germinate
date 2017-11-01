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

import com.google.gwt.dom.client.*;

import org.gwtbootstrap3.client.ui.*;

import jhi.germinate.shared.Style;

/**
 * @author Sebastian Raubach
 */
public class MdiTabListItem extends TabListItem
{
	public MdiTabListItem()
	{
	}

	public MdiTabListItem(String text)
	{
		super(text);
	}

	public void setMdi(String style)
	{
		SpanElement span = Document.get().createSpanElement();
		span.getStyle().setMarginRight(5, com.google.gwt.dom.client.Style.Unit.PX);
		span.addClassName(Style.mdi(style));
		anchor.getElement().insertFirst(span);
	}
}
