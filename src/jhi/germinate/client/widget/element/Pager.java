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

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;

import jhi.germinate.client.i18n.*;

/**
 * {@link Pager} is a class used to show a pager similar to {@link SimplePanel}. This pager can be used for any purpose.
 *
 * @author Sebastian Raubach
 */
public class Pager extends Composite
{
	private static final PagerUiBinder uiBinder = GWT.create(PagerUiBinder.class);

	interface PagerUiBinder extends UiBinder<Widget, Pager>
	{
	}

	private PagerClickHandler handler = null;

	private int position  = 0;
	private int pageSize  = 0;
	private int nrOfItems = 0;

	@UiField
	Button firstPage;

	@UiField
	Button prevPage;

	@UiField
	Button nextPage;

	@UiField
	Button lastPage;

	@UiField
	Button pagerText;

	public enum PagerButton
	{
		FIRST,
		PREV,
		NEXT,
		LAST
	}

	public Pager()
	{
		this(0, 0);
	}

	/**
	 * Creates a new Pager with the given click handler, page size and total number of items
	 *
	 * @param handler   The click handler
	 * @param pageSize  The page size
	 * @param nrOfItems The total number of items
	 */
	public Pager(PagerClickHandler handler, int pageSize, int nrOfItems)
	{
		this(pageSize, nrOfItems);
		this.handler = handler;
	}

	/**
	 * Creates a new Pager with the given page size and the total number of items
	 *
	 * @param pageSize  The page size
	 * @param nrOfItems The total number of items
	 */
	public Pager(final int pageSize, final int nrOfItems)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.pageSize = pageSize;
		this.nrOfItems = nrOfItems;
	}

	public void update(final int pageSize, final int nrOfItems)
	{
		this.pageSize = pageSize;
		this.nrOfItems = nrOfItems;

		onLoad();
	}

	public void jumpToPosition(int newPosition)
	{
		if (newPosition >= nrOfItems || newPosition < 0)
			return;

		position = (int) Math.floor(newPosition / pageSize) * pageSize;

        /*
		 * If there aren't pages to the "left" of the current page, disable
         * first and prev button
         */
		if (position - pageSize < 0)
		{
			setEnabled(PagerButton.FIRST, false);
			setEnabled(PagerButton.PREV, false);
		}
		/* If there are more pages, enable last and next button */
		if (nrOfItems > pageSize)
		{
			setEnabled(PagerButton.LAST, true);
			setEnabled(PagerButton.NEXT, true);
		}
		/* If there are no more pages left, disable last and next button */
		if (position + pageSize >= nrOfItems)
		{
			setEnabled(PagerButton.LAST, false);
			setEnabled(PagerButton.NEXT, false);
		}
		/* If there are previous pages, enable first and prev button */
		if (position > 0)
		{
			setEnabled(PagerButton.FIRST, true);
			setEnabled(PagerButton.PREV, true);
		}

		/* Update the page text with the current selection */
		updatePagerText();

        /* Notify the handler */
		if (handler != null)
			handler.onButtonClicked(PagerButton.FIRST, position);
	}

	@UiHandler("firstPage")
	void onFirstPageClicked(ClickEvent event)
	{
		/* Set position to first page and enable first and prev button */
		position = 0;
		setEnabled(PagerButton.FIRST, false);
		setEnabled(PagerButton.PREV, false);

		/* If there is more than one page, enable last and next buttons */
		if (nrOfItems > pageSize)
		{
			setEnabled(PagerButton.LAST, true);
			setEnabled(PagerButton.NEXT, true);
		}

		/* Update the page text with the current selection */
		updatePagerText();

		/* Notify the handler */
		if (handler != null)
			handler.onButtonClicked(PagerButton.FIRST, position);
	}

	@UiHandler("prevPage")
	void onPrevPageClicked(ClickEvent event)
	{
		/* Adjust the current position */
		if (position - pageSize < 0)
		{
			position = 0;
		}
		else
		{
			position -= pageSize;
		}

		/*
		 * If there aren't pages to the "left" of the current page,
		 * disable first and prev button
		 */
		if (position - pageSize < 0)
		{
			setEnabled(PagerButton.FIRST, false);
			setEnabled(PagerButton.PREV, false);
		}
		/* If there are more pages, enable last and next button */
		if (nrOfItems > pageSize)
		{
			setEnabled(PagerButton.LAST, true);
			setEnabled(PagerButton.NEXT, true);
		}

		/* Update the page text with the current selection */
		updatePagerText();

		/* Notify the handler */
		if (handler != null)
			handler.onButtonClicked(PagerButton.PREV, position);
	}

	@UiHandler("nextPage")
	void onNextPageClicked(ClickEvent event)
	{
		/* Adjust the current position */
		if (position + pageSize < nrOfItems)
		{
			position += pageSize;
		}

		/* If there are no more pages left, disable last and next button */
		if (position + pageSize >= nrOfItems)
		{
			setEnabled(PagerButton.LAST, false);
			setEnabled(PagerButton.NEXT, false);
		}
		/* If there are previous pages, enable first and prev button */
		if (position > 0)
		{
			setEnabled(PagerButton.FIRST, true);
			setEnabled(PagerButton.PREV, true);
		}

		/* Update the page text with the current selection */
		updatePagerText();

		/* Notify the handler */
		if (handler != null)
			handler.onButtonClicked(PagerButton.NEXT, position);
	}

	@UiHandler("lastPage")
	void onLastPageClicked(ClickEvent event)
	{
		/* Adjust the current position */
		position = (int) (Math.ceil(nrOfItems / (1.0 * pageSize))) * pageSize - pageSize;

		/* Disable last and next page button */
		setEnabled(PagerButton.LAST, false);
		setEnabled(PagerButton.NEXT, false);

		/* If there are previous pages, enable first and prev buttons */
		if (position > 0)
		{
			setEnabled(PagerButton.FIRST, true);
			setEnabled(PagerButton.PREV, true);
		}

		/* Update the page text with the current selection */
		updatePagerText();

		/* Notify the handler */
		if (handler != null)
			handler.onButtonClicked(PagerButton.LAST, position);
	}

	@Override
	public void onLoad()
	{
		updatePagerText();

        /* Initially, disable first and prev buttons */
		setEnabled(PagerButton.FIRST, position >= pageSize);
		setEnabled(PagerButton.PREV, position >= pageSize);

        /* Set state of next and last button based on number of items */
		setEnabled(PagerButton.NEXT, position + pageSize < nrOfItems);
		setEnabled(PagerButton.LAST, position + pageSize < nrOfItems);
	}

	/**
	 * Updates the pager text based on the current position and total number of items
	 */
	private void updatePagerText()
	{
		/* Get the localized number formatter */
		NumberFormat formatter = NumberFormat.getFormat(Text.LANG.pagerNumberFormat());

        /* Determine lower and upper bound */
		int lower = position + 1;
		int upper = Math.min(position + pageSize, nrOfItems);

        /* Use the internationalized text */
		String text;

		if (lower == upper)
			text = formatter.format(lower) + Text.LANG.pagerOf() + formatter.format(nrOfItems);
		else
			text = formatter.format(lower) + "-" + formatter.format(upper) + Text.LANG.pagerOf() + formatter.format(nrOfItems);

		pagerText.setText(text);
	}

	/**
	 * Enables/Disables the given pager button
	 *
	 * @param button  The button to enable/disable
	 * @param enabled Enable the button?
	 */
	public void setEnabled(PagerButton button, boolean enabled)
	{
		switch (button)
		{
			case FIRST:
				firstPage.setEnabled(enabled);
				break;
			case PREV:
				prevPage.setEnabled(enabled);
				break;
			case NEXT:
				nextPage.setEnabled(enabled);
				break;
			case LAST:
				lastPage.setEnabled(enabled);
				break;
		}
	}

	/**
	 * Sets the click handler to the given one
	 *
	 * @param handler The click handler to use
	 */
	public void setPagerClickHandler(PagerClickHandler handler)
	{
		this.handler = handler;
	}

	public interface PagerClickHandler
	{
		/**
		 * Will be called when a pager button is pressed
		 *
		 * @param button The button that was pressed on
		 */
		void onButtonClicked(PagerButton button, int currentPosition);
	}
}
