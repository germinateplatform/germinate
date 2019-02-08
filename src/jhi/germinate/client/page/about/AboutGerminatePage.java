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

package jhi.germinate.client.page.about;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.gwt.leaflet.client.basic.*;

/**
 * {@link AboutGerminatePage} is the page used for {@link Page#ABOUT_GERMINATE} which will show the {@link Text#aboutText(String, int)} as its content
 * and {@link Text#aboutAddress()} on the map. It also shows links to related content along the bottom.
 *
 * @author Sebastian Raubach
 */
public class AboutGerminatePage extends Composite implements HasLibraries
{
	private static AboutGerminatePageUiBinder ourUiBinder = GWT.create(AboutGerminatePageUiBinder.class);
	@UiField
	SimplePanel   banner;
	@UiField
	CategoryPanel homepage;
	@UiField
	CategoryPanel github;
	@UiField
	CategoryPanel publication;
	@UiField
	CategoryPanel documentation;
	@UiField
	HTML          content;
	@UiField
	SimplePanel   map;

	public AboutGerminatePage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		jsniBanner(banner.getElement());

		int i = 0;
		homepage.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		homepage.getAnchor().setTarget("_blank");
		github.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		github.getAnchor().setTarget("_blank");
		publication.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		publication.getAnchor().setTarget("_blank");
		documentation.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		documentation.getAnchor().setTarget("_blank");

		content.setHTML(Text.LANG.aboutText(GerminateSettingsHolder.get().gatekeeperUrl.getValue(), Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date()))));

		Location l = new Location()
				.setLatitude(56.4567)
				.setLongitude(-3.0695)
				.setName(Text.LANG.aboutAddress());

		new LeafletUtils.IndividualMarkerCreator(map, Collections.singletonList(l), (mapPanel, map) -> map.setView(LeafletLatLng.newInstance(56.4567, -3.0695), 5));
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.LEAFLET, Library.LEAFLET_MINIMAP, Library.HUTTON_BANNER};
	}

	private native void jsniBanner(Element element)/*-{
		$wnd.$(element).huttonBanner();
	}-*/;

	interface AboutGerminatePageUiBinder extends UiBinder<FlowPanel, AboutGerminatePage>
	{
	}
}