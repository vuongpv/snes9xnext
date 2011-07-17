/**
 * SNESDRoid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */

package ca.halsafar.snesdroid;

import ca.halsafar.snesdroid.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

public class AboutActivity extends Activity
{
     private static final String LOG_TAG = "AboutActivity";
     
     @Override
     public void onCreate(Bundle savedInstanceState)
     {
          Log.d(LOG_TAG, "onCreate()");
          
          super.onCreate(savedInstanceState);
          
          setContentView(R.layout.about);          
          
          TextView txt = (TextView) findViewById(R.id.txtVersion);
          txt.setText("Version: " + getVersion());                    
          
          TextView email = (TextView) findViewById(R.id.aboutEmail);
          Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
          
          TextView website = (TextView) findViewById(R.id.aboutWebsite);
          CharSequence text = "<a href=\""+getString(R.string.website)+"\">Website</a>";
          website.setText(Html.fromHtml(text.toString())); 
          website.setMovementMethod(LinkMovementMethod.getInstance());
          
          TextView issues = (TextView) findViewById(R.id.aboutIssues);
          text = "<a href=\""+getString(R.string.issueTracker)+"\">Report Issues</a>";
          issues.setText(Html.fromHtml(text.toString())); 
          issues.setMovementMethod(LinkMovementMethod.getInstance());
          
          TextView poweredBy = (TextView) findViewById(R.id.aboutPoweredBy);
          text = "<a href=\""+getString(R.string.poweredBy)+"\">Powered By: SNES9x</a>";
          poweredBy.setText(Html.fromHtml(text.toString())); 
          poweredBy.setMovementMethod(LinkMovementMethod.getInstance());
          
          TextView seeAlso = (TextView) findViewById(R.id.aboutSeeAlsoNes);
          text = "<a href=\"market://search?q=pname:ca.halsafar.nesdroid\">See Also: NESDroid</a>";          
          seeAlso.setText(Html.fromHtml(text.toString())); 
          seeAlso.setMovementMethod(LinkMovementMethod.getInstance());
          
          seeAlso = (TextView) findViewById(R.id.aboutSeeAlsoGen);
          text = "<a href=\"market://search?q=pname:ca.halsafar.genesisdroid\">See Also: GENPlusDroid</a>";          
          seeAlso.setText(Html.fromHtml(text.toString())); 
          seeAlso.setMovementMethod(LinkMovementMethod.getInstance());          
     }
     
     private String getVersion()
     {
          String version = "ERROR";
          try
          {
              PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
              version = pInfo.versionName;
          } 
          catch (NameNotFoundException e1)
          {
              Log.e(LOG_TAG, "VersionName not found", e1);
          }
          return version;
      }     
}
