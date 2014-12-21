package controllers.web;

import play.data.Form;
import java.util.*;
import java.text.*;
import play.db.ebean.Model;
import play.*;
import play.mvc.*;
import play.data.validation.ValidationError;
import views.html.home;
import views.html.orderSummary;
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
import models.orders.StaticData;
import models.orders.OrderSubject;
import models.orders.OrderCppMode;
import models.utility.Utilities;
import models.orders.OrderDeadlines;
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
	static Map<String,List<ValidationError>> error = new LinkedHashMap<String,List<ValidationError>>();
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
	  Map<Map<Long,String>,Boolean> documentSubjects = new HashMap<Map<Long,String>,Boolean>();
	  if(TempStore.getDocumentSubjects().size()>0){
	    documentSubjects = TempStore.getDocumentSubjects(); 
	  }
	   
	  Map<Map<Long,String>,Boolean> numberOfUnitsMap = new HashMap<Map<Long,String>,Boolean>();
	  if(TempStore.getNumberOfUnits().size()>0){
	    numberOfUnitsMap = TempStore.getNumberOfUnits();
	  }
	  
	  Map<Map<Long,String>,Boolean> mapLevel= new HashMap<Map<Long,String>,Boolean>(); 
	  if(OrderLevelOfWriting.fetchLevelMap().size()>0){
	    mapLevel = OrderLevelOfWriting.fetchLevelMap();
	  }
	  
	  Map<OrderCurrence,Boolean> currenceList = new LinkedHashMap<OrderCurrence,Boolean>();
	  if(OrderCurrence.getCurrenceList().size()>0){
	    currenceList = OrderCurrence.getCurrenceList();
	  } 
	  
	  Map<Spacing,Boolean> spacingMap = new LinkedHashMap<Spacing,Boolean>();
	  if(Spacing.getSpacingMap().size()>0){
	    spacingMap = Spacing.getSpacingMap();
	  }
	  
	  Map<Additions,Boolean> additionsList = new LinkedHashMap<Additions,Boolean>();
	  if(Additions.getAdditionsList().size()>0){
	    additionsList = Additions.getAdditionsList();
	  }
	  return ok(orderClientForm.render(orderForm,clientForm,loginForm,mapCountries,
	  mapDocuments,documentDeadlines,documentSubjects,numberOfUnitsMap,mapLevel,currenceList,
	  spacingMap,StaticData.getStyles(),StaticData.getLanguages(),StaticData.getDatabase(),StaticData.getReferenceCount(),additionsList));
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
	  Form<Orders> ordersBoundForm = orderForm.bindFromRequest();
	  Form<Client> clientBoundForm = clientForm.bindFromRequest();
	  
	  Map<String,String> clientFormDataMap = new HashMap<String,String>();
	  Map<String,String> orderFormDataMap = new HashMap<String,String>();
	  clientFormDataMap = clientBoundForm.data();
	  orderFormDataMap = ordersBoundForm.data();
	  // get request value from submitted form
	  Map<String, String[]> additionsMap = request().body().asFormUrlEncoded();
	  String[] checkedAdditions = additionsMap.get("orders_additions[]"); // get selected additions

	  //get values of the drop downs selected
	  String document_selected = orderFormDataMap.get("document_type");
	  String country_selected = clientFormDataMap.get("client_country");
	  String urgency_selected = orderFormDataMap.get("document_deadline");
	  String subject_selected = orderFormDataMap.get("document_subject");
	  String number_of_units_selected = orderFormDataMap.get("number_of_units");
	  String spacing_selected = orderFormDataMap.get("page_spacing");
	  String level_of_writing_selected = orderFormDataMap.get("level_of_writing");
	  String currency_selected = orderFormDataMap.get("order_currency");
	  String number_of_references_selected = orderFormDataMap.get("no_of_references");
	  String cpp_mode  = form().bindFromRequest().get("deadline_category_tracker");
	  String style_selected = orderFormDataMap.get("writing_style");
	    
	  //Logger.info("orders form " + ordersBoundForm.errorsAsJson().toString());
	  //Logger.info("client form " + clientBoundForm.errorsAsJson().toString());
	  
	  Map<Map<Long,String>,Boolean> mapDocuments = new HashMap<Map<Long,String>,Boolean>();
	  mapDocuments = OrderDocumentType.fetchDocumentMapForErrorForm(Long.valueOf(document_selected)); 
	  
	  //Map<Map<Long,String>,Boolean> documentDeadlines = new HashMap<Map<Long,String>,Boolean>();
	  Map<Map<Long,String>,Boolean> documentDeadlines = OrderDeadlines.getDocumentDeadlinesForErrorForm(Long.valueOf(urgency_selected),Long.valueOf(document_selected));

	  Map<Map<Long,String>,Boolean> documentSubjects = new HashMap<Map<Long,String>,Boolean>();
	  if(!cpp_mode.equalsIgnoreCase(OrderCppMode.CppModes.perassignment.toString())){
	    documentSubjects = OrderSubject.getOrderSubjectsForErrorForm(Long.valueOf(subject_selected),Long.valueOf(document_selected));
	  }
	    
	  
	  Map<Map<Long,String>,Boolean> numberOfUnitsMap = new OrderCppMode().getNumberOfUnitsMapForErrorForm(Long.valueOf(spacing_selected),Integer.parseInt(number_of_units_selected),Long.valueOf(document_selected));
	  
	  Map<Spacing,Boolean> spacingMap = new Spacing().getSpacingMapForErrorForm(Long.valueOf(spacing_selected));
	  
	  Map<Map<Long,String>,Boolean> mapLevel = new OrderLevelOfWriting().fetchLevelMapForErrorForm(Long.valueOf(level_of_writing_selected));

	  Map<OrderCurrence,Boolean> currMap = new OrderCurrence().getCurrencyMapForErrorForm(Long.valueOf(currency_selected));	    
	  //Country selected
	  Map<Map<Long,String>,Boolean> mapCountries = new HashMap<Map<Long,String>,Boolean>(); 
	  mapCountries = Countries.fetchCountriesMapForErrorForm(Long.valueOf(country_selected));  
	    
	  //selected additions
	  Map<Additions,Boolean> additionsList = new LinkedHashMap<Additions,Boolean>();
	  if(checkedAdditions != null){
	    additionsList = Additions.getMapForErrorForm(checkedAdditions);
	  }else{
	    if(Additions.getAdditionsList().size()>0){
	      additionsList = Additions.getAdditionsList();
	    }
	  }
	  
	  if(ordersBoundForm.hasErrors() || clientBoundForm.hasErrors()){
	    flash("clientorderformerros", "Please correct the errors in the form below.");    
	    error = clientBoundForm.errors();
	    if(!error.isEmpty()){  
	      for(Map.Entry<String,List<ValidationError>> entry : error.entrySet()) {
		String key = entry.getKey();
		Logger.info("main key:" + key);
		
		List<ValidationError> errorKeyValue = entry.getValue();
		
		for(ValidationError mainVal : errorKeyValue){
		  Logger.info("key:" + mainVal.key() + " message:" + mainVal.message());
		  if(mainVal.key().equals("country_code")){
		    flash("country_code",mainVal.message());
		  }else if(mainVal.key().equals("area_code")){
		    flash("area_code",mainVal.message());
		  }else if(mainVal.key().equals("phone_number")){
		    flash("phone_number",mainVal.message());
		  }
		}
	      }
	    }
	  
	    return badRequest(orderClientForm.render(ordersBoundForm,clientBoundForm,loginForm,mapCountries,
	    mapDocuments,documentDeadlines,documentSubjects,numberOfUnitsMap,mapLevel,currMap,spacingMap,StaticData.getStylesForErrorForm(style_selected),StaticData.getLanguages(),StaticData.getDatabase(),StaticData.getReferenceCount(),additionsList));
	  }
	  
	  	  
	 Client newClient = clientBoundForm.get();
	 Orders newOrders = ordersBoundForm.get();
	 
	  //check if email exists
	  String email = clientBoundForm.get().email;
	  if(Client.getClient(email) != null && newClient.id == null){
	    //email is already in urgency_selected
	    flash("emailinuse", "The email has been taken by another user.");
	    return badRequest(orderClientForm.render(ordersBoundForm,clientBoundForm,loginForm,mapCountries,
	    mapDocuments,documentDeadlines,documentSubjects,numberOfUnitsMap,mapLevel,currMap,spacingMap,StaticData.getStylesForErrorForm(style_selected),StaticData.getLanguages(),StaticData.getDatabase(),StaticData.getReferenceCount(),additionsList));	    
	  }

	 
	 //client with country
	 newClient.country = Countries.getCountryObject(Long.valueOf(country_selected));
	 //order with orderLevelOfWriting
	 newOrders.orderLevelOfWriting = OrderLevelOfWriting.getLevelObject(Long.valueOf(level_of_writing_selected));
	 //order with document type
	 newOrders.orderDocumentType = OrderDocumentType.getDocumentObject(Long.valueOf(document_selected));
	 //order with orderCurrence
	 newOrders.orderCurrence = OrderCurrence.getCurrencyObject(Long.valueOf(currency_selected));
	 //order with spacing
	 newOrders.spacing = Spacing.getSpacingObject(Long.valueOf(spacing_selected));
	 //order and orderSubject
	 if(!cpp_mode.equalsIgnoreCase(OrderCppMode.CppModes.perassignment.toString())){
	  newOrders.orderSubject = OrderSubject.getSubjectObject(Long.valueOf(subject_selected));
	  newOrders.database = "";
	  newOrders.operating_system = "";
	  newOrders.prog_language = "";
	 }
	 //order and Additions
	 if(checkedAdditions!=null){
	   newOrders.additions = Additions.getListOfAdditionsObjects(checkedAdditions);
	 }	 
	 //order with client
	 newOrders.client = newClient; 
	 //save Client then the order
	 Long returnedClientId = newClient.saveClient(); 
	 
	 //order date 
	 Date  orderDate = new Date();
	 try{
	  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	  orderDate = isoFormat.parse(isoFormat.format(new Date()));	
	  newOrders.order_date = orderDate;
	  //Logger.info("date date date: " + String.format(orderDate));
	 }catch(ParseException pe){
	  Logger.info("parse Exception");
	 }catch(Exception ex){
	  Logger.info("parse Exception");
	 }
	 //computer order total
	 double order_value = newOrders.computeOrderTotal(newOrders);
	 newOrders.order_total = order_value;
	 
	 Long returnedOrderId = newOrders.saveOrder();
	 flash("cliet_order_success","You have placed an order");  
	 //return redirect(controllers.web.client.routes.ClientActions.messages()); 
	 return redirect(controllers.web.routes.Application.previewOrder(returnedOrderId));
	}
	
	public static Result previewOrder(Long id){
	  Orders orders = Orders.getOrderById(id);
	  if(orders != null){
	    return ok(orderSummary.render(orders,loginForm));
	  }else{
	    return notFound("Order Was Not found");
	  }
	}
	
	public static Result editOrder(Long id){
	  Orders orders = Orders.getOrderById(id);
	  if(orders == null){
	    return notFound("Order Was Not found");	    
	  }
	  //refill the order form and render orderClientForm for editing
	  //Order form
	  Form<Orders> filledOrderForm = orderForm.fill(orders);
	  //client form
	  Client client = orders.client;
	  Form<Client> filledClientForm = clientForm.fill(client);
	  //Countries map
	  Map<Map<Long,String>,Boolean> mapCountries = new HashMap<Map<Long,String>,Boolean>(); 
	  mapCountries = Countries.fetchCountriesMapForErrorForm(client.country.id);
	  //document
	  Map<Map<Long,String>,Boolean> mapDocuments = new HashMap<Map<Long,String>,Boolean>();
	  mapDocuments = OrderDocumentType.fetchDocumentMapForErrorForm(orders.orderDocumentType.id); 
	  //documentDeadlines
	  Map<Map<Long,String>,Boolean> documentDeadlines = OrderDeadlines.getDocumentDeadlinesForErrorForm(Long.valueOf(orders.document_deadline),orders.orderDocumentType.id);
	  //subject done when cpp mode is not equal to perassignment i.e for programming
	  Map<Map<Long,String>,Boolean> documentSubjects = new HashMap<Map<Long,String>,Boolean>();
	  if(!orders.orderDocumentType.orderCppMode.order_cpp_mode_name.name().equalsIgnoreCase(OrderCppMode.CppModes.perassignment.toString())){
	    documentSubjects = OrderSubject.getOrderSubjectsForErrorForm(orders.orderSubject.id,orders.orderDocumentType.id);
	  }
	  //spacing
	  Map<Spacing,Boolean> spacingMap = new Spacing().getSpacingMapForErrorForm(orders.spacing.id);
	  //number_of_units
	  Map<Map<Long,String>,Boolean> numberOfUnitsMap = new OrderCppMode().getNumberOfUnitsMapForErrorForm(orders.spacing.id,orders.number_of_units,orders.orderDocumentType.id);
	  //level_of_writing
	  Map<Map<Long,String>,Boolean> mapLevel = new OrderLevelOfWriting().fetchLevelMapForErrorForm(orders.orderLevelOfWriting.id);
	  //currency_selected
	  Map<OrderCurrence,Boolean> currMap = new OrderCurrence().getCurrencyMapForErrorForm(orders.orderCurrence.order_currency_id);
	  //additions
	  //selected additions
	  List<Additions> filledAdditionList = orders.additions;
	  ArrayList<String> sList = new ArrayList<String>(); 
	  
	  if(filledAdditionList != null){
	    for(Additions fList: filledAdditionList){
	      sList.add(Long.toString(fList.id));
	    }
	  }	  
	  String[] checkedAdditions = new String[sList.size()];
	  checkedAdditions = sList.toArray(checkedAdditions);	  
	  Map<Additions,Boolean> additionsList = new LinkedHashMap<Additions,Boolean>();
	  if(checkedAdditions != null){
	    additionsList = Additions.getMapForErrorForm(checkedAdditions);
	  }else{
	    if(Additions.getAdditionsList().size()>0){
	      additionsList = Additions.getAdditionsList();
	    }
	  }
	  Logger.info(" Client phone is:" + client.phone_number);
	  return ok(orderClientForm.render(filledOrderForm,filledClientForm,loginForm,mapCountries,
	  mapDocuments,documentDeadlines,documentSubjects,numberOfUnitsMap,mapLevel,currMap,
	  spacingMap, StaticData.getStyles(),StaticData.getLanguages(),StaticData.getDatabase(),StaticData.getReferenceCount(),additionsList));
	}
	
	public static Result payForOrder(Long id){
	  return TODO;
	}
	
	public static class Login {
		public String email;
		public String password;
	} 
	
}