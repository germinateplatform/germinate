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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.TextArea;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class CommentWidget extends Composite
{
	interface CommentWidgetUiBinder extends UiBinder<HTMLPanel, CommentWidget>
	{
	}

	private static CommentWidgetUiBinder ourUiBinder = GWT.create(CommentWidgetUiBinder.class);

	@UiField
	HTML               html;
	@UiField
	SimplePanel        commentTablePanel;
	@UiField
	FlowPanel          addPanel;
	@UiField
	CommentTypeListBox commentTypeBox;
	@UiField
	TextArea           commentBox;

	private Long         id;
	private CommentTable commentTable;
	private List<CommentType> annotationTypes = new ArrayList<>();

	public CommentWidget()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		html.setHTML(Text.LANG.annotationsText());

		if (!ModuleCore.getUseAuthentication())
			addPanel.removeFromParent();
	}

	public CommentWidget(Long id, GerminateDatabaseTable table)
	{
		this();

		update(id, table);
	}

	public void update(Long id, GerminateDatabaseTable gTable)
	{
		this.id = id;

		if (commentTypeBox.getListBox().getItemCount() < 1)
		{
			/* Set up the callback object for the annotation data */
			CommentService.Inst.get().getTypes(Cookie.getRequestProperties(), gTable, new DefaultAsyncCallback<ServerResult<List<CommentType>>>()
			{
				@Override
				public void onFailureImpl(Throwable caught)
				{
					annotationTypes.clear();

					super.onFailureImpl(caught);
				}

				@Override
				public void onSuccessImpl(ServerResult<List<CommentType>> result)
				{
					annotationTypes.clear();

					if (result.getServerResult().size() > 0)
					{
						annotationTypes = result.getServerResult();

						if (!CollectionUtils.isEmpty(annotationTypes))
						{
							commentTypeBox.setValue(annotationTypes.get(0), false);
							commentTypeBox.setAcceptableValues(annotationTypes);
						}
					}
				}
			});
		}

		if (commentTable == null)
		{
			commentTable = new CommentTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Comment>>> callback)
				{
					filter = addToFilter(filter);

					return CommentService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, pagination, callback);
				}

				private PartialSearchQuery addToFilter(PartialSearchQuery filter)
				{
					try
					{
						if (filter == null)
							filter = new PartialSearchQuery();
						SearchCondition condition = new SearchCondition();
						condition.setColumnName(Comment.REFERENCE_ID);
						condition.setComp(new Equal());
						condition.addConditionValue(Long.toString(id));
						condition.setType(Long.class.getSimpleName());
						filter.add(condition);

						if (filter.getAll().size() > 1)
						{
							if (filterOperatorButton.getValue())
								filter.addLogicalOperator(new And());
							else
								filter.addLogicalOperator(new Or());
						}

						condition = new SearchCondition();
						condition.setColumnName(CommentType.REFERENCE_TABLE);
						condition.setComp(new Equal());
						condition.addConditionValue(gTable.name());
						condition.setType(String.class.getSimpleName());
						filter.add(condition);

						if (filter.getAll().size() > 1)
						{
							if (filterOperatorButton == null || filterOperatorButton.getValue())
								filter.addLogicalOperator(new And());
							else
								filter.addLogicalOperator(new Or());
						}
					}
					catch (InvalidArgumentException | InvalidSearchQueryException e)
					{
						e.printStackTrace();
					}

					return filter;
				}
			};

			commentTablePanel.add(commentTable);
		}
		else
		{
			commentTable.refreshTable();
		}
	}

	@UiHandler("submitButton")
	void onSubmitButtonClicked(ClickEvent e)
	{
		/* Set up a callback to add a new annotation */
		CommentType type = commentTypeBox.getSelection();

		String annotationText = commentBox.getText();

		if (StringUtils.isEmpty(annotationText) || Text.LANG.generalEnterAnnotation().equals(annotationText))
			return;

		CommentService.Inst.get().add(Cookie.getRequestProperties(), type, id, annotationText, new DefaultAsyncCallback<DebugInfo>()
		{
			@Override
			public void onSuccessImpl(DebugInfo result)
			{

				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.ANNOTATIONS, "add", Long.toString(id));

				commentTable.refreshTable();

				commentBox.clear();
			}
		});
	}
}