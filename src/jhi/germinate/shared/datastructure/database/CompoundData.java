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

package jhi.germinate.shared.datastructure.database;

import com.google.gwt.core.shared.*;

import java.sql.*;
import java.util.Date;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.database.query.writer.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class CompoundData extends DatabaseObject
{
	private static final long serialVersionUID = -559870448354653912L;

	public static final String ID                = "compounddata.id";
	public static final String COMPOUND_ID       = "compounddata.compound_id";
	public static final String GERMINATEBASE_ID  = "compounddata.germinatebase_id";
	public static final String DATASET_ID        = "compounddata.dataset_id";
	public static final String ANALYSISMETHOD_ID = "compounddata.analysismethod_id";
	public static final String COMPOUND_VALUE    = "compounddata.compound_value";
	public static final String RECORDING_DATE    = "compounddata.recording_date";
	public static final String CREATED_ON        = "compounddata.created_on";
	public static final String UPDATED_ON        = "compounddata.updated_on";

	private Compound       compound;
	private Accession      accession;
	private Dataset        dataset;
	private AnalysisMethod analysisMethod;
	private Double         value;
	private Long           recordingDate;
	private Long           createdOn;
	private Long           updatedOn;

	public CompoundData()
	{
	}

	public CompoundData(Long id)
	{
		super(id);
	}

	public Compound getCompound()
	{
		return compound;
	}

	public CompoundData setCompound(Compound compound)
	{
		this.compound = compound;
		return this;
	}

	public Accession getAccession()
	{
		return accession;
	}

	public CompoundData setAccession(Accession accession)
	{
		this.accession = accession;
		return this;
	}

	public Dataset getDataset()
	{
		return dataset;
	}

	public CompoundData setDataset(Dataset dataset)
	{
		this.dataset = dataset;
		return this;
	}

	public AnalysisMethod getAnalysisMethod()
	{
		return analysisMethod;
	}

	public CompoundData setAnalysisMethod(AnalysisMethod analysisMethod)
	{
		this.analysisMethod = analysisMethod;
		return this;
	}

	public Double getValue()
	{
		return value;
	}

	public CompoundData setValue(Double value)
	{
		this.value = value;
		return this;
	}

	public Long getRecordingDate()
	{
		return recordingDate;
	}

	public CompoundData setRecordingDate(Date recordingDate)
	{
		if (recordingDate == null)
			this.recordingDate = null;
		else
			this.recordingDate = recordingDate.getTime();
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public CompoundData setCreatedOn(Date createdOn)
	{
		if (createdOn == null)
			this.createdOn = null;
		else
			this.createdOn = createdOn.getTime();
		return this;
	}

	public Long getUpdatedOn()
	{
		return updatedOn;
	}

	public CompoundData setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<CompoundData>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE},
			 * not before.
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

		private static DatabaseObjectCache<Compound>       COMPOUND_CACHE;
		private static DatabaseObjectCache<Accession>      ACCESSION_CACHE;
		private static DatabaseObjectCache<Dataset>        DATASET_CACHE;
		private static DatabaseObjectCache<AnalysisMethod> ANALYSISMETHOD_CACHE;

		private Parser()
		{
			COMPOUND_CACHE = createCache(Compound.class, CompoundManager.class);
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
			DATASET_CACHE = createCache(Dataset.class, DatasetManager.class);
			ANALYSISMETHOD_CACHE = createCache(AnalysisMethod.class, AnalysisMethodManager.class);
		}

		@Override
		public CompoundData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new CompoundData(id)
							.setCompound(COMPOUND_CACHE.get(user, row.getLong(COMPOUND_ID), row, foreignsFromResultSet))
							.setAccession(ACCESSION_CACHE.get(user, row.getLong(GERMINATEBASE_ID), row, foreignsFromResultSet))
							.setDataset(DATASET_CACHE.get(user, row.getLong(DATASET_ID), row, foreignsFromResultSet))
							.setAnalysisMethod(ANALYSISMETHOD_CACHE.get(user, row.getLong(ANALYSISMETHOD_ID), row, foreignsFromResultSet))
							.setValue(row.getDouble(COMPOUND_VALUE))
							.setRecordingDate(row.getTimestamp(RECORDING_DATE))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<CompoundData>
	{
		@Override
		public void write(Database database, CompoundData object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `compounddata` (" + COMPOUND_ID + ", " + GERMINATEBASE_ID + ", " + DATASET_ID + ", " + COMPOUND_VALUE + ", " + RECORDING_DATE + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?)")
					.setLong(object.getCompound().getId())
					.setLong(object.getAccession().getId())
					.setLong(object.getDataset().getId())
					.setDouble(object.getValue());

			if (object.getRecordingDate() != null)
				query.setTimestamp(new Date(object.getRecordingDate()));
			else
				query.setNull(Types.TIMESTAMP);
			if (object.getCreatedOn() != null)
				query.setTimestamp(new Date(object.getCreatedOn()));
			else
				query.setNull(Types.TIMESTAMP);
			if (object.getUpdatedOn() != null)
				query.setTimestamp(new Date(object.getUpdatedOn()));
			else
				query.setNull(Types.TIMESTAMP);

			ServerResult<List<Long>> ids = query.execute(false);

			if (ids != null && !CollectionUtils.isEmpty(ids.getServerResult()))
				object.setId(ids.getServerResult().get(0));
		}

		public static final class Inst
		{
			public static Writer get()
			{
				return Writer.Inst.InstanceHolder.INSTANCE;
			}

			private static final class InstanceHolder
			{
				private static final Writer INSTANCE = new Writer();
			}
		}
	}
}
