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

import jhi.germinate.client.service.*;
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
public class Accession extends DatabaseObject
{
	private static final long serialVersionUID = -5621276905667573230L;

	public static final String ID                  = "germinatebase.id";
	public static final String GENERAL_IDENTIFIER  = "germinatebase.general_identifier";
	public static final String NUMBER              = "germinatebase.number";
	public static final String NAME                = "germinatebase.name";
	public static final String BANK_NUMBER         = "germinatebase.bank_number";
	public static final String BREEDERS_CODE       = "germinatebase.breeders_code";
	public static final String BREEDERS_NAME       = "germinatebase.breeders_name";
	public static final String SUBTAXA_ID          = "germinatebase.subtaxa_id";
	public static final String TAXONOMY_ID         = "germinatebase.taxonomy_id";
	public static final String INSTITUTION_ID      = "germinatebase.institution_id";
	public static final String PLANT_PASSPORT      = "germinatebase.plant_passport";
	public static final String DONOR_CODE          = "germinatebase.donor_code";
	public static final String DONOR_NAME          = "germinatebase.donor_name";
	public static final String DONOR_NUMBER        = "germinatebase.donor_number";
	public static final String ACQDATE             = "germinatebase.acqdate";
	public static final String COLLNUMB            = "germinatebase.collnumb";
	public static final String COLLNAME            = "germinatebase.collname";
	public static final String COLLDATE            = "germinatebase.colldate";
	public static final String COLLCODE            = "germinatebase.collcode";
	public static final String COLLMISSID          = "germinatebase.collmissid";
	public static final String OTHERNUMB           = "germinatebase.othernumb";
	public static final String DUPLSITE            = "germinatebase.duplsite";
	public static final String DUPLINSTNAME        = "germinatebase.duplinstname";
	public static final String MLSSTATUS           = "germinatebase.mlsstatus_id";
	public static final String PUID                = "germinatebase.puid";
	public static final String BIOLOGICALSTATUS_ID = "germinatebase.biologicalstatus_id";
	public static final String COLLSRC_ID          = "germinatebase.collsrc_id";
	public static final String LOCATION_ID         = "germinatebase.location_id";

	public static final String SYNONYMS = "synonyms";

	private String           generalIdentifier;
	private String           number;
	private String           name;
	private String           bankNumber;
	private String           breedersCode;
	private String           breedersName;
	private Subtaxa          subtaxa;
	private Taxonomy         taxonomy;
	private Institution      institution;
	private String           plantPassport;
	private String           donorCode;
	private String           donorName;
	private String           donorNumber;
	private String           acqDate;
	private String           collNumb;
	private Long             collDate;
	private String           collName;
	private String           collCode;
	private String           collMissId;
	private String           otherNumb;
	private String           duplSite;
	private String           duplInstName;
	private String           puid;
	private BiologicalStatus biologicalStatus;
	private CollectingSource collSrc;
	private Location         location;
	private MlsStatus        mlsStatus;
	private String           synonyms;
	private Long             createdOn;
	private Long             updatedOn;

	public Accession()
	{
		super();
	}

	public Accession(Long id)
	{
		super(id);
	}

	public String getGeneralIdentifier()
	{
		return generalIdentifier;
	}

	public Accession setGeneralIdentifier(String generalIdentifier)
	{
		this.generalIdentifier = generalIdentifier;
		return this;
	}

	public String getNumber()
	{
		return number;
	}

	public Accession setNumber(String number)
	{
		this.number = number;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Accession setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getBankNumber()
	{
		return bankNumber;
	}

	public Accession setBankNumber(String bankNumber)
	{
		this.bankNumber = bankNumber;
		return this;
	}

	public String getBreedersCode()
	{
		return breedersCode;
	}

	public Accession setBreedersCode(String breedersCode)
	{
		this.breedersCode = breedersCode;
		return this;
	}

	public String getBreedersName()
	{
		return breedersName;
	}

	public Accession setBreedersName(String breedersName)
	{
		this.breedersName = breedersName;
		return this;
	}

	public Subtaxa getSubtaxa()
	{
		return subtaxa;
	}

	public Accession setSubtaxa(Subtaxa subtaxa)
	{
		this.subtaxa = subtaxa;
		return this;
	}

	public Taxonomy getTaxonomy()
	{
		return taxonomy;
	}

	public Accession setTaxonomy(Taxonomy taxonomy)
	{
		this.taxonomy = taxonomy;
		return this;
	}

	public Institution getInstitution()
	{
		return institution;
	}

	public Accession setInstitution(Institution institution)
	{
		this.institution = institution;
		return this;
	}

	public String getDonorName()
	{
		return donorName;
	}

	public Accession setDonorName(String donorName)
	{
		this.donorName = donorName;
		return this;
	}

	public String getCollName()
	{
		return collName;
	}

	public Accession setCollName(String collName)
	{
		this.collName = collName;
		return this;
	}

	public String getPlantPassport()
	{
		return plantPassport;
	}

	public Accession setPlantPassport(String plantPassport)
	{
		this.plantPassport = plantPassport;
		return this;
	}

	public String getDonorCode()
	{
		return donorCode;
	}

	public Accession setDonorCode(String donorCode)
	{
		this.donorCode = donorCode;
		return this;
	}

	public String getDonorNumber()
	{
		return donorNumber;
	}

	public Accession setDonorNumber(String donorNumber)
	{
		this.donorNumber = donorNumber;
		return this;
	}

	public String getAcqDate()
	{
		return acqDate;
	}

	public Accession setAcqDate(String acqDate)
	{
		this.acqDate = acqDate;
		return this;
	}

	public String getCollNumb()
	{
		return collNumb;
	}

	public Accession setCollNumb(String collNumb)
	{
		this.collNumb = collNumb;
		return this;
	}

	public Long getCollDate()
	{
		return collDate;
	}

	public Accession setCollDate(Long collDate)
	{
		this.collDate = collDate;
		return this;
	}

	public Accession setCollDate(Date collDate)
	{
		if (collDate == null)
			this.collDate = null;
		else
			this.collDate = collDate.getTime();
		return this;
	}

	public String getCollCode()
	{
		return collCode;
	}

	public Accession setCollCode(String collCode)
	{
		this.collCode = collCode;
		return this;
	}

	public String getCollMissId()
	{
		return collMissId;
	}

	public Accession setCollMissId(String collMissId)
	{
		this.collMissId = collMissId;
		return this;
	}

	public String getOtherNumb()
	{
		return otherNumb;
	}

	public Accession setOtherNumb(String otherNumb)
	{
		this.otherNumb = otherNumb;
		return this;
	}

	public String getDuplSite()
	{
		return duplSite;
	}

	public Accession setDuplSite(String duplSite)
	{
		this.duplSite = duplSite;
		return this;
	}

	public String getDuplInstName()
	{
		return duplInstName;
	}

	public Accession setDuplInstName(String duplInstName)
	{
		this.duplInstName = duplInstName;
		return this;
	}

	public String getPuid()
	{
		return puid;
	}

	public Accession setPuid(String puid)
	{
		this.puid = puid;
		return this;
	}

	public BiologicalStatus getBiologicalStatus()
	{
		return biologicalStatus;
	}

	public Accession setBiologicalStatus(BiologicalStatus biologicalStatus)
	{
		this.biologicalStatus = biologicalStatus;
		return this;
	}

	public CollectingSource getCollSrc()
	{
		return collSrc;
	}

	public Accession setCollSrc(CollectingSource collSrc)
	{
		this.collSrc = collSrc;
		return this;
	}

	public Location getLocation()
	{
		return location;
	}

	public Accession setLocation(Location location)
	{
		this.location = location;
		return this;
	}

	public MlsStatus getMlsStatus()
	{
		return mlsStatus;
	}

	public Accession setMlsStatus(MlsStatus mlsStatus)
	{
		this.mlsStatus = mlsStatus;
		return this;
	}

	public String getSynonyms()
	{
		return synonyms;
	}

	public Accession setSynonyms(String synonyms)
	{
		this.synonyms = synonyms;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Accession setCreatedOn(Date createdOn)
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

	public Accession setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	@Override
	public String toString()
	{
		return "Accession{" +
				"id=" + id +
				'}';
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Accession>
	{
		@Override
		public Accession parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new Accession(id)
							.setGeneralIdentifier(row.getString(GENERAL_IDENTIFIER))
							.setNumber(row.getString(NUMBER))
							.setName(row.getString(NAME))
							.setBankNumber(row.getString(BANK_NUMBER))
							.setBreedersCode(row.getString(BREEDERS_CODE))
							.setBreedersName(row.getString(BREEDERS_NAME))
							.setPuid(row.getString(PUID))
							.setSubtaxa(SUBTAXA_CACHE.get(user, row.getLong(SUBTAXA_ID), row, foreignsFromResultSet))
							.setTaxonomy(TAXONOMY_CACHE.get(user, row.getLong(TAXONOMY_ID), row, foreignsFromResultSet))
							.setInstitution(INSTITUTION_CACHE.get(user, row.getLong(INSTITUTION_ID), row, false))
							.setPlantPassport(row.getString(PLANT_PASSPORT))
							.setDonorCode(row.getString(DONOR_CODE))
							.setDonorName(row.getString(DONOR_NAME))
							.setDonorNumber(row.getString(DONOR_NUMBER))
							.setAcqDate(row.getString(ACQDATE))
							.setCollNumb(row.getString(COLLNUMB))
							.setCollDate(row.getDate(COLLDATE))
							.setCollName(row.getString(COLLNAME))
							.setCollCode(row.getString(COLLCODE))
							.setOtherNumb(row.getString(OTHERNUMB))
							.setCollMissId(row.getString(COLLMISSID))
							.setDuplSite(row.getString(DUPLSITE))
							.setDuplInstName(row.getString(DUPLINSTNAME))
							.setMlsStatus(MLSSTATUS_CACHE.get(user, row.getLong(MLSSTATUS), row, foreignsFromResultSet))
							.setBiologicalStatus(BIOLOGICALSTATUS_CACHE.get(user, row.getLong(BIOLOGICALSTATUS_ID), row, foreignsFromResultSet))
							.setCollSrc(COLLECTINGSOURCE_CACHE.get(user, row.getLong(COLLSRC_ID), row, foreignsFromResultSet))
							.setLocation(LOCATION_CACHE.get(user, row.getLong(LOCATION_ID), row, foreignsFromResultSet))
							.setSynonyms(row.getString(SYNONYMS))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		private static DatabaseObjectCache<Subtaxa>          SUBTAXA_CACHE;
		private static DatabaseObjectCache<Taxonomy>         TAXONOMY_CACHE;
		private static DatabaseObjectCache<Institution>      INSTITUTION_CACHE;
		private static DatabaseObjectCache<BiologicalStatus> BIOLOGICALSTATUS_CACHE;
		private static DatabaseObjectCache<CollectingSource> COLLECTINGSOURCE_CACHE;
		private static DatabaseObjectCache<Location>         LOCATION_CACHE;
		private static DatabaseObjectCache<MlsStatus>        MLSSTATUS_CACHE;

		private Parser()
		{
			SUBTAXA_CACHE = createCache(Subtaxa.class, SubtaxaManager.class);
			TAXONOMY_CACHE = createCache(Taxonomy.class, TaxonomyManager.class);
			INSTITUTION_CACHE = createCache(Institution.class, InstitutionManager.class);
			BIOLOGICALSTATUS_CACHE = createCache(BiologicalStatus.class, BiologicalStatusManager.class);
			COLLECTINGSOURCE_CACHE = createCache(CollectingSource.class, CollectingSourceManager.class);
			LOCATION_CACHE = createCache(Location.class, LocationManager.class);
			MLSSTATUS_CACHE = createCache(MlsStatus.class, MlsStatusManager.class);
		}

		public static final class Inst
		{
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

			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}
	}

	@GwtIncompatible
	public static class DistanceParser extends Parser
	{
		public static final class Inst
		{
			/**
			 * {@link Inst.InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
			 * Inst.InstanceHolder#INSTANCE}, not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
			 * >Initialization-on-demand holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
			 * <code>synchronized</code>).
			 *
			 * @author Sebastian Raubach
			 */
			private static final class InstanceHolder
			{
				private static final DistanceParser INSTANCE = new DistanceParser();
			}

			public static DistanceParser get()
			{
				return Inst.InstanceHolder.INSTANCE;
			}
		}

		@Override
		public Accession parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Accession accession = super.parse(row, user, foreignsFromResultSet);

			if (accession != null)
				accession.setExtra(LocationService.DISTANCE, row.getString(LocationService.DISTANCE));

			return accession;
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Accession>
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
		public void write(Database database, Accession object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO germinatebase (" + GENERAL_IDENTIFIER + ", " + NUMBER + ", " + NAME + ", " + BANK_NUMBER + ", " + BREEDERS_CODE + ", " + BREEDERS_NAME + ", " + SUBTAXA_ID + ", " + TAXONOMY_ID + ", " + INSTITUTION_ID + ", " + PLANT_PASSPORT + ", " + DONOR_CODE + ", " + DONOR_NAME + ", " + DONOR_NUMBER + ", " + ACQDATE + ", " + COLLNUMB + ", " + COLLDATE + ", " + COLLCODE + ", " + COLLNAME + ", " + COLLMISSID + ", " + OTHERNUMB + ", " + DUPLSITE + ", " + DUPLINSTNAME + ", " + MLSSTATUS + ", " + PUID + ", " + BIOLOGICALSTATUS_ID + ", " + COLLSRC_ID + ", " + LOCATION_ID + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setString(object.getGeneralIdentifier())
					.setString(object.getNumber())
					.setString(object.getName())
					.setString(object.getBankNumber())
					.setString(object.getBreedersCode())
					.setString(object.getBreedersName())
					.setLong(object.getSubtaxa() != null ? object.getSubtaxa().getId() : null)
					.setLong(object.getTaxonomy() != null ? object.getTaxonomy().getId() : null)
					.setLong(object.getInstitution() != null ? object.getInstitution().getId() : null)
					.setString(object.getPlantPassport())
					.setString(object.getDonorCode())
					.setString(object.getDonorName())
					.setString(object.getDonorNumber())
					.setString(object.getAcqDate())
					.setString(object.getCollNumb())
					.setDate(object.getCollDate() != null ? new Date(object.getCollDate()) : null)
					.setString(object.getCollCode())
					.setString(object.getCollName())
					.setString(object.getCollMissId())
					.setString(object.getOtherNumb())
					.setString(object.getDuplSite())
					.setString(object.getDuplInstName())
					.setLong(object.getMlsStatus() != null ? object.getMlsStatus().getId() : null)
					.setString(object.getPuid())
					.setLong(object.getBiologicalStatus() != null ? object.getBiologicalStatus().getId() : null)
					.setLong(object.getCollSrc() != null ? object.getCollSrc().getId() : null)
					.setLong(object.getLocation() != null ? object.getLocation().getId() : null);

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
