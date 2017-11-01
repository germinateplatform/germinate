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

package jhi.germinate.client.widget.element;

import com.google.gwt.user.client.ui.Widget;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public abstract class ModalWizard
{
	private Modal       modal        = new Modal();
	private ModalBody   body         = new ModalBody();
	private Progress    progress     = new Progress();
	private ProgressBar progressBar  = new ProgressBar();
	private Alert       errorMessage = new Alert("", AlertType.DANGER);
	private FlowPanel   content      = new FlowPanel();
	private ModalFooter footer       = new ModalFooter();
	private Button      nextButton   = new Button(Text.LANG.generalNext(), event -> updateContent(true));
	private Button      backButton   = new Button(Text.LANG.generalBack(), event -> updateContent(false));

	private int          position = 0;
	private List<Widget> widgets  = new ArrayList<>();

	public ModalWizard()
	{
		backButton.addStyleName(Style.mdiLg(Style.MDI_ARROW_LEFT_BOLD));
		nextButton.addStyleName(Style.mdiLg(Style.MDI_ARROW_RIGHT_BOLD));
		modal.add(body);
		modal.add(footer);
		body.add(progress);
		progress.add(progressBar);
		body.add(errorMessage);
		body.add(content);
		footer.add(backButton);
		footer.add(nextButton);

		backButton.setType(ButtonType.DEFAULT);
		nextButton.setType(ButtonType.PRIMARY);

		modal.setRemoveOnHide(true);
		modal.setClosable(true);
		modal.setFade(true);
		modal.setDataBackdrop(ModalBackdrop.STATIC);
		modal.setDataKeyboard(true);

		progressBar.setType(ProgressBarType.INFO);
		progressBar.setPercent(0);

		errorMessage.setVisible(false);

		content.getElement().getStyle().setProperty("maxHeight", "70vh");
		content.getElement().getStyle().setOverflowY(com.google.gwt.dom.client.Style.Overflow.AUTO);
	}

	public void add(Widget widget)
	{
		widgets.add(widget);
	}

	public void open()
	{
		if (!CollectionUtils.isEmpty(widgets))
		{
			for (Widget widget : widgets)
			{
				widget.setVisible(false);
				content.add(widget);
			}

			widgets.get(0).setVisible(true);
		}

		updateControls();

		modal.show();
	}

	public void close()
	{
		modal.hide();
	}

	public void setTitle(String title)
	{
		modal.setTitle(title);
	}

	private void updateContent(boolean next)
	{
		if (next)
		{
			// Go to the next page
			if (position < widgets.size() - 1)
			{
				widgets.get(position).setVisible(false);
				position++;
				widgets.get(position).setVisible(true);
			}
			else
			{
				if (onFinished())
					modal.hide();
			}
		}
		else
		{
			// Go to the previous page
			if (position > 0)
			{
				widgets.get(position).setVisible(false);
				position--;
				widgets.get(position).setVisible(true);
			}
		}

		updateControls();
	}

	protected void updateControls()
	{
		// Update the button text
		if (position == widgets.size() - 1)
			nextButton.setText(Text.LANG.generalDone());
		else
			nextButton.setText(Text.LANG.generalNext());

		// Get the status to figure out what controls should be enabled
		NavigationStatus status = getNavigationStatus();

		// Set progress
		progressBar.setPercent(100.0 / widgets.size() * (position + 1));
		progressBar.setText(Text.LANG.wizardProgress(position + 1, widgets.size()));

		// Update controls based on child config
		nextButton.setEnabled(status.canGoForward);
		backButton.setEnabled(status.canGoBackward);
		errorMessage.setVisible(status.errorMessage != null);
		errorMessage.setText(status.errorMessage);

		// This overrules what we get from the child
		backButton.setEnabled(position > 0);
		backButton.setVisible(position > 0);
	}

	protected int getPageCount()
	{
		return widgets.size();
	}

	protected int getCurrentPage()
	{
		return position;
	}

	protected abstract NavigationStatus getNavigationStatus();

	protected abstract boolean onFinished();

	protected class NavigationStatus
	{
		private boolean canGoForward;
		private boolean canGoBackward;
		private String  errorMessage;

		public NavigationStatus()
		{
		}

		public NavigationStatus(boolean canGoForward, boolean canGoBackward, String errorMessage)
		{
			this.canGoForward = canGoForward;
			this.canGoBackward = canGoBackward;
			this.errorMessage = errorMessage;
		}

		public boolean isCanGoForward()
		{
			return canGoForward;
		}

		public void setCanGoForward(boolean canGoForward)
		{
			this.canGoForward = canGoForward;
		}

		public boolean isCanGoBackward()
		{
			return canGoBackward;
		}

		public void setCanGoBackward(boolean canGoBackward)
		{
			this.canGoBackward = canGoBackward;
		}

		public String getErrorMessage()
		{
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage)
		{
			this.errorMessage = errorMessage;
		}
	}
}