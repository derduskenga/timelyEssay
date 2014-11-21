package controllers.web.client;

import play.*;
import play.mvc.*;
import views.html.clientarea.*;

public class ClientActions extends Controller{

	public static Result index(){
			return ok(clienthome.render());
	}
	
	//path to messages
	public static Result messages(){
			return ok(clientmessages.render());
	}

}