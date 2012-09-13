package com.girillo;

import java.util.ArrayList;

import com.girillo.background.SendMailTask;
import com.girillo.background.StartRecordAudioTask;
import com.girillo.background.StopRecordAudioTask;
import com.girillo.entities.Attachement;
import com.girillo.entities.Contact;
import com.girillo.entities.Preferencias;
import com.girillo.entities.Attachement.AttachementType;
import com.girillo.utils.GirilloUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Actividad principal de la aplicación
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class GirilloActivity extends TabActivity implements TasksListener {

	// Atributos gráficos
	private TextView contactsSelectedUI;
	private ListView contactsUI;
	private ListView attachmentsUI;	
    private Button newAudio, newImage, newVideo;
    private Button sendEmail;
	private ProgressDialog progressDialog;
	
    // Atributos funcionales
	private ArrayList<Contact> 		  dbContacts;
	private ArrayList<Attachement>    attachments;
	private ArrayAdapter<Contact> 	  contactAdapter;
	private ArrayAdapter<Attachement> attachAdapter;
	
	private MediaRecorder recorder;
	private String 		  currentAudioFileName;
//	private static final String TAG_LOG = "girillo";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        Bundle 	   extras = this.getIntent().getExtras();
        
    	this.dbContacts  = extras.getParcelableArrayList("contacts");
    	this.attachments = new ArrayList<Attachement>();
    	
    	
        TabHost tabHost = this.getTabHost();
        LayoutInflater.from(this).inflate(R.layout.girillo_main, tabHost.getTabContentView(), true);
        
        String tab1Title = getResources().getString(R.string.contacts);
        String tab2Title = getResources().getString(R.string.emailContent);
        tabHost.addTab(tabHost.newTabSpec(tab1Title).setIndicator(tab1Title, getResources().getDrawable(R.drawable.contacts)) .setContent(R.id.contactsTab));
        tabHost.addTab(tabHost.newTabSpec(tab2Title).setIndicator(tab2Title, getResources().getDrawable(R.drawable.editmessage)) .setContent(R.id.emailTab));
        
        
        // Tab de contactos
        this.contactsUI  		 = (ListView) this.findViewById(R.id.contacts);
        this.contactsSelectedUI  = (TextView) this.findViewById(R.id.contactsSelected);        		
        this.contactsSelectedUI.setText(getResources().getString(R.string.contactsSelected) + " 0");
        
        this.contactAdapter    = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_multiple_choice, this.dbContacts);
        this.contactsUI.setAdapter(this.contactAdapter);
        this.contactsUI.setItemsCanFocus(false);
        this.contactsUI.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.contactsUI.setHorizontalScrollBarEnabled(true);
        

        // Tab de archivos
        this.newImage  		= (Button)   this.findViewById(R.id.newImage);
        this.newAudio  		= (Button)   this.findViewById(R.id.newAudio);
        this.newVideo  		= (Button)   this.findViewById(R.id.newVideo);
        
        this.sendEmail 		= (Button)   this.findViewById(R.id.sendEmail);        
        this.attachmentsUI  = (ListView) this.findViewById(R.id.attachments);
        this.attachAdapter  = new AttachmentAdapter(this, R.layout.attachments_row, this.attachments);
        this.attachmentsUI.setAdapter(this.attachAdapter);
        this.attachmentsUI.setChoiceMode(ListView.CHOICE_MODE_NONE);
        
        // Evitamos que entre en modo suspensión
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setupEvents();
    }

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		
		this.dbContacts  = state.getParcelableArrayList("contacts");
		this.attachments = state.getParcelableArrayList("attachments");
		
		for (Attachement att: attachments){
			this.attachAdapter.add(att);
		}
		this.attachAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelableArrayList("contacts",    this.dbContacts);
		outState.putParcelableArrayList("attachments", this.attachments);
	}

	@Override
	protected void onDestroy() {
		// Si está grabando, dejamos de hacerlo
		if (this.isAudioRecording()){
			this.stopAudioRecord();
		}
		super.onDestroy();
	}
	

    /**
     * @return Indica si estamos en fase de grabación
     */
    private boolean isAudioRecording(){
    	return newAudio.getText().equals(getResources().getText(R.string.stopRecord));
    }
    
    /**
     * Configura los eventos de la aplicación
     */
    private void setupEvents(){
    	newImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(GirilloActivity.this, PictureActivity.class), ActivityCodes.IMAGE_REQUEST_CODE);
			}
		});  

    	newVideo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(GirilloActivity.this, VideoActivity.class), ActivityCodes.VIDEO_REQUEST_CODE);
			}
		});  
    	
    	newAudio.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (! isAudioRecording()){
					startAudioRecord();
				} else {
					stopAudioRecord();
				}				
			}
    	});
       
        
    	sendEmail.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Preferencias prefs = Preferencias.getPreferences(GirilloActivity.this);
				if (GirilloUtils.isEmpty(prefs.getMailUserName()) || GirilloUtils.isEmpty( prefs.getMailUserPassword())){
					Toast.makeText(GirilloActivity.this, getResources().getString(R.string.emailSetupInvalid), Toast.LENGTH_LONG).show();
					return;
				}
				
				// Verificamos que haya contactos seleccionados.
				final int selectedCount = GirilloUtils.getSelectedContactCount(dbContacts);
				if (selectedCount == 0){
					GirilloUtils.showMessageDialog(GirilloActivity.this, R.string.noContactSelected);
					return;
				}
				
				
				String		   message    = getResources().getString(R.string.sendEmailConfirmation, selectedCount);
	            LayoutInflater factory	  = LayoutInflater.from(GirilloActivity.this);
	            final View	   dialogView = factory.inflate(R.layout.send_email_dialog, null);
	            
	            ((TextView) dialogView.findViewById(R.id.sendEmailDialogTitle)).setText(message);
	            
	            CheckBox chkAddText = (CheckBox) dialogView.findViewById(R.id.sendEmailAddTextCheck);
	            chkAddText.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						EditText 	txtBody 	  = (EditText) dialogView.findViewById(R.id.sendEmailDialogText);
						if (isChecked){
							txtBody.setVisibility(View.VISIBLE);
						} else {
							txtBody.setVisibility(View.GONE);	
						}						
					}
				});
	           
            	
            	if (selectedCount <= 1){
            		CheckBox 	chkCco  	  = (CheckBox) dialogView.findViewById(R.id.sendEmailDialogCheckCCO);
            		chkCco.setVisibility(View.GONE);
            	}	            
            	
	            AlertDialog.Builder builder = new AlertDialog.Builder(GirilloActivity.this);
	            builder.setTitle(R.string.sendEmail);
	            builder.setView(dialogView);

	            	
	            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	CheckBox 	chkCco  	  = (CheckBox) dialogView.findViewById(R.id.sendEmailDialogCheckCCO);
	                    	CheckBox 	chkCopy 	  = (CheckBox) dialogView.findViewById(R.id.sendEmailDialogCopyEmailToMe);
	                    	EditText 	txtBody 	  = (EditText) dialogView.findViewById(R.id.sendEmailDialogText);
	                    	
	                    	String		 subject	  = getResources().getString(R.string.emailSubject);
							String		 body    	  = txtBody.getText().toString() + "\n" + getResources().getString(R.string.emailPublicity);	
					    	SendMailTask sendMailTask = new SendMailTask(GirilloActivity.this, dbContacts, attachments, subject, body, chkCco.isChecked(), chkCopy.isChecked());
					    	sendMailTask.execute();
					       
					    	// Así evitamos que salte el teclado sobre el primer control
					    	chkCco.requestFocus();
					    	
					    	progressDialog = new ProgressDialog(GirilloActivity.this);
					    	progressDialog.setIcon(R.drawable.wait);
					    	progressDialog.setTitle(R.string.sendingEmail);
					    	progressDialog.setMessage(getResources().getString(R.string.waitPlease));
					    	progressDialog.setIndeterminate(true);
					    	progressDialog.show();	                    	
	                    	
	                    }
	                });
	            builder.setNegativeButton(R.string.cancel, null);
	            AlertDialog dialog = builder.create();
		    	dialog.show();
			}
			
		});  
    	
    	
    	this.contactsUI.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				CheckedTextView checkbox = (CheckedTextView) v;
				Contact contact = dbContacts.get(position);
				contact.setSelected(! checkbox.isChecked());
				
				contactsSelectedUI.setText(getResources().getString(R.string.contactsSelected) + " " + GirilloUtils.getSelectedContactCount(dbContacts));
			}
		});
    }
    
    private void startAudioRecord(){ 
    	this.currentAudioFileName = GirilloUtils.createNewFileName(AttachementType.Audio);
    	
    	this.newAudio.setText(getResources().getText(R.string.stopRecord));
		new StartRecordAudioTask(GirilloActivity.this).execute(currentAudioFileName);	
    }
    
    private void stopAudioRecord(){
		this.newAudio.setText(getResources().getText(R.string.audioNew));          	
		new StopRecordAudioTask(GirilloActivity.this).execute(recorder);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_CANCELED) {
			String message = data.getStringExtra(ActivityCodes.MESSAGE_ERROR);
			if (GirilloUtils.isNotEmpty(message)){
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		} else if (resultCode == Activity.RESULT_OK) {
			String	filePath = data.getStringExtra(ActivityCodes.FILE_PATH);
			if (requestCode == ActivityCodes.IMAGE_REQUEST_CODE){
				this.attachAdapter.add(new Attachement(filePath, AttachementType.Image));
			} else if (requestCode == ActivityCodes.VIDEO_REQUEST_CODE){
				this.attachAdapter.add(new Attachement(filePath, AttachementType.Video));
			}
		}
	}    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    

    
    /* 
     * El usuario ha hecho clic en una de las opciones del menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	try {
	        switch (item.getItemId()) {
	            case R.id.exit: {
	                this.exitClick();
	                return true;
	            }
	            case R.id.about: {
	            	startActivity(new Intent(this, AboutActivity.class));	            	
	                return true;
	            }
	            case R.id.preferences: {
	            	startActivity(new Intent(this, PreferencesActivity.class));
	            	return true;
	            }
	        }
    	} catch (Exception ex){
    		// Pendiente
    	}
    	return false;
    }  
    
    /**
     * El usuario desea finalizar la aplicación
     */
    private void exitClick(){
		 try {
			 this.recorder.release();
		 } catch (Exception ex){} // Descartamos las posibles excepciones

		 try {
			 if (this.isAudioRecording()){
				 stopAudioRecord();
			 }
		 } catch (Exception ex){} // Descartamos las posibles excepciones		 
		 
		 attachments.clear();
			
		 this.finish();
    }
    
	@Override
	public void endAudioRecording() {
		this.newAudio.setText(getResources().getText(R.string.audioNew));      
		// Agregamos el archivo (Se agrega automáticamente a la sessión)
		this.attachAdapter.add(new Attachement(this.currentAudioFileName, AttachementType.Audio));		
	}

	@Override
	public void startAudioRecording(MediaRecorder recorder) {
		this.recorder = recorder;
	}
	
	@Override	
	public Context getContext(){
		return this.getApplicationContext();
	}

	@Override
	public void endEmailSend(boolean isOk) {
		this.progressDialog.dismiss();

		int	resourceId = R.string.sendEmailOK;
		if (! isOk){
			resourceId = R.string.sendEmailKO;
		}
		
		Toast.makeText(this, getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
	}
}
