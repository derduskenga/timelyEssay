package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
//Would be pay per page, pay per assignment or pay per question 
public class OrderCppMode extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="CPP mode is required")
	public String order_cpp_mode_name;
	public String cpp_mode_description;
	
	//relationship fields
	@OneToMany(mappedBy="orderCppMode")
	List<OrderDocumentType> orderDocumentType;
} 
