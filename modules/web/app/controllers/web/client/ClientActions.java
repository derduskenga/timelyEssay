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
import models.common.security.PasswordHash;
import play.data.Form;
import models.client.PreferredWriter;
import models.common.mailing.Mailing;
import models.writer.FreelanceWriter;
import models.client.Client;
import models.client.Countries;
import models.utility.Utilities;
import java.io.*;
import play.data.validation.ValidationError;

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
	static Form<Client> profileForm = form(Client.class);
	static Map<String,List<ValidationError>> error = new LinkedHashMap<String,List<ValidationError>>();
	
	
	public static Result index(){  
	  if(session().get("email") != null){
	    Orders order = new Orders();
	    Long client_id = Client.getClient(session().get("email")).id;
	    return ok(clienthome.render(order.activeOrdersUnreadMessages(order.getActiveOrders(client_id)),order.completeOrdersUnreadMessages(order.getCompletedOrders(client_id)),order.closedOrdersUnreadMessages(order.getClosedOrders(client_id))));
	  } 
	  return(ok(clienthome.render(null,null,null)));
	}	
	
	public static Result saveClientMessage(Long order_code){
	  ObjectNode result = Json.newObject();
	  Form<OrderMessages> newBoundMessageForm = form(OrderMessages.class).bindFromRequest();
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return badRequest(clientmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("CLIENT"), getOrderMessages(order_code)));
	  }
	  if(newBoundMessageForm.hasErrors()) {
		  flash("error", "Please correct the form below.");
		  flash("show_form", "true");
		  return badRequest(clientmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("CLIENT"), getOrderMessages(order_code)));
	  }	
	  OrderMessages orderMessage = newBoundMessageForm.get();
	  orderMessage.msg_from = MessageParticipants.CLIENT;
	  orderMessage.orders = orders;
	  if(orderMessage.saveClientMessage()){
		  return redirect(controllers.web.client.routes.ClientActions.orderMessages(order_code));
	  }
	  return redirect(controllers.web.client.routes.ClientActions.orderMessages(order_code));
	}
	
	public static List<OrderMessages> getOrderMessages(Long order_code){
			List<OrderMessages> orderMessages = new ArrayList<OrderMessages>();
			orderMessages = OrderMessages.getClientOrderMessages(order_code);
			return orderMessages;
	}
	public static Result orderMessages(Long order_code){
	   return ok(clientmessages.render(Orders.getOrderByCode(order_code),newMessageForm, OrderMessages.getReceipientsMap("CLIENT"),getOrderMessages(order_code)));
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
	    return ok(clientvieworder.render(new Orders(),orderFilesForm,new OrderMessages().getUnreadMessages(order_code)));
	  }
	  return ok(clientvieworder.render(orders.orderClientLocalTime(orders),orderFilesForm,new OrderMessages().getUnreadMessages(order_code)));
	}
	
	public static Result saveOrderFile(Long order_code){
	  //get the order by order_code
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return redirect(controllers.web.client.routes.ClientActions.clientViewOrder(order_code));
	  }
	  Form<OrderFiles> orderFileBoundForm = orderFilesForm.bindFromRequest();
	  if(orderFileBoundForm.hasErrors()) {
	    flash("fileuploadresponseerror","There was an error.");
	    return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
	  }
	  OrderFiles orderFiles = orderFileBoundForm.get();
	  MultipartFormData body = request().body().asMultipartFormData();
	  MultipartFormData.FilePart part = body.getFile("order_file");	  
	  if(part != null){
	    File order_file = part.getFile();    
	    
	    if(order_file.length() > Utilities.FILE_UPLOAD_SIZE_LIMIT){
	      flash("fileuploadresponseerror","Please attach a file not exceeding 25 MB");
	      return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
	    }   
	    
	    if(part.getContentType().equals("application/x-ms-dos-executable")){
	      flash("fileuploadresponseerror","File not allowed!");
	      return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
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
	      }catch (IOException ioe){
		Logger.error("Server error on file upload:");
		flash("fileuploadresponseerror","Server error. Please try again");
		return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code))); 
	      }catch(Exception ex){
		Logger.error("Server error on file upload:");
		flash("fileuploadresponseerror","Server error. Please try again");
		return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code))); 
	      }
	      
	  }
	  flash("fileuploadresponseerror","No file was selected");
	  return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
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
	  JSONObject jobject = new JSONObject();
	  try{
	    Orders orders = Orders.getOrderByCode(order_code);
	    if(orders == null){
	      jobject.put("success",0);
	      jobject.put("message","Order was not found");
	      return ok(Json.parse(jobject.toString()));
	    }
	    orders.order_deadline = orders.newUtcOrderDeadline(new_date,orders);
	    orders.saveOrder();
	    jobject.put("success",1);
	    jobject.put("message","Deadline has been extended");
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
	  if(orders == null){
	    return redirect(controllers.web.client.routes.ClientActions.clientViewOrder(order_code));
	  }
	  OrderRevision orderRevision = new OrderRevision();
	  Date newDeadline = new Date(); 
	  orders.on_revision = true;
	  orders.is_complete = false;
	  try{
	    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    isoFormat.setTimeZone(TimeZone.getDefault());
	    newDeadline = isoFormat.parse(new_date);
	    orders.order_deadline = newDeadline;   
	    orderRevision.revision_instruction = revision_intructions;
	    orderRevision.orders = orders;
	    orderRevision.saveOrderRevision();
	    orders.saveOrder();
	    Logger.info("working");
	    jobject.put("success",1);
	    jobject.put("message","Revision has been placed");
	    return ok(Json.parse(jobject.toString()));
	  }catch(ParseException pe){
	    Logger.error("ParseException:" + pe.getMessage().toString());
	    jobject.put("success",0);
	    jobject.put("message","An error occured. Please try again");
	    return ok(Json.parse(jobject.toString()));
	  }catch(Exception ex){
	    Logger.error("blanket Exception:" + ex.getMessage().toString());
	    jobject.put("success",0);
	    jobject.put("message","An error occured. Please try again");
	    return ok(Json.parse(jobject.toString()));
	  } 
	}
	public static Result surveyFeedback(Long order_code, int rating){
	  Orders orders = Orders.getOrderByCode(order_code);
	  JSONObject jobject = new JSONObject();
	  if(orders == null){
	    jobject.put("success",0);
	    jobject.put("message","An error occured. Please try again");
	    return ok(Json.parse(jobject.toString()));
	  }    
	  orders.client_feedback = rating;
	  orders.saveOrder();  
	  jobject.put("success",1);
	  jobject.put("message","Thank you for your feedback");
	  return ok(Json.parse(jobject.toString()));
	}
	public static Result myProfile(){
	  if(session().get("email") == null){
	    session().clear();
	    return redirect(controllers.web.client.routes.ClientActions.index());
	  }
	  String user_email = session().get("email");
	  Client client = Client.getClient(user_email);
	  Form<Client> filledClientForm = profileForm.fill(client);
	  Map<Map<Long,String>,Boolean> mapCountries = new HashMap<Map<Long,String>,Boolean>(); 
	  mapCountries = Countries.fetchCountriesMapForErrorForm(client.country.id);  
	  return ok(clientprofile.render(client,filledClientForm,mapCountries));
	}
	public static Result editProfile(){
	  Form<Client> clientBoundForm = profileForm.bindFromRequest();
	  Map<String,String> clientFormDataMap = new HashMap<String,String>();
	  clientFormDataMap = clientBoundForm.data();
	  if(clientBoundForm.hasErrors()){
	    error = clientBoundForm.errors();
	    if(!error.isEmpty()){				
	      for(Map.Entry<String,List<ValidationError>> entry : error.entrySet()){
		String key = entry.getKey();					
		Logger.info("main key:" + key);				
							  
		List<ValidationError> errorKeyValue = entry.getValue();
							    
		for(ValidationError mainVal : errorKeyValue){
		  Logger.info("key:" + mainVal.key() + " message:" + mainVal.message());
							    
		}
	      }
	    }
	  }
	  Logger.info("id:" + clientFormDataMap.get("id"));
	  Client client = clientBoundForm.get();
	  String send_company_mail = clientFormDataMap.get("receive_company_mail");
	  if(send_company_mail.equals("yes")){
	    client.receive_company_mail = true;
	  }else{
	    client.receive_company_mail = false;
	  }
	  client.saveClient();
	  return redirect(controllers.web.client.routes.ClientActions.myProfile());
	}
	
	public static Result viewOrderMessage(Long order_code, Long message_id){
	  Logger.info("order_code:" + order_code + "message_id:" + message_id);
	  JSONObject jo = new JSONObject();
	  OrderMessages message = OrderMessages.getMessageById(message_id);
	  if(message == null){
	    jo.put("success",0);
	    jo.put("message","Request was unsuccessiful");
	    return ok(Json.parse(jo.toString()));
	  }
	  message.status = true;
	  message.saveClientMessage();
	  Logger.info("message status updated");
	  jo.put("success",1);
	  jo.put("message","Request was successfully");
	  return ok(Json.parse(jo.toString()));
	}
	
	public static Result changePassword(){
	    JSONObject jobject = new JSONObject();
	  try{
	  Map<String, String[]> change_password_values = new HashMap<String,String[]>();
	  change_password_values = request().body().asFormUrlEncoded();
	  if(change_password_values.isEmpty()){
	    jobject.put("success",0);
	    jobject.put("message","An error occured. Try again");
	    return ok(Json.parse(jobject.toString()));
	  }
	  String c_password_details[] = change_password_values.get("current_password");
	  //current password
	  String c_password = c_password_details[0];
	  
	  String n_password_details[] = change_password_values.get("new_password");
	  //new password
	  String n_password = n_password_details[0];
	  
	  String email = session().get("email");
	  Client client =  (email == null)? null : Client.getClient(email);
	  if(client == null){
			jobject.put("success",0);
			jobject.put("message","An error occured. Try again.");
			return ok(Json.parse(jobject.toString()));
	  }
	    
	 Boolean valid = PasswordHash.validatePassword(c_password, client.password+":"+client.salt);	
		if(valid){
					String hashedPassword = PasswordHash.createHash(n_password);
					String[] params = hashedPassword.split(":");				
					client.password = params[0];
					client.salt = params[1];
					client.saveClient();
		}else{
				jobject.put("success",0);
				jobject.put("message","Current password entered is invalid.");
				return ok(Json.parse(jobject.toString()));
		}
	  //do you code here; if password change is successful, return the JSONObject as shoen belo
	  jobject.put("success",1);
	  jobject.put("message","Password has been changed");
	  return ok(Json.parse(jobject.toString()));  
	  }catch(Exception e){
					Logger.error("Error creating hashed password",e);
					jobject.put("success",0);
					jobject.put("message","An error occured. Try again.");
					return ok(Json.parse(jobject.toString()));
	 }
	}
	
	public static Result pay(Long order_code){
	  return TODO;
	}
}