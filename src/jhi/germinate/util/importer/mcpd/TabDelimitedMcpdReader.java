/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.mcpd;

import org.apache.poi.openxml4j.exceptions.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link TabDelimitedMcpdReader} is a simple implementation of {@link IStreamableReader} that can parse tab-delimited MCPD files. The column
 * headers have to match the MCPD 2.1 field names, e.g. PUID for the Persistent unique identifier.
 *
 * @author Sebastian Raubach
 */
public class TabDelimitedMcpdReader implements IStreamableReader<Accession>
{
	private String[]     parts;
	private List<String> headers;

	private BufferedReader br;

	private String currentLine = null;

	@Override
	public void close() throws IOException
	{
		if (br != null)
			br.close();

		br = null;
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));

		do
		{
			currentLine = br.readLine();
		} while (currentLine.startsWith("#"));

		headers = new ArrayList<>(Arrays.asList(currentLine.split("\t", -1)));
	}

	@Override
	public boolean hasNext() throws IOException
	{
		boolean hasNext = (currentLine = br.readLine()) != null;

		if (!hasNext)
		{
			close();
		}

		return hasNext;
	}

	@Override
	public Accession next() throws IOException
	{
		parts = currentLine.split("\t", -1);

		StringUtils.trim(parts);

		return parse();
	}

	protected Accession parse()
	{
		Taxonomy taxonomy = parseTaxonomy();
		Accession accession = new Accession()
				.setPuid(getPart(McpdField.PUID))
				.setNumber(getPart(McpdField.ACCENAME))
				.setCollNumb(getPart(McpdField.COLLNUMB))
				.setCollCode(getPart(McpdField.COLLCODE))
				.setCollMissId(getPart(McpdField.COLLMISSID))
				.setDonorNumber(getPart(McpdField.DONORNUMB))
				.setDonorName(getPart(McpdField.DONORNAME))
				.setDonorCode(getPart(McpdField.DONORCODE))
				.setGeneralIdentifier(getPart(McpdField.ACCENUMB))
				.setName(getPart(McpdField.ACCENUMB))
				.setAcqDate(getPart(McpdField.ACQDATE))
				.setCollDate(getDateFromField(McpdField.COLLDATE))
				.setCollName(getPart(McpdField.COLLNAME))
				.setBreedersCode(getPart(McpdField.BREDCODE))
				.setBreedersName(getPart(McpdField.BREDNAME))
				.setOtherNumb(getPart(McpdField.OTHERNUMB))
				.setDuplSite(getPart(McpdField.DUPLSITE))
				.setDuplInstName(getPart(McpdField.DUPLINSTNAME))
				.setInstitution(parseInstitution())
				.setTaxonomy(taxonomy)
				.setSubtaxa(parseSubtaxa(taxonomy))
				.setLocation(parseLocation())
				.setBiologicalStatus(new BiologicalStatus(getLong(McpdField.SAMPSTAT)))
				.setCollSrc(new CollectingSource(getLong(McpdField.COLLSRC)))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
		accession.setExtra(McpdField.REMARKS.name(), getPart(McpdField.REMARKS));
		accession.setExtra(McpdField.STORAGE.name(), getPart(McpdField.STORAGE));
		accession.setExtra(McpdField.ANCEST.name(), getPart(McpdField.ANCEST));

		return accession;
	}

	private Location parseLocation()
	{
		return new Location()
				.setType(new LocationType(1L))
				.setName(getPart(McpdField.COLLSITE))
				.setLatitude(getLatitudeLongitude(getDouble(McpdField.DECLATITUDE), getPart(McpdField.LATITUDE)))
				.setLongitude(getLatitudeLongitude(getDouble(McpdField.DECLONGITUDE), getPart(McpdField.LONGITUDE)))
				.setCoordinateUncertainty(getInt(McpdField.COORDUNCERT))
				.setCoordinateDatum(getPart(McpdField.COORDDATUM))
				.setGeoreferencingMethod(getPart(McpdField.GEOREFMETH))
				.setElevation(getPart(McpdField.ELEVATION))
				.setCountry(parseCountry())
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}

	private Country parseCountry()
	{
		return new Country()
				.setCountryCode3(getPart(McpdField.ORIGCTY));
	}

	private Subtaxa parseSubtaxa(Taxonomy taxonomy)
	{
		return new Subtaxa()
				.setTaxonomy(taxonomy)
				.setTaxonomyIdentifier(getPart(McpdField.SUBTAXA))
				.setAuthor(getPart(McpdField.SUBTAUTHOR))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());

	}

	private Taxonomy parseTaxonomy()
	{
		return new Taxonomy()
				.setGenus(getPart(McpdField.GENUS))
				.setSpecies(getPart(McpdField.SPECIES))
				.setAuthor(getPart(McpdField.SPAUTHOR))
				.setCropName(getPart(McpdField.CROPNAME))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}

	private Institution parseInstitution()
	{
		return new Institution()
				.setCountry(new Country(-1L))
				.setCode(getPart(McpdField.INSTCODE))
				.setName(getPart(McpdField.INSTCODE))
				.setAddress(getPart(McpdField.COLLINSTADDRESS))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}

	/**
	 * Returns the MCPD field value. If this value is empty after calling {@link String#trim()} then <code>null</code> is returned.
	 *
	 * @param field The {@link McpdObject.McpdField}
	 * @return The trimmed field value or <code>null</code>.
	 */
	protected String getPart(McpdField field)
	{
		int i = headers.indexOf(field.name());

		if (i < 0 || i > parts.length - 1)
			return null;
		else
		{
			String result = parts[i];

			result = StringUtils.trim(result);

			return StringUtils.isEmpty(result) ? null : result;
		}
	}

	/**
	 * Tries to parse a data from the value of the given {@link McpdObject.McpdField}.
	 * Since Java can't represent missing months or days in a meaningful way, the first of each will be used in the case of a missing field.
	 *
	 * @param field The {@link McpdObject.McpdField}
	 * @return The parsed {@link Date} or null.
	 */
	protected Date getDateFromField(McpdField field)
	{
		String value = getPart(field);

		return IDataReader.getDate(value);
	}

	/**
	 * Tries to parse a {@link Double} from the given {@link McpdObject.McpdField}.
	 *
	 * @param field The {@link McpdObject.McpdField}
	 * @return The parsed {@link Double} or null.
	 */
	protected Double getDouble(McpdField field)
	{
		String value = getPart(field);

		if (!StringUtils.isEmpty(value))
		{
			try
			{
				return Double.parseDouble(value);
			}
			catch (NumberFormatException e)
			{
			}
		}

		return null;
	}

	/**
	 * Tries to parse an {@link Integer} from the given {@link McpdObject.McpdField}.
	 *
	 * @param field The {@link McpdObject.McpdField}
	 * @return The parsed {@link Integer} or null.
	 */
	protected Integer getInt(McpdField field)
	{
		String value = getPart(field);

		if (!StringUtils.isEmpty(value))
		{
			try
			{
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
			}
		}

		return null;
	}

	/**
	 * Tries to parse an {@link Integer} from the given {@link McpdObject.McpdField}.
	 *
	 * @param field The {@link McpdObject.McpdField}
	 * @return The parsed {@link Integer} or null.
	 */
	protected Long getLong(McpdField field)
	{
		String value = getPart(field);

		if (!StringUtils.isEmpty(value))
		{
			try
			{
				return Long.parseLong(value);
			}
			catch (NumberFormatException e)
			{
			}
		}

		return null;
	}

	/**
	 * Returns the lat/lng value based on the absence/presence of the DECLATITUTE/LATITUDE/DECLONGITUDE/LONGITUDE fields in the MCPD. The decimals
	 * will be prefered, but the DMS values will be converted as a fallback.
	 *
	 * @param decimal            The decimal lat/lng (can be null)
	 * @param degreeMinuteSecond The DMS lat/lng (can be null)
	 * @return The decimal lat/lng value
	 */
	private static Double getLatitudeLongitude(Double decimal, String degreeMinuteSecond)
	{
		if (decimal != null)
		{
			return decimal;
		}
		else if (!StringUtils.isEmpty(degreeMinuteSecond))
		{
			if (degreeMinuteSecond.length() == 7 || degreeMinuteSecond.length() == 8)
			{
				boolean lat = degreeMinuteSecond.length() == 7;

				Double value = null;

				Integer degree = 0;
				Integer minute = 0;
				Integer second = 0;

				try
				{
					if (lat)
						degree = Integer.parseInt(degreeMinuteSecond.substring(0, 2));
					else
						degree = Integer.parseInt(degreeMinuteSecond.substring(0, 3));
				}
				catch (NumberFormatException e)
				{
					return null;
				}
				try
				{
					if (lat)
						minute = Integer.parseInt(degreeMinuteSecond.substring(2, 4));
					else
						minute = Integer.parseInt(degreeMinuteSecond.substring(3, 5));
				}
				catch (NumberFormatException e)
				{
				}
				try
				{
					if (lat)
						second = Integer.parseInt(degreeMinuteSecond.substring(4, 6));
					else
						second = Integer.parseInt(degreeMinuteSecond.substring(5, 7));
				}
				catch (NumberFormatException e)
				{
				}

				value = degree + minute / 60d + second / 3600d;

				if (value != null && (degreeMinuteSecond.endsWith("S") || degreeMinuteSecond.endsWith("W")))
					value = -value;

				return value;
			}
		}

		return null;
	}
}
