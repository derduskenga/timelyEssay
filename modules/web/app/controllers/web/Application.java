package controllers.web;

import play.data.Form;
import java.util.List;
import java.util.ArrayList;
import play.db.ebean.Model;
import play.*;
import play.mvc.*;
import views.html.home;
import views.html.orderClientForm;
import models.client.Client;
import play.data.validation.Constraints;
import models.orders.Orders;
import models.orders.OrderDocumentType;
import models.orders.TempStore;
import models.client.Countries;
import models.orders.OrderLevelOfWriting;
import models.orders.OrderCurrence;
import models.orders.Spacing;
import models.orders.Additions;
import models.utility.Utilities;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import static play.data.Form.form;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Application extends Controller{
	static Form<Login> loginForm = form(Login.class);
	static Form<Orders> orderForm = form(Orders.class);
	static Form<Client> clientForm = form(Client.class);
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
	
	public static Result newOrder(){
	  Map<Map<Long,String>,Boolean> mapCountries = new HashMap<Map<Long,String>,Boolean>(); 
	  if(Countries.fetchCountriesMap().size()>0){
	    mapCountries = Countries.fetchCountriesMap();
	  }
	    
	  Map<Map<Long,String>,Boolean> mapDocuments = new HashMap<Map<Long,String>,Boolean>();
	  if(OrderDocumentType.fetchDocumentMap().size()>0){
	    mapDocuments = OrderDocumentType.fetchDocumentMap(); 
	  }
	  Map<Map<Long,String>,Boolean> documentDeadlines = new HashMap<Map<Long,String>,Boolean>();
	  if(TempStore.getDocumentDeadlines().size()>0){
	    documentDeadlines = TempStore.getDocumentDeadlines();
	  }
	  Map<Long,String> documentSubjects = new HashMap<Long,String>();
	  if(TempStore.getDocumentSubjects().size()>0){
	  documentSubjects = TempStore.getDocumentSubjects(); 
	  }
	   
	  Map<Map<Long,String>,String> numberOfUnitsMap = new HashMap<Map<Long,String>,String>();
	  if(TempStore.getNumberOfUnits().size()>0){
	    numberOfUnitsMap = TempStore.getNumberOfUnits();
	  }
	  
	  Map<Map<Long,String>,Boolean> mapLevel= new HashMap<Map<Long,String>,Boolean>(); 
	  if(OrderLevelOfWriting.fetchLevelMap().size()>0){
	    mapLevel = OrderLevelOfWriting.fetchLevelMap();
	  }
	  
	  List<OrderCurrence> currenceList = new ArrayList<OrderCurrence>();
	  if(OrderCurrence.getCurrenceList()!=null){
	    currenceList = OrderCurrence.getCurrenceList();
	  } 
	  
	  List<Spacing> spacingList = new ArrayList<Spacing>();
	  if(Spacing.getSpacingList()!=null){
	    spacingList = Spacing.getSpacingList();
	  }
	  
	  List<Additions> additionsList = new ArrayList<Additions>();
	  if(Additions.getAdditionsList()!=null){
	    additionsList = Additions.getAdditionsList();
	  }
	  return ok(orderClientForm.render(orderForm,clientForm,loginForm,mapCountries,
	  mapDocuments,documentDeadlines,documentSubjects,numberOfUnitsMap,mapLevel,currenceList,
	  spacingList,getStyles(),getLanguages(),getDatabase(),getReferenceCount(),additionsList));
	}
	
	public static Result fetchCountries(){
	  return ok(Json.toJson(Countries.getCountries()));
	}
	
	public static Result fetchDocument(Long docId){
	  //return ok(Json.toJson(OrderDocumentType.getDocumentById(docId)));
	  return ok(Json.parse(OrderDocumentType.getDocumentById(docId).toString()));
	}
	
	public static Result fetchCurrency(){
	  return ok(Json.parse(OrderCurrence.getCurrencyArray().toString()));
	}
	
	public static Result fetchAdditions(){
	   return ok(Json.parse(Additions.getAdditionsArray().toString()));
	}
	public static Result fetchLevelOfWriting(){
	   return ok(Json.parse(OrderLevelOfWriting.getLevelOfWriting().toString()));
	}
	
	public static Result fetchSpacing(){
	   return ok(Json.parse(Spacing.getSpacingArray().toString()));
	}
	
	public static Result saveClientOrder(){
	  Map<Map<Long,String>,Boolean> mapCountries = new HashMap<Map<Long,String>,Boolean>(); 
	  if(Countries.fetchCountriesMap().size()>0){
	    mapCountries = Countries.fetchCountriesMap();
	  }
	    
	  Map<Map<Long,String>,Boolean> mapDocuments = new HashMap<Map<Long,String>,Boolean>();
	  if(OrderDocumentType.fetchDocumentMap().size()>0){
	    mapDocuments = OrderDocumentType.fetchDocumentMap(); 
	  }
	  Map<Map<Long,String>,Boolean> documentDeadlines = new HashMap<Map<Long,String>,Boolean>();
	  if(TempStore.getDocumentDeadlines().size()>0){
	    documentDeadlines = TempStore.getDocumentDeadlines();
	  }
	  Map<Long,String> documentSubjects = new HashMap<Long,String>();
	  if(TempStore.getDocumentSubjects().size()>0){
	  documentSubjects = TempStore.getDocumentSubjects(); 
	  }
	   
	  Map<Map<Long,String>,String> numberOfUnitsMap = new HashMap<Map<Long,String>,String>();
	  if(TempStore.getNumberOfUnits().size()>0){
	    numberOfUnitsMap = TempStore.getNumberOfUnits();
	  }
	  
	  Map<Map<Long,String>,Boolean> mapLevel= new HashMap<Map<Long,String>,Boolean>(); 
	  if(OrderLevelOfWriting.fetchLevelMap().size()>0){
	    mapLevel = OrderLevelOfWriting.fetchLevelMap();
	  }
	  
	  List<OrderCurrence> currenceList = new ArrayList<OrderCurrence>();
	  if(OrderCurrence.getCurrenceList()!=null){
	    currenceList = OrderCurrence.getCurrenceList();
	  } 
	  
	  List<Spacing> spacingList = new ArrayList<Spacing>();
	  if(Spacing.getSpacingList()!=null){
	    spacingList = Spacing.getSpacingList();
	  }
	  
	  List<Additions> additionsList = new ArrayList<Additions>();
	  if(Additions.getAdditionsList()!=null){
	    additionsList = Additions.getAdditionsList();
	  }
	  
	  Form<Orders> ordersBoundForm = orderForm.bindFromRequest();
	  Form<Client> clientBoundForm = clientForm.bindFromRequest();
	  
	  if(ordersBoundForm.hasErrors() || clientBoundForm.hasErrors()){
	    flash("clientorderformerros", "Please correct the errors in the form below.");
	    return badRequest(orderClientForm.render(orderForm,clientForm,loginForm,mapCountries,
	    mapDocuments,documentDeadlines,documentSubjects,numberOfUnitsMap,mapLevel,currenceList,
	    spacingList,getStyles(),getLanguages(),getDatabase(),getReferenceCount(),additionsList));
	  }
	  
	  Orders orders = ordersBoundForm.get();
	  Client client = clientBoundForm.get();
	  
	  return redirect(controllers.web.client.routes.ClientActions.messages());
	 
	}
	
	public static class Login {
		public String email;
		public String password;
	} 
	
	public static Map<String,Boolean> getStyles(){
	  Map<String,Boolean> styleMap = new HashMap<String,Boolean>();
	  for(int i= 0; i<Utilities.STYLES.length;i++){
	    styleMap.put(Utilities.STYLES[i],false);
	  }
	  return new TreeMap<String,Boolean>(styleMap);
	}
	
	public static Map<String,Boolean> getLanguages(){
	  Map<String,Boolean> languageMap = new HashMap<String,Boolean>();
	  for(int i=0;i<Utilities.PROGRAMMING_LANGUAGES.length;i++){
	    languageMap.put(Utilities.PROGRAMMING_LANGUAGES[i],false);
	  }
	  return new TreeMap<String,Boolean>(languageMap);
	}
	
	public static Map<String,Boolean> getDatabase(){
	  Map<String,Boolean> databaseMap = new HashMap<String,Boolean>();
	  for(int i=0;i<Utilities.DATABASE.length;i++){
	    databaseMap.put(Utilities.DATABASE[i],false);
	  }
	  return new TreeMap<String,Boolean>(databaseMap);
	}
	
	public static Map<Integer,Boolean> getReferenceCount(){
	  Map<Integer,Boolean> refMap = new HashMap<Integer,Boolean>();
	  for(int i=1;i<=Utilities.NUMBER_OF_EFERENCES;i++){
	    refMap.put(i,false);
	  }
	  return new TreeMap<Integer,Boolean>(refMap);
	}
}