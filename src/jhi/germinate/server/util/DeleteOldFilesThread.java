/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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

package jhi.germinate.server.util;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class DeleteOldFilesThread extends Thread
{
	/**
	 * Creates a thread that deletes temporary files that are older than a given limit
	 *
	 * @param directory   The directory containing the temporary files
	 * @param timeInHours The maximal lifespan of the temporary files
	 */
	public DeleteOldFilesThread(File directory, Long timeInHours)
	{
		super(() ->
		{
			if (directory != null && timeInHours != null)
			{
				long purgeTime = System.currentTimeMillis() - (timeInHours * 1000 * 60 * 60);

				File[] files = directory.listFiles();

				if (files != null)
				{
					Arrays.stream(files)
						  .filter(f -> f.lastModified() < purgeTime)
						  .forEach(File::delete);
				}
			}
		});
	}
}
