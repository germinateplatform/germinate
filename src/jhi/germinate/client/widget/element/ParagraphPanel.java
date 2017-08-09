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

package jhi.germinate.client.widget.element;

import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class ParagraphPanel extends SimplePanel implements HasText
{
	public ParagraphPanel()
	{
		super(Document.get().createPElement());
	}

	public ParagraphPanel(String paragraph)
	{
		this();
		setText(paragraph);
	}

	@Override
	public String getText()
	{
		return getElement().getInnerText();
	}

	@Override
	public void setText(String text)
	{
		getElement().setInnerText(text);
	}
}
