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

package jhi.germinate.client.widget.element;

import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;

/**
 * @author Sebastian Raubach
 */
public class MdiHeading extends Heading
{
	private String mdi;
	private Span text = new Span();

	/**
	 * Creates a Heading with the passed in size.
	 *
	 * @param size size of the heading
	 */
	@UiConstructor
	public MdiHeading(final HeadingSize size) {
		super(size);
	}

	/**
	 * Creates a Heading with the passed in size and text.
	 *
	 * @param size size of the heading
	 * @param text text for the heading
	 */
	public MdiHeading(final HeadingSize size, final String text) {
		this(size);
		setText(text);
	}

	/**
	 * Creates a Heading with the passed in size and text.
	 *
	 * @param size    size of the heading
	 * @param text    text for the heading
	 * @param subText subtext for the heading
	 */
	public MdiHeading(final HeadingSize size, final String text, final String subText) {
		this(size, text);
		setSubText(subText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return text.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(final String text) {
		this.text.setText(text);
		insert(this.text, 0);
	}

	public String getMdi()
	{
		return mdi;
	}

	public void setMdi(String mdi)
	{
		this.mdi = mdi;
	}

	@Override
	protected void onAttach()
	{
		super.onAttach();

		if(!StringUtils.isEmpty(mdi))
		{
			Element i = Document.get().createElement("i");
			i.addClassName(Style.combine(Style.MDI, Style.FA_FIXED_WIDTH, mdi));
			i.getStyle().setMarginLeft(6, com.google.gwt.dom.client.Style.Unit.PX);
			i.getStyle().setMarginRight(6, com.google.gwt.dom.client.Style.Unit.PX);
			i.addClassName(Style.LAYOUT_V_ALIGN_MIDDLE);
			i.addClassName(Emphasis.PRIMARY.getCssName());
			text.getElement().addClassName(Style.LAYOUT_V_ALIGN_MIDDLE);
			getElement().insertAfter(i, text.getElement());
		}
	}
}
