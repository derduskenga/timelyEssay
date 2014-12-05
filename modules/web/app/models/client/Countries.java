package models.client;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;

@Entity
public class Countries extends Model{
  //fields
  @Id
  public Long id;
  @Constraints.Required(message="Country iso name is required")
  public String iso;
  @Constraints.Required(message="Country name is required")
  public String name;
  @Constraints.Required(message="Country nice name name is required")
  public String nicename;
  @Constraints.Required(message="Country iso name is required")
  public String iso3;
  @Constraints.Required(message="Country number code is required")
  public int numcode;
  @Constraints.Required(message="Country phone number code is required")
  public int phonecode;
  
  //field relationship
  @OneToOne(mappedBy="client_country")
  Client client;
  
  public Countries(){
  }
  
  public static Finder<Long, Countries> find() {
    return new Finder<Long, Countries>(Long.class, Countries.class);
  }
  
  public static Map<Map<Long,String>,Boolean> fetchCountriesMap(){
    List<Countries> countriesList = Countries.find().orderBy("nicename").findList();    
    Map<Long,String> innerMap = new HashMap<Long,String>();    
    for(int i = 0; i < countriesList.size(); i++){
      innerMap.put(countriesList.get(i).id,countriesList.get(i).nicename + "-" + countriesList.get(i).phonecode);
    }
    Map<Map<Long,String>,Boolean> countriesMap = new HashMap<Map<Long,String>,Boolean>();    
    countriesMap.put(innerMap,false);    
    return countriesMap;    
  } 
  
  public static List<Countries> getCountries(){
    List<Countries> countriesList = Countries.find().orderBy("nicename").findList(); 
    return countriesList;
  }
  
}