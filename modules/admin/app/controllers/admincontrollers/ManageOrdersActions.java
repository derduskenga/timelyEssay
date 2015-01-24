package controllers.admincontrollers;

import views.html.adminviews.adminhome;
import views.html.adminviews.createadminuser;
import views.html.adminviews.manageusers;
import views.html.adminviews.adminerror;
import views.html.adminviews.adminroles;
import views.html.adminviews.manageorders;
import views.html.adminviews.manageorder;
import views.html.adminviews.addrole;

import play.*;
import play.mvc.*;
import play.data.Form;
import com.avaje.ebean.Ebean;
import static play.data.Form.form;

import java.util.*;
import java.text.*;
import static play.mvc.Http.MultipartFormData;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import models.admin.adminmodels.AdminUser;
import controllers.admincontrollers.AdminSecured;
import models.admin.userpermissions.SecurityRole;
import models.orders.Orders;
import models.orders.MessageParticipants;
import models.orders.OrderProductFiles;
import models.utility.Utilities;
import models.orders.FileOwner;
import models.orders.OrderFiles;
import models.orders.FileType;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.admin.security.NoUserDeadboltHandler;

import models.orders.OrderMessages;

import com.avaje.ebean.Page;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Security.Authenticated(AdminSecured.class)
@Restrict({@Group({"Writer Support"})})
public class ManageOrdersActions extends Controller{
	
	public static Result manageOrders(){
			Orders orders = new Orders();
			Page<Orders> activeOrders = orders.getActiveOrders(0,10);
			Page<Orders> completedOrders = orders.getCompletedOrders(0,10);
			Page<Orders> closedOrders = orders.getClosedOrders(0,10);
			return ok(manageorders.render(activeOrders, completedOrders, closedOrders));
	}
	
	public static Result manageOrder(Long order_code){
			Form<OrderProductFiles> orderFilesForm = Form.form(OrderProductFiles.class);
			Orders order = new Orders().getOrderByCode(order_code);
			return ok(manageorder.render(order,orderFilesForm));
	}
		
	public static Result uploadProductFile(Long order_code){
				//get the order by order_code
			Orders orders = Orders.getOrderByCode(order_code);
			Form<OrderProductFiles> orderProductFileBoundForm = Form.form(OrderProductFiles.class);

			if(orders==null)
				return badRequest(manageorder.render(null,orderProductFileBoundForm));
			orderProductFileBoundForm =orderProductFileBoundForm.bindFromRequest();
			Map<String,String> clientProductFileMap = new HashMap<String,String>();
			clientProductFileMap = orderProductFileBoundForm.data();
			String file_local_date = clientProductFileMap.get("file_local_date");
			if(orderProductFileBoundForm.hasErrors()) {
				flash("fileuploadresponseerror","There was an error.");
				return badRequest(manageorder.render(orders,orderProductFileBoundForm));
			}
			OrderProductFiles orderFiles = orderProductFileBoundForm.get();
			MultipartFormData body = request().body().asMultipartFormData();
			MultipartFormData.FilePart part = body.getFile("file_name");	  
			if(part != null){
				File order_file = part.getFile();    
				
				if(order_file.length() > Utilities.FILE_UPLOAD_SIZE_LIMIT){
				  flash("fileuploadresponseerror","Please attach a file not exceeding 25 MB");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm));
				}   
				
				if(part.getContentType().equals("application/x-ms-dos-executable")){
				  flash("fileuploadresponseerror","File type not allowed!");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm));
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
				  orderFiles.upload_date = OrderMessages.computeMessageUtcTime(Utilities.WRITER_TIMEZONE_OFFSET,file_local_date);
				  File destination = new File(uploadPath, order_file.getName());
				  orderFiles.file_size = order_file.length();
				  orderFiles.storage_path = destination.toPath().toString();
				  orderFiles.file_sent_to = FileOwner.OrderFileOwner.CLIENT;
				  orderFiles.owner = FileOwner.OrderFileOwner.SUPPORT;
				  String product_file_type = form().bindFromRequest().get("product_file_type");
				  if(product_file_type.equals("")){
					  flash("fileuploadresponseerror","Could not upload file. Select File type.");
					  return badRequest(manageorder.render(orders,orderProductFileBoundForm)); 
				  }
				  if(product_file_type.equals("PRODUCT")){
					  orderFiles.product_file_type=FileType.ProductFileType.PRODUCT;
					  orders.is_complete = true;
				  }else if(product_file_type.equals("DRAFT"))
					  orderFiles.product_file_type=FileType.ProductFileType.DRAFT;
				  else if(product_file_type.equals("REVISION")){
					  orders.is_complete = true;
					  orderFiles.product_file_type=FileType.ProductFileType.REVISION;
				  }else if(product_file_type.equals("REFERENCE_MATERIAL"))
					  orderFiles.product_file_type=FileType.ProductFileType.REFERENCE_MATERIAL;
				  else if(product_file_type.equals("ADDITIONAL_FILE"))
					  orderFiles.product_file_type=FileType.ProductFileType.ADDITIONAL_FILE;
				  else if(product_file_type.equals("OTHER"))
					  orderFiles.product_file_type=FileType.ProductFileType.OTHER;
				  //FileUtils.moveFile(order_file, destination);
				  Files.move(order_file,destination);
				  Logger.info("File path:" + destination.toPath().toString());
				  flash("fileuploadresponsesuccess","Your file has been uploaded");
				  orderFiles.saveProductFile();
				  orders.saveOrder();
				  return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrder(orders.order_code));
				}catch (IOException ioe) {
				  Logger.error("Server error on file upload: " + ioe.getMessage().toString());
				  flash("fileuploadresponseerror","Server error. Please try again");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm)); 
				}catch(Exception ex){
				  Logger.error("Server error on file upload here: " + ex.getMessage().toString());
				  flash("fileuploadresponseerror","Server error. Please try again");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm)); 
				}
				
			}
			flash("fileuploadresponseerror","No file was selected");
			return badRequest(manageorder.render(orders,orderProductFileBoundForm));
		}
	public static Result AskForExtraPages(int pages, Long order_code, String date){
		Orders order = Orders.getOrderByCode(order_code);
		
		JSONObject jsonobject = new JSONObject();
		if(order == null){
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  return ok(Json.parse(jsonobject.toString()));
		}
		order.additional_pages = pages;
		//CREATE A MESSAGE TO BE SENT TO THE CLIENT FOR SUCH REQUEST
		//double new_order_total = order.computeOrderTotalForAdditionalPages(order);
		//order.order_total = new_order_total;
		//order.saveOrder();
		AdminUser user = AdminUser.findByEmail(session().get("admin_email"));
		if(user == null){
		  //user not found or user is not logged in
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  //return ok(Json.parse(jsonobject.toString())); 
		  return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrders());
		}
		Date message_date = new Date();
		Calendar calender = Calendar.getInstance();
		try{
		  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		  message_date = isoFormat.parse(isoFormat.format(new Date(Long.valueOf(date)))); 
		  calender.setTimeInMillis(message_date.getTime());
		  int offset = Integer.parseInt(user.admin_user_offset);
		  calender.add(Calendar.MINUTE,offset);//get UTC time to be stored
		}catch(ParseException pe){
		  Logger.error("ParseException:" + pe.getMessage().toString());
		  jsonobject.put("success",0);
		  jsonobject.put("message","An error occured. Please try again");
		  return ok(Json.parse(jsonobject.toString()));
		}
		OrderMessages orderMessage = new OrderMessages();
		orderMessage.msg_to = MessageParticipants.CLIENT;
		orderMessage.msg_from = MessageParticipants.WRITERS;
		orderMessage.status = false;
		orderMessage.orders = order;
		orderMessage.sent_on = calender.getTime();
		Long message_id = orderMessage.saveClientMessageReturningId();
		OrderMessages saveMessage = OrderMessages.getMessageById(message_id);
		
		saveMessage.message = OrderMessages.getAdditinalPagesMessageTemplate(order,pages);
		saveMessage.message_type = OrderMessages.ActionableMessageType.ADDITIONAL_PAGES;
		saveMessage.action_required = true;
		saveMessage.message_promise_value = pages + "";
		saveMessage.saveClientMessage();
		
		jsonobject.put("success",1);
		jsonobject.put("message","Your request has been sent to the client");
		return ok(Json.parse(jsonobject.toString()));
	}
	
	public static Result AskForDeadlineExtension(String deadline_,String date, String reason,Long order_code){
		String deadline_extension_reason = "";
		if(reason.equals("USING_SOFTWARE")){
		  deadline_extension_reason = "using a specific software to complete ";
		}else if(reason.equals("EXTENSIVE_RESEARCH")){
		  deadline_extension_reason = "an extensive research required to complete ";
		}else if(reason.equals("CLOSE_TO_DEADLINE")){
		  deadline_extension_reason = "geting close to the deadline, ";
		}else if(reason.equals("LIMITED_MATERIAL")){
		  deadline_extension_reason = "a limited materials, which are required to complete ";
		}
		Orders order = Orders.getOrderByCode(order_code);
		OrderMessages orderMessage = new OrderMessages();
		JSONObject jsonobject = new JSONObject();
		if(order == null){
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  return ok(Json.parse(jsonobject.toString()));
		}
		
		AdminUser user = AdminUser.findByEmail(session().get("admin_email"));
		if(user == null){
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  return ok(Json.parse(jsonobject.toString())); 
		}
		Date message_date = new Date();
		Date deadline = new Date();
		Calendar calender = Calendar.getInstance();
		Calendar deadline_calender = Calendar.getInstance();
		String str_promise = "";
		try{
		  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		  SimpleDateFormat isoDeadlineFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		  //For message date utc time
		  message_date = isoFormat.parse(isoFormat.format(new Date(Long.valueOf(date)))); 
		  calender.setTimeInMillis(message_date.getTime());
		  int offset = Integer.parseInt(user.admin_user_offset);
		  calender.add(Calendar.MINUTE,offset);//get UTC time to be stored
		  message_date = calender.getTime();
		  
		  //for deadline utc time
		  deadline =  isoDeadlineFormat.parse(deadline_); 
		  deadline_calender.setTimeInMillis(deadline.getTime());
		  int client_offset = Integer.parseInt(order.client.client_time_zone_offset);
		  deadline_calender.add(Calendar.MINUTE,client_offset);
		  deadline = deadline_calender.getTime();
		  str_promise = isoDeadlineFormat.format(deadline);
		}catch(ParseException pe){
		  Logger.error("ParseException:" + pe.getMessage().toString());
		  jsonobject.put("success",0);
		  jsonobject.put("message","An error occured. Please try again");
		  return ok(Json.parse(jsonobject.toString()));
		}
		orderMessage.msg_to = MessageParticipants.CLIENT;
		orderMessage.msg_from = MessageParticipants.WRITERS;
		orderMessage.status = false;
		orderMessage.orders = order;
		orderMessage.sent_on = message_date;
		Long message_id = orderMessage.saveClientMessageReturningId();
		OrderMessages saveMessage = OrderMessages.getMessageById(message_id);
		
		saveMessage.message = OrderMessages.getExtendDeadlineMessageTemplate(order,deadline,deadline_extension_reason);
		saveMessage.message_type = OrderMessages.ActionableMessageType.DEADLINE_EXTENSION;
		saveMessage.action_required = true;
		saveMessage.message_promise_value = str_promise;
		saveMessage.saveClientMessage();
		
		jsonobject.put("success",1);
		jsonobject.put("message","Your request has been sent to the client");
		return ok(Json.parse(jsonobject.toString()));
		
		
	}
	
	  public static Result downloadProductFile(Long file_id){
	    OrderProductFiles orderProductFiles = OrderProductFiles.getOrderProductFiles(file_id);
	    if(orderProductFiles == null){
	      flash("adminorderproductfiledownloaderror","File error. Please try again");
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
	  
	  public static Result adminDownloadOrderFile(Long file_id){
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
	  
	  public static Date orderSupportLocalTime(Date date,String support_email){
	    //This is a deadline 
	    AdminUser admin_user = AdminUser.findByEmail(support_email);
	    Date utcTime = date;
	    int client_offset = Integer.parseInt(admin_user.admin_user_offset);
	    Calendar calender = Calendar.getInstance();
	    calender.setTimeInMillis(utcTime.getTime());
	    calender.add(Calendar.MINUTE,(client_offset*(-1)));//get local time
	    Date localTime = calender.getTime();
	    //order.order_deadline = localTime;
	    return localTime;
	  }
}