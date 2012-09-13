package com.girillo.entities;

import com.girillo.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

public class Preferencias {
	private static final String PREFERENCES_FILE    = "girillo_pref";
	private static final String PREF_MAIL_SERVER_ADDR	= "PREF_MAIL_SERVER_ADDR";
	private static final String PREF_MAIL_SERVER_PORT	= "PREF_MAIL_SERVER_PORT";
	private static final String PREF_MAIL_SERVER_TTLS	= "PREF_MAIL_SERVER_TTLS";
	private static final String PREF_EMAIL_USER_NAME	= "PREF_EMAIL_USER_NAME";
	private static final String PREF_EMAIL_USER_PWD	 	= "PREF_EMAIL_USER_PWD";
	
	private static Preferencias preferences;
	private static Context	   context;
	
	private String  mailServerAddress; // Normalmente SMTP
	private int	    mailServerPort;
	private boolean	mailServerTTLS;
	
	// Autenticacion de cara al servidor
	private String mailUserName;
	private String mailUserPassword;
	
	private Preferencias(){}
	
	public String getMailServerAddress() {
		return mailServerAddress;
	}

	public void setMailServerAddress(String mailServerAddress) {
		this.mailServerAddress = mailServerAddress;
	}

	public int getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(int mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isMailServerTTLS() {
		return mailServerTTLS;
	}

	public void setMailServerTTLS(boolean mailServerTTLS) {
		this.mailServerTTLS = mailServerTTLS;
	}

	public String getMailUserName() {
		return mailUserName;
	}

	public void setMailUserName(String mailUserName) {
		this.mailUserName = mailUserName;
	}

	public String getMailUserPassword() {
		return mailUserPassword;
	}

	public void setMailUserPassword(String mailUserPassword) {
		this.mailUserPassword = mailUserPassword;
	}
	
	public static Preferencias getPreferences(Context context){
		if (Preferencias.preferences == null){
			Preferencias.context = context;
			Preferencias.loadPreferences();
		}
		return Preferencias.preferences;
	}
	
	/**
	 * Guarda las preferencias de la aplicación 
	 */
	public void save(){
		SharedPreferences  settings = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		Editor			   editor   = settings.edit();
		
		editor.putString(PREF_MAIL_SERVER_ADDR,   preferences.getMailServerAddress());
		editor.putString(PREF_EMAIL_USER_NAME,    preferences.getMailUserName());
		editor.putString(PREF_EMAIL_USER_PWD,     preferences.getMailUserPassword());
		editor.putBoolean(PREF_MAIL_SERVER_TTLS,  preferences.isMailServerTTLS());
		editor.putInt(PREF_MAIL_SERVER_PORT,      preferences.getMailServerPort());
		
		editor.commit();    
	}

	/**
	 * Creamos el objeto Preferences
	 */
	private static void loadPreferences(){

		SharedPreferences settings  = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		Resources		  resources = context.getResources();
		
		preferences = new Preferencias();
        preferences.setMailServerAddress(settings.getString(PREF_MAIL_SERVER_ADDR,  resources.getString(R.string.mail_smtp_host)));
        preferences.setMailServerPort(settings.getInt(PREF_MAIL_SERVER_PORT,        Integer.valueOf(resources.getString(R.string.mail_smtp_port))));
        preferences.setMailServerTTLS(settings.getBoolean(PREF_MAIL_SERVER_TTLS,    Boolean.valueOf(resources.getString(R.string.mail_smtp_starttls_enable))));
        preferences.setMailUserName(settings.getString(PREF_EMAIL_USER_NAME, 		resources.getString(R.string.mail_smtp_user_name)));
        preferences.setMailUserPassword(settings.getString(PREF_EMAIL_USER_PWD,		resources.getString(R.string.mail_smtp_user_password)));
	}
}
