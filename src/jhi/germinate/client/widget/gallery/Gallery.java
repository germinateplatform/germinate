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

package jhi.germinate.client.widget.gallery;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.element.Pager;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public abstract class Gallery extends GerminateComposite
{
	private Pagination pagination = Pagination.getDefault();
	private List<Image> storedResult;

	private SimplePanel top = new SimplePanel();
	private Row         row = new Row();

	private int perPage;

	private Pager pager;

	private boolean showHeading = true;
	private boolean showButton  = false;
	private boolean setUp       = false;

	/**
	 * Creates a new instance of {@link Gallery} with the default number of items as defined by {@link GerminateSettings#galleryImagesPerPage}.
	 */
	public Gallery()
	{
		this(GerminateSettingsHolder.get().galleryImagesPerPage.getValue());
	}


	/**
	 * Creates a new instance of {@link Gallery} with the default number of items as defined by {@link GerminateSettings#galleryImagesPerPage}.
	 */
	public Gallery(boolean showHeading, boolean showButton)
	{
		this(GerminateSettingsHolder.get().galleryImagesPerPage.getValue());
		this.showHeading = showHeading;
		this.showButton = showButton;
	}

	/**
	 * Creates a new instance of {@link Gallery} with the given number of items per page.
	 *
	 * @param perPage The number of items per page
	 */
	public Gallery(int perPage)
	{
		this.perPage = perPage;
	}

	@Override
	public Library[] getLibraryList()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		panel.getElement().setId("gallery-" + RandomUtils.generateRandomId());

		if (showHeading)
		{
			PageHeader header = new PageHeader();
			header.getElement().getStyle().setTextTransform(Style.TextTransform.CAPITALIZE);
			header.setText(Text.LANG.menuGallery());
			panel.add(header);
		}
		panel.add(top);
		panel.add(row);

		requestImages(0);
	}

	public void refresh()
	{
		requestImages(0);
	}

	/**
	 * Request the next chunk of images from the server
	 *
	 * @param start The start position of the new chunk
	 */
	private void requestImages(int start)
	{
		storedResult = null;
		getData(pagination.update(start, perPage), new DefaultAsyncCallback<PaginatedServerResult<List<Image>>>()
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				if (caught instanceof DatabaseException)
				{
					row.clear();
					top.clear();
					top.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoInformationFound()));
				}
				else
					super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(PaginatedServerResult<List<Image>> result)
			{
				top.clear();
				row.clear();

				if (!CollectionUtils.isEmpty(result.getServerResult()))
				{
					/* Update the pagination object */
					pagination.setResultSize(result.getResultSize());

					/* If there are more images than there is space on one page,
					 * add a pager */
					if (pager == null && pagination.getResultSize() > perPage)
					{
						/* Add a pager to navigate the gallery */
						pager = new Pager(perPage, pagination.getResultSize());
						pager.setPagerClickHandler((button, currentPosition) -> requestImages(currentPosition));
						panel.add(pager);
					}


					storedResult = result.getServerResult();
					fillContent();
				}
				else
				{
					top.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoInformationFound()));
				}
			}
		});
	}

	private void fillContent()
	{
		if (storedResult == null || storedResult.size() < 1)
			return;

		top.clear();
		row.clear();

        /* Create a GalleryItem for each image */
		int counter = 0;
		for (Image image : storedResult)
		{
			GalleryItem item = new GalleryItem(image, showButton);
			item.setSize(ColumnSize.XS_12, ColumnSize.SM_4, ColumnSize.MD_3, ColumnSize.LG_2);
			row.add(item);

			counter++;
			ClearFix fix = new ClearFix();
			fix.addStyleName(Responsiveness.VISIBLE_XS.getCssName());
			if (counter % 3 == 0)
				fix.addStyleName(Responsiveness.VISIBLE_SM.getCssName());
			if (counter % 4 == 0)
				fix.addStyleName(Responsiveness.VISIBLE_MD.getCssName());
			if (counter % 6 == 0)
				fix.addStyleName(Responsiveness.VISIBLE_LG.getCssName());

			row.add(fix);
		}

		jsniRun(panel.getElement().getId());
		setUp = true;
	}

	@Override
	protected void onUnload()
	{
		if (setUp)
			jsniDestroy();
		super.onUnload();
	}

	private native void jsniRun(String id)/*-{
		$wnd.baguetteBox.run('#' + id + ' .row', {'captions': 'true'});
	}-*/;

	private native void jsniDestroy()/*-{
		$wnd.baguetteBox.destroy();
	}-*/;

	/**
	 * This method will be called to get the next chunk of images from the server. The chunk starts from {@code start} and contains at most {@code
	 * length} images.
	 *
	 * @param pagination The {@link jhi.germinate.shared.datastructure.Pagination} with the start index of the requested chunk and the length of the
	 *                   requested chunk.
	 * @param callback   The callback that will take care of the server result.
	 */
	protected abstract void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback);
}
