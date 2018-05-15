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

package jhi.germinate.client.page.about;

import com.google.gwt.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.gwt.leaflet.client.basic.*;

/**
 * {@link AboutGerminatePage} is the page used for {@link Page#ABOUT_GERMINATE} which will show the {@link Text#aboutText(String, int)} as its content
 * and {@link Text#aboutAddress()} on the map. It also shows links to related content along the bottom.
 *
 * @author Sebastian Raubach
 */
public class AboutGerminatePage extends GerminateComposite
{
	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.LEAFLET, Library.LEAFLET_MINIMAP, Library.HUTTON_BANNER};
	}

	@Override
	protected void setUpContent()
	{
		SimplePanel banner = new SimplePanel();
		panel.add(banner);
		jsniBanner(banner.getElement());

		PageHeader header = new PageHeader();
		header.setText(Text.LANG.aboutTitle());
		panel.add(header);

		Row row = new Row();
		panel.add(row);

		int i = 0;
		Column column = new Column(ColumnSize.XS_6, ColumnSize.SM_6, ColumnSize.LG_3);
		column.addStyleName(Style.COL_XXS_12);
		CategoryPanel cp = new CategoryPanel();
		cp.setText(Text.LANG.aboutButtonsHomepageTitle());
		cp.setIcon(Style.MDI_WEB);
		cp.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		cp.setAnchor(Text.LANG.aboutButtonsHomepageUrl());
		cp.getAnchor().setTarget("_blank");
		column.add(cp);
		row.add(column);

		column = new Column(ColumnSize.XS_6, ColumnSize.SM_6, ColumnSize.LG_3);
		column.addStyleName(Style.COL_XXS_12);
		cp = new CategoryPanel();
		cp.setText(Text.LANG.aboutButtonsGithubTitle());
		cp.setIcon(Style.MDI_GITHUB_CIRCLE);
		cp.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		cp.setAnchor(Text.LANG.aboutButtonsGithubUrl());
		cp.getAnchor().setTarget("_blank");
		column.add(cp);
		row.add(column);

		column = new Column(ColumnSize.XS_6, ColumnSize.SM_6, ColumnSize.LG_3);
		column.addStyleName(Style.COL_XXS_12);
		cp = new CategoryPanel();
		cp.setText(Text.LANG.aboutButtonsPublicationTitle());
		cp.setIcon(Style.MDI_FILE_DOCUMENT);
		cp.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		cp.setAnchor(Text.LANG.aboutButtonsPublicationUrl());
		cp.getAnchor().setTarget("_blank");
		column.add(cp);
		row.add(column);

		column = new Column(ColumnSize.XS_6, ColumnSize.SM_6, ColumnSize.LG_3);
		column.addStyleName(Style.COL_XXS_12);
		cp = new CategoryPanel();
		cp.setText(Text.LANG.aboutButtonsDocumentationTitle());
		cp.setIcon(Style.MDI_GLASSES);
		cp.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
		cp.setAnchor(Text.LANG.aboutButtonsDocumentationUrl());
		cp.getAnchor().setTarget("_blank");
		column.add(cp);
		row.add(column);

		panel.add(new HTML(Text.LANG.aboutText(GerminateSettingsHolder.get().gatekeeperUrl.getValue(), Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date())))));

		Location l = new Location()
				.setLatitude(56.4567)
				.setLongitude(-3.0695)
				.setName(Text.LANG.aboutAddress());

		new LeafletUtils.IndividualMarkerCreator(panel, Collections.singletonList(l), (mapPanel, map) -> map.setView(LeafletLatLng.newInstance(56.4567, -3.0695), 5));
	}

	private native void jsniBanner(Element element)/*-{
		$wnd.$(element).huttonBanner();
	}-*/;
}
