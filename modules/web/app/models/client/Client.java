package models.client;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.Orders;
import models.orders.OrderMessages;

@Entity
public class Client extends Model{
	@Id
	public Long id;
	//fields
	@Constraints.Required(message="First name is required")
	public String f_name;
	@Constraints.Required(message="Last name is required")
	public String l_name;
	
	@Constraints.Required(message="Email is required.")
	@Constraints.Email(message="Your email does not seem valid.")
	public String email;
	
	@Constraints.Required(message="Password is required.")
	public String password;
	
	//relationship fields
	@OneToOne
	public Countries countries;
	
	@OneToMany(mappedBy="client")
	List <Orders> orders;
	
	@OneToMany(mappedBy="client")
	public List<OrderMessages> orderMessages;
	
	public static Finder<Long, Client> finder = new Finder<Long, Client>(Long.class, Client.class);
	
	public Client(){
	
	}
	
	public Client(String email, String password){
	  this.password = password;
	  this.email = email;
	}
	
	public static Client getClient(String email){
	  return finder.where().eq("emal", email).findUnique();
	}
	
	public static Client authenticate(String email, String password) {
	    return finder.where().eq("email", email).eq("password", password).findUnique();
	}
		
} 
