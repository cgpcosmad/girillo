package com.girillo.background;

import com.girillo.TasksListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.AsyncTask;

/**
 * Configura y comienza la grabación en background
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public class StartRecordAudioTask extends AsyncTask<String, Void, MediaRecorder> implements OnInfoListener {  // <Params, Progress, Result> 
	private TasksListener listener;
	private MediaRecorder recorder;
	
	public StartRecordAudioTask(TasksListener listener){
		this.listener = listener;
	}
	
	/* 
	 * @params Ruta al archivo a grabar
	 * @return Devuelve el numero de segundos que han sido grabados
	 */
	protected MediaRecorder doInBackground(String... params) {
		String audioFilePath   = params[0];
		
		try {
			recorder = new MediaRecorder();
	        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	        recorder.setOutputFile(audioFilePath);
	        recorder.setMaxDuration(1000 * 60 * 10);	// 10 minutos
			recorder.prepare();
			recorder.start();  
			recorder.setOnInfoListener(this);
		} catch (Exception ex) {
			// NullPointerException, IllegalStateException, IOException
			return null;
		}	 		
		
		return recorder;
	}

	@Override
	protected void onPostExecute(MediaRecorder result) {
		super.onPostExecute(result);
		this.listener.startAudioRecording(recorder);
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		switch (what){
			case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
			case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
			case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
				this.listener.endAudioRecording();
				break;
		}
	}
}
