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

package jhi.germinate.client.widget.listbox;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.text.shared.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.*;

import java.io.*;
import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;

/**
 * Implementation of {@link HasConstrainedValue} based on a {@link com.google.gwt.dom.client.SelectElement}. <p> A {@link Renderer Renderer<T>} is
 * used to get user-presentable strings to display in the select element.
 *
 * @param <T> the selected type
 */
public class GerminateValueListBox<T> extends Composite implements Focusable, HasEnabled, HasValueChangeHandlers<List<T>>
{
	private static final int DEFAULT_NUMBER_OF_VISIBLE_ITEMS = 10;

	private boolean multipleSelect = false;

	private final   FlowPanel            content               = new FlowPanel();
	protected final ButtonGroup          additionalButtonPanel = new ButtonGroup();
	private final   Button               selectAll             = new Button(Text.LANG.generalSelectAll());
	private final   List<T>              values                = new ArrayList<>();
	private final   Map<Object, Integer> valueKeyToIndex       = new HashMap<>();
	private final Renderer<? super T> renderer;
	private final ProvidesKey<T>      keyProvider;
	private       TooltipProvider<T>  tooltipProvider;

	private List<T> selected = new ArrayList<>();

	private ListBox listBox       = new ListBox();
	private boolean showSelectAll = true;

	public GerminateValueListBox()
	{
		this(ToStringRenderer.instance());
	}

	public GerminateValueListBox(Renderer<? super T> renderer)
	{
		this(renderer, new SimpleKeyProvider<>());
	}

	public GerminateValueListBox(Renderer<? super T> renderer, ProvidesKey<T> keyProvider)
	{
		this.keyProvider = keyProvider;
		this.renderer = renderer;

		initWidget(content);
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<T>> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void setTooltipProvider(TooltipProvider<T> tooltipProvider)
	{
		this.tooltipProvider = tooltipProvider;
	}

	@Override
	public int getTabIndex()
	{
		return getListBox().getTabIndex();
	}

	public List<T> getValues()
	{
		return values;
	}

	public List<T> getSelections()
	{
		updateSelection();
		return selected;
	}

	public T getSelection()
	{
		updateSelection();
		return CollectionUtils.isEmpty(selected) ? null : selected.get(0);
	}

	/**
	 * This fixes a Chrome bug where the selection change event isn't fired if the number of items matches the number of visible "row/spaces" and the
	 * selection is made by dragging from inside the list box to outside the list box.
	 *
	 * This method has to be called in all cases before returning the selection to the caller.
	 */
	private void updateSelection()
	{
		selected = getUpdatedSelection();
	}

	private List<T> getUpdatedSelection()
	{
		boolean[] selectedIndices = ListBoxUtils.getSelectedIndices(getListBox());

		List<T> selectedItems = new ArrayList<>();

		for (int i = 0; i < selectedIndices.length; i++)
		{
			if (selectedIndices[i])
			{
				T newValue = values.get(i);

				selectedItems.add(newValue);
			}
		}

		return selectedItems;
	}

	@Override
	public boolean isEnabled()
	{
		return getListBox().isEnabled();
	}

	public void setAcceptableValues(Collection<T> newValues)
	{
		values.clear();
		valueKeyToIndex.clear();
		ListBox listBox = getListBox();
		listBox.clear();

		newValues.forEach(this::addValue);

		updateListBox();
	}

	public void setAcceptableValues(T[] newValues)
	{
		setAcceptableValues(Arrays.asList(newValues));
	}

	private void updateTooltips()
	{
		if (tooltipProvider != null)
		{
			SelectElement selectElement = SelectElement.as(getListBox().getElement());

			NodeList<OptionElement> options = selectElement.getOptions();
			for (int i = 0; i < options.getLength(); i++)
			{
				String title = tooltipProvider.getTooltip(values.get(i));

				options.getItem(i).setTitle(title);
			}
		}
	}

	@Override
	public void setAccessKey(char key)
	{
		getListBox().setAccessKey(key);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		getListBox().setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focused)
	{
		getListBox().setFocus(focused);
	}

	@Override
	public void setTabIndex(int index)
	{
		getListBox().setTabIndex(index);
	}

	public void setValue(T value, boolean fireEvents)
	{
		if (value != null)
			setValues(Collections.singletonList(value), fireEvents);
	}

	public void setValues(List<T> value, boolean fireEvents)
	{
//		if (value == this.selected || (this.selected != null && this.selected.equals(value)))
//		{
//			return;
//		}

		List<T> before = this.selected == null ? null : new ArrayList<>(this.selected);
		this.selected = value;
		updateListBox();

		if (fireEvents)
		{
			ValueChangeEvent.fireIfNotEqual(this, before, value);
		}
	}

	public void reselectValue()
	{
		ValueChangeEvent.fire(this, selected);
	}

	/**
	 * Returns the index of the selected item
	 *
	 * @return The index of the selected item or -1 if no item is selected
	 */
	public int getSelectedIndex()
	{
		return values.indexOf(getSelection());
	}

	/**
	 * Clears this {@link GerminateValueListBox}.
	 */
	public void clear()
	{
		selected = null;
		values.clear();
		valueKeyToIndex.clear();
		ListBox listBox = getListBox();
		listBox.clear();
	}

	/**
	 * Selects the item at the given index
	 *
	 * @param index      The index of the element to select
	 * @param fireEvents fire events if true and value is new
	 */
	public void selectItem(int index, boolean fireEvents)
	{
		if (index > 0 && index < values.size() - 1)
			setValue(values.get(index), fireEvents);
	}

	private void addValue(T value)
	{
		Object key = keyProvider.getKey(value);
		if (valueKeyToIndex.containsKey(key))
		{
			throw new IllegalArgumentException("Duplicate selected: " + value);
		}

		valueKeyToIndex.put(key, values.size());
		values.add(value);
		getListBox().addItem(renderer.render(value));
		assert values.size() == getListBox().getItemCount();
	}

	public ListBox getListBox()
	{
		return listBox;
	}

	private void updateListBox()
	{
		for (int i = 0; i < values.size(); i++)
			getListBox().setItemSelected(i, false);

		for (T sel : selected)
		{
			Object key = keyProvider.getKey(sel);
			Integer index = valueKeyToIndex.get(key);
			if (index == null)
			{
				addValue(sel);
			}

			index = valueKeyToIndex.get(key);
			getListBox().setItemSelected(index, true);
		}

		updateTooltips();
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		content.add(listBox);
		content.add(additionalButtonPanel);
		additionalButtonPanel.setVisible(false);
//		additionalButtonPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);

		getListBox().addChangeHandler(event ->
		{
			List<T> selected1 = getUpdatedSelection();

			if (selected1.size() < 1)
			{
				return; // Not sure why this happens during addValue
			}

			setValues(selected1, true);
		});

		if (multipleSelect && showSelectAll)
		{
			additionalButtonPanel.add(selectAll);
			additionalButtonPanel.setVisible(true);
			content.addStyleName(jhi.germinate.shared.Style.LAYOUT_SELECT_BUTTON_COMBO);

			selectAll.setIcon(IconType.CHECK_SQUARE_O);
			selectAll.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			selectAll.addClickHandler(event -> setValues(values, false));
			selectAll.setType(ButtonType.DEFAULT);
		}

		updateTooltips();
	}

	public void setSelectAllVisible(boolean visible)
	{
		showSelectAll = visible;
	}

	public void setSelectAllVisible(String visible)
	{
		setSelectAllVisible(Boolean.parseBoolean(visible));
	}

	public void setMultipleSelect(String multipleSelect)
	{
		setMultipleSelect(Boolean.parseBoolean(multipleSelect));
	}

	public void setMultipleSelect(boolean multipleSelect)
	{
		this.multipleSelect = multipleSelect;

		listBox.setMultipleSelect(multipleSelect);

		if (multipleSelect)
			listBox.setVisibleItemCount(DEFAULT_NUMBER_OF_VISIBLE_ITEMS);
		else
			listBox.setVisibleItemCount(1);
	}

	/**
	 * Tries to select the item with the given display text, i.e. the value returned by the {@link Renderer}. <p/> If no item is found, this method is
	 * a NO-OP
	 *
	 * @param item The display text of the item to select
	 * @see Renderer#render(Object)
	 */
	public void selectItem(String item)
	{
		for (T row : values)
		{
			String name = renderer.render(row);

			if (name.equalsIgnoreCase(item))
			{
				setValue(row, true);
				return;
			}
		}
	}

	public interface TooltipProvider<T>
	{
		String getTooltip(T item);
	}

	public void setId(String id)
	{
		getListBox().setId(id);
	}

	public static abstract class GerminateRenderer<T> implements Renderer<T>
	{
		@Override
		public void render(T object, Appendable appendable) throws IOException
		{
			appendable.append(render(object));
		}
	}

	public static abstract class GerminateUnitRenderer<T> extends GerminateRenderer<T>
	{
		@Override
		public String render(T object)
		{
			String text = getText(object);
			String unit = getUnit(object);
			BracketType type = getBracketType();

			if (type == null)
				type = BracketType.SQUARE;

			StringBuilder builder = new StringBuilder();

			builder.append(text);

			if (!StringUtils.isEmpty(unit))
			{
				builder.append(" ")
					   .append(type.opening)
					   .append(unit)
					   .append(type.closing);
			}

			return builder.toString();
		}

		protected abstract String getText(T object);

		protected abstract String getUnit(T object);

		protected abstract BracketType getBracketType();

		public enum BracketType
		{
			SQUARE("[", "]"),
			ROUND("(", ")");

			private String opening;
			private String closing;

			BracketType(String opening, String closing)
			{
				this.opening = opening;
				this.closing = closing;
			}
		}
	}
}

