package models.orders;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import play.Logger;
import play.Logger.ALogger;
import models.utility.Utilities;
import java.util.Collections;

@Entity
//Would be pay per page, pay per assignment or pay per question 
public class OrderCppMode extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="CPP mode is required")
	public OrderCppMode.CppModes order_cpp_mode_name;
	public String cpp_mode_description;
	
	//relationship fields
	@OneToMany(mappedBy="orderCppMode")
	List<OrderDocumentType> orderDocumentType;
	
	public static Map<Map<Long,String>,String>getUnitsCount(OrderCppMode.CppModes order_cpp_mode){
	  Map<Map<Long,String>,String> outerMap = new HashMap<Map<Long,String>,String>();
	  Map<Long,String> innerMap = new HashMap<Long,String>();
	  String selectLabel = "";
	  //A page has a minimum of 280 words
	  switch(order_cpp_mode){
	    case perpage:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    innerMap.put(Long.valueOf(i),i + "page(s) or " + i*Utilities.PAGE_WORD_COUNT + " words");
		  }
		  outerMap.put(orderMap(innerMap),"Order page/word count");
		  break;
	    case perassignment:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    innerMap.put(Long.valueOf(i),i + "");
		  }
		  outerMap.put(orderMap(innerMap),"Number of assignments");
		  break;		  
	    case perquestion:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    innerMap.put(Long.valueOf(i),i + "");
		  }
		  outerMap.put(orderMap(innerMap),"Number of questions");
		  break;
	    default:
	      //do nothing, the empty map wil be returned
	      break;
	  }
	  return outerMap;
	  
	}
	
	public static Map<Long,String> orderMap(Map<Long,String> lsMap){
	  Map<Long, String> newMap = new TreeMap(Collections.reverseOrder());
	  newMap.putAll(lsMap);
	  return lsMap;
	}
	public enum CppModes{
	  perpage,perassignment,perquestion
	}
} 
