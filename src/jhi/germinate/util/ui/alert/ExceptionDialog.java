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

package jhi.germinate.util.ui.alert;

import java.awt.*;
import java.io.*;

import javax.swing.*;

/**
 * @author Sebastian Raubach
 */
public class ExceptionDialog extends JDialog
{
	public ExceptionDialog(Frame owner, String message, Exception e)
	{
		super(owner, "Error", true);

		setLayout(new BorderLayout());

		JLabel header = new JLabel();
		JTextArea area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		JPanel buttonBar = new JPanel();

		scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		header.setText(message);

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();

		area.setText(stackTrace);
		area.setCaretPosition(0);
		area.setEditable(false);

		JButton ok = new JButton("OK");
		ok.addActionListener(event -> ExceptionDialog.this.dispose());

		add(header, BorderLayout.PAGE_START);
		add(scroll, BorderLayout.CENTER);
		add(buttonBar, BorderLayout.PAGE_END);

		buttonBar.setLayout(new FlowLayout(FlowLayout.TRAILING));
		buttonBar.add(ok);

		setMinimumSize(new Dimension(400, 200));
	}
}
