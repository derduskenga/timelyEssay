package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class DeadlineDeadlineCategoryAssociation extends Model{

    //fields
    @Id
    public Long id;
    @ManyToOne
    public OrderDeadlines orderDeadlines;
    @ManyToOne
    public OrderDeadlineCategory orderDeadlineCategory;
    public double additional_price;
}