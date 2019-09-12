/*
 *  Copyright 2019 Information and Computational Sciences,
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

package jhi.germinate.util;

import org.apache.tools.ant.*;

/**
 * @author Sebastian Raubach
 */
public class GetCoresCount extends Task
{
	private String outputProperty;

	public void execute()
	{
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (availableProcessors > 1)
		{
			availableProcessors = (int) Math.floor(availableProcessors / 2);
		}
		getProject().setNewProperty(outputProperty, Integer.toString(availableProcessors));
	}

	public void setOutputProperty(String prop)
	{
		outputProperty = prop;
	}
}
