package min.bug.tra.web;

import java.util.Arrays;

import min.bug.tra.domain.Task;
import min.bug.tra.domain.TaskRepository;
import min.bug.tra.domain.Task.TaskStatus;
import min.bug.tra.domain.Task.TaskType;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

public class EditPage extends BasePage {

	public EditPage() {
		IModel<Task> model = new CompoundPropertyModel<Task>(
				new LoadableDetachableModel<Task>() {

					@Override
					protected Task load() {
						return new Task(null, "", "", TaskType.BUG,
								TaskStatus.OPEN);
					}
				});

		build(model, false);
	}

	public EditPage(final PageParameters params) {
		final Long taskId = params.getLong("task");

		IModel<Task> model = new CompoundPropertyModel<Task>(
				new LoadableDetachableModel<Task>() {
					@Override
					protected Task load() {
						return new TaskRepository().selectById(taskId);
					}
				});
		build(model, true);
	}

	private void build(IModel<Task> model, final boolean edit) {
		add(new BookmarkablePageLink<Void>("link", ListPage.class));
		add(new FeedbackPanel("feedback"));

		Form<Task> form = new Form<Task>("form", model) {
			@Override
			protected void onSubmit() {
				Task p = getModelObject();
				TaskRepository dao = new TaskRepository();
				if (edit)
					dao.update(p);
				else
					dao.insert(p);
				setResponsePage(ListPage.class);
			}

		};

		DropDownChoice<TaskType> taskType = new DropDownChoice<TaskType>(
				"type", Arrays.asList(TaskType.values()), TASK_TYPE_RENDERER);
		form.add(taskType);

		RadioChoice<TaskStatus> rc = new RadioChoice<TaskStatus>("status",
				Arrays.asList(TaskStatus.values()), TASK_STATUS_RENDERER);

		rc.setEnabled(edit);

		form.add(rc);

		TextField<String> firstNameField = new TextField<String>("title");
		firstNameField.setRequired(true);

		form.add(firstNameField);

		TextArea<String> lastNameField = new TextArea<String>("description");
		form.add(lastNameField);

		Button submit = new Button("submit", new Model<String>(
				edit ? "MettreAJour()" : "Ajouter()"));
		form.add(submit);

		add(form);
	}

	private static final IChoiceRenderer<TaskType> TASK_TYPE_RENDERER = new IChoiceRenderer<TaskType>() {

		public Object getDisplayValue(TaskType type) {
			switch (type) {
			case BUG:
				return "Bug";
			case NEW_FEATURE:
				return "Nouvelle fonctionnalité";
			case ENHANCEMENT:
				return "Amélioration";
			}
			return null;
		}

		public String getIdValue(TaskType type, int arg1) {
			return type.name();
		}

	};

	private static final IChoiceRenderer<TaskStatus> TASK_STATUS_RENDERER = new IChoiceRenderer<TaskStatus>() {

		public Object getDisplayValue(TaskStatus type) {
			switch (type) {
			case OPEN:
				return "Ouvert";
			case CLOSED:
				return "Fermé";
			}
			return null;
		}

		public String getIdValue(TaskStatus type, int arg1) {
			return type.name();
		}

	};

}
