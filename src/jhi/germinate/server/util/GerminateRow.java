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

package jhi.germinate.server.util;

import java.util.*;

import jhi.germinate.shared.*;

/**
 * {@link GerminateRow} is a simple wrapper class for {@link LinkedHashMap}&lt;{@link String}, {@link String}&gt;
 *
 * @author Sebastian Raubach
 */
public class GerminateRow extends LinkedHashMap<String, String>
{
	private static final long serialVersionUID = -815247304197367465L;

	protected long id = RandomUtils.RANDOM.nextLong();

	public GerminateRow()
	{
	}

	public long getId()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (size() < 1)
			return id == ((GerminateRow) obj).id;
		else
			return super.equals(obj);
	}
}
