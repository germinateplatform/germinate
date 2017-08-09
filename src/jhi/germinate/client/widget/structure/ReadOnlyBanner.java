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

package jhi.germinate.client.widget.structure;

import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class ReadOnlyBanner
{
	public static void show()
	{
		RootPanel p = RootPanel.get(Id.STRUCTURE_READ_ONLY_BANNER);
		p.clear();
		p.add(new ParagraphPanel(Text.LANG.readOnlyBanner()));
		p.removeFromParent();
	}
}
