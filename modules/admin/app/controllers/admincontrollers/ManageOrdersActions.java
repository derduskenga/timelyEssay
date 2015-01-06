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

import java.util.Map;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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

import com.avaje.ebean.Page;

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
				orderFiles.upload_date = new Date();
				File destination = new File(uploadPath, order_file.getName());
				orderFiles.file_size = order_file.length();
				orderFiles.storage_path = destination.toPath().toString();
				orderFiles.file_sent_to = FileOwner.OrderFileOwner.CLIENT;
				orderFiles.owner = FileOwner.OrderFileOwner.SUPPORT;
				String product_file_type = form().bindFromRequest().get("product_file_type");
				if(product_file_type.equals("PRODUCT"))
					orderFiles.product_file_type=FileType.ProductFileType.PRODUCT;
				else if(product_file_type.equals("DRAFT"))
					orderFiles.product_file_type=FileType.ProductFileType.DRAFT;
				else if(product_file_type.equals("REVISION"))
					orderFiles.product_file_type=FileType.ProductFileType.REVISION;
				else if(product_file_type.equals("REFERENCE_MATERIAL"))
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
				return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrder(orders.order_code));
				}catch (IOException ioe) {
				Logger.error("Server error on file upload:");
				flash("fileuploadresponseerror","Server error. Please try again");
				return badRequest(manageorder.render(orders,orderProductFileBoundForm)); 
				}catch(Exception ex){
				Logger.error("Server error on file upload:");
				flash("fileuploadresponseerror","Server error. Please try again");
				return badRequest(manageorder.render(orders,orderProductFileBoundForm)); 
				}
				
			}
			flash("fileuploadresponseerror","No file was selected");
					return badRequest(manageorder.render(orders,orderProductFileBoundForm));
		}
}