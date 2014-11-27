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
	public String first_name;
	public String last_name;
	public String email;
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
	
	
	
} 
