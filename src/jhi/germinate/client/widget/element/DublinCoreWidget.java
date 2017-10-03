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

import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.autobean.shared.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class DublinCoreWidget extends Composite
{
	public DublinCoreWidget(String dublinCore)
	{
		FlowPanel panel = new FlowPanel();

		panel.add(new Heading(HeadingSize.H3, Text.LANG.dublinCoreHeader()));

		DublinCore core = AutoBeanCodex.decode(DublinCore.DublinCoreFactory.Inst.get(), DublinCore.class, dublinCore).as();

		add(panel, Text.LANG.dublinCoreTitle(), core.getTitle());
		add(panel, Text.LANG.dublinCoreSubject(), core.getSubject());
		add(panel, Text.LANG.dublinCoreDescription(), core.getDescription());
		add(panel, Text.LANG.dublinCoreType(), core.getType());
		add(panel, Text.LANG.dublinCoreSource(), core.getSource());
		add(panel, Text.LANG.dublinCoreRelation(), core.getRelation());
		add(panel, Text.LANG.dublinCoreCoverage(), core.getCoverage());
		add(panel, Text.LANG.dublinCoreCreator(), core.getCreator());
		add(panel, Text.LANG.dublinCorePublisher(), core.getPublisher());
		add(panel, Text.LANG.dublinCoreContributor(), core.getContributor());
		add(panel, Text.LANG.dublinCoreRights(), core.getRights());
		add(panel, Text.LANG.dublinCoreDate(), core.getDate());
		add(panel, Text.LANG.dublinCoreFormat(), core.getFormat());
		add(panel, Text.LANG.dublinCoreIdentifier(), core.getIdentifier());
		add(panel, Text.LANG.dublinCoreLanguage(), core.getLanguage());

		initWidget(panel);
	}

	private void add(FlowPanel panel, String title, List<String> values)
	{
		if (!CollectionUtils.isEmpty(values))
		{
			for (String value : values)
				panel.add(new DescriptionWidget(panel, title, value));
		}
	}
}
