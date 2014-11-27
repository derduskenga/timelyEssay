package models.writer;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.OrderMessages;

@Entity
public class FreelanceWriter extends Model{
  //fields
  @Id
  public Long id;
  public String f_name;
  public String l_name;
  //relationship fields
  

}