package models.orders;
import java.util.*;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;

@Entity
public class Orders extends Model{
	//fields
	@Id
	public Long id;
	@Constraints.Required(message="Urgency required")
	public int order_urgency;//this is the period given to do the order; it is given in seconds
	@Constraints.Required(message="Order topic required")
	public String order_topic;
	@Constraints.Required(message="Order description required")
	public String order_instructions;
	@Constraints.Required(message="Number of assignments/pages/questions required")
	public int number_of_units;// this is the number of pages of order, assingments of questions
	@Constraints.Required(message="Style of writing required")
	public String style_of_writing;
	@Constraints.Required(message="Number of sources required")
	public int number_of_sources;
	public String operating_system;
	public String programming_language;
	public String database_used;
	public int prefered_writer_id;//if this is a returning customer
	@Temporal(TemporalType.TIMESTAMP)
	public Date order_date;
	public double order_total;//This is given in USD
	public double amount_paid=0.0; //This is shown in USD
	public boolean is_paid = false;
	public boolean is_writer_assigned = false;
	
	//relationship fields
	@ManyToOne
	public Client client;
	@ManyToOne
	public OrderLevelOfWriting orderLevelOfWriting;
	@ManyToOne
	public OrderDocumentType orderDocumentType;
	@ManyToOne
	public OrderCurrence orderCurrence;
	@OneToMany(mappedBy="orders")
	public List<OrderMessages> OrderMessages;
	@ManyToOne
	public Spacing spacing;
	@ManyToMany
	List<Additions> additions;
	@ManyToOne
	public OrderSubject orderSubject;
	
	@PrePersist
	protected void onCreate() {
	  order_date = new Date();
	}
	
} 
