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

import java.io.File;


import ca.halsafar.filechooser.FileChooser;
import ca.halsafar.snesdroid.Emulator;
import ca.halsafar.snesdroid.SettingsFacade;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * MainMenu Activity
 * @author halsafar
 *
 */
public class MainActivity extends Activity
{          
     private static final String LOG_TAG = "MainActivity";
                    
     //private boolean _useAudio = false; 
     private boolean _init = false;         
            

     @Override
     public void onCreate(Bundle savedInstanceState)
     {
          Log.d(LOG_TAG, "onCreate()");
          
          super.onCreate(savedInstanceState);              
     }
     
     
     @Override
     public void onStart()
     {
          Log.d(LOG_TAG, "onStart()");
          
          super.onStart();         

          System.gc();
                    
          init();             
     }     
              
     
     private boolean verifyExternalStorage()
     {
       // check for sd card first
          boolean mExternalStorageAvailable = false;
          boolean mExternalStorageWriteable = false;
          String state = Environment.getExternalStorageState();

          if (Environment.MEDIA_MOUNTED.equals(state))
          {
              // rw access
              mExternalStorageAvailable = mExternalStorageWriteable = true;
          } 
          else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
          {
              // r access
              mExternalStorageAvailable = true;
              mExternalStorageWriteable = false;
          }
          else 
          {
              // no access
              mExternalStorageAvailable = mExternalStorageWriteable = false;
          }
          
          // if we do not have storage warn user with dialog
          if (!mExternalStorageAvailable || !mExternalStorageWriteable)
          {
               Builder dialog = new AlertDialog.Builder(this)
                                        .setTitle(getString(R.string.app_name) + " Error")
                                        .setMessage("External Storage not mounted, are you in disk mode?")
                                        .setPositiveButton("Retry", new OnClickListener()
                                        {
                                             
                                             public void onClick(DialogInterface dialog, int which)
                                             {
                                                  init();
                                             }
                                        })
                                        .setNeutralButton("Exit", new OnClickListener()
                                        {
                                             
                                             public void onClick(DialogInterface dialog, int which)
                                             {
                                                  finish();
                                             }
                                        });
                                        
               dialog.show();
               
               return false;
          }          
          
          return true;
     }
     
     
     private void init()
     {
          if (verifyExternalStorage() && !_init)
          {    
        	  // generate APK path for the native side
              ApplicationInfo appInfo = null;
              PackageManager packMgmr = this.getPackageManager();
              try
              {
                   appInfo = packMgmr.getApplicationInfo(getString(R.string.package_name), 0);
              } 
              catch (NameNotFoundException e)
              {
                   e.printStackTrace();
                   throw new RuntimeException("Unable to locate assets, aborting...");
              }
              String _apkPath = appInfo.sourceDir;                  	  
        	  
        	  // always try and make application dirs
        	  String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        	  
        	  // create storage dir for app
              File myNewFolder = new File(extStorageDirectory + SettingsFacade.DEFAULT_DIR);
              if (!myNewFolder.exists())
              {
                   myNewFolder.mkdir();
              }            	  
        	  
        	  // check if config.xml exists
        	  boolean configExists = (new File(extStorageDirectory + SettingsFacade.DEFAULT_DIR + "/config.xml")).exists();
        	  
        	  // test to verify if we get correct values at this point
               // it appears we do
               int width = SettingsFacade.getRealScreenWidth(this);
               int height = SettingsFacade.getRealScreenHeight(this);
               Log.d(LOG_TAG, "Width: " + width);
               Log.d(LOG_TAG, "Height: " + height);                      
                                         
               // do first run welcome screen
               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());                             
               boolean firstRun = preferences.getBoolean(SettingsFacade.PREF_FIRST_RUN, true);
               
               if (!firstRun && !configExists)
               {
            	   Emulator.init(_apkPath, extStorageDirectory + SettingsFacade.DEFAULT_DIR);
            	   Emulator.resetConfig();
               }
               else if (firstRun)
               {     
            	   //init and reset input
                   Emulator.init(_apkPath, extStorageDirectory + SettingsFacade.DEFAULT_DIR);     
                   Emulator.resetConfig();
            	                       
                   // remove first run flag
                   Editor edit = preferences.edit();
                   edit.putBoolean(SettingsFacade.PREF_FIRST_RUN, false);
                   edit.commit();
                   
                   // start welcome activity
                   Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                   startActivity(intent);               
                   
                   // might as well exit, user probably bringing us down anyway
                   finish();
                   
                   return;
               }
               else if (!firstRun)
               {
                   Emulator.init(_apkPath, extStorageDirectory + SettingsFacade.DEFAULT_DIR);                                                                 	    
               }
               else
               {
            	   // init the emulator
            	   Emulator.init(_apkPath, extStorageDirectory + SettingsFacade.DEFAULT_DIR);
               }
               
               Log.d(LOG_TAG, "Config FILE: " + Emulator.getConfigFileName());
               
               boolean firstRunVersion = preferences.getBoolean(SettingsFacade.PREF_FIRST_RUN_UPDATE, true);
               if (firstRunVersion)
               {
                   Builder dialog = new AlertDialog.Builder(this)
                   .setTitle(getString(R.string.app_name) + " UPDATE INFO")
                   .setMessage(getString(R.string.whatsNew));
                   dialog.show();     
                   
                   Editor edit = preferences.edit();
                   edit.putBoolean(SettingsFacade.PREF_FIRST_RUN_UPDATE, false);
                   edit.commit();
               }
               
               // create emu storage paths                                       
               myNewFolder = new File((ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_ROMS, "value")));
               if (!myNewFolder.exists())
               {
                    myNewFolder.mkdir();
               }
               
               myNewFolder = new File((ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_STATES, "value")));
               if (!myNewFolder.exists())
               {
                    myNewFolder.mkdir();
               }
               
               myNewFolder = new File((ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SAVES, "value")));
               if (!myNewFolder.exists())
               {
                    myNewFolder.mkdir();
               }
               
               myNewFolder = new File((ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SHADERS, "value")));
               if (!myNewFolder.exists())
               {
                    myNewFolder.mkdir();
               }         
               
               myNewFolder = new File((ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_TEMP, "value")));
               if (!myNewFolder.exists())
               {
                    myNewFolder.mkdir();
               }     

               // load gui
               Log.d(LOG_TAG, "Done init()");
               setContentView(R.layout.main);
               initGUIEvent();               
               
               // set title
               super.setTitle(getString(R.string.app_name));                         
               
               _init = true;
               
               Log.d(LOG_TAG, "Done onCreate()");
          }           
     }
             
     
     private void initGUIEvent()
     {
          Button tmpButton = (Button) findViewById(R.id.buttonLoadRom);
          tmpButton.setOnClickListener(
             new View.OnClickListener()
             {
                public void onClick(View v)
                {
                    spawnFileChooser();
                }
             });               

          tmpButton = (Button) findViewById(R.id.buttonResume);
          tmpButton.setOnClickListener(
             new View.OnClickListener()
             {
                public void onClick(View v)
                {
                    if (Emulator.isRomLoaded())
                    {
                         spawnEmulatorActivity();
                    }
                    else
                    {
                         Toast.makeText(getApplicationContext(), "No ROM loaded...", Toast.LENGTH_SHORT);
                    }
                }
             });            
          
          tmpButton = (Button) findViewById(R.id.buttonSettings);
          tmpButton.setOnClickListener(
             new View.OnClickListener()
             {
                public void onClick(View v)
                {
                    spawnSettings();
                }
             });            
          
          tmpButton = (Button) findViewById(R.id.buttonExit);
          tmpButton.setOnClickListener(
             new View.OnClickListener()
             {
                public void onClick(View v)
                {
                    finish();
                }
             });                       
          
          tmpButton = (Button) findViewById(R.id.buttonAbout);
          tmpButton.setOnClickListener(
             new View.OnClickListener()
             {
                public void onClick(View v)
                {
                    Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                }
             });            
     }                   
     

     @Override
     public void onPause()
     {
          Log.d(LOG_TAG, "onPause()");
                    
          super.onPause();
     }
     
     
     @Override
     public void onStop()
     {
          Log.d(LOG_TAG, "onStop()");         
          
          super.onStop();
     }
     
     
     @Override
     public void onDestroy()
     {
          Log.d(LOG_TAG, "onDestroy()");

          super.onDestroy();      
                           
          Emulator.destroy();           
          
          // check for auto save
          SettingsFacade.DoAutoSave();
          
          // clean temp dir or all files, just files for now
          // @TODO - full clean
          File tempFolder = new File((ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_TEMP, "value")));
          if (tempFolder.exists())
          {
               String[] children = tempFolder.list();
               for (int i=0; i<children.length; i++)
               {
                    File tempFile = new File(tempFolder.getAbsoluteFile(), children[i]);
                    if (tempFile.exists())
                    {
                         tempFile.delete();
                    }
               }
          }    
          
          _init = false;
          
          // garbage collect... help android out a bit
          System.gc();
     }

         
     protected void spawnEmulatorActivity()
     {
          Intent myIntent = new Intent(MainActivity.this, EmulatorActivity.class);
          startActivity(myIntent);
     }
     
     
     protected void spawnFileChooser()
     {
    	 String romDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_ROMS, "value");
    	 String tempDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_TEMP, "value");
    	 
          // spawn the file chooser
          Intent myIntent = new Intent(MainActivity.this, FileChooser.class);
          myIntent.putExtra(FileChooser.EXTRA_START_DIR, romDir);
          myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, SettingsFacade.DEFAULT_ROM_EXTENSIONS);
          myIntent.putExtra(FileChooser.EXTRA_TEMP_DIR, tempDir);
          final int result=SettingsFacade.MENU_ROM_SELECT;
          startActivityForResult(myIntent, result);             
     }
     
     
     protected void spawnSettings()
     {
          // spawn the file chooser
          Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
          final int result=SettingsFacade.MENU_SETTINGS;
          startActivityForResult(myIntent, result);             
     }     
     
     
     @Override
     protected void onActivityResult(int requestCode,int resultCode,Intent data)
     {         
          Log.d(LOG_TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");
          
          if (resultCode == RESULT_CANCELED)
          {
               this.finish();
          }
          else if (requestCode == SettingsFacade.MENU_SETTINGS)
          {
               //PreferenceFacade.loadPrefs(this, getApplicationContext());
          }
          else if (requestCode == SettingsFacade.MENU_ROM_SELECT)
          {
               String romFile=data.getStringExtra(FileChooser.PAYLOAD_FILENAME);
               if (romFile != null)
               {                 
                    if (Emulator.loadRom(romFile) == 0)
                    {
                         // AutoLoad
                         SettingsFacade.DoAutoLoad();
                         
                         spawnEmulatorActivity();
                    }
               }
               else
               {
                    Toast.makeText(getApplicationContext(), "No rom selected!", Toast.LENGTH_SHORT).show();
               }
          }
          
          super.onActivityResult(requestCode, resultCode, data);          
     }                      
}