package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class OrderDocumentType extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="Document type is required")
	public String document_type_name;
	//all prices are in dollars
	public double base_price;
	public String description;
	
	//relationship fields
	@OneToMany(mappedBy="orderDocumentType")
	List<Orders> orders;
	
	@ManyToMany(mappedBy="orderDocumentType")
	List<OrderSubject> orderSubject;
	
	@ManyToOne
	OrderDeadlineCategory orderDeadlineCategory;
	
	@ManyToOne
	OrderCppMode orderCppMode;
	
	
	
} 
