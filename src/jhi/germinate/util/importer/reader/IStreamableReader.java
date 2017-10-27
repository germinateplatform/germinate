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
