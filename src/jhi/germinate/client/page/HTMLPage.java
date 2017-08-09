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

package jhi.germinate.client.page;

import com.google.gwt.core.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

/**
 * @author Sebastian Raubach
 */
public class HTMLPage extends Composite implements ParallaxBannerPage
{
	interface HTMLPageUiBinder extends UiBinder<HTMLPanel, HTMLPage>
	{
	}

	private static HTMLPageUiBinder ourUiBinder = GWT.create(HTMLPageUiBinder.class);

	@UiField
	PageHeader header;

	@UiField
	HTML content;

	public HTMLPage(String title, SafeHtml html)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		header.setText(title);
		content.setHTML(html);
	}

	public HTMLPage(String title, String html)
	{
		this(title, SafeHtmlUtils.fromTrustedString(html));
	}

	@Override
	public String getParallaxStyle()
	{
		return null;
	}
}