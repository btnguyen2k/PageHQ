package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public class Frontend extends Controller {

	public static Result index() {
		return Results.ok(views.html.Frontend.index.render());
	}

}
