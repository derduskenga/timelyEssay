package models.orders;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class Additions extends Model{
    //fields
    @Id
    public Long id;
    @Constraints.Required(message="UI label is required")
    public String ui_label;
    @Constraints.Required(message="Additional price is required")
    public double additional_price;
    
    //relationship fields
    @ManyToMany(mappedBy="additions")
    List<Orders> orders;
    
    
     public Additions(){}
      
      public static Finder<Long, Additions> find() {
	return new Finder<Long, Additions>(Long.class, Additions.class);
      }
      
      public static List<Additions> getAdditionsList(){
	List<Additions> additionList = new ArrayList<Additions>();
	additionList = Additions.find().findList();
	return additionList;
      } 
    
}