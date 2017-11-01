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

import org.gwtbootstrap3.client.ui.base.*;

import jhi.germinate.shared.*;

public class MdiAnchorListItem extends AbstractAnchorListItem implements com.google.gwt.user.client.ui.HasText {

    public MdiAnchorListItem() {
    }

    public MdiAnchorListItem(final String text) {
        setText(text);
    }

    @Override
    public String getText() {
        return anchor.getText();
    }

    @Override
    public void setText(final String text) {
        anchor.setText(text);
    }

    public void setMdi(String style)
    {
        anchor.addStyleName(Style.mdiLg(style));
    }
}
