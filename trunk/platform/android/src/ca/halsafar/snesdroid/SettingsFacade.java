package ca.halsafar.snesdroid;


import ca.halsafar.snesdroid.SettingsFacade;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.Log;
import android.view.Window;



public class SettingsFacade
{
     private static final String LOG_TAG = "SettingsFacade";
             
     // default dirs
     public static final String DEFAULT_DIR = "/SNESDroid";

     public static final String DEFAULT_ROM_EXTENSIONS = "smc|fig|sfc|gd3|gd7|dx2|bsx|swc|zip|7z";
     public static final String DEFAULT_SHADER_EXTENSIONS = "frag";         
     
     // menu keys
     public static final int MENU_ROM_SELECT = 1;
     public static final int MENU_SETTINGS = 2;
     public static final int MENU_INPUT_CONFIG = 3;
     public static final int MENU_SHADER_SELECT = 4;     
     public static final int MENU_CUSTOM_KEYS = 5;
     public static final int MENU_DIR_SELECT = 6;
     public static final int MENU_EMUCLOUD = 7;    
          
     public static final int PREF_AUTO_SAVE_SLOT = 10;
     
     // application settings
     public static final String PREF_FIRST_RUN = "FIRST_RUN_1_5";
     public static final String PREF_FIRST_RUN_UPDATE = "FIRST_RUN_UPDATE_1_5";            
     
     public static int getRealScreenHeight(final Activity activity)
     {
         Rect rect = new Rect();
         Window window = activity.getWindow();
         window.getDecorView().getWindowVisibleDisplayFrame(rect);
         
         //int screenWidth = Math.max(rect.right, rect.bottom);
         int screenHeight = Math.min(rect.right, rect.bottom);         

         return screenHeight;                
     }
     
     
     public static int getRealScreenWidth(final Activity activity)
     {
         Rect rect = new Rect();
         Window window = activity.getWindow();
         window.getDecorView().getWindowVisibleDisplayFrame(rect);
         
         int screenWidth = Math.max(rect.right, rect.bottom);
         //int screenHeight = Math.min(rect.right, rect.bottom);         

         return screenWidth;                
     }     
     
     
     public static int getScreenSizeFlag(final Activity activity)
     {
    	 int screenFlag = (activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
         if (screenFlag == Configuration.SCREENLAYOUT_SIZE_LARGE)
         {
      	   Log.d(LOG_TAG, "LARGE SCREEN");
         }
         else if (screenFlag == Configuration.SCREENLAYOUT_SIZE_NORMAL)
         {
      	   Log.d(LOG_TAG, "NORMAL SCREEN");
         }             
         else if (screenFlag == Configuration.SCREENLAYOUT_SIZE_SMALL)
         {
      	   Log.d(LOG_TAG, "SMALL SCREEN");
         }
         else if (screenFlag == 4)
         {
      	   Log.d(LOG_TAG, "XLARGE");
         }   
         
         return screenFlag;
     }           

           
     public static void DoAutoSave()
     {
          // check for auto save
    	 boolean autoSave = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_AUTO_SAVE, "value")) != 0;                    
          if (autoSave)
          {
               Emulator.saveState(SettingsFacade.PREF_AUTO_SAVE_SLOT);
          }
     }

     
     public static void DoAutoLoad()
     {
          boolean autoSave = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_AUTO_SAVE, "value")) != 0;
          boolean gameGenie = Integer.valueOf(ConfigXML.getNodeAttribute(ConfigXML.PREF_GAME_GENIE, "value")) != 0;
          
          if (autoSave && !gameGenie)
          {
               Emulator.loadState(SettingsFacade.PREF_AUTO_SAVE_SLOT);
          }
     }     
     
     
        
}
