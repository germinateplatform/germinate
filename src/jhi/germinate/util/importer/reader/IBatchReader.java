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
import java.util.*;

/**
 * The {@link IBatchReader} extends {@link IDataReader} and should be used for readers that need the whole file first and then return all items in one
 * go. I.e. Use this if your implementation cannot stream the items one by one.
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
