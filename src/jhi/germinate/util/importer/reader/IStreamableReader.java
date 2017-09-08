/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.reader;

import java.io.*;

/**
 * {@link IStreamableReader} extends {@link IDataReader} and should be used in cases where you can parse and stream {@link McpdObject} objects one
 * by one.
 *
 * @author Sebastian Raubach
 */
public interface IStreamableReader<T> extends IDataReader
{
	/**
	 * Returns <code>true</code> if there are more objects to stream.
	 *
	 * @return <code>true</code> if there are more objects to stream.
	 * @throws IOException Thrown if the I/O fails.
	 */
	boolean hasNext() throws IOException;

	/**
	 * Returns the next objects.
	 *
	 * @return The next objects.
	 * @throws IOException Thrown if the I/O fails.
	 */
	T next() throws IOException;
}
