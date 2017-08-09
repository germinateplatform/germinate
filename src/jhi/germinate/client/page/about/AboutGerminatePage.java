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

package jhi.germinate.client.page.about;

import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.gwt.leaflet.client.basic.*;

/**
 * {@link AboutGerminatePage} is the page used for {@link Page#ABOUT_GERMINATE} which will show the {@link Text#aboutText(String, int)} as its content
 * and {@link Text#aboutAddress()} on the map.
 *
 * @author Sebastian Raubach
 */
public class AboutGerminatePage extends GerminateComposite implements ParallaxBannerPage
{
	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.LEAFLET, Library.LEAFLET_MINIMAP};
	}

	@Override
	protected void setUpContent()
	{
		PageHeader header = new PageHeader();
		header.setText(Text.LANG.aboutTitle());
		panel.add(header);
		String text = Text.LANG.aboutText(GerminateSettingsHolder.get().gatekeeperUrl.getValue(), Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date())));
		panel.add(new HTML(text));
		panel.add(HTMLUtils.createBR());

		Location l = new Location()
				.setLatitude(56.4567)
				.setLongitude(-3.0695)
				.setName(Text.LANG.aboutAddress());

		new LeafletUtils.IndividualMarkerCreator(panel, Collections.singletonList(l), (mapPanel, map) -> map.setView(LeafletLatLng.newInstance(56.4567, -3.0695), 5));
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxAboutGerminate();
	}
}
