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
public class PhenotypeData extends DatabaseObject
{
	private static final long serialVersionUID = 60443754677583276L;

	public static final String ID               = "phenotypedata.id";
	public static final String PHENOTYPE_ID     = "phenotypedata.phenotype_id";
	public static final String GERMINATEBASE_ID = "phenotypedata.germinatebase_id";
	public static final String PHENOTYPE_VALUE  = "phenotypedata.phenotype_value";
	public static final String DATASET_ID       = "phenotypedata.dataset_id";
	public static final String RECORDING_DATE   = "phenotypedata.recording_date";
	public static final String LOCATION_ID      = "phenotypedata.location_id";
	public static final String TREATMENT_ID     = "phenotypedata.treatment_id";
	public static final String TRIALSERIES_ID   = "phenotypedata.trialseries_id";
	public static final String CREATED_ON       = "phenotypedata.created_on";
	public static final String UPDATED_ON       = "phenotypedata.updated_on";

	private Phenotype   phenotype;
	private Accession   accession;
	private String      value;
	private Dataset     dataset;
	private Long        recordingDate;
	private Location    location;
	private Treatment   treatment;
	private Trialseries trialseries;
	private Long        createdOn;
	private Long        updatedOn;

	public PhenotypeData()
	{
	}

	public PhenotypeData(Long id)
	{
		super(id);
	}

	public Phenotype getPhenotype()
	{
		return phenotype;
	}

	public PhenotypeData setPhenotype(Phenotype phenotype)
	{
		this.phenotype = phenotype;
		return this;
	}

	public Accession getAccession()
	{
		return accession;
	}

	public PhenotypeData setAccession(Accession accession)
	{
		this.accession = accession;
		return this;
	}

	public String getValue()
	{
		return value;
	}

	public PhenotypeData setValue(String value)
	{
		this.value = value;
		return this;
	}

	public Dataset getDataset()
	{
		return dataset;
	}

	public PhenotypeData setDataset(Dataset dataset)
	{
		this.dataset = dataset;
		return this;
	}

	public Long getRecordingDate()
	{
		return recordingDate;
	}

	public PhenotypeData setRecordingDate(Date recordingDate)
	{
		if (recordingDate == null)
			this.recordingDate = null;
		else
			this.recordingDate = recordingDate.getTime();
		return this;
	}

	public Location getLocation()
	{
		return location;
	}

	public PhenotypeData setLocation(Location location)
	{
		this.location = location;
		return this;
	}

	public Treatment getTreatment()
	{
		return treatment;
	}

	public PhenotypeData setTreatment(Treatment treatment)
	{
		this.treatment = treatment;
		return this;
	}

	public Trialseries getTrialseries()
	{
		return trialseries;
	}

	public PhenotypeData setTrialseries(Trialseries trialseries)
	{
		this.trialseries = trialseries;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public PhenotypeData setCreatedOn(Date createdOn)
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

	public PhenotypeData setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<PhenotypeData>
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

		private static DatabaseObjectCache<Phenotype>   PHENOTYPE_CACHE;
		private static DatabaseObjectCache<Accession>   ACCESSION_CACHE;
		private static DatabaseObjectCache<Dataset>     DATASET_CACHE;
		private static DatabaseObjectCache<Location>    LOCATION_CACHE;
		private static DatabaseObjectCache<Treatment>   TREATMENT_CACHE;
		private static DatabaseObjectCache<Trialseries> TRIALSERIES_CACHE;

		private Parser()
		{
			PHENOTYPE_CACHE = createCache(Phenotype.class, PhenotypeManager.class);
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
			DATASET_CACHE = createCache(Dataset.class, DatasetManager.class);
			LOCATION_CACHE = createCache(Location.class, LocationManager.class);
			TREATMENT_CACHE = createCache(Treatment.class, TreatmentManager.class);
			TRIALSERIES_CACHE = createCache(Trialseries.class, TrialseriesManager.class);
		}

		@Override
		public PhenotypeData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new PhenotypeData(id)
							.setPhenotype(PHENOTYPE_CACHE.get(user, row.getLong(PHENOTYPE_ID), row, foreignsFromResultSet))
							.setAccession(ACCESSION_CACHE.get(user, row.getLong(GERMINATEBASE_ID), row, foreignsFromResultSet))
							.setValue(row.getString(PHENOTYPE_VALUE))
							.setDataset(DATASET_CACHE.get(user, row.getLong(DATASET_ID), row, foreignsFromResultSet))
							.setRecordingDate(row.getTimestamp(RECORDING_DATE))
							.setLocation(LOCATION_CACHE.get(user, row.getLong(LOCATION_ID), row, foreignsFromResultSet))
							.setTreatment(TREATMENT_CACHE.get(user, row.getLong(TREATMENT_ID), row, foreignsFromResultSet))
							.setTrialseries(TRIALSERIES_CACHE.get(user, row.getLong(TRIALSERIES_ID), row, foreignsFromResultSet))
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
	public static class Writer implements BatchedDatabaseObjectWriter<PhenotypeData>
	{
		public static final class Inst
		{
			private static final class InstanceHolder
			{
				private static final Writer INSTANCE = new Writer();
			}

			public static Writer get()
			{
				return Writer.Inst.InstanceHolder.INSTANCE;
			}
		}

		@Override
		public DatabaseStatement getBatchedStatement(Database database) throws DatabaseException
		{
			return database.prepareStatement("INSERT INTO `phenotypedata` (" + PHENOTYPE_ID + ", " + GERMINATEBASE_ID + ", " + DATASET_ID + ", " + TREATMENT_ID + ", " + LOCATION_ID + ", " + TRIALSERIES_ID + ", " + PHENOTYPE_VALUE + ", " + RECORDING_DATE + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		}

		@Override
		public void writeBatched(DatabaseStatement stmt, PhenotypeData object) throws DatabaseException
		{
			int i = 1;
			stmt.setLong(i++, object.getPhenotype().getId());
			stmt.setLong(i++, object.getAccession().getId());
			stmt.setLong(i++, object.getDataset().getId());
			stmt.setLong(i++, object.getTreatment() == null ? null : object.getTreatment().getId());
			stmt.setLong(i++, object.getLocation() == null ? null : object.getLocation().getId());
			stmt.setLong(i++, object.getTrialseries() == null ? null : object.getTrialseries().getId());
			stmt.setString(i++, object.getValue());

			if (object.getRecordingDate() != null)
				stmt.setTimestamp(i++, new Date(object.getRecordingDate()));
			else
				stmt.setNull(i++, Types.TIMESTAMP);
			if (object.getCreatedOn() != null)
				stmt.setTimestamp(i++, new Date(object.getCreatedOn()));
			else
				stmt.setNull(i++, Types.TIMESTAMP);
			if (object.getUpdatedOn() != null)
				stmt.setTimestamp(i++, new Date(object.getUpdatedOn()));
			else
				stmt.setNull(i++, Types.TIMESTAMP);

			stmt.addBatch();
		}

		@Override
		public void write(Database database, PhenotypeData object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `phenotypedata` (" + PHENOTYPE_ID + ", " + GERMINATEBASE_ID + ", " + DATASET_ID + ", " + TREATMENT_ID + ", " + LOCATION_ID + ", " + TRIALSERIES_ID + ", " + PHENOTYPE_VALUE + ", " + RECORDING_DATE + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setLong(object.getPhenotype().getId())
					.setLong(object.getAccession().getId())
					.setLong(object.getDataset().getId())
					.setLong(object.getTreatment() == null ? null : object.getTreatment().getId())
					.setLong(object.getLocation() == null ? null : object.getLocation().getId())
					.setLong(object.getTrialseries() == null ? null : object.getTrialseries().getId())
					.setString(object.getValue());

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
	}
}
