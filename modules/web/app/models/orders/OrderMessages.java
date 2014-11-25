package models.orders;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import models.writer.FreelanceWriter;
import models.support.WriterSupport;

import java.util.Map;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;

@Entity
public class OrderMessages extends Model{
		//fields
		@Id
		public Long id;
		
		@Constraints.Required(message = "Please select message receipient.")
		public MessageParticipants msg_to;
		
		public MessageParticipants msg_from;
		
		@Column(columnDefinition="boolean default TRUE")
		public Boolean status; 
		//relationship fields
		@ManyToOne
		public Orders orders;
		
		
		@Column(nullable=false)
		@Temporal(TemporalType.TIMESTAMP)
		public Date sent_on=new Date();
		
		@Constraints.Required(message = "Please select message receipient")
		public String message;
		
		public static Map<String, Boolean> getReceipientsMap(String exclude) {
				Map<String, Boolean> 	receipientsMap = new HashMap<String, Boolean>();
				for (MessageParticipants msgParticipant : MessageParticipants.values()) {
					String receipient = msgParticipant.name().trim();
					if(!receipient.equals(exclude.trim()))
						receipientsMap.put(msgParticipant.name(), false);
				}
				return receipientsMap;
		}	
		
		public Boolean saveClientMessage(){
				save();
				return true;
		}
		
		public static Finder<Long, OrderMessages> orderMessagesFinder = 
									new Finder<Long, OrderMessages>(Long.class, OrderMessages.class);
		
		public static List<OrderMessages> getOrderMessages(){
					return  orderMessagesFinder.orderBy("sent_on").findList();
		}
}