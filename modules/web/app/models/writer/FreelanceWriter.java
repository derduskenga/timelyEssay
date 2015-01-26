package models.writer;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.OrderMessages;
import models.client.PreferredWriter;

@Entity
public class FreelanceWriter extends Model{
  //fields
  @Id
  public Long freelance_writer_id;
  public String f_name;
  public String l_name;
  //relationship fields
  @OneToMany(mappedBy="freelanceWriter")
  public List<PreferredWriter> preferredWriter;
  
  public static Finder<Long, FreelanceWriter> find = new Finder<Long, FreelanceWriter>(Long.class, FreelanceWriter.class);
  
  public static FreelanceWriter findFreelanceWriterById(Long writer_id){
		return find.where().eq("freelance_writer_id",writer_id).findUnique();
  }
  
}