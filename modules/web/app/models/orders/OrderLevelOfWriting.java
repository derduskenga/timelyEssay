package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class OrderLevelOfWriting extends Model{
	@Id
	public Long id;
	
	@Constraints.Required(message="Level of writing is required")
	public String order_level;
	
	@Constraints.Required(message="Level additional price is required")
	//all prices are in US Dollars
	public double additional_price;
	public String description;
	
	@OneToMany(mappedBy="orderLevelOfWriting")
	List<Orders> orders;
	
} 
