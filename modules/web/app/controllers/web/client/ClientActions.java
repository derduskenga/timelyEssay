package controllers.web.client;

import play.*;
import play.mvc.*;
import views.html.clientarea.*;
import models.orders.OrderMessages;
import models.orders.Orders;
import models.orders.OrderFiles;
import models.orders.OrderRevision;
import models.orders.OrderProductFiles;
import models.orders.MessageParticipants;
import play.data.Form;
import models.client.PreferredWriter;
import models.common.mailing.Mailing;
import models.writer.FreelanceWriter;
import models.client.Client;
import models.utility.Utilities;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.*;
import java.text.*;
import play.data.validation.Constraints;
import controllers.web.Secured;

import static play.mvc.Http.MultipartFormData;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import models.orders.FileOwner;

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
	static Form<OrderFiles> orderFilesForm = form(OrderFiles.class);
	
	
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
	    return ok(payorder.render(new Orders()));
	  }  
	}
	public static Result clientViewOrder(Long order_code){
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return ok(clientvieworder.render(new Orders(),orderFilesForm));
	  }
	  return ok(clientvieworder.render(orders,orderFilesForm));
	}
	
	public static Result saveOrderFile(Long order_code){
	  //get the order by order_code
	  Orders orders = Orders.getOrderByCode(order_code);
	  Form<OrderFiles> orderFileBoundForm = orderFilesForm.bindFromRequest();
	  if(orderFileBoundForm.hasErrors()) {
	    flash("fileuploadresponseerror","There was an error.");
	    return badRequest(clientvieworder.render(orders,orderFileBoundForm));
	  }
	  OrderFiles orderFiles = orderFileBoundForm.get();
	  MultipartFormData body = request().body().asMultipartFormData();
	  MultipartFormData.FilePart part = body.getFile("order_file");	  
	  if(part != null){
	    File order_file = part.getFile();    
	    
	    if(order_file.length() > Utilities.FILE_UPLOAD_SIZE_LIMIT){
	      flash("fileuploadresponseerror","Please attach a file not exceeding 25 MB");
	      return badRequest(clientvieworder.render(orders,orderFileBoundForm));
	    }   
	    
	    if(part.getContentType().equals("application/x-ms-dos-executable")){
	      flash("fileuploadresponseerror","File not allowed!");
	      return badRequest(clientvieworder.render(orders,orderFileBoundForm));
	    }
	      try{
		//orderFiles.order_file = Files.toByteArray(order_file);
		orderFiles.orders = orders;
		String file_name  = part.getFilename();
		String file_key = part.getKey();
		String contentType = part.getContentType();
		Logger.info("file name:" + file_name + " file key:" + file_key + " content type:" + contentType);	      
		//String myUploadPath = Play.application().configuration().getString("myUploadPath");
		String uploadPath = Play.application().configuration().getString("orderfilespath", "/tmp/");
		//order_file.renameTo(order_file,);
		//order_file.renameTo(new File(uploadPath + file_name));
		orderFiles.file_name = file_name;
		orderFiles.content_type = contentType;
		orderFiles.upload_date = new Date();
		File destination = new File(uploadPath, order_file.getName());
		orderFiles.file_size = order_file.length();
		orderFiles.storage_path = destination.toPath().toString();
		orderFiles.file_sent_to = FileOwner.OrderFileOwner.WRITER;
		orderFiles.owner = FileOwner.OrderFileOwner.CLIENT;
		//FileUtils.moveFile(order_file, destination);
		Files.move(order_file,destination);
		Logger.info("File path:" + destination.toPath().toString());
		flash("fileuploadresponsesuccess","Your file has been uploaded");
		orderFiles.saveOrderFile();
		return redirect(controllers.web.client.routes.ClientActions.clientViewOrder(order_code));
	      }catch (IOException ioe) {
		Logger.error("Server error on file upload:");
		flash("fileuploadresponseerror","Server error. Please try again");
		return badRequest(clientvieworder.render(orders,orderFileBoundForm)); 
	      }catch(Exception ex){
		Logger.error("Server error on file upload:");
		flash("fileuploadresponseerror","Server error. Please try again");
		return badRequest(clientvieworder.render(orders,orderFileBoundForm)); 
	      }
	      
	  }
	  flash("fileuploadresponseerror","No file was selected");
	  return badRequest(clientvieworder.render(orders,orderFileBoundForm));
	}
	
	public static Result downloadOrderFile(Long file_id){
	  OrderFiles orderFiles = OrderFiles.getOrderFileById(file_id);
	  if(orderFiles == null){
	    flash("orderfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	  response().setContentType(orderFiles.content_type);  
	  response().setHeader("Content-disposition","attachment; filename=" + orderFiles.file_name); 
	  response().setHeader("Content-Length",String.valueOf(new File(orderFiles.storage_path).length()));
	  try{
	    return ok(new File(orderFiles.storage_path));
	  }catch(Exception ex){
	    flash("orderfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	}
	
	public static Result downloadProductFile(Long file_id){
	  //orderproductfilespath
	  OrderProductFiles orderProductFiles = OrderProductFiles.getOrderProductFiles(file_id);
	  if(orderProductFiles == null){
	    flash("orderproductfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	  response().setContentType(orderProductFiles.content_type);  
	  response().setHeader("Content-disposition","attachment; filename=" + orderProductFiles.file_name); 
	  response().setHeader("Content-Length",String.valueOf(new File(orderProductFiles.storage_path).length()));
	  try{
	    orderProductFiles.has_been_downloaded = true;
	    orderProductFiles.download_date = new Date();
	    orderProductFiles.saveProductFile();
	    return ok(new File(orderProductFiles.storage_path));
	  }catch(Exception ex){
	    flash("orderproductfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	}
	
	public static Result VoluntaryDeadlineExtension(String new_date, Long order_code){
	  Date newOrderDate = new Date();
	  JSONObject jobject = new JSONObject();
	  try{
	    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    isoFormat.setTimeZone(TimeZone.getDefault());
	    newOrderDate = isoFormat.parse(new_date);
	    Orders orders = Orders.getOrderByCode(order_code);
	    orders.order_deadline = newOrderDate;
	    orders.saveOrder();
	    jobject.put("success",1);
	    jobject.put("message","Deadline has been extended");
	    return ok(Json.parse(jobject.toString()));    
	  }catch(ParseException pe){
	    Logger.error("ParseException error" + pe.getMessage().toString());
	    jobject.put("success",0);
	    jobject.put("message","An error occured");
	    return ok(Json.parse(jobject.toString()));    
	  }catch(Exception ex){
	    Logger.error("Exception error" + ex.getMessage().toString());
	    jobject.put("success",0);
	    jobject.put("message","An error occured");
	    return ok(Json.parse(jobject.toString()));
	  }
	}
	public static Result fetchDeadline(Long order_code){
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return ok();
	  }
	  return ok(orders.getStringDeadline(orders.order_deadline));
	}
	public static Result askForRevision(Long order_code, String revision_deadline){
	  Map<String, String[]> revisionValues = new HashMap<String,String[]>();
	  revisionValues = request().body().asFormUrlEncoded();
	  JSONObject jobject = new JSONObject();
	  String new_date = revision_deadline;
	  String revision_intructions = "";
	 
	 if(revisionValues.isEmpty()){
	    jobject.put("success",0);
	    jobject.put("message","No data was sent");
	    return ok(Json.parse(jobject.toString()));
	  }
	  
	  String instructionsDetails[] = revisionValues.get("revision_instructions");
	  revision_intructions = instructionsDetails[0];
	  
	  Logger.info("revision_instructions:" + revision_intructions + " revision_deadline:" + revision_deadline);
	  Orders orders = Orders.getOrderByCode(order_code);
	  OrderRevision orderRevision = new OrderRevision();
	  Date newDeadline = new Date(); 
	  orders.on_revision = true;
	  orders.on_revision = false;
	  try{
	    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    isoFormat.setTimeZone(TimeZone.getDefault());
	    newDeadline = isoFormat.parse(new_date);
	    orders.order_deadline = newDeadline;   
	    orderRevision.revision_instruction = revision_intructions;
	    orderRevision.orders = orders;
	    orderRevision.saveOrderRevision();
	    orders.saveOrder();
	    jobject.put("success",1);
	    jobject.put("message","Revision has been placed");
	    return ok(Json.parse(jobject.toString()));
	  }catch(ParseException pe){
	    jobject.put("success",0);
	    jobject.put("message","An error occured");
	    return ok(Json.parse(jobject.toString()));
	  }catch(Exception ex){
	    jobject.put("success",0);
	    jobject.put("message","An error occured");
	    return ok(Json.parse(jobject.toString()));
	  } 
	}
	public static Result surveyFeedback(Long order_code, int rating){
	  String d = "";
	  Orders orders = Orders.getOrderByCode(order_code);
	  JSONObject jobject = new JSONObject();
	  if(orders == null){
	    jobject.put("success",0);
	    jobject.put("message","An error occured");
	    return ok(Json.parse(jobject.toString()));
	  }    
	  orders.client_feedback = rating;
	  orders.saveOrder();  
	  jobject.put("success",1);
	  jobject.put("message","Thank you for your feedback");
	  return ok(Json.parse(jobject.toString()));
	}
	public static Result pay(Long order_code){
	  return TODO;
	}
}