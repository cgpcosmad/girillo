package com.girillo;

import java.io.FileOutputStream;
import java.io.IOException;

import com.girillo.entities.Attachement.AttachementType;
import com.girillo.utils.GirilloUtils;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * Actividad para capturar fotografía
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class PictureActivity extends Activity implements SurfaceHolder.Callback, android.hardware.Camera.PictureCallback {
    private Camera  camera;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.picture);
    
        SurfaceView		surfaceView	  = (SurfaceView) findViewById(R.id.surface);
        SurfaceHolder	surfaceHolder = surfaceView.getHolder();
        
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		this.releaseQuietly();
    		
            Intent intent = new Intent();
            intent.putExtra(ActivityCodes.MESSAGE_ERROR, "");
            this.setResult(Activity.RESULT_CANCELED,  intent);
    		this.finish();
            return true;            
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            camera.takePicture(null, null, this);
            return true;
        }
        return false;
    }
    

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			// No se producirá, ignoramos.
		}
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
       this.camera = Camera.open();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	this.releaseQuietly();
    }
    
	public void onPictureTaken(byte[] data, Camera camera) {
        FileOutputStream fos = null;
        
        try {
        	String filePath = GirilloUtils.createNewFileName(AttachementType.Image);
        	fos 			= new FileOutputStream(filePath);
            fos.write(data);
            
            Intent intent = new Intent();
            intent.putExtra(ActivityCodes.FILE_PATH,  filePath);
            this.setResult(Activity.RESULT_OK,  intent);
        } catch (Exception ex) {
            Intent intent = new Intent();
            intent.putExtra(ActivityCodes.MESSAGE_ERROR, ex.getMessage());
            this.setResult(Activity.RESULT_CANCELED,  intent);
        } finally {
        	GirilloUtils.closeQuietly(fos);
        	this.finish();
        }	
	}    
	
	/**
	 * Liberamos recursos ignorando las excepciones
	 */
    private void releaseQuietly() {
    	try {
    		this.camera.stopPreview();
    	} catch (Exception ex){}
    	
    	try {
    		this.camera.release();
    	} catch (Exception ex){}
	}	
}