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

package jhi.germinate.client.widget.table;

import com.google.gwt.user.client.ui.*;

import jhi.germinate.shared.datastructure.database.*;

/**
 * {@link TableTooltipHandler} is an interface for adding tooltips to {@link jhi.germinate.client.widget.table.pagination.DatabaseObjectPaginationTable}s
 * used to show {@link DatabaseObject}s. Implementing classes have to implement {@link #getAlignment()} to set the alignment of the tooltip and {@link
 * #getTooltipContent(T, int)}  to get the actual content to show in the tooltip.
 *
 * @author Sebastian Raubach
 */
public interface TableTooltipHandler<T extends DatabaseObject>
{
	/**
	 * Returns the alignment of the tooltip with the table cell as a reference
	 *
	 * @return The alignment of the tooltip with the table cell as a reference
	 */
	Alignment getAlignment();

	/**
	 * Returns the content of the tooltip to display. Returning <code>null</code> will result in no tooltip to be displayed (=> tooltip just for
	 * specific columns possible)
	 *
	 * @param row         The row the tooltip is for
	 * @param columnIndex The column of that row that we are currently hovering over
	 * @return The content of the tooltip to display
	 */
	IsWidget getTooltipContent(T row, int columnIndex);

	/**
	 * {@link Alignment}s are used to specify how the tooltip aligns with respect to the element it is aligned to.
	 *
	 * @author Sebastian Raubach
	 */
	enum Alignment
	{
		/** The tooltip will be aligned on top of the reference with its left aligned to the reference's left */
		ON_TOP_ALIGN_LEFT,
		/** The tooltip will be aligned on top of the reference with its right aligned to the reference's right */
		ON_TOP_ALIGN_RIGHT,
		/** The tooltip will be aligned below the reference with its left aligned to the reference's left */
		BELOW_ALIGN_LEFT,
		/** The tooltip will be aligned below the reference with its right aligned to the reference's right */
		BELOW_ALIGN_RIGHT,
		/** The tooltip will be aligned to the left of the reference with its top aligned to the reference's top */
		ON_LEFT_ALIGN_TOP,
		/** The tooltip will be aligned to the left of the reference with its bottom aligned to the reference's bottom */
		ON_LEFT_ALIGN_BOTTOM,
		/** The tooltip will be aligned to the right of the reference with its top aligned to the reference's top */
		ON_RIGHT_ALIGN_TOP,
		/** The tooltip will be aligned to the right of the reference with its bottom aligned to the reference's bottom */
		ON_RIGHT_ALIGN_BOTTOM;

		/**
		 * Returns the top coordinate of the tooltip
		 *
		 * @param topOfReference    The top of the element the tooltip is attached to
		 * @param bottomOfReference The bottom of the element the tooltip is attached to
		 * @param height            The height of the tooltip
		 * @return The top coordinate of the tooltip
		 */
		public int getTop(int topOfReference, int bottomOfReference, int height)
		{
			switch (this)
			{
				case ON_TOP_ALIGN_LEFT:
				case ON_TOP_ALIGN_RIGHT:
					return topOfReference - height;
				case BELOW_ALIGN_LEFT:
				case BELOW_ALIGN_RIGHT:
					return bottomOfReference;
				case ON_LEFT_ALIGN_TOP:
				case ON_RIGHT_ALIGN_TOP:
					return topOfReference;
				case ON_LEFT_ALIGN_BOTTOM:
				case ON_RIGHT_ALIGN_BOTTOM:
					return bottomOfReference - height;
			}

			return bottomOfReference;
		}

		/**
		 * Returns the left coordinate of the tooltip
		 *
		 * @param leftOfReference  The left of the element the tooltip is attached to
		 * @param rightOfReference The right of the element the tooltip is attached to
		 * @param width            The width of the tooltip
		 * @return The left coordinate of the tooltip
		 */
		public int getLeft(int leftOfReference, int rightOfReference, int width)
		{
			switch (this)
			{
				case ON_TOP_ALIGN_LEFT:
				case BELOW_ALIGN_LEFT:
					return leftOfReference;
				case ON_TOP_ALIGN_RIGHT:
				case BELOW_ALIGN_RIGHT:
					return rightOfReference - width;
				case ON_LEFT_ALIGN_TOP:
				case ON_LEFT_ALIGN_BOTTOM:
					return leftOfReference - width;
				case ON_RIGHT_ALIGN_TOP:
				case ON_RIGHT_ALIGN_BOTTOM:
					return rightOfReference;
			}

			return leftOfReference;
		}
	}
}