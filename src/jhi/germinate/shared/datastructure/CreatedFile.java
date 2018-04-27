/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.shared.datastructure;

import com.google.gwt.core.shared.*;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class CreatedFile implements Serializable
{
	private String name;
	private Long   size;

	public CreatedFile()
	{
	}

	public CreatedFile(String name, Long size)
	{
		this.name = name;
		this.size = size;
	}

	@GwtIncompatible
	public CreatedFile(File input)
	{
		this.name = input.getName();
		this.size = input.length();
	}

	public String getName()
	{
		return name;
	}

	public CreatedFile setName(String name)
	{
		this.name = name;
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public CreatedFile setSize(Long size)
	{
		this.size = size;
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CreatedFile that = (CreatedFile) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode()
	{

		return Objects.hash(name);
	}
}
