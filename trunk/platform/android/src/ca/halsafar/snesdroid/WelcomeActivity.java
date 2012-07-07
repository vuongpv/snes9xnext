/**
 * EMULATOR FRAMEWORK
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */

package ca.halsafar.snesdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity
{
     private static final String LOG_TAG = "WelcomeActivity";
     
     @Override
     public void onCreate(Bundle savedInstanceState)
     {
          Log.d(LOG_TAG, "onCreate()");
          
          super.onCreate(savedInstanceState);
          
          setContentView(R.layout.welcome);
          
          Button tmp = (Button)findViewById(R.id.btnWelcomeDone);
          tmp.setOnClickListener(new OnClickListener()
          {
               
               public void onClick(View v)
               {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);                    
                    startActivity(intent);
                    
                    finish();
               }
          });
          
          tmp = (Button)findViewById(R.id.btnWelcomeExit);
          tmp.setOnClickListener(new OnClickListener()
          {
               
               public void onClick(View v)
               {
                    finish();
               }
          });          
     }
}
