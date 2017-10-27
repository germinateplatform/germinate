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

import com.google.gwt.core.shared.*;

import java.io.*;
import java.util.*;
import java.util.Map;

import jhi.germinate.client.i18n.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * {@link TrialsRow} is a simple wrapper class for {@link Map}&lt;String, {@link TrialsCell}&gt;
 *
 * @author Sebastian Raubach
 */
public class TrialsRow implements Serializable
{
	private static final long serialVersionUID = 5399469724834737633L;

	private Gradient  gradient;
	private Phenotype phenotype;
	private Map<String, TrialsCell> yearsToValues = new HashMap<>();

	public Phenotype getPhenotype()
	{
		return phenotype;
	}

	public void setPhenotype(Phenotype phenotype)
	{
		this.phenotype = phenotype;
	}

	public Gradient getGradient()
	{
		return gradient;
	}

	public void setGradient(Gradient gradient)
	{
		this.gradient = gradient;
	}

	public void addCell(String year, TrialsCell cell)
	{
		yearsToValues.put(year, cell);
	}

	public Map<String, TrialsCell> getYearToValues()
	{
		return yearsToValues;
	}

	public enum TrialsAttribute implements Serializable
	{
		MIN,
		MAX,
		AVG,
		COUNT;

		public String getI18nName()
		{
			if (GWT.isClient())
			{
				switch (this)
				{
					case MIN:
						return Text.LANG.generalMinimum();
					case MAX:
						return Text.LANG.generalMaximum();
					case AVG:
						return Text.LANG.generalAverage();
					case COUNT:
						return Text.LANG.generalCount();
				}
			}

			return "";
		}
	}

	public static class TrialsCell implements Serializable
	{
		private static final long serialVersionUID = -8092860182098589073L;

		private long  count = 0;
		private float min   = Float.MAX_VALUE;
		private float max   = -Float.MAX_VALUE;
		private float avg   = 0;
		private String maxAccessionId;
		private String minAccessionId;
		private String maxAccessionName;
		private String minAccessionName;
		private String description;

		public TrialsCell()
		{
		}

		public TrialsCell(String description)
		{
			this.description = description;
		}

		public TrialsCell(String count, String min, String max, String avg, String maxAccessionId, String maxAccessionName, String minAccessionId, String minAccessionName)
		{
			super();
			this.count = Long.parseLong(count);
			this.min = Float.parseFloat(min);
			this.max = Float.parseFloat(max);
			this.avg = Float.parseFloat(avg);
			this.maxAccessionId = maxAccessionId;
			this.maxAccessionName = maxAccessionName;
			this.minAccessionId = minAccessionId;
			this.minAccessionName = minAccessionName;
		}

		public TrialsCell(long count, float min, float max, float avg, String maxAccessionId, String maxAccessionName, String minAccessionId, String minAccessionName)
		{
			super();
			this.count = count;
			this.min = min;
			this.max = max;
			this.avg = avg;
			this.maxAccessionId = maxAccessionId;
			this.maxAccessionName = maxAccessionName;
			this.minAccessionId = minAccessionId;
			this.minAccessionName = minAccessionName;
		}

		public void add(String newValue, String accessionId, String accessionName)
		{
			try
			{
				float value = Float.parseFloat(newValue);

				count++;

				if (value < min)
				{
					min = value;
					minAccessionId = accessionId;
					minAccessionName = accessionName;
				}
				if (value > max)
				{
					max = value;
					maxAccessionId = accessionId;
					maxAccessionName = accessionName;
				}

				avg = avg + ((value - avg) / count);

			}
			catch (Exception e)
			{
			}
		}

		public long getCount()
		{
			return count;
		}

		public float getMin()
		{
			return min;
		}

		public float getMax()
		{
			return max;
		}

		public float getAvg()
		{
			return avg;
		}

		public String getMaxAccessionId()
		{
			return maxAccessionId;
		}

		public String getMinAccessionId()
		{
			return minAccessionId;
		}

		public String getDescription()
		{
			return description;
		}

		public String getMaxAccessionName()
		{
			return maxAccessionName;
		}

		public void setMaxAccessionName(String maxAccessionName)
		{
			this.maxAccessionName = maxAccessionName;
		}

		public String getMinAccessionName()
		{
			return minAccessionName;
		}

		public void setMinAccessionName(String minAccessionName)
		{
			this.minAccessionName = minAccessionName;
		}

		public String getAttribute(TrialsAttribute trialsAttribute)
		{
			switch (trialsAttribute)
			{
				case COUNT:
					return Long.toString(count);
				case MIN:
					return Float.toString(min);
				case AVG:
					return Float.toString(avg);
				case MAX:
					return Float.toString(max);
				default:
					return null;
			}
		}
	}
}
