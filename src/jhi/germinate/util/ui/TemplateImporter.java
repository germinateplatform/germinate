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

package jhi.germinate.util.ui;

import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.*;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import jhi.germinate.util.ui.alert.*;
import jhi.germinate.util.ui.handler.*;

/**
 * @author Sebastian Raubach
 */
public class TemplateImporter extends JFrame
{
	public TemplateImporter()
	{
		initUI();
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(() -> {
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
			{
			}

			TemplateImporter ex = new TemplateImporter();
			ex.setVisible(true);
		});
	}

	private void initUI()
	{
		setTitle("Germinate Template Importer");
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Get the container, then listen to drop events
		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		container.setDropTarget(new TemplateDropTarget()
		{
			@Override
			protected void dealWithIt(List<File> files)
			{
				if (files.size() > 0)
					dealWithTemplate(files.get(0));
			}
		});

		// Add a button to manually select the file with a dialog
		JButton button = new JButton("Drag file here or click to select");
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setFont(button.getFont().deriveFont(Font.BOLD, 24));
		button.addActionListener(e -> {
			// Create a file chooser
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Choose template file");
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(new FileFilter()
			{
				@Override
				public boolean accept(File f)
				{
					// Only accept Excel spreadsheets
					return f.isDirectory() || f.getName().endsWith(".xlsx");
				}

				@Override
				public String getDescription()
				{
					return "Germinate Templates (.xlsx)";
				}
			});
			fileChooser.addChoosableFileFilter(new FileFilter()
			{
				@Override
				public boolean accept(File f)
				{
					// Only accept Excel spreadsheets
					return f.isDirectory() || f.getName().endsWith(".txt");
				}

				@Override
				public String getDescription()
				{
					return "Germinate Genotype Text Template (.txt)";
				}
			});

			// Open it
			int result = fileChooser.showOpenDialog(button);

			// If the user selected a file, deal with it
			if (result == JFileChooser.APPROVE_OPTION)
				dealWithTemplate(fileChooser.getSelectedFile());
		});
		container.add(button);
	}

	/**
	 * Start the import process of the given file.
	 *
	 * @param file The {@link File} to import
	 */
	private void dealWithTemplate(File file)
	{
		TemplateType type = getType(file);

		// If the type couldn't be determined automatically, ask the user what the type is
		if (type == TemplateType.unknown)
			type = AlertUtils.askForTemplateType(this);

		if (type != null)
		{
			final TemplateType t = type;
			// Create a wizard to guide the user through the import process
			JWizardDialog wizard = new JWizardDialog(this, type.description, true)
			{
				@Override
				protected void onFinish(List<JWizardPage> pages)
				{
					for (JWizardPage page : pages)
					{
						if (page instanceof DatabasePage)
						{
							DatabasePage db = (DatabasePage) page;

							String server = db.getServer();
							String database = db.getDatabase();
							String port = db.getPort();
							String username = db.getUsername();
							String password = db.getPassword();

							final JDialog dlg = new JDialog(TemplateImporter.this, "Data import", true);
							JPanel panel = new JPanel();
							panel.setLayout(new BorderLayout());
							panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
							JProgressBar dpb = new JProgressBar(0, 100);
							dpb.setIndeterminate(true);
							panel.add(BorderLayout.CENTER, dpb);
							panel.add(BorderLayout.NORTH, new JLabel("Importing..."));
							dlg.add(BorderLayout.CENTER, panel);
							dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
							dlg.setSize(300, 95);
							dlg.setLocationRelativeTo(TemplateImporter.this);

							new SwingWorker<String, Object>()
							{
								@Override
								protected String doInBackground()
								{
									SwingUtilities.invokeLater(() -> dlg.setVisible(true));

									try
									{
										t.callImporter(file, server, database, port, username, password);
									}
									catch (Exception e)
									{
										new ExceptionDialog(TemplateImporter.this, "Data import failed. Error message below.", new RuntimeException(e.getLocalizedMessage())).setVisible(true);
									}

									return null;
								}

								@Override
								protected void done()
								{
									super.done();
									SwingUtilities.invokeLater(dlg::dispose);
								}
							}.execute();
						}
					}
				}

				@Override
				protected Dimension getPreferedSize()
				{
					return new Dimension(500, 350);
				}
			};
			// Add the intro page
			wizard.addPage(new IntroPage(type));
			// Add the database setup page
			wizard.addPage(new DatabasePage());

			// Position, then show
			wizard.setLocationRelativeTo(this);
			wizard.setVisible(true);
		}
	}

	/**
	 * Tries to determine the {@link TemplateType} of the given file.
	 *
	 * @param file The {@link File} to check
	 * @return The {@link TemplateType} of the file or {@link TemplateType#unknown} if it cannot be determined.
	 */
	private TemplateType getType(File file)
	{
		try
		{
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			return TemplateType.valueOf(workbook.getProperties().getCoreProperties().getCategory());
		}
		catch (Exception e)
		{
			return TemplateType.unknown;
		}
	}
}
