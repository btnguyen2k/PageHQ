package models;

import play.data.validation.Constraints.Required;

public class Page {

	@Required
	public String title = "MyPages";
	
	@Required
	public String keywords = "facebook, page, fanpage, page manager, fanpage manager";
	
	@Required
	public String description = "Fanpage Management Tool";

	public Page() {
	}

}