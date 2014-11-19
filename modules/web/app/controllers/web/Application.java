package controllers.web;

import play.data.Form;
import java.util.List;
import play.db.ebean.Model;
import play.*;
import play.mvc.*;
import views.html.home;
import models.Client;
import play.data.validation.Constraints;

import static play.data.Form.form;

public class Application extends Controller{
	static Form<Login> loginForm = form(Login.class);
	public static Result index(){
		return ok(home.render(loginForm));
	}
	
	public static Result logout(){
		session().clear();
		return redirect(routes.Application.index());	
	}
	
	public static Result authenticate() {
			
			Form<Login> loginForm = form(Login.class).bindFromRequest();
			
			if(loginForm.hasErrors()) {
				flash("error", "Wrong email/password");
				return redirect(routes.Application.index());	
			}
			String email= loginForm.get().email;
			String password = loginForm.get().password;
			Client client=Client.authenticate(email, password) ;
			if (client== null){
				flash("error", "Wrong email/password");
				return redirect(routes.Application.index());	
			}
			//Context context = Context.current();
			//String referrer = context.session().get("referrer");
			session().clear();
			session("email", email);
			session("f_name", client.f_name);
			session("l_name", client.l_name);
			return redirect(routes.Application.index());	
	}
	
	public static class Login {
		public String email;
		public String password;
	}
}