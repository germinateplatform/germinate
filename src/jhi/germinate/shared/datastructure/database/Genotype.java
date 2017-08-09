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

package jhi.germinate.shared.datastructure.database;

import com.google.gwt.core.shared.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;


/**
 * @author Sebastian Raubach
 */
public class Genotype extends DatabaseObject
{
	private static final long serialVersionUID = -5317875286238907353L;

	public static final String ID               = "genotypes.id";
	public static final String MARKER_ID        = "genotypes.marker_id";
	public static final String DATASET_ID       = "genotypes.dataset_id";
	public static final String GERMINATEBASE_ID = "genotypes.germinatebase_id";
	public static final String SAMPLE_NAME      = "genotypes.sample_name";
	public static final String ALLELE_1         = "genotypes.allele1";
	public static final String ALLELE_2         = "genotypes.allele2";
	public static final String X                = "genotypes.x";
	public static final String Y                = "genotypes.y";

	private Marker    marker;
	private Dataset   dataset;
	private Accession accession;
	private String    sampleName;
	private String    alleleOne;
	private String    alleleTwo;
	private Double    x;
	private Double    y;

	public Genotype()
	{
	}

	public Genotype(Long id)
	{
		super(id);
	}

	public Genotype(Long id, Marker marker, Dataset dataset, Accession accession, String sampleName, String alleleOne, String alleleTwo, Double x, Double y)
	{
		super(id);
		this.marker = marker;
		this.dataset = dataset;
		this.accession = accession;
		this.sampleName = sampleName;
		this.alleleOne = alleleOne;
		this.alleleTwo = alleleTwo;
		this.x = x;
		this.y = y;
	}

	public Marker getMarker()
	{
		return marker;
	}

	public Genotype setMarker(Marker marker)
	{
		this.marker = marker;
		return this;
	}

	public Dataset getDataset()
	{
		return dataset;
	}

	public Genotype setDataset(Dataset dataset)
	{
		this.dataset = dataset;
		return this;
	}

	public Accession getAccession()
	{
		return accession;
	}

	public Genotype setAccession(Accession accession)
	{
		this.accession = accession;
		return this;
	}

	public String getSampleName()
	{
		return sampleName;
	}

	public Genotype setSampleName(String sampleName)
	{
		this.sampleName = sampleName;
		return this;
	}

	public String getAlleleOne()
	{
		return alleleOne;
	}

	public Genotype setAlleleOne(String alleleOne)
	{
		this.alleleOne = alleleOne;
		return this;
	}

	public String getAlleleTwo()
	{
		return alleleTwo;
	}

	public Genotype setAlleleTwo(String alleleTwo)
	{
		this.alleleTwo = alleleTwo;
		return this;
	}

	public Double getX()
	{
		return x;
	}

	public Genotype setX(Double x)
	{
		this.x = x;
		return this;
	}

	public Double getY()
	{
		return y;
	}

	public Genotype setY(Double y)
	{
		this.y = y;
		return this;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Genotype>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
			 * InstanceHolder#INSTANCE}, not before.
			 * <p/>
			 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
			 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
			 *
			 * @author Sebastian Raubach
			 */
			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}

			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		private static DatabaseObjectCache<Accession> ACCESSION_CACHE;
		private static DatabaseObjectCache<Marker>    MARKER_CACHE;

		private static DatasetManager DATASET_MANAGER = new DatasetManager();

		private Parser()
		{
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
			MARKER_CACHE = createCache(Marker.class, MarkerManager.class);
		}

		@Override
		public Genotype parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new Genotype(id)
							.setSampleName(row.getString(SAMPLE_NAME))
							.setAlleleOne(row.getString(ALLELE_1))
							.setAlleleTwo(row.getString(ALLELE_2))
							.setX(row.getDouble(X))
							.setY(row.getDouble(Y))
							.setDataset(DATASET_MANAGER.getById(user, row.getLong(DATASET_ID)).getServerResult())
							.setMarker(MARKER_CACHE.get(user, row.getLong(MARKER_ID), row, foreignsFromResultSet))
							.setAccession(ACCESSION_CACHE.get(user, row.getLong(GERMINATEBASE_ID), row, foreignsFromResultSet));
			}
			catch (InsufficientPermissionsException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}
