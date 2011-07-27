package ca.halsafar.snesdroid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import ca.halsafar.snesdroid.EmulatorButtons;
import ca.halsafar.snesdroid.InputPreferences;
import ca.halsafar.snesdroid.PreferenceFacade;

public enum EmulatorButtons
{
     BUTTON_INDEX_B,
     BUTTON_INDEX_A,
     BUTTON_INDEX_X,
     BUTTON_INDEX_Y,
     BUTTON_INDEX_L,
     BUTTON_INDEX_R,
     BUTTON_INDEX_START,
     BUTTON_INDEX_SELECT,
     BUTTON_INDEX_REWIND,
     BUTTON_INDEX_FORWARD,
     BUTTON_INDEX_LEFT,
     BUTTON_INDEX_UP,
     BUTTON_INDEX_RIGHT,
     BUTTON_INDEX_DOWN,     
     BUTTON_INDEX_COUNT;
     
	private static String LOG_TAG = "EmulatorButtons"; 
     
     public static void resetInput(final Activity activity, final Context context)
     {          
    	 float screenWidth = PreferenceFacade.getRealScreenWidth(activity, context);
    	 float screenHeight = PreferenceFacade.getRealScreenHeight(activity, context);
         
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
}

