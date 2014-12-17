package controllers.admincontrollers;

import views.html.adminviews.adminhome;
import views.html.adminviews.createadminuser;
import views.html.adminviews.manageusers;
import views.html.adminviews.adminerror;
import views.html.adminviews.adminroles;
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
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.admin.security.NoUserDeadboltHandler;

@Security.Authenticated(AdminSecured.class)
@Restrict({@Group({"Super Admin"})})
public class AdminActions extends Controller{
	
	public static Result index(){
			return ok(adminhome.render());
	}
	
	public static Result createNewAdminUser(){
			Form<AdminUser> adminUserForm = Form.form(AdminUser.class);
			AdminUser adminUser = new AdminUser();
			return ok(createadminuser.render(adminUserForm,adminUser.getAdminRoles(adminUser.getRoles())));
	}
	
	public static Result saveNewAdminUser(){			
			Form<AdminUser> 	adminUserForm = form(AdminUser.class).bindFromRequest();	
			AdminUser adminUser = new AdminUser();
			
			if(adminUserForm.hasErrors()) {
				flash("error", "Please correct the form below.");
						Map<String, String[]> map = request().body().asFormUrlEncoded();
						String[] checkedVal = map.get("admin_priviledges[]");
						List<String> adminPriviledges = null;
						if(checkedVal != null){
								adminPriviledges  = Arrays.asList(checkedVal);
								adminUser.roles = new ArrayList<SecurityRole>();
								for(String priviledge: adminPriviledges){
										SecurityRole secRole = SecurityRole.findByName(priviledge);
										if(secRole != null)
												adminUser.roles.add(secRole);
								}
						}
					return badRequest(createadminuser.render(adminUserForm, adminUser.getAdminRoles(adminUser.getRoles())));
			}
			adminUser = adminUserForm.get();
			Map<String, String[]> map = request().body().asFormUrlEncoded();
			String[] checkedVal = map.get("admin_priviledges[]");
			List<String> adminPriviledges = null;
			if(checkedVal != null){
					 adminPriviledges  = Arrays.asList(checkedVal);
					 adminUser.roles = new ArrayList<SecurityRole>();
					 for(String priviledge: adminPriviledges){
							SecurityRole secRole = SecurityRole.findByName(priviledge);
							if(secRole != null)
									adminUser.roles.add(secRole);
									
					 }
			}
			
			if(adminUser.saveAdminUser()){
				Ebean.saveManyToManyAssociations(adminUser,"roles");
				flash("success", "Admin user successfully saved.");
				return redirect(controllers.admincontrollers.routes.AdminActions.createNewAdminUser());
			}
			flash("error", "Could not save new admin user.");
			return redirect(controllers.admincontrollers.routes.AdminActions.createNewAdminUser());
	}
	
	public static Result manageAdminUsers(){
			AdminUser users = new AdminUser();
			List<AdminUser> list = users.fetchAll();
			return ok(manageusers.render(list));
	}
	
	public static Result deactivateAdminUser(Long admin_user_id){
				ObjectNode result = Json.newObject();
				AdminUser user = new AdminUser();
				user = user.getUserById(admin_user_id);
				if(user != null){
					if(user.active){
							if(!user.deactivate()){
								result.put("status", "0");
								result.put("deactivation", "1");
								result.put("message", "User Not Found");
								return ok(result);
							}
							result.put("status", "1");
							result.put("deactivation", "1");
							result.put("message", user.first_name+" "+user.last_name+" has successfully been deactivated.");
							return ok(result);
						}else{
							if(!user.activate()){
								result.put("status", "0");
								result.put("deactivation", "0");
								result.put("message", "User Not Found");
								return ok(result);
							}
							result.put("status", "1");
							result.put("deactivation", "0");
							result.put("message", user.first_name+" "+user.last_name+" has successfully been activated.");
							return ok(result);
				}
			}
			result.put("status", "-1");
			result.put("deactivation", "-1");
			result.put("message", "User Not Found");
			return ok(result);
	}
		
	public static Result editAdminUser(Long user_id){	
			AdminUser user = new AdminUser();
			user = user.getUserById(user_id);
			if(user==null){
				String error = "User not found.";
				return badRequest(adminerror.render(error));
			}
			Form<AdminUser> adminUserForm = Form.form(AdminUser.class);
			Form<AdminUser> filledUserForm = adminUserForm.fill(user);
			AdminUser adminUser = new AdminUser();
			return ok(createadminuser.render(filledUserForm,adminUser.getAdminRoles(user.getRoles())));
	}
	
	public static Result newAdminRole(){
			Form<SecurityRole> securityRoleForm = Form.form(SecurityRole.class);
			List<SecurityRole> securityRoles = new SecurityRole().getAllSecurityRoles();
			return ok(adminroles.render(securityRoleForm, securityRoles));
	}
	
	public static Result saveNewAdminRole(){
			Form<SecurityRole> boundForm = Form.form(SecurityRole.class).bindFromRequest();
			if(boundForm.hasErrors()){
					flash("error","Please correct the form below.");
					flash("show_form","true");
					List<SecurityRole> securityRoles = new SecurityRole().getAllSecurityRoles();
					return badRequest(adminroles.render(boundForm,securityRoles));
			}
			SecurityRole role = boundForm.get();
			role.save();
			flash("success", "Security role successfully added");
			return redirect(controllers.admincontrollers.routes.AdminActions.newAdminRole());
	}
}