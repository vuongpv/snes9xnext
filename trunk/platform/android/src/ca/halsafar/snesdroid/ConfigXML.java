package ca.halsafar.snesdroid;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class ConfigXML 
{
	 private static final String LOG_TAG = "ConfigXML";

	 public static final String PREF_SAMPLE_RATE = "/app/config/audio/sampleRate";
	 public static final String PREF_SAMPLE_STRETCH = "/app/config/audio/stretch";
	 public static final String PREF_AUDIO_ENABLED = "/app/config/audio/enabled";
	 
	 public static final String PREF_MAINTAIN_ASPECT = "/app/config/graphics/maintainAspect";
	 public static final String PREF_GRAPHICS_FILTER = "/app/config/graphics/graphicsFilter";
	 public static final String PREF_SHADER_FILE = "/app/config/graphics/shader";
	 public static final String PREF_ORIENTATION = "/app/config/graphics/orientation";	 
	 
	 public static final String PREF_FRAME_SKIP = "/app/config/emulation/frameSkip";
	 public static final String PREF_AUTO_SAVE = "/app/config/emulation/autoSave";
	 public static final String PREF_FAST_FORWARD = "/app/config/emulation/fastForward";
	 public static final String PREF_REWIND = "/app/config/emulation/rewind";
	 public static final String PREF_GAME_GENIE = "/app/config/emulation/gameGenie";
	 
	 public static final String PREF_DIR_ROMS = "/app/config/directories/roms";
	 public static final String PREF_DIR_SAVES = "/app/config/directories/saves";
	 public static final String PREF_DIR_STATES = "/app/config/directories/states";
	 public static final String PREF_DIR_CHEATS = "/app/config/directories/cheats";
	 public static final String PREF_DIR_SHADERS = "/app/config/directories/shaders";
	 public static final String PREF_DIR_TEMP = "/app/config/directories/temp";
	 
	 public static final String PREF_INPUT = "/app/config/input";
	 
	 public static final String PREF_EMU_BUTTONS = "/app/config/input/buttons/button";
	 public static final String PREF_TOUCH_BUTTONS = "/app/config/input/touch/button";
	 public static final String PREF_TOUCH_ANALOGS = "/app/config/input/touch/analog";
	 public static final String PREF_KEYS_PADS = "/app/config/input/keys/pad";
	 
	 private static Document _doc = null;
     //private static File _xmlFile = null;
     private static XPath _xp = null;    
        
     
     public static boolean openConfigXML()
     {
    	 Log.d(LOG_TAG, "openConfigXML()");

    	 try
    	 {    	
        	 File _xmlFile = new File(Emulator.getConfigFileName());
        	 Log.d(LOG_TAG, "XMLFILE: " + _xmlFile.toURI().toString());
    		 _doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_xmlFile.toURI().toString());

    		 _xp = XPathFactory.newInstance().newXPath(); 
    		 
    		 return true;
    	 } 
    	 catch (Exception e)
    	 {
    		 Log.d(LOG_TAG, "Exception: " + e);
    		 e.printStackTrace();
    	 }
    	 
    	 return false;
     }
     
     
     public static void writeConfigXML()
     {
    	 Log.d(LOG_TAG, "writeConfigXML(" + Emulator.getConfigFileName() + ")");
    	 
    	 File _xmlFile = new File(Emulator.getConfigFileName());
    	 Transformer xformer;
    	 try
    	 {
    		 xformer = TransformerFactory.newInstance().newTransformer();
    		 xformer.transform(new DOMSource(_doc), new StreamResult(_xmlFile));
    	 } 
    	 catch (Exception e)
    	 {
    		 Log.d(LOG_TAG, "Exception: " + e);
    		 e.printStackTrace();
    	 }    	 
     }
     
     
     public static String getNodeAttribute(String xPath, String attribName)
     {
    	 if (_doc == null)
    	 {
    		 if (!openConfigXML())
    		 {
    			 return null;
    		 }
    	 }
    	 
    	 try
    	 {
    		 Node n = (Node)_xp.evaluate(xPath, _doc, XPathConstants.NODE);
    		 
    		 _xp.reset();
    		 
    		 return n.getAttributes().getNamedItem(attribName).getNodeValue();
    	 }
    	 catch (Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 
    	 return null;
     }     
     
     
     public static void setNodeAttribute(String xPath, String attribName, String nodeValue)
     {
    	 if (_doc == null)
    	 {
    		 openConfigXML();
    	 }    	 
    	 
    	 try
    	 {
    		 Node n = (Node)_xp.evaluate(xPath, _doc, XPathConstants.NODE);
    		 
    		 _xp.reset();
    		 
    		 n.getAttributes().getNamedItem(attribName).setNodeValue(nodeValue);
    	 }
    	 catch (Exception e)
    	 {
    		 e.printStackTrace();
    	 }
     }
       
     
     public static NodeList getNodeChildren(String xPath)
     {
    	 try
    	 {
    		 NodeList n = (NodeList)_xp.evaluate(xPath, _doc, XPathConstants.NODESET);
    		 
    		 _xp.reset();
    		 
    		 return n;
    	 }
    	 catch (Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 
    	 return null;
     }     
}
