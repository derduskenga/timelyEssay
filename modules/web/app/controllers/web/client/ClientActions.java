package controllers.web.client;

import play.*;
import play.mvc.*;
import views.html.clientarea.*;
import models.orders.OrderMessages;
import models.orders.MessageParticipants;
import play.data.Form;
import models.common.mailing.Mailing;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import controllers.web.Secured;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import static play.data.Form.form;
@Security.Authenticated(Secured.class)
public class ClientActions extends Controller{
	static Form<OrderMessages> newMessageForm = form(OrderMessages.class);
	public static Result index(){
			return ok(clienthome.render());
	}
	//path to messages
	public static Result messages(){			
				return ok(clientmessages.render(newMessageForm, OrderMessages.getReceipientsMap("CLIENT"),getOrderMessages()));
	}	
	
	public static Result saveClientMessage(){
				ObjectNode result = Json.newObject();
				Form<OrderMessages> newBoundMessageForm = form(OrderMessages.class).bindFromRequest();
				
				if(newBoundMessageForm.hasErrors()) {
					flash("error", "Please correct the form below.");
					flash("show_form", "true");
					return badRequest(clientmessages.render(newBoundMessageForm, OrderMessages.getReceipientsMap("CLIENT"), getOrderMessages()));
				}	
				OrderMessages orderMessage = newBoundMessageForm.get();
				orderMessage.msg_from = MessageParticipants.CLIENT;
				if(orderMessage.saveClientMessage()){
					return redirect(controllers.web.client.routes.ClientActions.messages());
				}
				return redirect(controllers.web.client.routes.ClientActions.messages());
	}
	
	public static List<OrderMessages> getOrderMessages(){
			List<OrderMessages> orderMessages = new ArrayList<OrderMessages>();
			orderMessages = OrderMessages.getClientOrderMessages();
			return orderMessages;
	}
	
	public static Result affiliateProgram(){
			Form<NewEmail> newMailForm = form(NewEmail.class);
			return ok(affiliateprogram.render(newMailForm));
	}
	
	public static Result sendInvitationEmail(){
			Form<NewEmail> newMailForm = form(NewEmail.class).bindFromRequest();
			
			if(newMailForm.hasErrors()){
				flash("client_mail_invitation_error", "Could not send email. Please correct the form below.");
				flash("show_form", "true");
				return redirect(controllers.web.client.routes.ClientActions.affiliateProgram());
			}
			NewEmail newMail = newMailForm.get();
			Mailing mail = new Mailing();
			mail.sendClientInvitationMail(newMail.email);
			flash("client_mail_invitation_success", "Thanks! Email sent successfully.");
			flash("show_form", "true");
			return redirect(controllers.web.client.routes.ClientActions.affiliateProgram());
	}
	
	public static class NewEmail{
		@Constraints.Required(message="Please enter email.")
		@Constraints.Email(message="The email you entered does not look valid.")
		public String email;
		@Constraints.Required(message="Please enter write your message.")
		public String message;
	}
	
}