package com.girillo;

/**
 * Comunicaci�n entre la actividad principal y las subactivades (fotograf�a, video) 
 * @author Carlos Garc�a. cgpcosmad@gmail.com 
 */
public interface ActivityCodes {
	public static final int		IMAGE_REQUEST_CODE  = 1;
	public static final int		VIDEO_REQUEST_CODE  = 2;
	public static final String  FILE_PATH 			= "FILE_PATH";
	public static final String  MESSAGE_ERROR 		= "MESSAGE_ERROR";
}
