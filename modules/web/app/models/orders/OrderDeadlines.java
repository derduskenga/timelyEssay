package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
//These are real order durations with their time equivalent in seconds
public class OrderDeadlines extends Model{
	@Id
	public Long id;
	//this is the value of units
	@Constraints.Required(message="Value required")
	public int deadline_value;
	//unit of time measure
	@Constraints.Required(message="Unit required Hrs, days or months")
	public String deadline_unit;
	//this is the standard value of duration in seconds
	@Constraints.Required(message="Elapsed time in seconds is required")
	public Long seconds_elapsing_to_deadline; 
	
	//note that value with unit will give the select label in the interface.
	
	//relationship fields
	@OneToMany(mappedBy="orderDeadlines")
	List<DeadlineDeadlineCategoryAssociation> deadlineDeadlineCategoryAssociation;

} 
