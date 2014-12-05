package models.orders;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

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
      
      public static JSONArray getAdditionsArray(){
	JSONArray jArray = new JSONArray();
	List<Additions> additionlist = getAdditionsList();
	for(int i = 0; i<additionlist.size(); i++){
	  JSONObject jobject = new JSONObject();
	  jobject.put("id",additionlist.get(i).id);
	  jobject.put("ui_label",additionlist.get(i).ui_label);
	  jobject.put("additional_price",additionlist.get(i).additional_price);
	  jArray.add(jobject);
	}
	return jArray;
      }
    
}