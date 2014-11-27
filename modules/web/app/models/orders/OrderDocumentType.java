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
import java.util.Collections;

@Entity
public class OrderDocumentType extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="Document type is required")
	public String document_type_name;
	//all prices are in dollars
	public double base_price;
	public String description;
	
	//relationship fields
	@OneToMany(mappedBy="orderDocumentType")
	List<Orders> orders;
	
	@ManyToMany(mappedBy="orderDocumentType")
	List<OrderSubject> orderSubject;
	
	@ManyToOne
	OrderDeadlineCategory orderDeadlineCategory;
	
	@ManyToOne
	OrderCppMode orderCppMode;
	
	//default constructor
	public OrderDocumentType(){}
	
	 
	public static Finder<Long, OrderDocumentType> find() {
	  return new Finder<Long, OrderDocumentType>(Long.class, OrderDocumentType.class);
	}
	
	public static Map<Map<Long,String>,Boolean> fetchDocumentMap(){
	  
	  List<OrderDocumentType> documentList = OrderDocumentType.find().orderBy("document_type_name").findList();	
	  if(documentList.size()!=0){
	    Map<Long,String> innerMap = new HashMap<Long,String>();  
	    for(int i = 0; i < documentList.size(); i++){
	      innerMap.put(documentList.get(i).id,documentList.get(i).document_type_name);
	    //  Logger.info("here " + documentList.get(i).id);
	    }
	    
	  //get subject by documennt
	    Map<Long,String>subjectByDocumentMapInnerMap = new HashMap<Long,String>();
	    List<OrderSubject> subjectsForDocument = documentList.get(0).orderSubject;
	    if(subjectsForDocument.size() > 0){
	      for(int j = 0 ; j<subjectsForDocument.size(); j++){
		Logger.info("subject " + j + ": " + subjectsForDocument.get(j).subject_name);
		subjectByDocumentMapInnerMap.put(subjectsForDocument.get(j).id, subjectsForDocument.get(j).subject_name);
	      }	     
	    }
	    
	    Logger.info("size at sending" + subjectByDocumentMapInnerMap.size());
	    TempStore.setDocumentSubjects(new TreeMap<Long,String>(subjectByDocumentMapInnerMap));
	    //sort the list
	    Collections.sort(documentList, new ListSort());
	    
	    //get dealines for document 
	    Map<Map<Long,String>,Boolean> documentDeadlineMap = new HashMap<Map<Long,String>,Boolean>();
	    documentDeadlineMap = OrderDeadlines.getDeadlines(documentList.get(0).orderDeadlineCategory);
	    TempStore.setDocumentDeadlines(documentDeadlineMap);
	    
	    //getcpp mode
	    OrderCppMode.CppModes order_cpp_mode = documentList.get(0).orderCppMode.order_cpp_mode_name;
	    Map<Map<Long,String>,String> numberOfUnitsMap = OrderCppMode.getUnitsCount(order_cpp_mode);
	    //store this map to TempStore so as to retrieve later
	    TempStore.setnumberOfUnitsMap(numberOfUnitsMap);
	    
	    Map<Map<Long,String>,Boolean> documentMap = new HashMap<Map<Long,String>,Boolean>();	
	    //innerMap is sorted by use of TreeMap the added to the documentMap
	    documentMap.put(new TreeMap<Long,String>(innerMap),false);
	    return documentMap; 
	  }else{
	    TempStore.setDocumentSubjects(new TreeMap<Long,String>(new HashMap<Long,String>()));
	    TempStore.setDocumentDeadlines(new HashMap<Map<Long,String>,Boolean>());
	    TempStore.setnumberOfUnitsMap(new  HashMap<Map<Long,String>,String>());
	    
	    return new HashMap<Map<Long,String>,Boolean>(); 
	  }
	}  
	
	
	
	
} 
