package min.bug.tra.web;

import java.util.List;

import min.bug.tra.domain.Task;
import min.bug.tra.domain.TaskRepository;
import min.bug.tra.domain.Task.TaskStatus;
import min.bug.tra.domain.Task.TaskType;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

public class ListPage extends BasePage {
	private static final ResourceReference BUG_IMAGE = new ResourceReference(
			ListPage.class, "pics/circle_red.png");
	private static final ResourceReference ENHANCEMENT_IMAGE = new ResourceReference(
			ListPage.class, "pics/circle_blue.png");
	private static final ResourceReference NEW_FEATURE_IMAGE = new ResourceReference(
			ListPage.class, "pics/circle_green.png");

	public ListPage() {
		add(new BookmarkablePageLink<EditPage>("link", EditPage.class));
		add(new FeedbackPanel("feedback"));

		IModel<List<Task>> listModel = new LoadableDetachableModel<List<Task>>() {
			@Override
			protected List<Task> load() {
				return new TaskRepository().selectAll();
			}
		};
		ListView<Task> list = new ListView<Task>("task", listModel) {

			@Override
			protected void populateItem(final ListItem<Task> item) {
				final Task t = item.getModelObject();
				if (t.getStatus() == TaskStatus.CLOSED) {
					item
							.add(new SimpleAttributeModifier("class",
									"task closed"));
				}
				item
						.add(new Image(
								"type",
								t.getType() == TaskType.BUG ? BUG_IMAGE
										: t.getType() == TaskType.ENHANCEMENT ? ENHANCEMENT_IMAGE
												: NEW_FEATURE_IMAGE));
				item.add(new Label("title", t.getTitle()));
				item.add(new Label("description", t.getDescription().replace(
						"\n", "<br />")).setEscapeModelStrings(false));
				item
						.add(new BookmarkablePageLink<EditPage>("editLink",
								EditPage.class, new PageParameters("task="
										+ t.getId())));
				Link<Long> deleteLink = new Link<Long>("deleteLink",
						new Model<Long>(t.getId())) {
					@Override
					public void onClick() {
						new TaskRepository().deleteById(getModelObject());
					}

				};
				deleteLink
						.add(new SimpleAttributeModifier("onclick",
								"return confirm('Supprimer ["
										+ t.getTitle().replace("\"", "\\\"")
										+ "] ?');"));
				item.add(deleteLink);
			}
		};
		add(list);
	}
}
