/**
 * NESDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */

package ca.halsafar.snesdroid;

import ca.halsafar.audio.AudioPlayer;
import ca.halsafar.filechooser.FileChooser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
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
          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.emulator, myMenu);

          return true;
     }


     @Override
     public void onResume()
     {
          super.onResume();

          Log.d(LOG_TAG, "onResume()");

          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
          boolean audio = prefs.getBoolean(PreferenceFacade.PREF_ENABLE_AUDIO, true);
          if (audio)
          {
               AudioPlayer.resume();
          }

          _view.onResume();

          // help android out a bit... seems to be slow at releasing sometimes
          System.gc();
     }


     @Override
     public void onPause()
     {
          Log.d(LOG_TAG, "onPause()");

          super.onPause();

          AudioPlayer.pause();

          _view.onPause();

          PreferenceFacade.DoAutoSave(getApplicationContext());
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
     public boolean onKeyUp(int keyCode, KeyEvent event)
     {
          if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                    keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                    keyCode == KeyEvent.KEYCODE_MENU)
               return super.onKeyUp(keyCode, event);

          Emulator.onKeyUp(event.getKeyCode());

          return true;
          // return super.onKeyUp(keyCode, event);
     }


     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event)
     {
          if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                    keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                    keyCode == KeyEvent.KEYCODE_MENU)
               return super.onKeyUp(keyCode, event);

          Emulator.onKeyDown(event.getKeyCode());

          return true;
     }


     @Override
     public boolean onOptionsItemSelected(MenuItem item)
     {
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
                    Intent myIntent = new Intent(EmulatorActivity.this,
                              FileChooser.class);
                    myIntent.putExtra(FileChooser.EXTRA_START_DIR,
                                      PreferenceFacade.getShaderDir(getApplicationContext()));
                    myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS,
                              PreferenceFacade.DEFAULT_SHADER_EXTENSIONS);
                    final int result = PreferenceFacade.MENU_SHADER_SELECT;
                    startActivityForResult(myIntent, result);
                    return true;
               case R.id.menuResetShader:
                    _view.queueEvent(new Runnable()
                    {

                         public void run()
                         {
                              SharedPreferences prefs = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                              Editor edit = prefs.edit();
                              edit.putString(PreferenceFacade.PREF_SHADER_FILE, null);
                              edit.commit();

                              Emulator.setShaderFile(null);
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
          // spawn the file chooser
          Context context = getApplicationContext();
          Intent myIntent = new Intent(EmulatorActivity.this, FileChooser.class);
          myIntent.putExtra(FileChooser.EXTRA_START_DIR, PreferenceFacade.getRomDir(context));
          myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, PreferenceFacade.DEFAULT_ROM_EXTENSIONS);
          myIntent.putExtra(FileChooser.EXTRA_TEMP_DIR, PreferenceFacade.getTempDir(context));
          final int result = PreferenceFacade.MENU_ROM_SELECT;
          startActivityForResult(myIntent, result);
     }


     protected void spawnSettings()
     {
          // spawn the file chooser
          Intent myIntent = new Intent(EmulatorActivity.this, SettingsActivity.class);
          final int result = PreferenceFacade.MENU_SETTINGS;
          startActivityForResult(myIntent, result);
     }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data)
     {
          Log.d(LOG_TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");

          if (requestCode == PreferenceFacade.MENU_SETTINGS)
          {
               _view.queueEvent(new Runnable()
               {
                    public void run()
                    {
                         PreferenceFacade.loadPrefs(getApplicationContext());
                    }
               });
          }
          else if (requestCode == PreferenceFacade.MENU_ROM_SELECT)
          {
               String romFile = data.getStringExtra("Filename");
               if (romFile != null)
               {
                    if (Emulator.loadRom(romFile) != 0)
                    {
                         finish();
                    }

                    PreferenceFacade.DoAutoLoad(getApplicationContext());
               }
          }
          else if (requestCode == PreferenceFacade.MENU_SHADER_SELECT)
          {
               String shaderFile = data
                         .getStringExtra(FileChooser.PAYLOAD_FILENAME);

               Log.d(LOG_TAG, "Shader Selected: " + shaderFile);

               SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
               Editor edit = prefs.edit();
               edit.putString(PreferenceFacade.PREF_SHADER_FILE, shaderFile);
               edit.commit();

               Emulator.setShaderFile(shaderFile);
          }
     }

}
