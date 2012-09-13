package com.girillo;

import android.content.Context;
import android.media.MediaRecorder;

public interface TasksListener {
	
	public Context getContext();
	
	/**
	 * Se ha comienzado la grabación
	 */
	public void startAudioRecording(MediaRecorder recorder);
	
	/**
	 * Ha finalizado la grabación
	 */
	public void endAudioRecording();
	
	
	/**
	 * Ha finalizado el envio de correo electrónico
	 * @param isOk Indica si se ha enviado correctamente o no
	 */
	public void endEmailSend(boolean isOk);	
}
