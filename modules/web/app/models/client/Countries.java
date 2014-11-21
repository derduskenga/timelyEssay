package models.client;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;


@Entity
public class Countries extends Model{
  //fields
  @Id
  public Long id;
  public String iso;
  public String name;
  public String nicename;
  public String iso3;
  public int numcode;
  public int phonecode;
  
  //field relationship
  @OneToOne(mappedBy="countries")
  Client client;
}