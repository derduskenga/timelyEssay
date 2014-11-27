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
public class OrderSubject extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="Order subject is required")
	public String subject_name;
	public String description;
	
	
	//relationship field
	@ManyToMany
	List<OrderDocumentType> orderDocumentType;
	
	@ManyToOne
	OrderSubjectCategory orderSubjectCategory;
	
	public OrderSubject(){}

	
} 
