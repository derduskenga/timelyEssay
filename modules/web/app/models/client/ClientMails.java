package models.client;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.microtripit.mandrillapp.lutung.view.MandrillUserInfo;

import models.client.Client;

import java.io.IOException;
import play.Logger;

public class ClientMails{
			
			MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
			
			public void sendClientInvitationMail(String email){
					MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
					MandrillMessage message = new MandrillMessage();
					message.setSubject("Check Out This Service.");
					message.setFromEmail("sales@timelyessay.com");
					message.setFromName("sales@timelyessay.com");
					try{
					message.setHtml("<p><span style='font-family: arial black,avant garde;'>Hello,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I recently came across TimelyEssay.com.</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'> They provide excellent writing services in all academic fields, and have exceptional customer support. </span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I am pretty sure you will enjoy their services. <strong>Use the promocode {INSERT_CODE_HERE} and get a 18% discount</strong>. </span></p>"+
													"<p>&nbsp;</p>"+
													"<p><span style='font-family: arial black,avant garde;'>Cheers,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>{INSERT_NAME_HERE}</span></p>");
					message.setAutoText(true);
					
					ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
					MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
					recipient.setEmail(email);
					recipients.add(recipient);
					message.setTo(recipients);
					message.setPreserveRecipients(false);
					MandrillMessageStatus[] messageStatusReports = mandrillApi
							.messages().send(message, false); 			
					} catch (MandrillApiError mandrillApiError) {
							mandrillApiError.printStackTrace();
					} catch (IOException e) {
							e.printStackTrace();
					}
			
			}
			
			public void sendRegisteredClientFirstEmail(final Client client,final String password){
					Thread t = new Thread() {
						public void run() {
										MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
										MandrillMessage message = new MandrillMessage();
										message.setSubject("Timely Essay Login Details");
										message.setFromEmail("info@timelyessay.com");
										message.setFromName("sales@timelyessay.com");
										try{
										message.setHtml("<p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Dear "+client.f_name+",</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Thank you for choosing Timely Essay for your professional academic writing services. </span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Your Login credentials are as follows:</span></p><p style='padding-left: 120px;'><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Email: <strong>"+client.email+"</strong></span></p><p style='padding-left: 120px;'><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Password: <strong>"+password+"</strong></span></p><p>&nbsp;</p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Kind Regards,</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Your Friends at Timely Essay.</span></p><p>&nbsp;</p>");
										message.setAutoText(true);
										ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
										MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
										recipient.setEmail(client.email);
										recipients.add(recipient);
										message.setTo(recipients);
										message.setPreserveRecipients(false);
										MandrillMessageStatus[] messageStatusReports = mandrillApi
												.messages().send(message, false); 			
										} catch (MandrillApiError mandrillApiError) {
												mandrillApiError.printStackTrace();
										} catch (IOException e) {
												e.printStackTrace();
										}
								
								}
					};
					t.start();
					
		}
		
}
 
