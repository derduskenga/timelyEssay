package controllers.web.client;

import play.*;
import play.mvc.*;
import views.html.clientarea.*;
import models.orders.OrderMessages;
import models.orders.Orders;
import models.orders.MessageParticipants;
import play.data.Form;
import models.client.PreferredWriter;
import models.common.mailing.Mailing;
import models.writer.FreelanceWriter;
import models.client.Client;

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
	static Form<PreferredWriterForm> prefWriterForm = form(PreferredWriterForm.class);
	
	public static Result index(){
	  List<Orders> activeOrders = new ArrayList<Orders>();
	  List<Orders> completeOrders = new ArrayList<Orders>();
	  List<Orders> closedOrders = new ArrayList<Orders>();	  
	  if(session().get("email") != null){
	    Long client_id = Client.getClient(session().get("email")).id;
	    activeOrders = new Orders().getActiveOrders(client_id);
	    completeOrders = new Orders().getCompletedOrders(client_id);
	    closedOrders = new Orders().getClosedOrders(client_id);
	  } 
	  return(ok(clienthome.render(activeOrders,completeOrders,closedOrders)));
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
	public static Result orderMessages(Long order_code){
	  return TODO;
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
	
	public static Result preferredWriters(){
		return ok(preferredwriters.render(prefWriterForm,  Client.getPreferedWriters()));	
	}
	
	public static Result savePreferredWriter(){
		Form<PreferredWriterForm> preferredWriterForm = form(PreferredWriterForm.class).bindFromRequest();		
		if(preferredWriterForm.hasErrors()){
			flash("show_form","true");
			return badRequest(preferredwriters.render(preferredWriterForm,  Client.getPreferedWriters()));	
		}
		PreferredWriterForm prefWriter = preferredWriterForm.get();
		FreelanceWriter freelanceWriter = new FreelanceWriter();
		freelanceWriter = freelanceWriter.findFreelanceWriterById(prefWriter.writer_id);
		if(freelanceWriter == null){
			flash("writer_not_found_error","Writer ID  "+prefWriter.writer_id+" not found");
			flash("show_form","true");
			prefWriterForm=preferredWriterForm;
			return redirect(controllers.web.client.routes.ClientActions.preferredWriters());
		}
		PreferredWriter preferredWriter = new PreferredWriter();
		preferredWriter.freelanceWriter = freelanceWriter;
		preferredWriter.save();
		flash("show_form","true");
		flash("writer_added_success","Successfully added writer "+freelanceWriter.freelance_writer_id+" to your preferred writers.");
		return redirect(controllers.web.client.routes.ClientActions.preferredWriters());	
	}	
	public static class NewEmail{
		@Constraints.Required(message="Please enter email.")
		@Constraints.Email(message="The email you entered does not look valid.")
		public String email;
		@Constraints.Required(message="Please enter write your message.")
		public String message;
	}	
	public static class PreferredWriterForm{
		@Constraints.Required(message="Writer ID is required.")
		public Long writer_id;
	}	
	public static Result proceedToPay(Long order_code){
	  Orders orders  = Orders.getOrderByCode(order_code);
	  //send an email to the user containing password
	  //let the user pay now
	  if(orders != null){
	    return ok(payorder.render(orders));
	  }else{
	    return notFound("Order Was Not found");
	  }  
	}
	public static Result clientViewOrder(Long order_code){
	  return TODO;
	}
	public static Result pay(Long order_code){
	  return TODO;
	}
}