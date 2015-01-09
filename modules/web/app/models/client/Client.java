package models.client;
import java.util.*;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.Orders;
import models.orders.OrderMessages;
import play.Logger;
import models.client.PreferredWriter;

@Entity
public class Client extends Model{ 
	@Id
	public Long id;
	//fields
	@Constraints.Required(message="First name is required.")
	public String f_name;
	@Constraints.Required(message="Last name is required.")
	public String l_name;
	
	@Constraints.Required(message="Email is required.")
	@Constraints.Email(message="Your email does not seem valid.")
	public String email;
	
	public String c_email;
	
	public String password;
	
	@Constraints.Required(message="Country code is required - Phone number is invalid or empty")
	public String country_code; 
	@Constraints.Required(message="Area code is required - Phone number is invalid or empty")
	public String area_code; 
	@Constraints.Required(message="Phone number is required - Phone number is invalid or empty")
	public String phone_number;
	
	public String alternative_phone;
	public boolean receive_company_mail=true;
	public String client_time_zone;
	public String client_time_zone_offset;
	public TimeZone client_time_zone_real;
	
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	public Date created_on;
	
	//relationship fields
	@OneToMany(mappedBy="client")
	public List <Orders> orders;
	
	@OneToOne
	public Countries country;
	
	@OneToMany(mappedBy="client")
	public List<PreferredWriter> preferredWriter;  
	
	
	public static Finder<Long, Client> finder = new Finder<Long, Client>(Long.class, Client.class);
	
	public Client(){
	
	}
	
	@PrePersist
	protected void onCreate() {
	  created_on = new Date();
	}
	
	public Client(String email, String password){
	  this.password = password;
	  this.email = email;
	}
	
	public static Client getClient(String email){
	  return finder.where().eq("email", email).findUnique();
	}
	
	public static Client authenticate(String email, String password) {
	    return finder.where().eq("email", email).eq("password", password).findUnique();
	}
	
	public Long saveClient(){
	  if(this.id == null){
	    save();
	    return id;
	  }else{
	   Logger.info("also updating client");
	   update();
	    return id;
	  }
	}
	
	public static Finder<Long, PreferredWriter> preferredWriterFinder = new Finder<Long, PreferredWriter>(Long.class, PreferredWriter.class);
	
	public static List<PreferredWriter> getPreferedWriters(){
		return preferredWriterFinder.all();
	}
		
} 
