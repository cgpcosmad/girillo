package com.girillo.background;

import com.girillo.TasksListener;
import android.media.MediaRecorder;

/**
 * Finaliza la grabación en curso, almacenando el archivo en disco y estableciendo sus propiedades.
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class StopRecordAudioTask extends android.os.AsyncTask<Object, Void, Void> {  // <Params, Progress, Result> 
	private TasksListener listener;
	
	public StopRecordAudioTask(TasksListener listener){
		this.listener = listener;
	}
	
	/* 
	 * Tareas en background
	 */
	protected Void doInBackground(Object... params) {
		MediaRecorder recorder 		= (MediaRecorder) params[0];
		
		// Finalizamos la grabación y liberamos recursos
		// Este proceso puede tardar debido a la escritura en disco
		try {
			recorder.stop();
		} catch (Exception ex){}
		
		try {
			recorder.reset();
		} catch (Exception ex){}		

		try {
			recorder.release();
		} catch (Exception ex){}		
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		this.listener.endAudioRecording();
	}
}
