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

package jhi.germinate.client.page.image;

import com.google.gwt.core.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.gallery.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class ImagePage extends Composite
{
	interface ImagePageUiBinder extends UiBinder<HTMLPanel, ImagePage>
	{
	}

	private static ImagePageUiBinder ourUiBinder = GWT.create(ImagePageUiBinder.class);

	@UiField
	ImageTypeListBox imageTypeBox;
	@UiField(provided = true)
	Gallery          gallery;

	public ImagePage()
	{
		gallery = new Gallery(false, true)
		{
			@Override
			protected void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback)
			{
				if (imageTypeBox.getSelection() != null)
					ImageService.Inst.get().getForType(Cookie.getRequestProperties(), imageTypeBox.getSelection(), pagination, callback);
				else
					callback.onSuccess(new PaginatedServerResult<>(null, new ArrayList<>(), 0));
			}
		};

		initWidget(ourUiBinder.createAndBindUi(this));

		ImageService.Inst.get().getTypes(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<List<ImageType>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<ImageType>> result)
			{
				ImageType dummy = new ImageType(-1L)
						.setDescription(Text.LANG.galleryAllImageFiles());

				result.getServerResult().add(0, dummy);

				imageTypeBox.setValue(result.getServerResult().get(0), true);
				imageTypeBox.setAcceptableValues(result.getServerResult());

				gallery.refresh();
			}
		});
	}

	@UiHandler("imageTypeBox")
	void onImageTypeChanged(ValueChangeEvent<List<ImageType>> selection)
	{
		gallery.refresh();
	}
}