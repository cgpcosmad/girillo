package com.girillo;


import com.girillo.entities.Attachement.AttachementType;
import com.girillo.utils.GirilloUtils;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Actividad para grabar video
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class VideoActivity extends Activity {
	private MediaRecorder	 recorder;
	private VideoSurfaceView preview;
	private String			 videoFullPath;
	private ToggleButton	 btnStartStop;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.video);

		this.btnStartStop  = (ToggleButton) this.findViewById(R.id.btn_video);
		
		this.videoFullPath = GirilloUtils.createNewFileName(AttachementType.Video);
		this.recorder	   = new MediaRecorder();
		this.preview	   = (VideoSurfaceView) this.findViewById(R.id.videoSurfaceView);
		this.preview.setMediaRecorder(recorder);
		
		// ¡¡ El orden de configuración importa !!
		this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		this.recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		this.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		this.recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        
		
		this.recorder.setOutputFile(this.videoFullPath);
		this.setupEvents();
	}

	private void setupEvents(){
		btnStartStop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					startRecordClick();	
				} else {
					stopRecordClick();
				}				
			}
			
		});
	}

    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		this.releaseRecorderQuietly();
    		
            Intent intent = new Intent();
            intent.putExtra(ActivityCodes.MESSAGE_ERROR, "");
            this.setResult(Activity.RESULT_CANCELED,  intent);
    		this.finish();
            return true;
        }
    	
        return false;
    }
    
    /**
     * El usuario desea iniciar la grabación de video
     */
    private void startRecordClick(){
		try {
			this.recorder.start();
		} catch (Exception e) {
			this.stopRecordClick();
		} 
    }
    
    /**
     * El usuario desea finalizar el video
     */
    private void stopRecordClick(){
        try {
			recorder.stop();
			recorder.release();
        	
            Intent intent = new Intent();
            intent.putExtra(ActivityCodes.FILE_PATH,  videoFullPath);
            this.setResult(Activity.RESULT_OK,  intent);
        } catch (Exception ex) {
    		this.releaseRecorderQuietly();
    		
            Intent intent = new Intent();
            intent.putExtra(ActivityCodes.MESSAGE_ERROR, ex.getMessage());
            this.setResult(Activity.RESULT_CANCELED,  intent);
        } finally {
        	this.finish();
        }
    }
    
	/**
	 * Libera recursos ignorando excepciones
	 */
	private void releaseRecorderQuietly(){
		try {
			recorder.stop();
		} catch (Exception ex2){}				
		try {
			recorder.release();
		} catch (Exception ex2){}
	}	
}

