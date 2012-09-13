package com.girillo;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.widget.Toast;

import com.girillo.background.LoadContactsTask;
import com.girillo.entities.Contact;

public class SplashActivity extends Activity {
	private static final long MIN_SPLASH_TIME = 3000;
	private ArrayList<Contact> dbContacts;
	private long startupTime = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.splash);
    
        
        String state = android.os.Environment.getExternalStorageState();
        if(! state.equals(Environment.MEDIA_MOUNTED)){
             Toast.makeText(this, R.string.sdcard_notfound, Toast.LENGTH_LONG).show();
             finish();   
        }
        
        
        this.dbContacts  = new ArrayList<Contact>(100);
        
        
        this.startupTime = System.currentTimeMillis();
        // Inicializa el interface gráfico con los contactos 
        new LoadContactsTask(this).execute();        
    }
	
	public void loadContactsFinish(java.util.List<Contact> contactsLoaded) {
		this.dbContacts.clear();
		this.dbContacts.addAll(contactsLoaded);

		// Como mínimo mostramos el Splash durante 3 segundos
		long diffTime = (System.currentTimeMillis() - this.startupTime);
		if ( diffTime < MIN_SPLASH_TIME){
			try {
				Thread.sleep(MIN_SPLASH_TIME - diffTime);
			} catch (InterruptedException e) {}
		}
		
		// Establecer el array dbContacts al intent
		Intent intent = new Intent(getApplicationContext(), GirilloActivity.class);
		intent.putParcelableArrayListExtra("contacts",  (ArrayList<? extends Parcelable>)    dbContacts);
    	startActivity(intent);
    	finish();
	}	
}