package models.client;

import java.util.*;
import java.text.*;
import models.client.Client;
import models.writer.FreelanceWriter;
import models.support.WriterSupport;
import models.utility.*;
import models.orders.*;
import play.*;
import play.mvc.*;


public class ClientEmailTemplates{
	public static String writerAsksForExtraPagesMessage(Orders order, int pages){
		  String message_text = "<strong>Dear " + order.client.l_name + ",</strong> <br><br>" +
					"Our writer is asking you give " + pages + " additinal page(s) to your work so as to fulfill your requirements<br>" +
					"Please login into our system to respond this concern. <a href='timelyessay.com:" + Utilities.ACCESS_PORT + "/mydashboard/order/messages/" + order.order_code + "'>Login</a><br><br>" + 
					"Your Friends at Timelyessay";
		  return message_text;
	}
}