package controllers.admin;

import play.*;
import play.mvc.*;
import views.html.*;


public class AdminActions extends Controller{

	public static Result index(){
			return ok(adminhome.render());
	}

}