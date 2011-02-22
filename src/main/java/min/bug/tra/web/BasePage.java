package min.bug.tra.web;

import org.apache.wicket.markup.html.WebPage;

public class BasePage extends WebPage {
	public BasePage() {
		getResponse().setCharacterEncoding("UTF-8");
	}
}
