package com.girillo;

import android.content.Context;
import android.media.MediaRecorder;

public interface TasksListener {
	
	public Context getContext();
	
	/**
	 * Se ha comienzado la grabaci�n
	 */
	public void startAudioRecording(MediaRecorder recorder);
	
	/**
	 * Ha finalizado la grabaci�n
	 */
	public void endAudioRecording();
	
	
	/**
	 * Ha finalizado el envio de correo electr�nico
	 * @param isOk Indica si se ha enviado correctamente o no
	 */
	public void endEmailSend(boolean isOk);	
}
