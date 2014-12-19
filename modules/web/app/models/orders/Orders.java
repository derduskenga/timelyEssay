package models.orders;
import java.util.*;
import play.data.validation.*;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import play.Logger;


@Entity
public class Orders extends Model{
	//fields
	@Id
	public Long order_id;
	@Constraints.Required(message="Urgency required")
	public int document_deadline;//this is the period given to do the order; it is given in seconds
	@Constraints.Required(message="Order topic required")
	public String topic;
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
	public double order_total;//This is given in USD
	public double amount_paid=0.0; //This is shown in USD
	public boolean is_paid = false;
	public boolean is_writer_assigned = false;
	
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
	public List<OrderMessages> OrderMessages;
	@ManyToOne
	public Spacing spacing;
	@ManyToMany
	public List<Additions> additions;
	@ManyToOne
	public OrderSubject orderSubject;
	
	
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
	    update();
	    return order_id;
	  }
	}
	
	public static Orders getOrderById(Long id){
	  return Orders.find().byId(id);
	} 
	
	public OrderDeadlines getDeadlineObject(int dValue){
	  return OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(dValue));
	}
	
	public double computeOrderTotal(Orders newOrder){
	  double order_value = 0.0; 
	  double cost_per_unit = 0.0;

	  if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().equals(OrderCppMode.CppModes.perassignment.toString())){
	    //perassignment
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlines.seconds_elapsing_to_deadline.equals(Long.valueOf(newOrder.document_deadline))){
		deadline_additional_price = d.additional_price;
	      }
	    }
	     
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of assignments
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    
	    cost_per_unit = base_price + deadline_additional_price + level_of_writing_additional_price;
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	    
	  }else if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().equals(OrderCppMode.CppModes.perpage.toString())){//per page
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlines.seconds_elapsing_to_deadline.equals(Long.valueOf(newOrder.document_deadline))){
		deadline_additional_price = d.additional_price;
	      }
	    }
	    
	    double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of pages  
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    int spacing_factor = newOrder.spacing.factor;
	    
	    cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price + subject_additinal_price)*spacing_factor;
	    
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	  }else{
	  
	  //per question
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlines.seconds_elapsing_to_deadline.equals(Long.valueOf(newOrder.document_deadline))){
		deadline_additional_price = d.additional_price;
	      }
	    }
	    
	    double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of questions  
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    
	    cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price + subject_additinal_price);
	    
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	  }
	  return Math.round(order_value*100)/100.00;
	}
	
} 
