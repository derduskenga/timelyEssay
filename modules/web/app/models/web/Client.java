package models.web;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class Client extends Model{
	@Id
	public Long id;
	//fields
	public String f_name;
	public String l_name;
	public String email;
	
	//Entityrelationship mapping
	@OneToMany(mappedBy="client")
	List <Order> project;
	
} 
