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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.user.cellview.client.*;

/**
 * @author Sebastian Raubach
 */
public abstract class MultiPageBooleanHeader extends Header<Boolean>
{
	private int size = 0;

	/**
	 * Construct a Header with a given {@link Cell}.
	 *
	 * @param cell the {@link Cell} responsible for rendering menus in the header
	 */
	public MultiPageBooleanHeader(Cell<Boolean> cell)
	{
		super(cell);
	}

	public void setValue(int size)
	{
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}
}
