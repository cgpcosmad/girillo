package com.girillo;

import com.girillo.entities.Preferencias;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Preferencias de la aplicación
 * @author Carlos García
 */
public class PreferencesActivity extends Activity implements OnClickListener {
	

	private Preferencias prefs;
	
	private CheckBox smtp_starttls_enable;
	private EditText smtp_user_password;
	private EditText smtp_user_name;
	private EditText smtp_port;
	private EditText smtp_host;
	private Button accept, cancel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.preferences_mailserver);
        this.prefs    = Preferencias.getPreferences(this);
        

        this.smtp_host  		  = (EditText) this.findViewById(R.id.mail_smtp_host);
        this.smtp_port     		  = (EditText) this.findViewById(R.id.mail_smtp_port);
        this.smtp_user_name       = (EditText) this.findViewById(R.id.mail_smtp_user_name);
        this.smtp_user_password   = (EditText) this.findViewById(R.id.mail_smtp_user_password);
        this.smtp_starttls_enable = (CheckBox) this.findViewById(R.id.mail_smtp_starttls_enable);
        this.accept				  = (Button)   this.findViewById(R.id.ok);
        this.cancel				  = (Button)   this.findViewById(R.id.ko);
        
        this.smtp_host.setText(prefs.getMailServerAddress());
        this.smtp_port.setText(String.valueOf(prefs.getMailServerPort()));
        this.smtp_user_name.setText(prefs.getMailUserName());
        this.smtp_user_password.setText(prefs.getMailUserPassword());
		this.smtp_starttls_enable.setChecked(prefs.isMailServerTTLS());    	

        
    	this.accept.setOnClickListener(this);
    	this.cancel.setOnClickListener(this);        
    }



	@Override
	public void onClick(View v) {
		if (v == this.accept){
	        prefs.setMailServerAddress(smtp_host.getText().toString());
	       
	        try {
	        	prefs.setMailServerPort(Integer.parseInt(smtp_port.getText().toString()));
	        } catch (Exception ex){
	        	// Nada
	        }
	        
	        prefs.setMailUserName(smtp_user_name.getText().toString());
	        prefs.setMailUserPassword(smtp_user_password.getText().toString());
	        prefs.setMailServerTTLS(smtp_starttls_enable.isChecked());
			prefs.save(); 
		}
		
		finish();
	}
}