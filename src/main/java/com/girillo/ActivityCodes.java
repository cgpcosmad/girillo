package com.girillo;

/**
 * Comunicación entre la actividad principal y las subactivades (fotografía, video) 
 * @author Carlos García. cgpcosmad@gmail.com 
 */
public interface ActivityCodes {
	public static final int		IMAGE_REQUEST_CODE  = 1;
	public static final int		VIDEO_REQUEST_CODE  = 2;
	public static final String  FILE_PATH 			= "FILE_PATH";
	public static final String  MESSAGE_ERROR 		= "MESSAGE_ERROR";
}
