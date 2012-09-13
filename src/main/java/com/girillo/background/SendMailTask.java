package com.girillo.background;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.girillo.GirilloActivity;
import com.girillo.entities.Attachement;
import com.girillo.entities.Contact;
import com.girillo.entities.Preferencias;
import android.os.AsyncTask;

/**
 * Envio de correo electrónico 
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class SendMailTask extends AsyncTask<Void, Void, Boolean> {  // <Params, Progress, Result> 

	private GirilloActivity activity;
	private List<Contact>	contacts;
	private ArrayList<Attachement>   attachments;
	private String			subject;
	private String			body;
	private boolean			cco;
	private boolean 		copyToSource;
	
	public SendMailTask(GirilloActivity activity, List<Contact> contacts, ArrayList<Attachement> attachments, String subject, String body, boolean cco, boolean copyToSource){
		this.activity  = activity;
		this.contacts  = contacts;
		this.attachments = attachments;
		this.subject   = subject;
		this.body	   = body;
		this.cco	   = cco;
		this.copyToSource	   = copyToSource;
    	
	}
	
	protected Boolean doInBackground(Void... params) {
		final 	 Preferencias prefs  = Preferencias.getPreferences(activity);
		String[] targets = null;
		Boolean  isOk	 = true;
		
		// Calculamos el número de contactos seleccionados
		int numSelected = 0;
		for (Contact contact : contacts){
			 if (contact.isSelected()){
				 numSelected++;
			 }
		}	
		
		// Creamos un array de destinatarios con el número de elementos seleccionados 
		targets = new String[numSelected];
		if (numSelected > 0){
			int current = 0;
			for (Contact contact : contacts){
				 if (contact.isSelected()){
					 targets[current] = contact.getEmail();
					 current++;
				 }
			 }		
		} 

		try {
			// add handlers for main MIME types
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);
			
			Properties props = new Properties();
			props.put("mail.smtp.host", prefs.getMailServerAddress());
			props.put("mail.smtp.port", String.valueOf(prefs.getMailServerPort()));
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "false");
			props.put("mail.smtp.starttls.enable", String.valueOf(prefs.isMailServerTTLS()));
			
			if (prefs.isMailServerTTLS()){
				props.put("mail.smtp.socketFactory.port",     String.valueOf(prefs.getMailServerPort()));
				props.put("mail.smtp.socketFactory.class",    "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.socketFactory.fallback", "false");
			}			
			
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					String userName = prefs.getMailUserName();
					String userPass = prefs.getMailUserPassword();
					
					return new PasswordAuthentication(userName, userPass);
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prefs.getMailUserName()));
			
			
			// ¿Enviamos como copia oculta?
			Message.RecipientType rtype = Message.RecipientType.TO;
			if (cco){
				rtype = Message.RecipientType.BCC;
			}
			
			for (int i = 0; i < targets.length; i++){
				message.addRecipient(rtype, new InternetAddress(targets[i]));
			}
			
			// Hay que enviar una copia a la persona que desea enviar el correo
			if (copyToSource){
				message.addRecipient(rtype, new InternetAddress(prefs.getMailUserName()));
			}
			
			
			message.setSubject(subject);

		    Multipart multipart = new MimeMultipart();
		    this.appendBody(multipart, body);
		    
		    for (int i = 0, num = this.attachments.size(); i <  num; i++){
		    	this.appendAttachment(multipart, this.attachments.get(i).getFullPath());
		    }
		    
		    message.setContent(multipart);

			Transport.send(message);
			
		} catch (Exception ex){
			isOk = false;
		}
		
		
		return isOk;
	}
	
	private void appendBody(Multipart multipart, String body) throws MessagingException {
	    BodyPart part = new MimeBodyPart();
	    part.setText(body);
	    multipart.addBodyPart(part);
	    
	}	
	
	private BodyPart appendAttachment(Multipart multipart, String pathFile) throws MessagingException {
		String fileName  = pathFile;
		int posSeparator = pathFile.lastIndexOf("/");
		if (posSeparator > 0){
			fileName = pathFile.substring(posSeparator + 1);
		}
		
	    BodyPart part = null;
	    File	 file = new File(pathFile);
	    if (file.exists() && file.canRead()){
	    	part = new MimeBodyPart();
	    	part.setDataHandler(new DataHandler(new FileDataSource(file)));
	    	part.setFileName(fileName);
	    	multipart.addBodyPart(part);
	    }
	    
	    return part;
	}	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		this.activity.endEmailSend(result);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		this.activity.endEmailSend(false);
	}	
}

