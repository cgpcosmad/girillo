package com.girillo.background;

import java.util.ArrayList;
import java.util.TreeSet;
import com.girillo.SplashActivity;
import com.girillo.entities.Contact;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.provider.Contacts.People;

/**
 * Inicializa los contactos
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class LoadContactsTask extends AsyncTask<Void, Void, java.util.List<Contact>> {  // <Params, Progress, Result> 

	private SplashActivity   activity;
	
	public LoadContactsTask(SplashActivity activity){
		this.activity 	 = activity;
	}
	
	protected java.util.List<Contact> doInBackground(Void... params) {
		java.util.Set<Contact>  contacts = new TreeSet<Contact>();
		
    	// Campos que necesitamos 
    	String[] projection = new String[] {People._ID, People.DISPLAY_NAME, People.NUMBER, People.PRIMARY_EMAIL_ID,  };
    	
    	// Consultamos
    	Cursor cursor = activity.managedQuery(People.CONTENT_URI,
    	                         projection, // Columnas que queremos obtener 
    	                         null,       // Devolvemos todas las columnas
    	                         null,       // Selection arguments (none)
    	                         People.DISPLAY_NAME + " ASC"); // Ordemamos por nombre
    	
        
    	activity.startManagingCursor(cursor);
        
        int nameCol  = cursor.getColumnIndex(People.DISPLAY_NAME);
        int emailCol = cursor.getColumnIndex(People.PRIMARY_EMAIL_ID);
        
         while (cursor.moveToNext()) {
              String  name    = cursor.getString(nameCol);
        
              if ((name != null) && (!"".equals(name))){
                  long     emailId     = cursor.getLong(emailCol);
            	  Uri	   emailUri    = ContentUris.withAppendedId(Contacts.ContactMethods.CONTENT_URI, emailId);
            	  String[] emailColumn = new String[] {Contacts.ContactMethods.DATA}; 
            	  Cursor   emailCursor = activity.managedQuery(emailUri, emailColumn, null, null, null);
            	  String   email	   = null;
            	  
            	  activity.startManagingCursor(emailCursor);
            	  
	              if (emailCursor.moveToNext()){
	            	  email = emailCursor.getString(0);
	              }
	              activity.stopManagingCursor(emailCursor);
	              
	              if ((email != null) && (! "".equals(email))){
	            	  Contact contact = new Contact();
	            	  if (email.equalsIgnoreCase(name)){
	            		  contact.setEmail(email);  
	            	  } else {
	            		  contact.setName(name);
	            		  contact.setEmail(email);
	            	  }
	            	  contacts.add(contact);
	              }
              }
         }
         activity.stopManagingCursor(cursor); 
         
         // Devuelvo una copia pues daba problemas al iterar
		return new ArrayList<Contact>(contacts);
	}
	
	/* 
	 * Construimos usando el Thread UI los registros encontrados
	 */
	@Override
	protected void onPostExecute(java.util.List<Contact> result) {
		super.onPostExecute(result);
		activity.loadContactsFinish(result);
	}	
}

