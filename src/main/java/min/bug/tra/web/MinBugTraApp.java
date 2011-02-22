package min.bug.tra.web;

import min.bug.tra.domain.TaskRepository;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

public class MinBugTraApp extends WebApplication {

	@Override
	protected void init() {
		super.init();
		mountBookmarkablePage("list", ListPage.class);
		mountBookmarkablePage("edit", EditPage.class);
	}

	@Override
	public Class<? extends WebPage> getHomePage() {
		return ListPage.class;
	}

}
