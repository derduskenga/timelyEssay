package controllers.web;

import play.*;
import play.mvc.*;
import views.html.home;

public class Application extends Controller{

	public static Result index(){
			return ok(home.render("Views Work Fine"));
	}

}