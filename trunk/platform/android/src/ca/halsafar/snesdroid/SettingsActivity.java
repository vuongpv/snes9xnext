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

import ca.halsafar.downloader.Decompress;
import ca.halsafar.downloader.DecompressListener;
import ca.halsafar.downloader.DownloadFile;
import ca.halsafar.downloader.DownloadListener;
import ca.halsafar.filechooser.FileChooser;
import ca.halsafar.widgets.SeekBarAdvancedDrawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * Settings activity. Allows easy access to any/all desired settings
 * and manages to auto save them
 * 
 * @author halsafar
 * 
 */
public class SettingsActivity extends Activity implements DownloadListener, DecompressListener
{
     private static final String LOG_TAG         = "SettingsActivity";

     private ProgressDialog      _progressDialog = null;

     private String _dirKey = "";

     @Override
     protected void onCreate(Bundle savedInstanceState)
     {
          Log.d(LOG_TAG, "onCreate()");

          super.onCreate(savedInstanceState);

          setContentView(R.layout.settings_two);       
          
          // open emu cloud
          /*customPref = (Preference) findPreference("openEmuCloud");
          customPref.setSummary("Sync your save games");
          customPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
          {
               public boolean onPreferenceClick(Preference preference)
              {
                    // spawn the file chooser
                    Intent myIntent = new Intent(SettingsActivity.this,
                              EmuCloudActivity.class);
                    
                    final int result = PreferenceFacade.MENU_EMUCLOUD;
                    startActivityForResult(myIntent, result);
                    
                    return true;
              }
          });   */       
     }
     
     
     @Override
     public void onStart()
     {
          super.onStart();
          
       // set framework option
          Spinner frameSkipSpinner = (Spinner) findViewById(R.id.spinnerFrameSkip);
          int frameSkip = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_FRAME_SKIP, "value"));
          frameSkipSpinner.setSelection(frameSkip);
          frameSkipSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
          {
			@Override
			public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_FRAME_SKIP, "value", parent.getItemAtPosition(pos).toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}        	  
          });
                    
          // auto save enabled
          final CheckBox autoSaveCheck = (CheckBox)findViewById(R.id.checkAutoSave);
          boolean autoSave = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_AUTO_SAVE, "value")) != 0;
          autoSaveCheck.setChecked(autoSave);
          autoSaveCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_AUTO_SAVE, "value", Integer.toString((isChecked ? 1 : 0)));
			}
          });    
          
          // game genie enabled
          final CheckBox gameGenieCheck = (CheckBox)findViewById(R.id.checkGameGenie);
          boolean gameGenie = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_GAME_GENIE, "value")) != 0;
          gameGenieCheck.setChecked(gameGenie);
          gameGenieCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_GAME_GENIE, "value", Integer.toString((isChecked ? 1 : 0)));
				
				if (isChecked)
				{
					// check for gg rom
                    
                    /*String romFile = Environment.getExternalStorageDirectory() + PreferenceFacade.DEFAULT_DIR + "/gg.rom";
                    File f = new File(romFile);
                    if (!f.exists())
                    {
                         Builder dialog = new AlertDialog.Builder(SettingsActivity.this)
                         .setTitle("Game Genie Not Found")
                         .setMessage("Game Genie not found, place game genie image at: " + romFile)
                         .setPositiveButton("Ok", null);
                         
                         dialog.show();   
                         
                         ((CheckBoxPreference)preference).setChecked(false);
                    }					
                    */
				}
				
				if (isChecked && autoSaveCheck.isChecked())
				{
					Toast.makeText(getApplicationContext(), "Cannot auto load with game genie enabled, will still auto save." , Toast.LENGTH_LONG).show();
				}
			}
          });          
          
          // fast forward enabled
          final CheckBox forwardCheck = (CheckBox)findViewById(R.id.checkForward);
          boolean forward = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_FAST_FORWARD, "value")) != 0;
          forwardCheck.setChecked(forward);
          forwardCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_FAST_FORWARD, "value", Integer.toString((isChecked ? 1 : 0)));
			}
          });
          
          // fast forward enabled
          final CheckBox rewindCheck = (CheckBox)findViewById(R.id.checkRewind);
          boolean rewind = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_REWIND, "value")) != 0;
          rewindCheck.setChecked(rewind);
          rewindCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_REWIND, "value", Integer.toString((isChecked ? 1 : 0)));
			}
          });            
          
          // audio enabled
          final CheckBox audioEnableCheck = (CheckBox)findViewById(R.id.checkAudio);
          boolean audioEnabled = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_AUDIO_ENABLED, "value")) != 0;
          audioEnableCheck.setChecked(audioEnabled);
          audioEnableCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_AUDIO_ENABLED, "value", Integer.toString((isChecked ? 1 : 0)));
			}
          });
          
          // audio samplerate
          final Spinner sampleRateSpinner = (Spinner)findViewById(R.id.spinnerSampleRate);
          int sampleRate = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_SAMPLE_RATE, "value"));
          sampleRateSpinner.setSelection(findSpinnerIndexByValue(sampleRateSpinner, Integer.toString(sampleRate)));
          sampleRateSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
          {
  			@Override
  			public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
  				ConfigXML.setNodeAttribute(ConfigXML.PREF_SAMPLE_RATE, "value", parent.getItemAtPosition(pos).toString());
  			}

  			@Override
  			public void onNothingSelected(AdapterView<?> arg0) {}        	  
          });
                            
          // audio stretch
          final SeekBar sampleStretchSeek = (SeekBar)findViewById(R.id.seekAudioStretch);
          int audioStretch = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_SAMPLE_STRETCH, "value"));
          setupSeekBar(sampleStretchSeek, audioStretch, 1000, "%");
          sampleStretchSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_SAMPLE_STRETCH, "value", Integer.toString(seekBar.getProgress()));
			}
          });
          
          // graphics orientation
          final Spinner orientationSpinner = (Spinner)findViewById(R.id.spinnerOrientation);
          int orientation = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_ORIENTATION, "value"));
          orientationSpinner.setSelection(orientation);
          orientationSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
          {
  			@Override
  			public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
  				ConfigXML.setNodeAttribute(ConfigXML.PREF_ORIENTATION, "value", Integer.toString(pos));
  			}

  			@Override
  			public void onNothingSelected(AdapterView<?> arg0) {}        	  
          });          
          
          // graphics aspect ratio
          final CheckBox aspectRatioCheck = (CheckBox)findViewById(R.id.checkAspectRatio);
          boolean aspectRatio = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_MAINTAIN_ASPECT, "value")) != 0;
          aspectRatioCheck.setChecked(aspectRatio);
          aspectRatioCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_MAINTAIN_ASPECT, "value", Integer.toString((isChecked ? 1 : 0)));
			}
          });  
          
          // hardware filter
          final Spinner hardwareFilterSpinner = (Spinner)findViewById(R.id.spinnerHardwareFilter);
          int hardwareFilter = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_GRAPHICS_FILTER, "value"));
          hardwareFilterSpinner.setSelection(hardwareFilter);          
          hardwareFilterSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
          {
  			@Override
  			public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
  				ConfigXML.setNodeAttribute(ConfigXML.PREF_GRAPHICS_FILTER, "value", Integer.toString(pos));
  			}

  			@Override
  			public void onNothingSelected(AdapterView<?> arg0) {}        	  
          });      
          
          // shader select button
          final String shaderFile = ConfigXML.getNodeAttribute(ConfigXML.PREF_SHADER_FILE, "value");
          final String shaderDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SHADERS, "value");
          final Button selectShaderButton = (Button)findViewById(R.id.buttonSelectShader);
          selectShaderButton.setText("Select Shader (Current: " + shaderFile +")");
          selectShaderButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// spawn the file chooser
				
                Intent myIntent = new Intent(SettingsActivity.this, FileChooser.class);
                myIntent.putExtra(FileChooser.EXTRA_START_DIR, shaderDir);
                myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, SettingsFacade.DEFAULT_SHADER_EXTENSIONS);
                final int result = SettingsFacade.MENU_SHADER_SELECT;
                startActivityForResult(myIntent, result);				
			}
          });
          
          // reset shader button
          final Button resetShaderButton = (Button)findViewById(R.id.buttonResetShader);
          resetShaderButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_SHADER_FILE, "value", "");		
			}
          });          
          
          // download shader button
          final Button downloadShaderButton = (Button)findViewById(R.id.buttonDownloadShader);
          downloadShaderButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
                _progressDialog = new ProgressDialog(SettingsActivity.this);
                _progressDialog.setMessage("Downloading Shaders...");
                _progressDialog.setIndeterminate(false);
                _progressDialog.setMax(100);
                _progressDialog
                          .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                String outFile = shaderDir + "/shaders2.zip";
                DownloadFile downloadFile = new DownloadFile(outFile, getApplicationContext(), _progressDialog, SettingsActivity.this);
                downloadFile.execute(getString(R.string.shaderURL));
                _progressDialog.show();			
			}
          });  
          
          // config touch input button
          final Button configTouchButton = (Button)findViewById(R.id.buttonConfigTouchInput);
          configTouchButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				try
                {
                    Intent intent = new Intent(SettingsActivity.this,InputConfigActivity.class);
                    final int result = SettingsFacade.MENU_INPUT_CONFIG;
                    startActivityForResult(intent, result);
                }
                catch (Exception e)
                {
                     e.printStackTrace();
                }		
			}
          });     
          
          // config key gamepad input button
          final Button configKeyButton = (Button)findViewById(R.id.buttonConfigKeyInput);
          configKeyButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
                try
                {
                    Intent intent = new Intent(SettingsActivity.this,
                              KeyboardConfigActivity.class);
                    final int result = SettingsFacade.MENU_CUSTOM_KEYS;
                    startActivityForResult(intent, result);
                }
                catch (Exception e)
                {
                     e.printStackTrace();
                }	
			}
          });
          
          // button reset input
          final Button resetInputButton = (Button)findViewById(R.id.buttonResetInput);
          resetInputButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				ConfigXML.writeConfigXML();
                Emulator.resetInputConfig();
                ConfigXML.openConfigXML();
			}
          });   
          
          // showTouch Check
          final CheckBox showTouchCheck = (CheckBox)findViewById(R.id.checkShowTouchInput);
          boolean showTouch = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_INPUT, "showTouch")) != 0;
          showTouchCheck.setChecked(showTouch);
          showTouchCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_INPUT, "showTouch", Integer.toString((isChecked ? 1 : 0)));
			}
          });            

          // set button transparency
          final SeekBar seekTransparency = (SeekBar) findViewById(R.id.seekButtonTransparency);
          float buttonAlpha = Float.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_INPUT, "alpha"));
          setupSeekBar(seekTransparency, (int)(buttonAlpha*100), 100, "%");
          seekTransparency.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_INPUT, "alpha", Float.toString(seekBar.getProgress()/100.0f));
			}
          });          
          
          // set x/y sensitivity seek bar
          final SeekBar xSensStretchSeek = (SeekBar) findViewById(R.id.seekXSensitivity);
          float xSensitivity = Float.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_TOUCH_ANALOGS+"[@id='0']", "xSensitivity"));
          setupSeekBar(xSensStretchSeek, (int)(xSensitivity*100), 100, "%");
          xSensStretchSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_TOUCH_ANALOGS+"[@id='0']", "xSensitivity", Float.toString(seekBar.getProgress()/100.0f));
			}
          });
          
          final SeekBar ySensStretchSeek = (SeekBar) findViewById(R.id.seekYSensitivity);          
          float ySensitivity = Float.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_TOUCH_ANALOGS+"[@id='0']", "ySensitivity"));
          setupSeekBar(ySensStretchSeek, (int)(ySensitivity*100), 100, "%");
          ySensStretchSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ConfigXML.setNodeAttribute(ConfigXML.PREF_TOUCH_ANALOGS+"[@id='0']", "ySensitivity", Float.toString(seekBar.getProgress()/100.0f));
			}
          });      
          
          // directories
          final String romDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_ROMS, "value");
          final String stateDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_STATES, "value");
          final String savesDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SAVES, "value");
          
          // rom dir
          final Button romDirButton = (Button)findViewById(R.id.buttonRomDir);
          romDirButton.setText("ROM DIR (" + romDir + ")");
          romDirButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
                // spawn the file chooser
                Intent myIntent = new Intent(SettingsActivity.this, FileChooser.class);
                myIntent.putExtra(FileChooser.EXTRA_SELECT_DIR, "true");
                myIntent.putExtra(FileChooser.EXTRA_START_DIR, romDir);
                myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, "");
                final int result = SettingsFacade.MENU_DIR_SELECT;
                startActivityForResult(myIntent, result);

                _dirKey = ConfigXML.PREF_DIR_ROMS;                
			}
          });  
          
          // states dir
          final Button stateDirButton = (Button)findViewById(R.id.buttonStatesDir);
          stateDirButton.setText("STATES DIR (" + stateDir + ")");
          stateDirButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
                // spawn the file chooser
                Intent myIntent = new Intent(SettingsActivity.this, FileChooser.class);
                myIntent.putExtra(FileChooser.EXTRA_SELECT_DIR, "true");
                myIntent.putExtra(FileChooser.EXTRA_START_DIR, stateDir);
                myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, "");
                final int result = SettingsFacade.MENU_DIR_SELECT;
                startActivityForResult(myIntent, result);

                _dirKey = ConfigXML.PREF_DIR_STATES;    
			}
          });  
          
          // saves dir
          final Button savesDirButton = (Button)findViewById(R.id.buttonSavesDir);
          savesDirButton.setText("SAVES DIR (" + savesDir + ")");
          savesDirButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
                // spawn the file chooser
                Intent myIntent = new Intent(SettingsActivity.this, FileChooser.class);
                myIntent.putExtra(FileChooser.EXTRA_SELECT_DIR, "true");
                myIntent.putExtra(FileChooser.EXTRA_START_DIR, savesDir);
                myIntent.putExtra(FileChooser.EXTRA_EXTENSIONS, "");
                final int result = SettingsFacade.MENU_DIR_SELECT;
                startActivityForResult(myIntent, result);

                _dirKey = ConfigXML.PREF_DIR_SAVES;   
			}
          });            
     }


     @Override
     protected void onActivityResult(int requestCode, int resultCode,
               Intent data)
     {
          Log.d(LOG_TAG, "onActivityResult()");

          if (requestCode == SettingsFacade.MENU_INPUT_CONFIG)
          {
               Log.d(LOG_TAG, "onActivityResult(INPUT_CONFIG)");
          }
          else if (requestCode == SettingsFacade.MENU_DIR_SELECT)
          {
               Log.d(LOG_TAG, "onActivityResult(DIR_SELECT)");
               
               String directory = data.getStringExtra(FileChooser.PAYLOAD_SELECTED_DIR);
               if (directory != null)
               {
                    Log.d(LOG_TAG, "NewDir: " + directory + " @ Key: " + _dirKey);
                    ConfigXML.setNodeAttribute(_dirKey, "value", directory);
               }               
          }
          else if (requestCode == SettingsFacade.MENU_SHADER_SELECT)
          {
               String shaderFile = data.getStringExtra(FileChooser.PAYLOAD_FILENAME);

               Log.d(LOG_TAG, "onActivityResult(SHADER_SELECT: " + shaderFile + ")");
               
               if (shaderFile != null)
               {
            	   String shaderFileName = shaderFile.substring(0, shaderFile.lastIndexOf('.'));
            	   ConfigXML.setNodeAttribute(ConfigXML.PREF_SHADER_FILE, "value", shaderFileName);
               }
          }
          
          ConfigXML.writeConfigXML();
     }
     
     
     @Override
     public void onPause()
     {
    	 Log.d(LOG_TAG, "onPause()");
    	 
    	 ConfigXML.writeConfigXML();
    	 
    	 super.onPause();
     }     
     
     
     @Override
     public void onStop()
     {
    	 Log.d(LOG_TAG, "onStop()");
    	 
    	 ConfigXML.writeConfigXML();
    	 
    	 super.onStop();
     }


     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event)
     {
          Log.d(LOG_TAG, "onKeyDown(" + keyCode + ")");
          if (keyCode == KeyEvent.KEYCODE_BACK)
          {
               Intent intent = new Intent();
               setResult(RESULT_OK, intent);

               finish();

               return true;
          }

          return super.onKeyDown(keyCode, event);
     }



     public void onDownloadAlreadyUpdated(String filename)
     {
          _progressDialog.setProgress(_progressDialog.getMax());
          
          String shaderDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SHADERS, "value");
          Decompress unzip = new Decompress(filename, shaderDir, _progressDialog, this);
          unzip.execute();   
          
          // mark up to date
          //Preference customPref = (Preference) findPreference("downloadShaderPref");
          //customPref.setSummary("Already up to date!");
     }


     public void onDownloadSuccess(String filename, Long sizeBytes)
     {
    	 String shaderDir = ConfigXML.getNodeAttribute(ConfigXML.PREF_DIR_SHADERS, "value");
          Decompress unzip = new Decompress(filename, shaderDir, _progressDialog, this);
          unzip.execute();
          
          //Preference customPref = (Preference) findPreference("downloadShaderPref");
          //customPref.setSummary("Updated Successfully!");
     }


     public void onDownloadFail(String filename)
     {
          Builder dialog = new AlertDialog.Builder(SettingsActivity.this)
          .setTitle("Download Shader Error")
          .setMessage("There was an error trying to download shaders, check your internet connection!")
          .setPositiveButton("Ok", null);
          
          dialog.show();
          
          //Preference customPref = (Preference) findPreference("downloadShaderPref");
          //customPref.setSummary("Download failed...");          
     }


     public void onDecompressSuccess(String filename, Integer numFiles)
     {
          // TODO Auto-generated method stub
          
     }


     public void onDecompressFail(String filename)
     {
          //Preference customPref = (Preference) findPreference("downloadShaderPref");
          //customPref.setSummary("Cannot decompress shaders...!");
     }

     
     private void setupSeekBar(final SeekBar seekBar, final int startValue, final int divisor, final String unit)
     {
    	 Log.d(LOG_TAG, "setupSeekBar(" + seekBar + ", " + startValue + ", " + divisor + ", " + unit + ")");
    	 Rect bounds = seekBar.getProgressDrawable().getBounds();
    	 Resources res = getResources();
    	 final Drawable d = new SeekBarAdvancedDrawable(res, startValue, seekBar.getMax(), divisor, unit);
    	 seekBar.setProgressDrawable(d);
    	 seekBar.getProgressDrawable().setBounds(bounds);
    	 
    	 this.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				seekBar.setProgress(startValue);
				d.setLevel(startValue * divisor);
			}
		});    	
     }     
     
     private int findSpinnerIndexByValue(final Spinner spinner, String value)
     {
    	 for (int i = 0; i < spinner.getCount(); i++)
    	 {
    		 if (spinner.getItemAtPosition(i).equals(value))
    		 {
    			 return i;
    		 }
    	 }
    	 
    	 return -1;
     }
}
