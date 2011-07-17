package ca.halsafar.snesdroid;


import ca.halsafar.audio.AudioPlayer;
import ca.halsafar.snesdroid.PreferenceFacade;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
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
     
     public static final float DEFAULT_AUDIO_STRETCH = 1.08f;     
     
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
     
     
     protected static void loadPrefs(Context context)
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
          int rewindCount = prefs.getInt(PreferenceFacade.PREF_REWIND_COUNT, 4);
          int rewindFrequency = prefs.getInt(PreferenceFacade.PREF_REWIND_FREQUENCY, 256);
          boolean showTouchInput = prefs.getBoolean(PreferenceFacade.PREF_SHOW_TOUCH_INPUT, true);

          // emulator settings
          Emulator.setFrameSkip(frameSkip);
          Emulator.setGameGenie(useGameGenie);
          
          Emulator.setEnableRewind(enableRewind, rewindCount, rewindFrequency);          
                    
          // audio
          Emulator.setAudioEnabled(useAudio);
          if (useAudio)
          {
               Emulator.setAudioSampleRate((int)(sampleRate * PreferenceFacade.DEFAULT_AUDIO_STRETCH));
               AudioPlayer.create(sampleRate, 16, 2);
               AudioPlayer.resume();
               Emulator.initAudioBuffer(AudioPlayer.getMinBufferSize());
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
                             
               PreferenceFacade.resetInput(context);
               
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
          int padmap = InputPreferences.getButtonMap(context, 0);          
          
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
     
     public static void resetInput(Context context)
     {          
          // calculate screen dimensions, max then out, @HACK - breaks if portrait
          WindowManager mWinMgr = (WindowManager)context.getSystemService(Activity.WINDOW_SERVICE);
          float tmpScreenWidth = mWinMgr.getDefaultDisplay().getWidth();
          float tmpScreenHeight = mWinMgr.getDefaultDisplay().getHeight();
          float screenWidth = Math.max(tmpScreenWidth, tmpScreenHeight);
          float screenHeight = Math.min(tmpScreenWidth, tmpScreenHeight);
          
          Log.d(LOG_TAG, "resetInput(" + screenWidth + ", " + screenHeight + ")");          
          
          float maxSize = (screenWidth > screenHeight ? screenWidth : screenHeight);

          float dpadWidth = maxSize * 0.20f;
          float dpadHeight = dpadWidth;

          float dPadX = 0 + maxSize * 0.01f;
          float dPadY = screenHeight - dpadHeight - (maxSize * 0.01f);

          float buttonsWidth = maxSize * 0.085f;
          float buttonsHeight = maxSize * 0.085f;
          float buttonSep = maxSize * 0.010f;
          
          float yButtonX = screenWidth - ((buttonsWidth * 3) + buttonSep + (maxSize *0.015f));
          float yButtonY = screenHeight - (buttonsHeight * 2 + (maxSize * 0.01f));
          
          float aButtonX = yButtonX + (buttonsWidth * 2) + buttonSep + (maxSize *0.015f);
          float aButtonY = yButtonY;

          float bButtonX = screenWidth - (buttonsWidth * 2) - (maxSize * 0.015f);
          float bButtonY = yButtonY + (buttonsHeight);
          
          float xButtonX = bButtonX;
          float xButtonY = yButtonY - ((buttonsHeight) - (maxSize * 0.015f));
          
          float lButtonX = yButtonX;
          float lButtonY = yButtonY - (buttonsHeight + ((maxSize *0.015f)));
          
          float rButtonX = aButtonX;
          float rButtonY = aButtonY - (buttonsHeight + ((maxSize *0.015f)));;

          float startButtonWidth = maxSize * 0.075f;
          float startButtonHeight = maxSize * 0.04f;

          float startButtonX = (screenWidth / 2);
          float startButtonY = screenHeight - startButtonHeight - (maxSize * 0.01f);

          float selectButtonX = (screenWidth / 2) - (startButtonWidth);
          float selectButtonY = startButtonY;         
          
          float ffButtonWidth = maxSize * 0.05f;
          float ffButtonHeight = maxSize * 0.025f;
          float ffButtonX = screenWidth - (ffButtonWidth*2 + (maxSize * 0.015f));
          float ffButtonY = 0 + ffButtonHeight + (maxSize * 0.015f);
          
          float rrButtonWidth = maxSize * 0.05f;
          float rrButtonHeight = maxSize * 0.025f;
          float rrButtonX = 0 + rrButtonWidth + (maxSize * 0.015f);
          float rrButtonY = 0 + rrButtonHeight + (maxSize * 0.015f);          
          
       // default textures
          String aTex = "Textures/ButtonA.png";
          String bTex = "Textures/ButtonB.png";
          String xTex = "Textures/ButtonX.png";
          String yTex = "Textures/ButtonY.png";
          String lTex = "Textures/ButtonL.png";
          String rTex = "Textures/ButtonR.png";
          String startTex = "Textures/StartButton.png";
          String selectTex = "Textures/SelectButton.png";
          String rewindTex = "Textures/Rewind.png";
          String forwardTex = "Textures/FastForward.png";
          String dPadTex = "Textures/DirectionalPad.png";
          
          
          // default button codes - using Xperia Play
          int aCode = 23; // X
          int bCode = 99; // Square
          int xCode = 100; // Triangle
          int yCode = 100; // Triangle
          int lCode = 100; // Triangle
          int rCode = 100; // Triangle
          int startCode = 108; // Start
          int selectCode = 109; // Select
          int rewindCode = 102; // L1
          int forwardCode = 103; // R1
          int leftCode = 21;
          int upCode = 19;
          int rightCode = 22;
          int downCode = 20;
          
          InputPreferences.clearButtons(context);
          
          InputPreferences.setButton(context, 
                  xButtonX, xButtonY, buttonsWidth, buttonsHeight, 
                  xCode, xTex, EmulatorButtons.BUTTON_INDEX_X.ordinal());   
          
          InputPreferences.setButton(context, 
                  yButtonX, yButtonY, buttonsWidth, buttonsHeight, 
                  yCode, yTex, EmulatorButtons.BUTTON_INDEX_Y.ordinal()); 
          
          InputPreferences.setButton(context, 
                  lButtonX, lButtonY, buttonsWidth, buttonsHeight, 
                  lCode, lTex, EmulatorButtons.BUTTON_INDEX_L.ordinal());   
          
          InputPreferences.setButton(context, 
                  rButtonX, rButtonY, buttonsWidth, buttonsHeight, 
                  rCode, rTex, EmulatorButtons.BUTTON_INDEX_R.ordinal());                
          
          InputPreferences.setButton(context, 
                    bButtonX, bButtonY, buttonsWidth, buttonsHeight, 
                    bCode, bTex, EmulatorButtons.BUTTON_INDEX_B.ordinal());
          
          InputPreferences.setButton(context, 
                    aButtonX, aButtonY, buttonsWidth, buttonsHeight, 
                    aCode, aTex, EmulatorButtons.BUTTON_INDEX_A.ordinal());
          
          InputPreferences.setButton(context, 
                    startButtonX, startButtonY, startButtonWidth, startButtonHeight, 
                    startCode, startTex, EmulatorButtons.BUTTON_INDEX_START.ordinal());
          
          InputPreferences.setButton(context, 
                    selectButtonX, selectButtonY, startButtonWidth, startButtonHeight, 
                    selectCode, selectTex, EmulatorButtons.BUTTON_INDEX_SELECT.ordinal());
          
          InputPreferences.setButton(context, 
                    ffButtonX, ffButtonY, ffButtonWidth, ffButtonHeight, 
                    forwardCode, forwardTex, EmulatorButtons.BUTTON_INDEX_FORWARD.ordinal());
          
          InputPreferences.setButton(context, 
                    rrButtonX, rrButtonY, rrButtonWidth, rrButtonHeight, 
                    rewindCode, rewindTex, EmulatorButtons.BUTTON_INDEX_REWIND.ordinal());  
          
          // set the pseudo buttons for movements
          InputPreferences.setButton(context, 
                    0, 0, 0, 0, 
                    leftCode, null, EmulatorButtons.BUTTON_INDEX_LEFT.ordinal());          

          InputPreferences.setButton(context, 
                    0, 0, 0, 0, 
                    upCode, null, EmulatorButtons.BUTTON_INDEX_UP.ordinal());
          
          InputPreferences.setButton(context, 
                    0, 0, 0, 0, 
                    rightCode, null, EmulatorButtons.BUTTON_INDEX_RIGHT.ordinal());
          
          InputPreferences.setButton(context, 
                    0, 0, 0, 0, 
                    downCode, null, EmulatorButtons.BUTTON_INDEX_DOWN.ordinal());          
          
          InputPreferences.setAnalog(context, 
                    dPadX, dPadY, dpadWidth, dpadHeight, 
                    leftCode, upCode, rightCode, downCode,
                    dPadTex, 
                    0);                  
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
