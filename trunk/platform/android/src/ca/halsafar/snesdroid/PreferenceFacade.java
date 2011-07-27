package ca.halsafar.snesdroid;


import ca.halsafar.audio.AudioPlayer;
import ca.halsafar.snesdroid.PreferenceFacade;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class PreferenceFacade
{
     private static final String LOG_TAG = "PreferenceFacade";
     
     // pref defaults
     public static final boolean DEFAULT_AUDIO_SETTING = true;
     public static final boolean DEFAULT_KEEP_NES_ASPECT_RATIO = true;
     public static final String DEFAULT_SOUND_SAMPLE_RATE = "22050";
     public static final int DEFAULT_X_SENSITIVTY = 30;
     public static final int DEFAULT_Y_SENSITIVTY = 35;
     
     public static final int DEFAULT_AUDIO_STRETCH = 8;     
     
     // default dirs
     public static final String DEFAULT_DIR = "/SNESDroid";
     public static final String DEFAULT_DIR_ROMS = DEFAULT_DIR + "/roms";
     public static final String DEFAULT_DIR_STATES = DEFAULT_DIR + "/states";
     public static final String DEFAULT_DIR_SRAM = DEFAULT_DIR + "/sram";
     public static final String DEFAULT_DIR_CHEATS = DEFAULT_DIR + "/cheats";
     public static final String DEFAULT_DIR_SHADERS = DEFAULT_DIR + "/shaders";
     public static final String DEFAULT_DIR_TEMPFILES = DEFAULT_DIR + "/temp";
     public static final String DEFAULT_ROM_EXTENSIONS = "smc|fig|sfc|gd3|gd7|dx2|bsx|swc|zip";
     public static final String DEFAULT_SHADER_EXTENSIONS = "glsl";
     
     public static final String PREF_DIR_ROMS = "prefRomDir";
     public static final String PREF_DIR_STATES = "prefStatesDir";
     public static final String PREF_DIR_SRAM = "prefSramDir";
     public static final String PREF_DIR_CHEATS = "prefCheatsDir";
     public static final String PREF_DIR_SHADERS = "prefCheatsDir";
     public static final String PREF_DIR_TEMP = "prefTempDir";     
     
     // menu keys
     public static final int MENU_ROM_SELECT = 1;
     public static final int MENU_SETTINGS = 2;
     public static final int MENU_INPUT_CONFIG = 3;
     public static final int MENU_SHADER_SELECT = 4;     
     public static final int MENU_CUSTOM_KEYS = 5;
     public static final int MENU_DIR_SELECT = 6;     
     
     // input keys
     public static final String PREF_SHOW_TOUCH_INPUT = "prefShowTouchInput";
     
     public static final String PREF_X_SENSITIVITY = "xAxisSensitivity";
     public static final String PREF_Y_SENSITIVITY = "yAxisSensitivity";          
     
     public static final String PREF_USE_DEFAULT_INPUT = "useDefaultInput";
     
     public static final String PREF_FIRST_SHADER_SELECT = "prefFirstShader";
     
     // graphics settings
     public static final String PREF_MAINTAIN_ASPECT_RATIO = "aspectRatio";
     public static final String PREF_SHADER_FILE = "prefShaderFile";
     public static final String PREF_GRAPHICS_FILTER = "graphicsFilter";

     // audio
     public static final String PREF_ENABLE_AUDIO = "audio";
     public static final String PREF_SOUND_SAMPLE_RATE = "soundSampleRate"; 
     public static final int AUDIO_MIX_SAMPLES_MAX = 2048;
     
     // emulator settings
     public static final String PREF_FRAME_SKIP = "frameSkip";
     public static final String PREF_ENABLE_GAME_GENIE = "useGameGenie";
     public static final String PREF_ENABLE_AUTO_SAVE = "enableAutosave";
     public static final String PREF_ENABLE_REWIND = "enableRewind";
     public static final String PREF_ENABLE_FORWARD = "enableForward";
     public static final String PREF_REWIND_COUNT = "rewindCount";
     public static final String PREF_REWIND_FREQUENCY = "rewindFrequency";
     public static final int PREF_AUTO_SAVE_SLOT = 10;
     
     // application settings
     public static final String PREF_FIRST_RUN = "firstRun1_8";
     
     
     public static String getRomDir(Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          String dir = prefs.getString(PREF_DIR_ROMS, Environment.getExternalStorageDirectory() + DEFAULT_DIR_ROMS);
          
          return dir;
     }
     
     
     public static String getStateDir(final Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          String dir = prefs.getString(PREF_DIR_STATES, Environment.getExternalStorageDirectory() + DEFAULT_DIR_STATES);
          
          return dir;
     }
     
     
     public static String getSramDir(final Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          String dir = prefs.getString(PREF_DIR_SRAM, Environment.getExternalStorageDirectory() + DEFAULT_DIR_SRAM);
          
          return dir;
     }
     
     
     public static String getCheatsDir(final Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          String dir = prefs.getString(PREF_DIR_CHEATS, Environment.getExternalStorageDirectory() + DEFAULT_DIR_CHEATS);
          
          return dir;
     }  
     
     
     public static String getShaderDir(final Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          String dir = prefs.getString(PREF_DIR_SHADERS, Environment.getExternalStorageDirectory() + DEFAULT_DIR_SHADERS);
          
          return dir;
     }        
     
     public static String getTempDir(final Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          String dir = prefs.getString(PREF_DIR_TEMP, Environment.getExternalStorageDirectory() + DEFAULT_DIR_TEMPFILES);
          
          return dir;
     }  
     
     
     public static int getRealScreenHeight(final Activity activity, final Context context)
     {
         Rect rect = new Rect();
         Window window = activity.getWindow();
         window.getDecorView().getWindowVisibleDisplayFrame(rect);
         
         //int screenWidth = Math.max(rect.right, rect.bottom);
         int screenHeight = Math.min(rect.right, rect.bottom);         

         return screenHeight;                
     }
     
     
     public static int getRealScreenWidth(final Activity activity, final Context context)
     {
         Rect rect = new Rect();
         Window window = activity.getWindow();
         window.getDecorView().getWindowVisibleDisplayFrame(rect);
         
         int screenWidth = Math.max(rect.right, rect.bottom);
         //int screenHeight = Math.min(rect.right, rect.bottom);         

         return screenWidth;                
     }     
     
     
     protected static void loadPrefs(final Activity activity, final Context context)
     {
          // load up default prefs
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          boolean useAudio = prefs.getBoolean(PreferenceFacade.PREF_ENABLE_AUDIO, PreferenceFacade.DEFAULT_AUDIO_SETTING);          
          float xAxisSens = prefs.getInt(PreferenceFacade.PREF_X_SENSITIVITY, PreferenceFacade.DEFAULT_X_SENSITIVTY) / 100.0f;
          float yAxisSens = prefs.getInt(PreferenceFacade.PREF_Y_SENSITIVITY, PreferenceFacade.DEFAULT_Y_SENSITIVTY) / 100.0f;
          int sampleRate = Integer.parseInt(prefs.getString(PreferenceFacade.PREF_SOUND_SAMPLE_RATE, PreferenceFacade.DEFAULT_SOUND_SAMPLE_RATE));
          int frameSkip = Integer.parseInt(prefs.getString(PreferenceFacade.PREF_FRAME_SKIP, "1"));
          boolean useGameGenie = prefs.getBoolean(PreferenceFacade.PREF_ENABLE_GAME_GENIE, false);
          boolean enableRewind = prefs.getBoolean(PreferenceFacade.PREF_ENABLE_REWIND, false);
          boolean enableForward = prefs.getBoolean(PreferenceFacade.PREF_ENABLE_FORWARD, false);
          boolean showTouchInput = prefs.getBoolean(PreferenceFacade.PREF_SHOW_TOUCH_INPUT, true);
          float audioStretchPercent = 1.0f + (prefs.getInt("audioStretchPercent", PreferenceFacade.DEFAULT_AUDIO_STRETCH) / 100.0f);
          
          // emulator settings
          Emulator.setFrameSkip(frameSkip);
          Emulator.setGameGenie(useGameGenie);
          
          Emulator.setEnableRewind(enableRewind);          
                    
          // audio
          Emulator.setAudioEnabled(useAudio);
          if (useAudio)
          {
               Emulator.setAudioSampleRate((int)(sampleRate * audioStretchPercent));
               AudioPlayer.create(sampleRate, 16, 2);
               AudioPlayer.resume();
               Emulator.initAudioBuffer(AudioPlayer.getMaxBufferSize());
          }
          else
          {
               Emulator.setAudioSampleRate(0);
               AudioPlayer.destroy();
          }

          Emulator.setSensitivity(xAxisSens, yAxisSens);
          
                   
          // check if we have defaults already
          boolean useDefaultInput = true;
          if (prefs.contains(PreferenceFacade.PREF_USE_DEFAULT_INPUT))
          {
               useDefaultInput = prefs.getBoolean(PreferenceFacade.PREF_USE_DEFAULT_INPUT, true);
          }
          
          if (useDefaultInput)
          {             
               Log.d(LOG_TAG, "Setting default input!");
                             
               EmulatorButtons.resetInput(activity, context);
               
               Editor edit = prefs.edit();               
               edit.putBoolean(PreferenceFacade.PREF_USE_DEFAULT_INPUT, false);
               edit.commit();               
          }          
          
       // load analog
          float padx = InputPreferences.getAnalogX(context, 0);
          float pady = InputPreferences.getAnalogY(context, 0);
          float padw = InputPreferences.getAnalogWidth(context, 0);
          float padh = InputPreferences.getAnalogHeight(context, 0);
          int padcodel = InputPreferences.getAnalogLeftCode(context, 0);
          int padcodeu = InputPreferences.getAnalogUpCode(context, 0);
          int padcoder = InputPreferences.getAnalogRightCode(context, 0);
          int padcoded = InputPreferences.getAnalogDownCode(context, 0);
          //int padmap = InputPreferences.getButtonMap(context, 0);          
          
          Emulator.setAnalog(padx, pady, padw, padh,
                    padcodel, padcodeu, padcoder, padcoded,
                    showTouchInput);
          
          // load all buttons
          int numButtons = InputPreferences.getNumButtons(context);
          for (int i = 0; i < numButtons; i++)
          {
               float x = InputPreferences.getButtonX(context, i);
               float y = InputPreferences.getButtonY(context, i);
               float w = InputPreferences.getButtonWidth(context, i);
               float h = InputPreferences.getButtonHeight(context, i);
               int code = InputPreferences.getButtonCode(context, i);
               int map = InputPreferences.getButtonMap(context, i);
           
               if (i == EmulatorButtons.BUTTON_INDEX_FORWARD.ordinal())
               {
                    Emulator.setButton(map, x, y, w, h, code, showTouchInput && enableForward);
               }
               else if (i == EmulatorButtons.BUTTON_INDEX_REWIND.ordinal())
               {
                    Emulator.setButton(map, x, y, w, h, code, showTouchInput && enableRewind);
               }
               else
               {
                    Emulator.setButton(map, x, y, w, h, code, showTouchInput);    
               }
          }
          
          // set directories
          String romDir = getRomDir(context);
          String stateDir = getStateDir(context);
          String sramDir = getSramDir(context);
          String cheatsDir = getCheatsDir(context);
          String shadersDir = getShaderDir(context);
          String tempDir = getTempDir(context);
          
          Log.d(LOG_TAG, "Directories: \n\t" +   
                  "Roms: " + romDir + "\n\t" + 
                  "States: " + stateDir + "\n\t" + 
                  "SRAM: " + sramDir + "\n\t" + 
                  "Cheats: " + cheatsDir + "\n\t" +
                  "Shaders: " + shadersDir + "\n\t" + 
                  "Temp: " + tempDir);
     }

     
     /**
      * Do not call without proper opengl context thread
      * ie. Only from GLSurfaceView pipe
      */
     public static void loadGraphicsPrefs(Context context)
     {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          boolean keepAspect = prefs.getBoolean(PreferenceFacade.PREF_MAINTAIN_ASPECT_RATIO, PreferenceFacade.DEFAULT_KEEP_NES_ASPECT_RATIO);
          
          // aspect ratio
          if (keepAspect)
          {
               Emulator.setAspectRatio(4.0f/3.0f);
          }
          else
          {
               WindowManager mWinMgr = (WindowManager)context.getSystemService(Activity.WINDOW_SERVICE);
               float screenWidth = mWinMgr.getDefaultDisplay().getWidth();
               float screenHeight = mWinMgr.getDefaultDisplay().getHeight();
               
               Emulator.setAspectRatio(screenWidth / screenHeight);
          }
          
          
          // linear or point filtering
          boolean smooth = Boolean.parseBoolean(prefs.getString(PreferenceFacade.PREF_GRAPHICS_FILTER, "true"));
          Emulator.setSmoothFiltering(smooth);
          
          // shader
          String shaderFileName = prefs.getString(PreferenceFacade.PREF_SHADER_FILE, null);
          if (shaderFileName != null)
          {               
               Log.d(LOG_TAG, "Shader: " + shaderFileName);
               Emulator.setShaderFile(shaderFileName);
          }
          else
          {
               Log.d(LOG_TAG, "NULL SHADER");
               Emulator.setShaderFile(null);
          }          
          
          Log.d(LOG_TAG, "keepAspect: " + keepAspect + "\n" +
                         "shaderFile: " + shaderFileName + "\n");
     }      

     
     public static void DoAutoSave(Context context)
     {
          // check for auto save
          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);                             
          boolean autoSave = preferences.getBoolean(PreferenceFacade.PREF_ENABLE_AUTO_SAVE, false);                    
          if (autoSave)
          {
               Emulator.saveState(PreferenceFacade.PREF_AUTO_SAVE_SLOT);
          }
     }

     
     public static void DoAutoLoad(Context context)
     {
          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
          boolean autoSave = preferences.getBoolean(PreferenceFacade.PREF_ENABLE_AUTO_SAVE, false);
          boolean gameGenie = preferences.getBoolean(PreferenceFacade.PREF_ENABLE_GAME_GENIE, false);
          
          if (autoSave && !gameGenie)
          {
               Emulator.loadState(PreferenceFacade.PREF_AUTO_SAVE_SLOT);
          }
     }     
}
