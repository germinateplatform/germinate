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

package jhi.germinate.util.ui.handler;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public abstract class TemplateDropTarget extends DropTarget
{
	/** Keep track of the files that are currently hovering over the target and are of the correct type */
	private List<File> files = new ArrayList<>();

	@Override
	public synchronized void dragEnter(DropTargetDragEvent evt)
	{
		files.clear();
		Transferable t = evt.getTransferable();

		// Check if it's a list of files
		if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			try
			{
				// Get the list of files
				Object td = t.getTransferData(DataFlavor.javaFileListFlavor);

				if (td instanceof List)
				{
					// For each potential file
					for (Object value : (List) td)
					{
						if (value instanceof File)
						{
							File file = (File) value;
							// Check if it's an excel file
							if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".txt"))
							{
								files.add(file);
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			// Reject as not supported
			evt.rejectDrag();
		}

		// If there are files, accept the drop
		if (files.size() > 0)
			evt.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		else
			evt.rejectDrag();
	}

	@Override
	public synchronized void dragExit(DropTargetEvent dte)
	{
		super.dragExit(dte);
		// Clear files list
		files.clear();
	}

	@Override
	public synchronized void drop(DropTargetDropEvent evt)
	{
		// Deal with files if there are any
		if (files.size() > 0)
		{
			evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			dealWithIt(files);
		}
		else
		{
			evt.rejectDrop();
		}
	}

	/**
	 * Deals with the template files that have been dropped on the targed.
	 *
	 * @param files The {@link List} of dropped template {@link File}s
	 */
	protected abstract void dealWithIt(List<File> files);
}
