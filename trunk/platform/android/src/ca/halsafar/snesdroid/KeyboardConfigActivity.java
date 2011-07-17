package ca.halsafar.snesdroid;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


class KeyBinding
{
     String name;
     int key;
     
     public String toString()
     {
          return "Key: " + name + " | Code: " + key;
     }
}


public class KeyboardConfigActivity extends ListActivity
{
     private static final String LOG_TAG = "KeyboardConfigActivity";

     private int _modPos = 0;
     private int _modKeyCode = 0;
     
     private ListView _view = null;
     
     private ArrayAdapter<KeyBinding> _adapter;

     public void onCreate(Bundle state)
     {
          super.onCreate(state);

          reloadButtons();

          _view = getListView();

          _view.setOnItemClickListener(new OnItemClickListener()
          {
               public void onItemClick(AdapterView<?> parent, View view,
                         int position, long id)
               {
                    _modPos = position; 
                    
                    int pos = parent.getPositionForView(view);
                    KeyBinding o = _adapter.getItem(pos);
                    
                    new AlertDialog.Builder(KeyboardConfigActivity.this)
                    .setTitle("Binding: " + o.name)
                    .setMessage("Current Key: " + o.key)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                              InputPreferences.setButton(getApplicationContext(), _modKeyCode, _modPos);
                              
                              reloadButtons();                              
                         }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int id) {
                              _modPos = -1;
                              _modKeyCode = -1;
                              
                              dialog.cancel();
                         }
                    })
                    .setOnKeyListener(new OnKeyListener()
                    {                         
                         public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                         {
                        	      if(keyCode == KeyEvent.KEYCODE_VOLUME_UP || 
                        			 keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                        			 keyCode == KeyEvent.KEYCODE_MENU)
                        	      {
                        	           return false;
                        	      }
                        	 
                        	      if (_modPos >= 0)
                        	      {
                        	           ((AlertDialog)dialog).setMessage("New Key: " + keyCode);
                        	           _modKeyCode = keyCode;
                        	      }
	                           
                        	      return true;
                         }
                    }).create().show();

               }
          });
          
          /*lv.setOnKeyListener(new OnKeyListener()
          {
               
               public boolean onKey(View v, int keyCode, KeyEvent event)
               {
                    Log.d(LOG_TAG, "onKey(" + event + ")");

                    if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_MENU)
                    {
                         if (_modPos >= 0)
                         {
                              InputPreferences.setButton(getApplicationContext(), event.getKeyCode(), _modPos);
                              
                              reloadButtons();
                         }
                    }
                    
                    return false;
               }
          });*/
     }
     
     
     @Override
     public void onDestroy()
     {
          Log.d(LOG_TAG, "onDestroy()");
          
          super.onDestroy();                    
     }
     
     
     public void reloadButtons()
     {
          int numButtons = EmulatorButtons.BUTTON_INDEX_COUNT.ordinal();
          KeyBinding names[] = new KeyBinding[numButtons];
          for (EmulatorButtons button : EmulatorButtons.values())
          {
               if (button.ordinal() != numButtons)
               {
                    int keyCode = InputPreferences.getButtonCode(getApplicationContext(), button.ordinal());
                    
                    names[button.ordinal()] = new KeyBinding();
                    names[button.ordinal()].name = button.name();
                    names[button.ordinal()].key = keyCode;
               }
          }

          /*Toast.makeText(getApplicationContext(),
                    "States Array lentgh is : " + names.length,
                    Toast.LENGTH_LONG).show();*/
          _adapter = new ArrayAdapter<KeyBinding>(this,
                    R.layout.custom_key_view,
                    names);
          setListAdapter(_adapter);          
     }


     /*@Override
     public boolean onKeyDown(int keyCode, KeyEvent event)
     {
          //Log.d(LOG_TAG, "onKeyDown(" + event + ")");

          return super.onKeyDown(keyCode, event);
     }*/


     @Override
     public boolean onCreateOptionsMenu(Menu myMenu)
     {
          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.keyboard_config_menu, myMenu);

          return true;
     }


     @Override
     public boolean onOptionsItemSelected(MenuItem item)
     {
          switch (item.getItemId())
          {
               /*case R.id.menuCustomKeysShowKeyboard:
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                              InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return true;*/
               default:
                    return super.onOptionsItemSelected(item);
          }
     }
}
