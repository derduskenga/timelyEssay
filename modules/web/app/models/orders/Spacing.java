package models.orders;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import play.Logger;
import java.util.Collections;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Entity
public class Spacing extends Model{
      //fields
      @Id
      public Long id;
      @Constraints.Required(message="Spacing required")
      public String spacing;
      
      @Constraints.Required(message="Alias required")
      public String alias;
      
      @Constraints.Required(message="Price muliplier factor required")
      public int factor;
      
      
      @OneToMany(mappedBy="spacing")
      List<Orders> orders;
      
      public Spacing(){}
      
      public static Finder<Long, Spacing> find() {
	      return new Finder<Long, Spacing>(Long.class, Spacing.class);
      }
      
      public static List<Spacing> getSpacingList(){
	  List<Spacing> spacingList = new ArrayList<Spacing>();
	  spacingList = Spacing.find().findList();
	  //sort the list
	  Collections.sort(spacingList, new SpacingListSort());
	  return spacingList;
      }
      
      public static JSONArray getSpacingArray(){
	JSONArray jArray = new JSONArray();
	List<Spacing> slist = new ArrayList<Spacing>();
	slist = getSpacingList();
	for(int i=0; i<slist.size();i++){
	  JSONObject job = new JSONObject();
	  job.put("id",slist.get(i).id);
	  job.put("spacing",slist.get(i).spacing);
	  job.put("alias",slist.get(i).alias);
	  job.put("factor",slist.get(i).factor);
	  jArray.add(job);
	}
	return jArray;
      }
  
  
}