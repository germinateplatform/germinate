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

import java.io.*;
import java.util.*;

import jhi.germinate.server.service.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class PedigreeWriter
{
	private Set<String>                                         visitedIds = new HashSet<>();
	private Set<Edge>                                           edges      = new HashSet<>();
	private BufferedWriter                                      bw;
	private Map<String, List<PedigreeServiceImpl.PedigreePair>> data;
	private boolean                                             up;
	private int                                                 maxLevels;

	public PedigreeWriter(BufferedWriter bw, Map<String, List<PedigreeServiceImpl.PedigreePair>> data, boolean up, int maxLevels)
	{
		this.bw = bw;
		this.data = data;
		this.up = up;
		this.maxLevels = maxLevels;
	}

	public void run(String accessionId, int currentLevel) throws IOException
	{
		if (!visitedIds.contains(accessionId))
		{
			visitedIds.add(accessionId);

			List<PedigreeServiceImpl.PedigreePair> objects = data.get(accessionId);

			if (!CollectionUtils.isEmpty(objects))
			{
				for (PedigreeServiceImpl.PedigreePair object : objects)
				{
					Edge edge;
					if (up)
						edge = new Edge(accessionId, object);
					else
						edge = new Edge(object, accessionId);

					if (!edges.contains(edge))
					{
						edges.add(edge);
						bw.newLine();
						bw.write(edge.source.name + "\t" + edge.target.name + "\t" + edge.target.type);

						if (currentLevel < maxLevels)
							run(up ? edge.target.name : edge.source.name, currentLevel + 1);
					}
				}
			}
		}
	}

	private static class Edge
	{
		PedigreeServiceImpl.PedigreePair source;
		PedigreeServiceImpl.PedigreePair target;

		public Edge(String source, PedigreeServiceImpl.PedigreePair target)
		{
			this.source = new PedigreeServiceImpl.PedigreePair(source, target.type);
			this.target = target;
		}

		public Edge(PedigreeServiceImpl.PedigreePair source, String target)
		{
			this.source = source;
			this.target = new PedigreeServiceImpl.PedigreePair(target, source.type);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Edge edge = (Edge) o;

			if (!source.equals(edge.source)) return false;
			return target.equals(edge.target);
		}

		@Override
		public int hashCode()
		{
			int result = source.hashCode();
			result = 31 * result + target.hashCode();
			return result;
		}
	}

}
