package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class OrderCurrence extends Model{
	@Id
	public Long order_currency_id;
	
	@Constraints.Required(message="Currence name is required")
	public String currency_name;
	
	@Constraints.Required(message="Currence symbol is required")
	public String currency_symbol;
	
	@Constraints.Required(message="Currence convertion rate is required")
	public double convertion_rate;
	
	
	//relationship fields
	@OneToMany(mappedBy="orderCurrence")
	List<Orders> orders;
	
	
} 
