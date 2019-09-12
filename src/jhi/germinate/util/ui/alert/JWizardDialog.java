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
import java.util.*;
import java.util.List;

import javax.swing.*;

/**
 * A wizard dialog based on {@link JDialog} that shows {@link JWizardPage}s.
 * Use {@link #addPage(JWizardPage)} to add pages.
 *
 * @author Sebastian Raubach
 */
public abstract class JWizardDialog extends JDialog
{
	private final JButton cancel;
	private final JButton prev;
	private final JButton next;

	private List<JWizardPage> pages = new ArrayList<>();

	private int    currentPage = 0;
	private JPanel content     = new JPanel();
	private JPanel header      = new JPanel();

	public JWizardDialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);

		setLayout(new BorderLayout());

		JPanel buttonBar = new JPanel();

		cancel = new JButton("Cancel");
		prev = new JButton("Previous");
		next = new JButton("Next");

		buttonBar.setLayout(new FlowLayout(FlowLayout.TRAILING));
		buttonBar.add(cancel);
		buttonBar.add(prev);
		buttonBar.add(next);
		add(header, BorderLayout.PAGE_START);
		add(content, BorderLayout.CENTER);
		add(buttonBar, BorderLayout.PAGE_END);
		header.setLayout(new FlowLayout());
		header.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
		content.setLayout(new BorderLayout());
		content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		setPreferredSize(getPreferedSize());

		addButtonHandlers();

		pack();
	}

	public void addPage(JWizardPage page)
	{
		pages.add(page);
	}

	@Override
	public void setVisible(boolean show)
	{
		if (show && pages.size() > 0)
			updateContent();

		super.setVisible(show);
	}

	private void addButtonHandlers()
	{
		prev.addActionListener(e -> {
			if (currentPage > 0)
				currentPage--;
			updateContent();
		});
		next.addActionListener(e -> {
			if (pages.get(currentPage).canContinue())
			{
				if ((currentPage <= pages.size() - 1))
					currentPage++;
				updateContent();
			}
			else
			{
				// TODO: indicate why not
			}
		});
		cancel.addActionListener(e -> JWizardDialog.this.dispose());
	}

	private void updateContent()
	{
		updateButtons();
		if (currentPage <= pages.size() - 1)
		{
			content.removeAll();
			content.add(pages.get(currentPage), BorderLayout.CENTER);
			content.doLayout();
			header.removeAll();
			JLabel label = new JLabel(pages.get(currentPage).getTitle());
			label.setFont(label.getFont().deriveFont(Font.BOLD, 24));
			header.add(label);
			header.doLayout();
			repaint();
		}
		else
		{
			onFinish(pages);
			dispose();
		}
	}

	private void updateButtons()
	{
		prev.setEnabled(currentPage > 0);
		next.setText(currentPage < pages.size() - 1 ? "Next" : "Finish");
	}

	protected abstract void onFinish(List<JWizardPage> pages);

	protected abstract Dimension getPreferedSize();
}
