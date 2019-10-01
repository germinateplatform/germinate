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

import jhi.germinate.client.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.database.query.writer.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class Dataset extends DatabaseObject
{
	private static final long serialVersionUID = -60721756688175967L;

	public static final String ID               = "datasets.id";
	public static final String EXPERIMENT_ID    = "datasets.experiment_id";
	public static final String LOCATION_ID      = "datasets.location_id";
	public static final String NAME             = "datasets.name";
	public static final String DESCRIPTION      = "datasets.description";
	public static final String DATE_START       = "datasets.date_start";
	public static final String DATE_END         = "datasets.date_end";
	public static final String SOURCE_FILE      = "datasets.source_file";
	public static final String DATATYPE         = "datasets.datatype";
	public static final String DUBLIN_CORE      = "datasets.dublin_core";
	public static final String CONTACT          = "datasets.contact";
	public static final String VERSION          = "datasets.version";
	public static final String CREATED_BY       = "datasets.created_by";
	public static final String DATASET_STATE_ID = "datasets.dataset_state_id";
	public static final String LICENSE_ID       = "datasets.license_id";
	public static final String IS_EXTERNAL      = "datasets.is_external";
	public static final String HYPERLINK        = "datasets.hyperlink";
	public static final String CREATED_ON       = "datasets.created_on";
	public static final String UPDATED_ON       = "datasets.updated_on";

	public static final String NR_OF_DATA_OBJECTS = "nr_of_data_objects";
	public static final String NR_OF_DATA_POINTS  = "nr_of_data_points";

	private Experiment          experiment;
	private Location            location;
	private String              name;
	private String              description;
	private Date                dateStart;
	private Date                dateEnd;
	private String              sourceFile;
	private String              datatype;
	private String              dublinCore;
	private String              contact;
	private String              version;
	private Long                userId;
	private DatasetState        datasetState;
	private License             license;
	private Boolean             isExternal;
	private String              hyperlink;
	private Long                createdOn;
	private Long                updatedOn;
	private Long                size       = 0L;
	private Long                dataPoints = 0L;
	private List<Collaborator>  collaborators;
	private List<AttributeData> attributeData;

	public Dataset()
	{
	}

	public Dataset(Long id)
	{
		super(id);
	}

	public Experiment getExperiment()
	{
		return experiment;
	}

	public Dataset setExperiment(Experiment experiment)
	{
		this.experiment = experiment;
		return this;
	}

	public Location getLocation()
	{
		return location;
	}

	public Dataset setLocation(Location location)
	{
		this.location = location;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Dataset setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Dataset setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Date getDateStart()
	{
		return dateStart;
	}

	public Dataset setDateStart(Date dateStart)
	{
		this.dateStart = dateStart;
		return this;
	}

	public Date getDateEnd()
	{
		return dateEnd;
	}

	public Dataset setDateEnd(Date dateEnd)
	{
		this.dateEnd = dateEnd;
		return this;
	}

	public String getSourceFile()
	{
		return sourceFile;
	}

	public Dataset setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
		return this;
	}

	public String getDatatype()
	{
		return datatype;
	}

	public Dataset setDatatype(String datatype)
	{
		this.datatype = datatype;
		return this;
	}

	public String getDublinCore()
	{
		return dublinCore;
	}

	public Dataset setDublinCore(String dublinCore)
	{
		this.dublinCore = dublinCore;
		return this;
	}

	public String getContact()
	{
		return contact;
	}

	public Dataset setContact(String contact)
	{
		this.contact = contact;
		return this;
	}

	public String getVersion()
	{
		return version;
	}

	public Dataset setVersion(String version)
	{
		this.version = version;
		return this;
	}

	public Long getUserId()
	{
		return userId;
	}

	public Dataset setUserId(Long userId)
	{
		this.userId = userId;
		return this;
	}

	public DatasetState getDatasetState()
	{
		return datasetState;
	}

	public Dataset setDatasetState(DatasetState datasetState)
	{
		this.datasetState = datasetState;
		return this;
	}

	public License getLicense()
	{
		return license;
	}

	public Dataset setLicense(License license)
	{
		this.license = license;
		return this;
	}

	public Boolean isExternal()
	{
		return isExternal;
	}

	public Dataset setIsExternal(Boolean isExternal)
	{
		this.isExternal = isExternal;
		return this;
	}

	public String getHyperlink()
	{
		return hyperlink;
	}

	public Dataset setHyperlink(String hyperlink)
	{
		this.hyperlink = hyperlink;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Dataset setCreatedOn(Date createdOn)
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

	public Dataset setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public Dataset setSize(Long size)
	{
		this.size = size;
		return this;
	}

	public Long getDataPoints()
	{
		return dataPoints;
	}

	public Dataset setDataPoints(Long dataPoints)
	{
		this.dataPoints = dataPoints;
		return this;
	}

	public List<AttributeData> getAttributeData()
	{
		return attributeData;
	}

	public Dataset setAttributeData(List<AttributeData> attributeData)
	{
		this.attributeData = attributeData;
		return this;
	}

	public List<Collaborator> getCollaborators()
	{
		return collaborators;
	}

	public Dataset setCollaborators(List<Collaborator> collaborators)
	{
		this.collaborators = collaborators;
		return this;
	}

	public boolean hasLicenseBeenAccepted(UserAuth user)
	{
		if (!ModuleCore.getUseAuthentication() && (user == null || user.getId() == null))
			return license == null || license.getLicenseLog() != null && license.getLicenseLog().getUser() == -1;
		else
			return license == null || license.getLicenseLog() != null && Objects.equals(license.getLicenseLog().getUser(), user.getId());
	}

	@Override
	public String toString()
	{
		return "Dataset{" +
				"experiment=" + experiment +
				", location=" + location +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", dateStart=" + dateStart +
				", dateEnd=" + dateEnd +
				", sourceFile='" + sourceFile + '\'' +
				", datatype='" + datatype + '\'' +
				", dublinCore='" + dublinCore + '\'' +
				", contact='" + contact + '\'' +
				", version='" + version + '\'' +
				", userId=" + userId +
				", datasetState=" + datasetState +
				", license=" + license +
				", isExternal=" + isExternal +
				", hyperlink='" + hyperlink + '\'' +
				", createdOn=" + createdOn +
				", updatedOn=" + updatedOn +
				", size=" + size +
				", dataPoints=" + dataPoints +
				", attributeData=" + attributeData +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends MinimalParser
	{
		private Parser()
		{
			super();
		}

		@Override
		public Dataset parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Dataset dataset = super.parse(row, user, foreignsFromResultSet);

			try
			{
				PartialSearchQuery filter = new PartialSearchQuery();
				filter.add(new SearchCondition(Dataset.ID, new Equal(), Long.toString(dataset.getId()), Long.class));

				dataset.setAttributeData(AttributeDataManager.getAllForDatasetFilter(user, filter, Pagination.getDefault(), false).getServerResult());
			}
			catch (Exception e)
			{
				// Ignore this
			}

			try
			{
				dataset.setCollaborators(CollaboratorManager.getForDatasetId(user, dataset.id).getServerResult());
			}
			catch (Exception e)
			{
				// Ignore this
			}

			return dataset;
		}

		public static final class Inst
		{
			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}

			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE},
			 * not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand
			 * holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
			 * <code>synchronized</code>).
			 *
			 * @author Sebastian Raubach
			 */
			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}
		}
	}

	@GwtIncompatible
	public static class MinimalParser extends DatabaseObjectParser<Dataset>
	{
		@Override
		public Dataset parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
				{
					Dataset dataset = new Dataset(id)
							.setExperiment(EXPERIMENT_CACHE.get(user, row.getLong(EXPERIMENT_ID), row, foreignsFromResultSet))
							.setLocation(LOCATION_CACHE.get(user, row.getLong(LOCATION_ID), row, foreignsFromResultSet))
							.setLicense(LICENSE_CACHE.get(user, row.getLong(LICENSE_ID), row, foreignsFromResultSet))
							.setName(row.getString(NAME))
							.setDescription(row.getString(DESCRIPTION))
							.setDateStart(row.getDate(DATE_START))
							.setDateEnd(row.getDate(DATE_END))
							.setSourceFile(row.getString(SOURCE_FILE))
							.setDatatype(row.getString(DATATYPE))
							.setDublinCore(row.getString(DUBLIN_CORE))
							.setContact(row.getString(CONTACT))
							.setVersion(row.getString(VERSION))
							.setUserId(row.getLong(CREATED_BY))
							.setDatasetState(DatasetState.getById(row.getLong(DATASET_STATE_ID)))
							.setIsExternal(row.getBoolean(IS_EXTERNAL))
							.setHyperlink(row.getString(HYPERLINK))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					try
					{
						dataset.setSize(row.getLong(NR_OF_DATA_OBJECTS));
					}
					catch (Exception e)
					{
						// Ignore this
					}

					try
					{
						dataset.setDataPoints(row.getLong(NR_OF_DATA_POINTS));
					}
					catch (Exception e)
					{
						// Ignore this
					}

					return dataset;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		private static DatabaseObjectCache<Experiment> EXPERIMENT_CACHE;
		private static DatabaseObjectCache<Location>   LOCATION_CACHE;
		private static DatabaseObjectCache<License>    LICENSE_CACHE;

		private MinimalParser()
		{
			EXPERIMENT_CACHE = createCache(Experiment.class, ExperimentManager.class);
			LOCATION_CACHE = createCache(Location.class, LocationManager.class);
			LICENSE_CACHE = createCache(License.class, LicenseManager.class);
		}

		public static final class Inst
		{
			public static MinimalParser get()
			{
				return InstanceHolder.INSTANCE;
			}

			private static final class InstanceHolder
			{
				private static final MinimalParser INSTANCE = new MinimalParser();
			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Dataset>
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
		public void write(Database database, Dataset object, boolean isUpdate) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `datasets` (" + EXPERIMENT_ID + ", " + LOCATION_ID + ", " + NAME + ", " + DESCRIPTION + ", " + DATE_START + ", " + DATE_END + ", " + SOURCE_FILE + ", " + DATATYPE + ", " + DUBLIN_CORE + ", " + VERSION + ", " + CREATED_BY + ", " + DATASET_STATE_ID + ", " + IS_EXTERNAL + ", " + HYPERLINK + ", " + CONTACT + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setLong(object.getExperiment().getId())
					.setLong(object.getLocation() == null ? null : object.getLocation().getId())
					.setString(object.getName())
					.setString(object.getDescription())
					.setDate(object.getDateStart())
					.setDate(object.getDateEnd())
					.setString(object.getSourceFile())
					.setString(object.getDatatype())
					.setString(object.getDublinCore())
					.setString(object.getVersion())
					.setLong(object.getUserId())
					.setLong(object.getDatasetState().getId())
					.setBoolean(object.isExternal)
					.setString(object.getHyperlink())
					.setString(object.getContact());

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
