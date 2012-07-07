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


import ca.halsafar.filechooser.FileChooser;
import ca.halsafar.snesdroid.Emulator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Manages the activity exclusive to dealing with rendering and update step
 * aspects of emulation.
 * 
 * @author halsafar
 * 
 */
public class EmulatorActivity extends Activity
{
     private static final String LOG_TAG           = "EmulatorActivity";
     private EmulatorView        _view;
     private int                 _currentSaveState = 0;


     @Override
     public void onCreate(Bundle savedInstanceState)
     {
          Log.d(LOG_TAG, "onCreate()");

          super.onCreate(savedInstanceState);
         
          // start displaying
          _view = new EmulatorView(this, getApplication());
          setContentView(_view);
     }


     @Override
     public boolean onCreateOptionsMenu(Menu myMenu)
     {
    	 Log.d(LOG_TAG, "onCreateOptionsMenu(" + myMenu + ")");
    	 
    	 _view.queueEvent(new Runnable()
         {
              public void run()
              {
            	  Emulator.onPause();
              }
         });
    	     	 
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.emulator, myMenu);

         return true;
     }


     @Override
     public void onResume()
     {
          super.onResume();

          Log.d(LOG_TAG, "onResume()");

          // force orientation
          int orientation = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_ORIENTATION, "value"));          
          if (orientation == 0)
          {
        	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          }
          else if (orientation == 1)
          {
        	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          }
          else
          {
        	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          }          
          
     	 _view.queueEvent(new Runnable()
         {

              public void run()
              {
            	  Emulator.onResume();
              }
         });
     	 
          _view.onResume();

          // help android out a bit... seems to be slow at releasing sometimes
          System.gc();
     }


     @Override
     public void onPause()
     {
          Log.d(LOG_TAG, "onPause()");

          super.onPause();

      	 _view.queueEvent(new Runnable()
         {

              public void run()
              {
            	  Emulator.onPause();
              }
         });

          _view.onPause();

          SettingsFacade.DoAutoSave();
     }


     @Override
     public void onDestroy()
     {
          super.onDestroy();
     }


     @Override
     public void onStop()
     {
          Log.d(LOG_TAG, "onStop()");

          super.onStop();
     }

     
     @Override
     public boolean onOptionsItemSelected(MenuItem item)
     {
    	 _view.queueEvent(new Runnable()
         {
              public void run()
              {
            	  Emulator.onResume();
              }
         });
    	 
          switch (item.getItemId())
          {
               /*
                * case R.id.menuShowKeyboard: InputMethodManager inputMgr =
                * (InputMethodManager
                * )getSystemService(Context.INPUT_METHOD_SERVICE);
                * inputMgr.showSoftInput(_view, InputMethodManager.SHOW_FORCED);
                * //inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                * InputMethodManager.HIDE_IMPLICIT_ONLY); return true;
                */
               /*
                * case R.id.menuExit: Intent intent = new Intent();
                * setResult(RESULT_OK, intent);
                * 
                * this.finish();
                * 
                * return true;
                */
               case R.id.menuMenu:
                    this.finish();
                    return true;
               case R.id.menuSettings:
                    spawnSettings();
                    return true;
               case R.id.menuLoadROM:
                    spawnFileChooser();
                    return true;
               case R.id.menuSelectState:
                    return true;
               case R.id.menuLoadState:
                    Emulator.loadState(_currentSaveState);
                    return true;
               case R.id.menuSaveState:
                    Emulator.saveState(_currentSaveState);
                    return true;
               case R.id.menuResetGame:
                    Emulator.resetGame();
                    return true;
               case R.id.menuSelectShader:
            	   	String shaderDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SHADERS, "value");
            	   
                    Intent myIntent = new Intent(EmulatorActivity.this, FileChooser.class);
                    myIntent.putExtra(FileChooser.EXTRA_START_DIR, shaderDir);
                    myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, SettingsFacade.DEFAULT_SHADER_EXTENSIONS);
                    final int result = SettingsFacade.MENU_SHADER_SELECT;
                    startActivityForResult(myIntent, result);
                    return true;
               case R.id.menuResetShader:
                    _view.queueEvent(new Runnable()
                    {

                         public void run()
                         {
                        	 ConfigXML.setNodeAttribute(ConfigXML.PREF_SHADER_FILE, "value", "");
                        	 ConfigXML.writeConfigXML();
                        	 Emulator.processGraphicsConfig();
                        	 /*_view.queueEvent(new Runnable() {							
 							@Override
 							public void run() {
 								Emulator.processGraphicsConfig();
 							}
 						}); */   
                         }
                    });
                    return true;
               case R.id.menuState0:
                    _currentSaveState = 0;
                    return true;
               case R.id.menuState1:
                    _currentSaveState = 1;
                    return true;
               case R.id.menuState2:
                    _currentSaveState = 2;
                    return true;
               case R.id.menuState3:
                    _currentSaveState = 3;
                    return true;
               case R.id.menuState4:
                    _currentSaveState = 4;
                    return true;
               case R.id.menuState5:
                    _currentSaveState = 5;
                    return true;
               case R.id.menuState6:
                    _currentSaveState = 6;
                    return true;
               case R.id.menuState7:
                    _currentSaveState = 7;
                    return true;
               case R.id.menuState8:
                    _currentSaveState = 8;
                    return true;
               case R.id.menuState9:
                    _currentSaveState = 9;
                    return true;
               default:
                    return super.onOptionsItemSelected(item);
          }
          
          
     }


     protected void spawnFileChooser()
     {
    	 String romDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_ROMS, "value");
    	 String tempDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_TEMP, "value");
    	 
          // spawn the file chooser
          Intent myIntent = new Intent(EmulatorActivity.this, FileChooser.class);
          myIntent.putExtra(FileChooser.EXTRA_START_DIR, romDir);
          myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, SettingsFacade.DEFAULT_ROM_EXTENSIONS);
          myIntent.putExtra(FileChooser.EXTRA_TEMP_DIR, tempDir);
          final int result=SettingsFacade.MENU_ROM_SELECT;
          startActivityForResult(myIntent, result);             
     }


     protected void spawnSettings()
     {
          // spawn the file chooser
          Intent myIntent = new Intent(EmulatorActivity.this, SettingsActivity.class);
          final int result = SettingsFacade.MENU_SETTINGS;
          startActivityForResult(myIntent, result);
     }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data)
     {
          Log.d(LOG_TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");

          if (requestCode == SettingsFacade.MENU_SETTINGS)
          {
               _view.queueEvent(new Runnable()
               {
                    public void run()
                    {
                         Emulator.processConfig();                                             
                    }
               });
          }
          else if (requestCode == SettingsFacade.MENU_ROM_SELECT)
          {
               String romFile = data.getStringExtra("Filename");
               if (romFile != null)
               {
                    if (Emulator.loadRom(romFile) != 0)
                    {
                         finish();
                    }

                    SettingsFacade.DoAutoLoad();
               }
          }
          else if (requestCode == SettingsFacade.MENU_SHADER_SELECT)
          {
               String shaderFile = data
                         .getStringExtra(FileChooser.PAYLOAD_FILENAME);

               Log.d(LOG_TAG, "Shader Selected: " + shaderFile);

               if (shaderFile != null)
               {
            	   String shaderFileName = shaderFile.substring(0, shaderFile.lastIndexOf('.'));               
            	   ConfigXML.setNodeAttribute(ConfigXML.PREF_SHADER_FILE, "value", shaderFileName);
            	   ConfigXML.writeConfigXML();
            	   _view.queueEvent(new Runnable()
                   {

                        public void run()
                        {
                        	Emulator.processGraphicsConfig();
                        }
                   });
               }
          }
     }

}
