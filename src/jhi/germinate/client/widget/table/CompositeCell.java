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

package jhi.germinate.client.widget.table;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;

import java.util.*;

/**
 * <p> A {@link Cell} that is composed of other {@link Cell}s. </p>
 *
 * <p> When this cell is rendered, it will render each component {@link Cell} inside a span. If the component {@link Cell} uses block level elements
 * (such as a Div), the component cells will stack vertically. </p>
 *
 * @param <T> the type that this Cell represents
 */
public class CompositeCell<T> extends AbstractCell<T>
{
	/** The events consumed by this cell. */
	private Set<String> consumedEvents;

	/** Indicates whether or not this cell depends on selection. */
	private boolean dependsOnSelection;

	/** Indicates whether or not this cell handles selection. */
	private boolean handlesSelection;

	/**
	 * The cells that compose this {@link Cell}.
	 *
	 * NOTE: Do not add add/insert/remove hasCells methods to the API. This cell assumes that the index of the cellParent corresponds to the index in
	 * the hasCells array.
	 */
	private final List<HasCell<T, ?>> hasCells;

	/**
	 * Construct a new {@link CompositeCell}.
	 *
	 * @param hasCells the cells that makeup the composite
	 */
	public CompositeCell(List<HasCell<T, ?>> hasCells)
	{
		// Create a new array so cells cannot be added or removed.
		this.hasCells = new ArrayList<>(hasCells);

		// Get the consumed events and depends on selection.
		Set<String> theConsumedEvents = null;
		for (HasCell<T, ?> hasCell : hasCells)
		{
			Cell<?> cell = hasCell.getCell();
			Set<String> events = cell.getConsumedEvents();
			if (events != null)
			{
				if (theConsumedEvents == null)
				{
					theConsumedEvents = new HashSet<>();
				}
				theConsumedEvents.addAll(events);
			}
			if (cell.dependsOnSelection())
			{
				dependsOnSelection = true;
			}
			if (cell.handlesSelection())
			{
				handlesSelection = true;
			}
		}
		if (theConsumedEvents != null)
		{
			this.consumedEvents = Collections.unmodifiableSet(theConsumedEvents);
		}
	}

	@Override
	public boolean dependsOnSelection()
	{
		return dependsOnSelection;
	}

	@Override
	public Set<String> getConsumedEvents()
	{
		return consumedEvents;
	}

	@Override
	public boolean handlesSelection()
	{
		return handlesSelection;
	}

	@Override
	public boolean isEditing(Context context, Element parent, T value)
	{
		Element curChild = getContainerElement(parent).getFirstChildElement();
		for (HasCell<T, ?> hasCell : hasCells)
		{
			if (isEditingImpl(context, curChild, value, hasCell))
			{
				return true;
			}
			curChild = curChild.getNextSiblingElement();
		}
		return false;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, T value,
							   NativeEvent event, ValueUpdater<T> valueUpdater)
	{
		int index = 0;
		EventTarget eventTarget = event.getEventTarget();
		if (Element.is(eventTarget))
		{
			Element target = eventTarget.cast();
			Element container = getContainerElement(parent);
			Element wrapper = container.getFirstChildElement();
			while (wrapper != null)
			{
				if (wrapper.isOrHasChild(target))
				{
					onBrowserEventImpl(context, wrapper, value, event, valueUpdater,
							hasCells.get(index));
				}

				index++;
				wrapper = wrapper.getNextSiblingElement();
			}
		}
	}

	@Override
	public void render(Context context, T value, SafeHtmlBuilder sb)
	{
		for (HasCell<T, ?> hasCell : hasCells)
		{
			render(context, value, sb, hasCell);
		}
	}

	@Override
	public boolean resetFocus(Context context, Element parent, T value)
	{
		Element curChild = getContainerElement(parent).getFirstChildElement();
		for (HasCell<T, ?> hasCell : hasCells)
		{
			// The first child that takes focus wins. Only one child should ever be in
			// edit mode, so this is safe.
			if (resetFocusImpl(context, curChild, value, hasCell))
			{
				return true;
			}
			curChild = curChild.getNextSiblingElement();
		}
		return false;
	}

	@Override
	public void setValue(Context context, Element parent, T object)
	{
		Element curChild = getContainerElement(parent).getFirstChildElement();
		for (HasCell<T, ?> hasCell : hasCells)
		{
			setValueImpl(context, curChild, object, hasCell);
			curChild = curChild.getNextSiblingElement();
		}
	}

	/**
	 * Get the element that acts as the container for all children. If children are added directly to the parent, the parent is the container. If
	 * children are added in a table row, the row is the parent.
	 *
	 * @param parent the parent element of the cell
	 * @return the container element
	 */
	protected Element getContainerElement(Element parent)
	{
		return parent;
	}

	/**
	 * Render the composite cell as HTML into a {@link SafeHtmlBuilder}, suitable for passing to {@link Element#setInnerHTML} on a container element.
	 *
	 * <p> Note: If your cell contains natively focusable elements, such as buttons or input elements, be sure to set the tabIndex to -1 so that they
	 * do not steal focus away from the containing widget. </p>
	 *
	 * @param context the {@link com.google.gwt.cell.client.Cell.Context Context} of the cell
	 * @param value   the cell value to be rendered
	 * @param sb      the {@link SafeHtmlBuilder} to be written to
	 * @param hasCell a {@link HasCell} instance containing the cells to be rendered within this cell
	 */
	protected <S> void render(Context context, T value, SafeHtmlBuilder sb, HasCell<T, S> hasCell)
	{
		Cell<S> cell = hasCell.getCell();
		sb.appendHtmlConstant("<span>");
		cell.render(context, hasCell.getValue(value), sb);
		sb.appendHtmlConstant("</span>");
	}

	private <S> boolean isEditingImpl(Context context, Element cellParent, T object, HasCell<T, S> hasCell)
	{
		return hasCell.getCell().isEditing(context, cellParent, hasCell.getValue(object));
	}

	private <S> void onBrowserEventImpl(final Context context, Element parent, final T object, NativeEvent event, final ValueUpdater<T> valueUpdater, final HasCell<T, S> hasCell)
	{
		Cell<S> cell = hasCell.getCell();
		String eventType = event.getType();
		Set<String> cellConsumedEvents = cell.getConsumedEvents();
		if (cellConsumedEvents == null || !cellConsumedEvents.contains(eventType))
		{
			// If this sub-cell doesn't consume this event.
			return;
		}
		ValueUpdater<S> tempUpdater = null;
		final FieldUpdater<T, S> fieldUpdater = hasCell.getFieldUpdater();
		if (fieldUpdater != null)
		{
			tempUpdater = value ->
			{
				fieldUpdater.update(context.getIndex(), object, value);
				if (valueUpdater != null)
				{
					valueUpdater.update(object);
				}
			};
		}
		cell.onBrowserEvent(context, parent, hasCell.getValue(object), event, tempUpdater);
	}

	private <S> boolean resetFocusImpl(Context context, Element cellParent, T value, HasCell<T, S> hasCell)
	{
		S cellValue = hasCell.getValue(value);
		return hasCell.getCell().resetFocus(context, cellParent, cellValue);
	}

	private <S> void setValueImpl(Context context, Element cellParent, T object, HasCell<T, S> hasCell)
	{
		hasCell.getCell().setValue(context, cellParent, hasCell.getValue(object));
	}

	public Cell<?> getCell(int index)
	{
		if (index < 0 || index >= hasCells.size())
			return null;
		else
			return hasCells.get(index).getCell();
	}

	public List<Cell<?>> getCells()
	{
		List<Cell<?>> result = new ArrayList<>();

		for (HasCell hasCell : hasCells)
		{
			result.add(hasCell.getCell());
		}

		return result;
	}
}
