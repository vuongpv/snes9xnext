package ca.halsafar.snesdroid;

/*import com.phonegap.DroidGap;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;

public class EmuCloudActivity extends DroidGap 
{
	private static EmuCloudActivity _instance;
	
	public EmuCloudActivity()
	{
		
	}
	
	public static EmuCloudActivity getInstance()
	{
		return _instance;
	}
	
    // Called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        // singleton hack for android so sms plugin can access activity
        _instance = this;        
        
        //super.loadUrl("file:///android_asset/www/index.html");
        super.loadUrl("http://halsafar.ca/EmuCloud/web/index.html");
        
        // fixed annoying zoom in on touching input fields
        WebSettings settings = appView.getSettings(); 
        settings.setSupportZoom(false); 
        settings.setBuiltInZoomControls(false);
        settings.setDefaultZoom(ZoomDensity.FAR);
    }
}*/