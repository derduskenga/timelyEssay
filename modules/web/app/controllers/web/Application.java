package controllers.web;

import play.data.Form;
import java.util.List;
import play.db.ebean.Model;
import play.*;
import play.mvc.*;
import views.html.home;
import models.Client;

import static play.data.Form.form;

public class Application extends Controller{
	static Form<Login> loginForm = form(Login.class);
	public static Result index(){
		return ok(home.render(loginForm));

	}
	
	public static Result authenticate() {
			
			Form<Login> loginForm = form(Login.class).bindFromRequest();
			String email= loginForm.get().email;
			String password = loginForm.get().password;
			if(loginForm.hasErrors() || loginForm.hasErrors()) {
				flash("error", "Wrong email/password");
				return badRequest(home.render(form(Login.class)));
			}
			if (Client.authenticate(email, password) == null){
				flash("error", "Wrong email/password");
				return badRequest(home.render(form(Login.class)));
			}
			//Context context = Context.current();
			//String referrer = context.session().get("referrer");
			//session().clear();
			//Users user = Users.getUser(username);
			session("username", email);
			//session("f_name", user.first_name);
			//session("l_name", user.last_name);
			return redirect(routes.Application.index());	
	}
	
	public static class Login {
		public String email;
		public String password;
	}

}