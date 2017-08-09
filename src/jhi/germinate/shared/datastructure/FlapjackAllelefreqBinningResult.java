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

package jhi.germinate.shared.datastructure;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class FlapjackAllelefreqBinningResult implements Serializable
{
	private static final long serialVersionUID = 928555372594183865L;

	private String      debugOutput;
	//	private String      projectFile;
//	private String      mapFile;
//	private String      rawDataFile;
	private String      histogramFile;
	private Set<String> deletedMarkers;

	public FlapjackAllelefreqBinningResult()
	{

	}

	public String getDebugOutput()
	{
		return debugOutput;
	}

	public FlapjackAllelefreqBinningResult setDebugOutput(String debugOutput)
	{
		this.debugOutput = debugOutput;
		return this;
	}

	//	public String getProjectFile()
//	{
//		return projectFile;
//	}
//
//	public FlapjackProjectCreationResult setProjectFile(String projectFile)
//	{
//		this.projectFile = projectFile;
//		return this;
//	}
//
//	public String getMapFile()
//	{
//		return mapFile;
//	}
//
//	public FlapjackProjectCreationResult setMapFile(String mapFile)
//	{
//		this.mapFile = mapFile;
//		return this;
//	}
//
//	public String getRawDataFile()
//	{
//		return rawDataFile;
//	}
//
//	public FlapjackProjectCreationResult setRawDataFile(String rawDataFile)
//	{
//		this.rawDataFile = rawDataFile;
//		return this;
//	}
//
	public String getHistogramFile()
	{
		return histogramFile;
	}

	public FlapjackAllelefreqBinningResult setHistogramFile(String histogramFile)
	{
		this.histogramFile = histogramFile;
		return this;
	}

	public Set<String> getDeletedMarkers()
	{
		return deletedMarkers;
	}

	public FlapjackAllelefreqBinningResult setDeletedMarkers(Set<String> deletedMarkers)
	{
		this.deletedMarkers = deletedMarkers;
		return this;
	}
}
