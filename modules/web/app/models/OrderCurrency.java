package models;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class OrderCurrency extends Model{
	@Id
	public Long order_currency_id;
	
} 
