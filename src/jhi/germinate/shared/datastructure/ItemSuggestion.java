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

package jhi.germinate.shared.datastructure;

import java.io.*;

/**
 * {@link ItemSuggestion} can be used to transfer data used in {@link com.google.gwt.user.client.ui.SuggestBox}es.
 *
 * @author Sebastian Raubach
 */
public class ItemSuggestion implements Serializable
{
	private static final long serialVersionUID = -4671559182761408790L;

	private Long   id;
	private String name;

	public ItemSuggestion()
	{
	}

	public ItemSuggestion(Long id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public Long getId()
	{
		return id;
	}

	public String getDisplayString()
	{
		return name;
	}

	public String getReplacementString()
	{
		return name;
	}
}
