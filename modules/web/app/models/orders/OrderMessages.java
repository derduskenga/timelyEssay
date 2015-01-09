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
import com.avaje.ebean.Expr;

@Entity
public class OrderMessages extends Model{
		//fields
		@Id
		public Long id;
		
		@Constraints.Required(message = "Please select message receipient.")
		public MessageParticipants msg_to;
		
		public MessageParticipants msg_from;
		
		@Column(columnDefinition="boolean default false")
		public Boolean status = false; 
		//relationship fields
		@ManyToOne
		public Orders orders;		
		@Column(nullable=false)
		@Temporal(TemporalType.TIMESTAMP)
		public Date sent_on=new Date();
		
		@Constraints.Required(message = "Please select message receipient")
		public String message;
		
		public OrderMessages(){}
		
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
		      if(this.id == null){
			save();
			return true;
		      }
		      update();
		      return true;
		}
		
		public static Finder<Long, OrderMessages> orderMessagesFinder = 
						new Finder<Long, OrderMessages>(Long.class, OrderMessages.class);
						
		public static OrderMessages getMessageById(Long id){
			return orderMessagesFinder.byId(id);
		}
		
		public static List<OrderMessages> getClientOrderMessages(Long order_code){
					return  orderMessagesFinder.where()
							.eq("orders.order_code",order_code)
							.or(Expr.eq("msg_to", MessageParticipants.CLIENT),Expr.eq("msg_from", MessageParticipants.CLIENT))
							.orderBy("sent_on").findList();
		}
		
		public int getUnreadMessages(Long order_code){
		  Orders order = Orders.getOrderByCode(order_code);
		  if(order == null){
		    return 0;
		  }  
		  List<OrderMessages> orderList = order.orderMessages;		  
		  int unread = 0;
		  for(OrderMessages messages:orderList){
		    if(!messages.status && messages.msg_to == MessageParticipants.CLIENT){//unread messages
		      unread = unread + 1;
		    }
		  }
		  return unread;
		}
}