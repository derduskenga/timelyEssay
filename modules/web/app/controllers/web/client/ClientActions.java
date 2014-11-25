package controllers.web.client;

import play.*;
import play.mvc.*;
import views.html.clientarea.*;
import models.orders.OrderMessages;
import models.orders.MessageParticipants;
import play.data.Form;

import java.util.List;
import java.util.ArrayList;


import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


import static play.data.Form.form;

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
	
}