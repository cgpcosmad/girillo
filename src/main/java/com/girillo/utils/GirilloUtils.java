package com.girillo.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;

import com.girillo.entities.Attachement;
import com.girillo.entities.Contact;
import com.girillo.entities.Attachement.AttachementType;

/**
 * Clase de utilidad genérica
 * @author Carlos García. cgpcosmad@gmail.com
 */
public final class GirilloUtils {
	
	public static boolean isAudio(Attachement attch){
		return AttachementType.Audio.equals(attch.getAttachementType());
	}
	
	public static boolean isImage(Attachement attch){
		return AttachementType.Image.equals(attch.getAttachementType());
	}
	
	/**
	 * @return Generamos un nombre de archivo para el adjunto
	 */
	public static String createNewFileName(AttachementType type){
		Calendar 	  cal  = Calendar.getInstance();
		StringBuilder name = new StringBuilder(64);
		
		name.append(Environment.getExternalStorageDirectory().getAbsolutePath());
		name.append("/girillo/");
		
		File appDir = new File(name.toString());
		if (! appDir.exists()){
			appDir.mkdirs();
		}
		
		name.append(cal.get(Calendar.DAY_OF_MONTH));
		name.append(cal.get(Calendar.MONTH) + 1);
		name.append(cal.get(Calendar.YEAR));
		name.append(cal.get(Calendar.HOUR_OF_DAY));
		name.append(cal.get(Calendar.MINUTE));
		name.append(cal.get(Calendar.SECOND));
		
		if (AttachementType.Audio.equals(type)){
			name.append(".mp4");
		} else if (AttachementType.Video.equals(type)){
			name.append(".mp4");
		} else if (AttachementType.Image.equals(type)){
			name.append(".jpg");
		}
		
		return name.toString();
	}

	public static void closeQuietly(java.io.OutputStream out){
		try {
			out.close();
		} catch (Exception ex){
			// Nada
		}		
	}
	
	/**
	 * Borra un archivo descartando excepciones
	 */
	public static void deleteQuietly(String fullpath){
		try {
			File file = new File(fullpath);
			file.delete();
		} catch (Exception ex){
			// Nada
		}
	}
	
    /**
     * @return El número de contactos seleccionados
     */
    public static int getSelectedContactCount(ArrayList<Contact> contacts){
		int selectedCount = 0;
		for (Contact c : contacts){
			if (c.isSelected()){
				selectedCount++;
			}
		} 
		return selectedCount;
    }
    
    public static void showMessageDialog(Context context, int resourceId){
     	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(resourceId);
    	builder.setPositiveButton(com.girillo.R.string.ok, null);
    	builder.create().show();
    }

	public static boolean isEmpty(String str) {
		return ((str == null) || ("".equals(str.trim())));
	}
	
	public static boolean isNotEmpty(String str) {
		return ! GirilloUtils.isEmpty(str);
	}
}
