package com.girillo;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Grabación de video
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private MediaRecorder recorder;
	
    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
		this.mHolder  = this.getHolder();
		
		this.mHolder.addCallback(this);
		this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);        
    }
    
	public void setMediaRecorder(MediaRecorder recorder){
		this.recorder = recorder;
	}

	public Surface getSurface(){
		return mHolder.getSurface();
	}

	public void surfaceCreated(SurfaceHolder holder){
		recorder.setPreviewDisplay(mHolder.getSurface());
		
		try {
			recorder.prepare();
		} catch (Exception e) {
			try {
				recorder.release();
			} catch (Exception ex){}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder){
		try {
			recorder.release();
		} catch (Exception ex){
			// Descartamos excepciones
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){}
}
