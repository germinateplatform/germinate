/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.reader;

import java.io.*;
import java.util.*;

/**
 * The {@link IBatchReader} extends {@link IDataReader} and should be used for readers that need the whole file first and then return all items in
 * one go. I.e. Use this if your implementation cannot stream the items one by one.
 *
 * @author Sebastian Raubach
 */
public interface IBatchReader<T> extends IDataReader
{
	/**
	 * Reads from the given {@link InputStream} and returns a {@link List} of objects.
	 *
	 * @return A {@link List} of objects
	 * @throws IOException Thrown if the file interaction fails
	 */
	List<T> readAll() throws IOException;
}
