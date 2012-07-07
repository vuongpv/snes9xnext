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

import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


class KeyBinding
{
     String name;
     String xpath;
     int key;
     boolean adjusting;
     
     public String toString()
     {
          return "Key: " + name + " | Code: " + key;
     }
}


class KeyBindingAdapter extends ArrayAdapter<KeyBinding>
{
	//private static final String LOG_TAG = "KeyBindingAdapter";
	
	// used to keep selected position in ListView
	private int selectedPos = -1;	// init value for not-selected

	public KeyBindingAdapter(Context context, int textViewResourceId,
							KeyBinding[] objects)
	{
		super(context, textViewResourceId, objects);
	}

	public void setSelectedPosition(int pos)
	{
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public int getSelectedPosition(){
		return selectedPos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
	    View v = convertView;

	    // only inflate the view if it's null
	    if (v == null) {
	        LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = vi.inflate(R.layout.custom_key_view, null);
	    }

	    // get text view
        TextView label = (TextView)v.findViewById(R.id.txtKeyCode);        
        Button btn = (Button)v.findViewById(R.id.btnUnmap);

        // change the row color based on selected state
        String extra = "";
        if(selectedPos == position)
        {
        	label.setBackgroundColor(R.color.orange);
        	v.setBackgroundColor(Color.RED);
        	extra = " (Press any button to set new key) ";
        	btn.setEnabled(true);
        }
        else
        {
        	label.setBackgroundColor(R.color.black);
        	v.setBackgroundColor(Color.BLACK);
        	btn.setEnabled(false);
        }

        label.setText(this.getItem(position).toString() + extra);

        return(v);
	}
}


public class KeyboardConfigActivity extends ListActivity
{
     private static final String LOG_TAG = "KeyboardConfigActivity";

     private int _modPos = -1;
     
     private ListView _view = null;
     private KeyBindingAdapter _adapter;
     
     
     @Override
     public void onCreate(Bundle state)
     {
          super.onCreate(state);       
     }
     
     
     @Override
     public void onStart()
     {
    	 loadButtonsFromConfig();

         _view = getListView();
         _view.setClickable(true);

         _view.setOnItemClickListener(new OnItemClickListener()
         {
              public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id)
              {
           	   	Log.d(LOG_TAG, "onItemClick(" + parent + ", " + view + ", " + position + ", " + id + ")");
           	   
           	   	_modPos = position; 
                                       
           	   	int pos = parent.getPositionForView(view);
                   
           	   	_adapter.setSelectedPosition(pos);
           	   	
                Button btn = (Button)view.findViewById(R.id.btnUnmap);
                btn.setOnClickListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				Log.d(LOG_TAG, "UNMAP ONCLICK");
        				
        	      		KeyBinding key = _adapter.getItem(_modPos);
        	      		key.key = 0;
        	      		ConfigXML.setNodeAttribute(key.xpath, "keycode", ""+0);
        	           
        	      		_adapter.notifyDataSetChanged();	    
        	           
        	      		_modPos = -1;
        	           
        	      		_adapter.setSelectedPosition(-1);        				
        			}
        		});
              }
         });
                   
         InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
         mgr.showInputMethodPicker();   
         
         super.onStart();
     }
     
     
     @Override
     public void onStop()
     {
    	 Log.d(LOG_TAG, "onStop()");
    	 
    	 ConfigXML.writeConfigXML();
    	 
    	 super.onStart();
     }
     
     
     @Override
     public void onDestroy()
     {
          Log.d(LOG_TAG, "onDestroy()");
          
          super.onDestroy();                    
     }
     
     
     public void loadButtonsFromConfig()
     {
    	 NodeList padsRoot = ConfigXML.getNodeChildren(ConfigXML.PREF_KEYS_PADS);
    	 
    	 // get button count
    	 int numButtons = 0;
    	 for (int i = 0; i < padsRoot.getLength(); i++)
    	 {
    		 NodeList padButtons = ConfigXML.getNodeChildren(ConfigXML.PREF_KEYS_PADS + "[@id=" + (i+1) + "]/*");
    		 Log.d(LOG_TAG, "Pad: " + i + " has button count: " + padButtons.getLength());
    		 
    		 numButtons += padButtons.getLength();
    	 }
    	 
    	 KeyBinding names[] = new KeyBinding[numButtons];
    	 int index = 0;
    	 for (int i = 0; i < padsRoot.getLength(); i++)
    	 {
    		 NodeList padButtons = ConfigXML.getNodeChildren(ConfigXML.PREF_KEYS_PADS + "[@id=" + (i+1) + "]/*");
    		 for (int j = 0; j < padButtons.getLength(); j++)
    		 {
    			 String id = padButtons.item(j).getAttributes().getNamedItem("id").getNodeValue();
    			 
    			 names[index] = new KeyBinding();
    			 names[index].xpath = "/app/config/input/keys/pad[@id="+(i+1)+"]/*[@id='"+id+"']"; 
    			 names[index].name = "PLAYER [" + (i+1) + "]: " + id;
    			 names[index].key = Integer.valueOf(padButtons.item(j).getAttributes().getNamedItem("keycode").getNodeValue());
    			 names[index].adjusting = false;
	    		 	    		 
	    		 index++;
    		 }
    	 }

    	 _adapter = new KeyBindingAdapter(this, R.layout.custom_key_view, names);
    	 
    	 setListAdapter(_adapter);
     }


     @Override
     public boolean dispatchKeyEvent(KeyEvent event)
     {
    	 Log.d(LOG_TAG, "dispatchKeyEvent(" + event + ")");
    	 
         if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || 
        		 event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN ||
        		 event.getKeyCode() == KeyEvent.KEYCODE_MENU)
	      	{
	      		return super.dispatchKeyEvent(event);
	      	}    	 
    	 
      	if (_modPos >= 0)
      	{
      		Log.d(LOG_TAG, "NewButton: " + event.getKeyCode());
      		int keyCode = event.getKeyCode();
            
      		KeyBinding key = _adapter.getItem(_modPos);
      		key.key = keyCode;
      		ConfigXML.setNodeAttribute(key.xpath, "keycode", ""+keyCode);
           
      		_adapter.notifyDataSetChanged();	    
           
      		_modPos = -1;
           
      		_adapter.setSelectedPosition(-1);
           
      		return true;
      	}          
   
       return super.dispatchKeyEvent(event);    	 
     }
     

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
