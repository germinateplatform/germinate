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

package jhi.germinate.client.widget.table;

import jhi.germinate.shared.datastructure.database.*;

/**
 * {@link TableMouseHoverHandler} is an interface that can be used to react to hover events on the table. <p/> <p/> A {@link TableMouseHoverHandler}
 * can be added to {@link jhi.germinate.client.widget.table.pagination.DatabaseObjectPaginationTable}s.
 *
 * @author Sebastian Raubach
 */
public interface TableMouseHoverHandler<T extends DatabaseObject>
{
	/**
	 * Called when the JS event "mouseover" is fired on a table row
	 *
	 * @param row The {@link DatabaseObject} the mouse is hovering over
	 */
	void onMouseOverRow(T row);

	/**
	 * Called when the JS event "mouseout" is fired on a table row
	 *
	 * @param row The {@link DatabaseObject} the mouse is no longer hovering over
	 */
	void onMouseOutRow(T row);

	/**
	 * Called when the JS event "mouseover" is fired on the table
	 */
	void onMouseOverTable();

	/**
	 * Called when the JS event "mouseout" is fired on the table
	 */
	void onMouseOutTable();
}