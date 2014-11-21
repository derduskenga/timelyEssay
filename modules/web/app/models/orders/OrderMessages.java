package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import models.writer.FreelanceWriter;
import models.support.WriterSupport;

@Entity
public class OrderMessages extends Model{
  //fields
  @Id
  public Long id;
  
  
  
  //relationship fields
  @ManyToOne
  public Orders orders;
  @ManyToOne
  public Client client;
  @ManyToOne
  public FreelanceWriter freelanceWriter;
  @ManyToOne
  public WriterSupport writerSupport;
  
}