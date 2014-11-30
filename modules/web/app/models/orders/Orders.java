package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;

@Entity
public class Orders extends Model{
	//fields
	@Id
	public Long id;
	public int order_urgency;//this is the period given to do the order; it is given in seconds
	public String order_topic;
	public String order_instructions;
	public int number_of_units;// this is the number of pages of order, assingments of questions
	public String style_of_writing;
	public int number_of_sources;
	public String operating_system;
	public String programming_language;
	public String database_used;
	public int prefered_writer_id;//if this is a returning cistomer
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
	
	
} 
