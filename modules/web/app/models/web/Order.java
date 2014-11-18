package models.web;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class Order extends Model{
	@Id
	public Long id;
	public String first_name;
	public String last_name;
	public String email;
	 
	//fields
	
	
	@ManyToOne
	public Client client;
	
} 
