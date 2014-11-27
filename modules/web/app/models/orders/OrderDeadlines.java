package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import play.Logger;
import play.Logger.ALogger;

@Entity
//These are real order durations with their time equivalent in seconds
public class OrderDeadlines extends Model{
	@Id
	public Long id;
	//this is the value of units
	@Constraints.Required(message="Value required")
	public int deadline_value;
	//unit of time measure
	@Constraints.Required(message="Unit required Hrs, days or months")
	public String deadline_unit;
	//this is the standard value of duration in seconds
	@Constraints.Required(message="Elapsed time in seconds is required")
	public Long seconds_elapsing_to_deadline; 
	
	//note that value with unit will give the select label in the interface.
	
	//relationship fields
	@OneToMany(mappedBy="orderDeadlines")
	List<DeadlineDeadlineCategoryAssociation> deadlineDeadlineCategoryAssociation;
	
	public OrderDeadlines(){}
	
	public static Map<Map<Long,String>,Boolean> getDeadlines(OrderDeadlineCategory orderDeadlineCategory){
	  Logger.info("in getDeadlines " + orderDeadlineCategory.order_deadline_category_name);
	  Map<Long,String> documentDeadlinesMap = new HashMap<Long,String>();
	  List<DeadlineDeadlineCategoryAssociation> deadline_deadline_category_association_list = orderDeadlineCategory.deadlineDeadlineCategoryAssociation;
	  for(int i = 0; i < deadline_deadline_category_association_list.size(); i++){
	    //elapsedseconds
	    documentDeadlinesMap.put(deadline_deadline_category_association_list.get(i).orderDeadlines.seconds_elapsing_to_deadline,
				      String.valueOf(deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_value) + " " +
				      deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_unit);
				      //Logger.info("counting");
	  }
	  Map<Map<Long,String>,Boolean> documentDeadlinesMapOuter =  new HashMap<Map<Long,String>,Boolean>();
	  
	  documentDeadlinesMapOuter.put(documentDeadlinesMap,false);
	 
	  return documentDeadlinesMapOuter;
	  
	}

} 
