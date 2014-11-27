package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import play.Logger;
import java.util.Collections;

@Entity
public class OrderCurrence extends Model{
	@Id
	public Long order_currency_id;
	
	@Constraints.Required(message="Currence name is required")
	public String currency_name;
	
	@Constraints.Required(message="Currence symbol is required")
	public String currency_symbol;
	
	@Constraints.Required(message="Currence symbol is required")
	public String currency_symbol_2;
	
	@Constraints.Required(message="Currence convertion rate is required")
	public double convertion_rate;
	
	
	//relationship fields
	@OneToMany(mappedBy="orderCurrence")
	List<Orders> orders;
	
	public OrderCurrence(){}
	
	public static Finder<Long, OrderCurrence> find() {
	  return new Finder<Long, OrderCurrence>(Long.class, OrderCurrence.class);
	}
	
	public static List<OrderCurrence> getCurrenceList(){
	  List<OrderCurrence> currenceList = new ArrayList<OrderCurrence>();
	  currenceList = OrderCurrence.find().findList();
	  //sort the list
	  Collections.sort(currenceList, new CurrenceListSort());
	  return currenceList;
	} 
	
	
} 
