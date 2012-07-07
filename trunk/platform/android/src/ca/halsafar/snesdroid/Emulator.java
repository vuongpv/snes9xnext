/**
 * ANDROID EMUFRAMEWORK
 * 
 * SEE LICENSE FILE FOR LICENSE INFO
 * 
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */
package ca.halsafar.snesdroid;

import ca.halsafar.filechooser.ZipFileType;

/**
 * MAJOR TODO 	- remove app specific stuff in PreferenceFacade
 */

/**
 * Emulator bridge to native side.
 * All Java to Native communication happens through this one class.
 * See jni/EmulatorBridge.cpp to see the implementation 
 * @author halsafar
 *
 */


public class Emulator
{	
     // load our native library
	static
	{
		System.loadLibrary("emu");	   
	}	
	
	// lock instantiation
	private Emulator()
	{
		
	}
	
	public static native int init(final String apkAbsolutePath, final String externalStoragePath);
		
	public static native int initGraphics(int width, int height, int screenType);
		
	public static native int resetConfig();
	
	public static native int resetInputConfig();
	
	public static native int processConfig();
	
	public static native int processGraphicsConfig();
	
	public static native String getConfigFileName();
	
	public static native int onPause();
	
	public static native int onResume();
		
	public static native int loadRom(final String fileName);
	
	public static native boolean isRomLoaded();
	
	public static native void onTouchDown(final int finger, final float x, final float y, final float radius);
	
	public static native void onTouchUp(final int finger, final float x, final float y, final float radius);
	
	public static native void onTouchMove(final int finger, final float x, final float y, final float radius);
	
	public static native void onKeyDown(int keyCode);
	
	public static native void onKeyUp(int keyCode);
	
	public static native void saveState(final int i);
	
	public static native void loadState(final int i);
	
	public static native void selectState(final int i);

	public static native void resetGame();
	
	public static native void parse7Zip(final String fileName, String[] directories, ZipFileType[] files);
	
	public static native int unzip7ZFile(final String zipFileName, int id, final String extractFileName, final String outLocation);	
		
	public static native int unzipFile(final String zipFileName, final String extractFileName, final String outLocation);	
		
	public static native void destroy();
	
	public static native void step();
	
	public static native void draw();
}