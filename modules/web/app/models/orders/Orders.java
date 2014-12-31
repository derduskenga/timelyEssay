package models.orders;
import java.util.*;
import play.data.validation.*;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import models.utility.Utilities;
import play.Logger;
import com.avaje.ebean.*;


@Entity
public class Orders extends Model{
	//fields
	@Id
	public Long order_id;
	public Long order_code;
	@Constraints.Required(message="Urgency required")
	public int document_deadline;//this is the period given to do the order; it is given in seconds
	@Constraints.Required(message="Order topic required")
	public String topic;
	@Lob
	@Constraints.Required(message="Order description required")
	public String order_instruction; 
	@Constraints.Required(message="Number of assignments/pages/questions required")
	public int number_of_units;// this is the number of pages of order, assingments of questions
	@Constraints.Required(message="Style of writing required")
	public String writing_style;
	@Constraints.Required(message="Number of sources required")
	public int no_of_references;
	public String operating_system;
	public String prog_language;
	public String database;
	public String prefered_writer;//if this is a returning customer
	@Temporal(TemporalType.TIMESTAMP)
	public Date order_date;
	@Temporal(TemporalType.TIMESTAMP)
	public Date order_deadline;
	public double order_total;//This is given in USD
	public double amount_paid = 0.0; //This is shown in USD
	public boolean is_paid = false;
	public boolean is_writer_assigned = false;//is being worked on 
	public boolean is_complete = false;
	public boolean is_closed = false;
	public boolean on_revision = false;
	public int client_feedback;
	//Order Files and types of files (e.g for revision, additional files, reference materials, order product,draft)
	//Order fines is an entity(id,date,amount,reason,removed)
	//order revisions (id,revision instructions)
	//fine categories (plagiarism, low quality, late reassignment, late submission, dissapper from order, late revision submission)
	
	//relationship fields
	//@Valid
	@ManyToOne
	public Client client;
	
	//@Valid
	@ManyToOne
	public OrderLevelOfWriting orderLevelOfWriting;
	
	//@Valid
	@ManyToOne
	public OrderDocumentType orderDocumentType;
	@ManyToOne
	public OrderCurrence orderCurrence;
	@OneToMany(mappedBy="orders")
	public List<OrderMessages> orderMessages;
	@ManyToOne
	public Spacing spacing;
	@ManyToMany
	public List<Additions> additions;
	@ManyToOne
	public OrderSubject orderSubject;
	@OneToMany(mappedBy="orders")
	public List<OrderFiles> orderFiles;
	@OneToMany(mappedBy="orders")
	public List<OrderProductFiles> orderProductFiles;	
	@OneToMany(mappedBy="orders")
	public List<OrderFines> orderFines;
	@OneToMany(mappedBy="orders")
	public List<OrderRevision> orderRevision;
	
	
	@PrePersist
	protected void onCreate(){
	  order_date = new Date();
	}
	
	public static Finder<Long, Orders> find() {
		return new Finder<Long, Orders>(Long.class, Orders.class);
	}
	
	public Long saveOrder(){
	  if(this.order_id == null){
	    save();
	    return order_id;
	  }else{
	    Logger.info("updating");
	    this.order_code = order_id + Utilities.ORDER_CODE_CONSTANT;
	    update();
	    return order_id;
	  }
	}
	
	public static Orders getOrderById(Long id){
	  return Orders.find().byId(id);
	} 
	public static Orders getOrderByCode(Long order_code){
	  return Orders.find().where().eq("order_code", order_code).findUnique();
	}

	public List<Orders> getActiveOrders(Long client_id){
	  return Orders.find().where().eq("is_complete",false).eq("client_id",client_id).orderBy("order_deadline asc").findList();
	}
	
	public  List<Orders> getCompletedOrders(Long client_id){
	  return Orders.find().where().eq("is_complete",true).eq("is_closed",false).eq("client_id",client_id).orderBy("order_deadline asc").findList();
	}
	public List<Orders> getClosedOrders(Long client_id){
	  return Orders.find().where().eq("is_closed",true).eq("client_id",client_id).orderBy("order_deadline asc").findList();
	}
	
	//Method overloading
	public Page<Orders> getActiveOrders(int page, int page_list_size){
	  return Orders.find().where().eq("is_complete",false).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	public  Page<Orders> getCompletedOrders(int page, int page_list_size){
	  return Orders.find().where().eq("is_complete",true).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	public Page<Orders> getClosedOrders(int page, int page_list_size){
	  return Orders.find().where().eq("is_closed",true).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	
	public OrderDeadlines getDeadlineObject(int dValue){
	  return OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(dValue));
	}
	
	public double computeOrderTotal(Orders newOrder){
	  double order_value = 0.0; 
	  double cost_per_unit = 0.0;

	  if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().toString().equals(OrderCppMode.CppModes.perassignment.toString())){
	    //perassignment
	    Logger.info("type:perassignment");
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		deadline_additional_price = d.additional_price;
		Logger.info("deadline_additional_price:"+deadline_additional_price);
	      }
	    }
	     
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of assignments
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    
	    cost_per_unit = base_price + deadline_additional_price + level_of_writing_additional_price;
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	    
	  }else if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().toString().equals(OrderCppMode.CppModes.perpage.toString())){
	    //per_page
	    Logger.info("type:per_page");
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
// 	    Logger.info("id"+deadline.id);
// 	    Logger.info("value:"+deadline.deadline_value);
// 	    Logger.info("unit:"+deadline.deadline_unit);
// 	    Logger.info("time:"+deadline.seconds_elapsing_to_deadline);
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		deadline_additional_price = d.additional_price;
		Logger.info("deadline_additional_price:"+deadline_additional_price);
	      }
	    }
	    
	    double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of pages  
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    int spacing_factor = newOrder.spacing.factor;
	    
	    cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price+subject_additinal_price)*spacing_factor;
	    
	    order_value = (cost_per_unit*number_of_units) + (total_additions*spacing_factor);
	  }else{	  
	  //per question
	    Logger.info("type:per_question");
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		deadline_additional_price = d.additional_price;
		Logger.info("deadline_additional_price:"+deadline_additional_price);
	      }
	    }
	    
	    double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
	    Logger.info("subject_additinal_price:" + subject_additinal_price);
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of questions  
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    
	    cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price+subject_additinal_price);
	    
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	  }
	  return Math.round(order_value*100)/100.00;
	}	
	public Date computeDeadline(Date date, int sec){
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(date.getTime());
	 
	  calender.add(Calendar.SECOND, sec);
	  Date changeDate=calender.getTime();
	  return changeDate;
	}
	
	public String getStringDeadline(Date date){
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(date.getTime());
	  String date_ = calender.DATE + "";
	  String year = calender.YEAR + "";
	  String month = (calender.MONTH + 1) + "";
	  String hour = calender.HOUR_OF_DAY + "";
	  String minute = calender.MINUTE + "";
	  return "";
	}
	
} 
