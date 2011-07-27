/**
 * HALSAFAR DownloaderHelpers
 * 
 * SEE LICENSE FILE
 * 	Free to use, non-commercial license.
 * 
 * Copyright 2011 - Stephen Damm (shinhalsafar@gmail.com)
 */

package ca.halsafar.downloader;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;


public class DownloadFile extends AsyncTask<String, Integer, Long>
{
     private final String LOG_TAG = "DownloadFile";
     private DownloadListener _listener;
     private ProgressDialog _dialog;
     private Context _context;
     private String _outLocation = null; 
     private long _lastModTime = 0;
     
     public DownloadFile(String outLocation, Context context, ProgressDialog progressDailog, DownloadListener listener)
     {
          _outLocation = outLocation;
          _listener = listener;
          _context = context;
          _dialog = progressDailog;
     }
     
     @Override
     protected Long doInBackground(String... uri)
     {
          int count = 0;
          try
          {
               URL url = new URL(uri[0]);
               URLConnection conexion = url.openConnection();
               conexion.connect();
               // this will be useful so that you can show a tipical 0-100%
               // progress bar
               int lenghtOfFile = conexion.getContentLength();
                              
               
               // modification check
               _lastModTime = conexion.getLastModified();
               SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
               long oldLastModified = prefs.getLong("pref_" + _outLocation, 0l);
               
               if (_lastModTime == oldLastModified)
               {
                    Log.d("DownloadFile", "Update to date");
                    return 0l;
               }                                       
               
               // downlod the file
               InputStream input = new BufferedInputStream(url
                         .openStream());
               OutputStream output = new FileOutputStream(_outLocation);

               byte data[] = new byte[1024];

               long total = 0;

               while ((count = input.read(data)) != -1)
               {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
               }

               output.flush();
               output.close();
               input.close();

               return total;
          }
          catch (IOException e)
          {
               Log.e(LOG_TAG, e.toString());
          }
                         
          return null;
     }


     @Override
     public void onProgressUpdate(Integer... progress)
     {
          // here you will have to update the progressbar
          // with something like
          _dialog.setProgress(progress[0]);
     }


     @Override
     protected void onPostExecute(Long result)
     {
          Log.d(LOG_TAG, "Downloaded " + result + " bytes");

          if (result == null)
          {
               _dialog.setMessage("Error occured, no connection could be made.");
               _listener.onDownloadFail(_outLocation);
          }
          else if (result > 0)
          {
               _listener.onDownloadSuccess(_outLocation, result);
               
               // store modified time
               SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
               Editor edit = prefs.edit();
               edit.putLong("pref_" + _outLocation, _lastModTime);
               edit.commit();   
          }
          else
          {
               _dialog.setMessage("Already Up to Date");
               _listener.onDownloadAlreadyUpdated(_outLocation);
          }
     }
}