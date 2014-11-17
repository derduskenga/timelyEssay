package controllers.admin;

import play.*;
import play.mvc.*;
import views.html.*;


public class Application extends Controller{

	public static Result index(){
			Logger.info("Kenga");
			return ok("Works Fine");
	}

}