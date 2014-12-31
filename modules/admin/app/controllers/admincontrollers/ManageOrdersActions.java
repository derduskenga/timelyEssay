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
import java.util.List;
import java.util.ArrayList;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import models.admin.adminmodels.AdminUser;
import controllers.admincontrollers.AdminSecured;
import models.admin.userpermissions.SecurityRole;
import models.orders.Orders;
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
	
	public static Result manageOrder(String order_code){
			return ok(manageorder.render());
	}
}