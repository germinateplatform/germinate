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

package jhi.germinate.shared.datastructure;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.Tuple.*;

/**
 * Simple bean class holding information for the map export feature.
 *
 * @author Sebastian Raubach
 */
public class MapExportOptions implements Serializable
{
	private static final long serialVersionUID = 7913244334289693428L;

	private Map<String, List<Region>> regions;
	private Pair<String, String>      interval;
	private Pair<String, Region>      radius;
	private List<String>              chromosomes;

	public MapExportOptions()
	{
	}

	public Map<String, List<Region>> getRegions()
	{
		return regions;
	}

	public void setRegions(Map<String, List<Region>> regions)
	{
		this.regions = regions;
	}

	public Pair<String, String> getInterval()
	{
		return interval;
	}

	public void setInterval(Pair<String, String> interval)
	{
		this.interval = interval;
	}

	public Pair<String, Region> getRadius()
	{
		return radius;
	}

	public void setRadius(Pair<String, Region> radius)
	{
		this.radius = radius;
	}

	public List<String> getChromosomes()
	{
		return chromosomes;
	}

	public void setChromosomes(List<String> chromosomes)
	{
		this.chromosomes = chromosomes;
	}
}
