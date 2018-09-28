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

package jhi.germinate.server.database;

import java.util.logging.*;

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * Calculates Passport Data Completeness Index (PDCI) for all accessions.
 *
 * <p>
 * Theo van Hintum, Frank Menting and Elisabeth van Strien (2011). <b>Quality indicators for passport data in ex situ genebanks.</b> Plant Genetic Resources, 9, pp 478-485.
 * <p>
 * doi:10.1017/S1479262111000682
 */
public class PDCIRunnable implements Runnable
{
	public static final String HAS_PEDIGREE     = "has_pedigree";
	public static final String HAS_PEDIGREE_DEF = "has_pedigree_def";
	public static final String HAS_STORAGE      = "has_storage";
	public static final String HAS_URL          = "has_url";

	@Override
	public void run()
	{
		try
		{
			long start = System.currentTimeMillis();
			DatabaseObjectStreamer<Accession> streamer = AccessionManager.getObjectStreamerForPDCI();

			Accession acc;

			while ((acc = streamer.next()) != null)
			{
				// Calculate the generic score for this accession
				float value = getGeneric(acc);

				// Calculate remaining score based on population type
				switch (acc.getBiologicalStatus() != null ? (int) (acc.getBiologicalStatus().getId() / 100) : -1)
				{
					case 1:
					case 2:
						value += getWildWeedy(acc);
						break;
					case 3:
						value += getLandrace(acc);
						break;
					case 4:
						value += getBreedingMaterial(acc);
						break;
					case 5:
						value += getCultivar(acc);
						break;
					default:
						value += getOther(acc);
						break;
				}

				// Divide by 100 to get a value between 0 and 10.
				value = value / 100f;

				AccessionManager.updatePDCI(acc.getId(), value);
			}

			start = System.currentTimeMillis() - start;

			Logger.getLogger("").log(Level.INFO, "PDCI calculation complete in " + start + "ms");
		}
		catch (InvalidColumnException | DatabaseException | InvalidSearchQueryException | InvalidArgumentException e)
		{
			e.printStackTrace();
		}
	}

	private int getGeneric(Accession acc)
	{
		int value = 0;

		if (acc.getTaxonomy() != null && !StringUtils.isEmpty(acc.getTaxonomy().getGenus()))
		{
			value += 120;

			if (!StringUtils.isEmpty(acc.getTaxonomy().getSpecies()))
			{
				value += 80;


				if (!StringUtils.isEmpty(acc.getTaxonomy().getTaxonomyAuthor()))
					value += 5;
				if (acc.getTaxonomy() != null && !StringUtils.isEmpty(acc.getTaxonomy().getSubtaxa()))
					value += 40;
				if (acc.getTaxonomy() != null && !StringUtils.isEmpty(acc.getTaxonomy().getSubtaxaAuthor()))
					value += 5;
				if (!StringUtils.isEmpty(acc.getTaxonomy().getCropName()))
					value += 45;
			}
		}

		if (!StringUtils.isEmpty(acc.getAcqDate()))
			value += 10;

		if (acc.getBiologicalStatus() != null)
			value += 80;
		if (!StringUtils.isEmpty(acc.getDonorCode()))
			value += 40;
		else if (!StringUtils.isEmpty(acc.getDonorName()))
			value += 20;
		if (!StringUtils.isEmpty(acc.getDonorNumber()))
		{
			if (StringUtils.isEmpty(acc.getDonorCode()) && StringUtils.isEmpty(acc.getDonorName()))
				value += 20;
			else
				value += 40;
		}
		if (!StringUtils.isEmpty(acc.getOtherNumb()))
			value += 35;
		if (!StringUtils.isEmpty(acc.getDuplSite()))
			value += 30;
		else if (!StringUtils.isEmpty(acc.getDuplInstName()))
			value += 15;

		if (!StringUtils.isEmpty(acc.getExtra(HAS_STORAGE)))
			value += 15;

		if (!StringUtils.isEmpty(acc.getExtra(HAS_URL)))
			value += 40;

		if (acc.getMlsStatus() != null)
			value += 15;

		return value;
	}

	private int getOther(Accession acc)
	{
		int value = 0;

		if(acc.getLocation() != null)
		{
			if (acc.getLocation().getCountry() != null)
				value += 40;

			if (!StringUtils.isEmpty(acc.getLocation().getName()))
			{
				if (acc.getLocation().getLatitude() == null || acc.getLocation().getLongitude() == null)
					value += 20;
				else
					value += 10;
			}

			if (acc.getLocation().getLatitude() != null && acc.getLocation().getLongitude() != null)
				value += 30;

			if (acc.getLocation().getElevation() != null)
				value += 5;
		}

		if (acc.getCollDate() != null)
			value += 10;

		if (!StringUtils.isEmpty(acc.getBreedersCode()))
			value += 10;
		else if (!StringUtils.isEmpty(acc.getBreedersName()))
			value += 10;

		if (!StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)) || !StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)))
			value += 40;

		if (acc.getCollSrc() != null)
			value += 25;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 40;

		if (!StringUtils.isEmpty(acc.getCollNumb()))
			value += 20;

		if (!StringUtils.isEmpty(acc.getCollCode()))
			value += 20;
		else if (!StringUtils.isEmpty(acc.getCollName()))
			value += 10;

		return value;
	}

	private int getCultivar(Accession acc)
	{
		int value = 0;

		if (acc.getLocation() != null && acc.getLocation().getCountry() != null)
			value += 40;

		if (!StringUtils.isEmpty(acc.getBreedersCode()))
			value += 80;
		else if (!StringUtils.isEmpty(acc.getBreedersName()))
			value += 40;

		if (!StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)) || !StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)))
			value += 100;

		if (acc.getCollSrc() != null)
			value += 20;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 160;

		return value;
	}

	private int getBreedingMaterial(Accession acc)
	{
		int value = 0;

		if (acc.getLocation() != null && acc.getLocation().getCountry() != null)
			value += 40;

		if (!StringUtils.isEmpty(acc.getBreedersCode()))
			value += 110;
		else if (!StringUtils.isEmpty(acc.getBreedersName()))
			value += 55;

		if (!StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)) || !StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)))
			value += 150;

		if (acc.getCollSrc() != null)
			value += 20;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 80;

		return value;
	}

	private int getLandrace(Accession acc)
	{
		int value = 0;

		if(acc.getLocation() != null)
		{
			if (acc.getLocation().getCountry() != null)
				value += 80;

			if (!StringUtils.isEmpty(acc.getLocation().getName()))
			{
				if (acc.getLocation().getLatitude() == null || acc.getLocation().getLongitude() == null)
					value += 45;
				else
					value += 15;
			}

			if (acc.getLocation().getLatitude() != null && acc.getLocation().getLongitude() != null)
				value += 80;

			if (acc.getLocation().getElevation() != null)
				value += 15;
		}

		if (acc.getCollDate() != null)
			value += 30;

		if (!StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)) || !StringUtils.isEmpty(acc.getExtra(HAS_PEDIGREE)))
			value += 10;

		if (acc.getCollSrc() != null)
			value += 50;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 50;

		if (!StringUtils.isEmpty(acc.getCollNumb()))
			value += 40;

		if (!StringUtils.isEmpty(acc.getCollCode()))
			value += 30;
		else if (!StringUtils.isEmpty(acc.getCollName()))
			value += 15;

		return value;
	}

	private int getWildWeedy(Accession acc)
	{
		int value = 0;

		if(acc.getLocation() != null)
		{
			if (acc.getLocation().getCountry() != null)
				value += 80;

			if (!StringUtils.isEmpty(acc.getLocation().getName()))
			{
				if (acc.getLocation().getLatitude() == null || acc.getLocation().getLongitude() == null)
					value += 70;
				else
					value += 20;
			}

			if (acc.getLocation().getLatitude() != null && acc.getLocation().getLongitude() != null)
				value += 120;

			if (acc.getLocation().getElevation() != null)
				value += 20;
		}

		if (acc.getCollDate() != null)
			value += 30;

		if (acc.getCollSrc() != null)
			value += 30;

		if (!StringUtils.isEmpty(acc.getCollNumb()))
			value += 60;

		if (!StringUtils.isEmpty(acc.getCollCode()))
			value += 40;
		else if (!StringUtils.isEmpty(acc.getCollName()))
			value += 20;

		return value;
	}
}