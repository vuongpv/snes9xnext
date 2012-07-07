/**
 * GENPlusDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */
#include <unistd.h>
#include <tinyxml.h>
#include <zip.h>

#include <engine/engine.h>
#include <resources/resources.h>
#include <audio/interface.h>
#include <util/basicMath.h>

#include "Application.h"

using namespace Sandstorm;

namespace Base
{

// APK and EXTERNAL PATHS
extern char g_apkFileName[MAX_PATH_LENGTH];
extern char g_externalStoragePath[MAX_PATH_LENGTH];

}


namespace EmulatorBase
{
     // Directories
     char g_romsDir[MAX_PATH_LENGTH];
     char g_statesDir[MAX_PATH_LENGTH];
     char g_savesDir[MAX_PATH_LENGTH];
     char g_shadersDir[MAX_PATH_LENGTH];
     char g_cheatsDir[MAX_PATH_LENGTH];
     char g_tempDir[MAX_PATH_LENGTH];
     char g_configFilePath[MAX_PATH_LENGTH];

     void loadPaths()
     {
          TiXmlDocument doc(EmulatorBase::g_configFilePath);
          doc.LoadFile();

          TiXmlHandle hDoc(&doc);
          TiXmlElement* pElem = NULL;
          TiXmlHandle hRoot(0);
          TiXmlHandle configRoot(0);
          TiXmlHandle keysRoot(0);
          TiXmlHandle touchRoot(0);

          // retrieve the root element
          pElem=hDoc.FirstChildElement().Element();

          // error in xml
          if (!pElem)
          {
               LOGE("Cannot load paths... Error parsing config.xml!");
               return;
          }

          // root handle, save this for later
          hRoot=TiXmlHandle(pElem);
          configRoot = hRoot.FirstChild("config");

          strcpy(EmulatorBase::g_romsDir, configRoot.FirstChild("directories").FirstChild("roms").Element()->Attribute("value"));
          strcpy(EmulatorBase::g_savesDir, configRoot.FirstChild("directories").FirstChild("saves").Element()->Attribute("value"));
          strcpy(EmulatorBase::g_statesDir, configRoot.FirstChild("directories").FirstChild("states").Element()->Attribute("value"));
          strcpy(EmulatorBase::g_cheatsDir, configRoot.FirstChild("directories").FirstChild("cheats").Element()->Attribute("value"));
          strcpy(EmulatorBase::g_shadersDir, configRoot.FirstChild("directories").FirstChild("shaders").Element()->Attribute("value"));
          strcpy(EmulatorBase::g_tempDir, configRoot.FirstChild("directories").FirstChild("temp").Element()->Attribute("value"));

          doc.Clear();
     }
}


Application::Application()
{
	LOGD("APPLICATION()");

     _initialized = false;
     _romLoaded = false;
     _emuInitialized = false;
     _sampleRate = 0;
     _curState = 0;

     // paths
     _currentRom = (char*)malloc(MAX_PATH * sizeof(char));

     // screen info
     _screenWidth = 0;
     _screenHeight = 0;
     _screenType = 0;

     // input count
     _emuControllerCount = 0;

     _rewindState = NULL;
     _rewindBuf = NULL;

     GFX.Screen = NULL;
}


Application::~Application()
{
	LOGD("Application::Destructor()");

     deinit();
}


int Application::init()
{
     LOGD("init()");

     // config file path
     strcpy(EmulatorBase::g_configFilePath, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_configFilePath, CONFIG_FILE_NAME);

     // capture paths
     EmulatorBase::loadPaths();

     if (!_emuInitialized)
     {
          initEmulator();
     }

     _initialized = true;

     return NATIVE_OK;
}


void Application::deinit()
{
     LOGD("Start Application.destroy()");

     // shutdown snes
     if (_romLoaded)
     {
          saveSRam();
     }
     S9xGraphicsDeinit();
     S9xDeinitAPU();
     Deinit();

     // free rewinder
     /*if (_rewindState != NULL)
     {
          state_manager_free(_rewindState);
          _rewindState = NULL;
     }*/

     // set to false
     _romLoaded = false;

     ResourceManager::unloadAll();

     LOGD("Finished Application.destroy()");
}



void Application::buildConfigFile()
{
     LOGD("Application::buildConfigFile()");

     //build elements
     TiXmlDocument doc;
     TiXmlDeclaration * decl = new TiXmlDeclaration( "1.0", "", "" );
     TiXmlElement * root = new TiXmlElement( "app" );

     TiXmlElement * configRoot = new TiXmlElement( "config" );

     TiXmlElement * audioRoot = new TiXmlElement( "audio" );
     TiXmlElement * audioSampleRate = new TiXmlElement( "sampleRate" );
     TiXmlElement * audioStretch = new TiXmlElement( "stretch" );
     TiXmlElement * audioEnabled = new TiXmlElement( "enabled" );

     TiXmlElement * graphicsRoot = new TiXmlElement( "graphics" );
     TiXmlElement * graphicsOrientation = new TiXmlElement( "orientation" );
     TiXmlElement * graphicsMaintainAspect = new TiXmlElement( "maintainAspect" );
     TiXmlElement * graphicsFilter = new TiXmlElement( "graphicsFilter" );
     TiXmlElement * graphicsShader = new TiXmlElement( "shader" );

     TiXmlElement * emulationRoot = new TiXmlElement( "emulation" );
     TiXmlElement * emulationFrameSkip = new TiXmlElement( "frameSkip" );
     TiXmlElement * emulationAutoSave = new TiXmlElement( "autoSave" );
     TiXmlElement * emulationFastForward = new TiXmlElement( "fastForward" );
     TiXmlElement * emulationRewind = new TiXmlElement( "rewind" );
     TiXmlElement * emulationGameGenie = new TiXmlElement( "gameGenie" );

     TiXmlElement * directoryRoot = new TiXmlElement( "directories" );
     TiXmlElement * directoryRoms = new TiXmlElement( "roms" );
     TiXmlElement * directorySaves = new TiXmlElement( "saves" );
     TiXmlElement * directoryStates = new TiXmlElement( "states" );
     TiXmlElement * directoryCheats = new TiXmlElement( "cheats" );
     TiXmlElement * directoryShaders = new TiXmlElement( "shaders" );
     TiXmlElement * directoryTemp = new TiXmlElement( "temp" );

     TiXmlElement * inputRoot = new TiXmlElement( "input" );
     TiXmlElement * inputTouchRoot = new TiXmlElement( "touch" );
     TiXmlElement * inputKeysRoot = new TiXmlElement( "keys" );

     TiXmlElement * inputKeysPad1 = new TiXmlElement( "pad" );
     TiXmlElement * inputKeysPadKey = new TiXmlElement( "key" );
     TiXmlElement * inputKeysPad2 = new TiXmlElement( "pad" );

     TiXmlElement * inputTouchButton = new TiXmlElement( "button" );
     TiXmlElement * inputTouchAnalog = new TiXmlElement( "analog" );

     // build DOM
     audioRoot->LinkEndChild(audioSampleRate);
     audioRoot->LinkEndChild(audioStretch);
     audioRoot->LinkEndChild(audioEnabled);

     graphicsRoot->LinkEndChild(graphicsMaintainAspect);
     graphicsRoot->LinkEndChild(graphicsFilter);
     graphicsRoot->LinkEndChild(graphicsShader);
     graphicsRoot->LinkEndChild(graphicsOrientation);

     emulationRoot->LinkEndChild(emulationFrameSkip);
     emulationRoot->LinkEndChild(emulationAutoSave);
     emulationRoot->LinkEndChild(emulationFastForward);
     emulationRoot->LinkEndChild(emulationRewind);
     emulationRoot->LinkEndChild(emulationGameGenie);

     directoryRoot->LinkEndChild(directoryRoms);
     directoryRoot->LinkEndChild(directorySaves);
     directoryRoot->LinkEndChild(directoryStates);
     directoryRoot->LinkEndChild(directoryCheats);
     directoryRoot->LinkEndChild(directoryShaders);
     directoryRoot->LinkEndChild(directoryTemp);

     // x,y,a,b,l,r,up,down,left,right,start,select,forward,rewind,save,load
     inputKeysRoot->LinkEndChild(inputKeysPad1);
     inputKeysPad1->LinkEndChild(inputKeysPadKey);
     for (int i = 0; i < BUTTON_INDEX_COUNT-1; i++)
     {
          inputKeysPad1->LinkEndChild(inputKeysPadKey->Clone());
     }

     inputKeysRoot->LinkEndChild(inputKeysPad2);
     for (int i = 0; i < BUTTON_INDEX_COUNT; i++)
     {
          inputKeysPad2->LinkEndChild(inputKeysPadKey->Clone());
     }

     inputKeysRoot->LinkEndChild(inputKeysPad2->Clone());
     inputKeysRoot->LinkEndChild(inputKeysPad2->Clone());
     inputKeysRoot->LinkEndChild(inputKeysPad2->Clone());

     inputTouchRoot->LinkEndChild(inputTouchButton);
     for (int i = 0; i < TOUCH_BUTTON_COUNT - 1; i++)
     {
          inputTouchRoot->LinkEndChild(inputTouchButton->Clone());
     }
     inputTouchRoot->LinkEndChild(inputTouchAnalog);

     inputRoot->LinkEndChild(inputTouchRoot);
     inputRoot->LinkEndChild(inputKeysRoot);

     configRoot->LinkEndChild(audioRoot);
     configRoot->LinkEndChild(graphicsRoot);
     configRoot->LinkEndChild(emulationRoot);
     configRoot->LinkEndChild(directoryRoot);
     configRoot->LinkEndChild(inputRoot);

     root->LinkEndChild( configRoot );

     doc.LinkEndChild( decl );
     doc.LinkEndChild( root );

     // save to disk
     LOGD("Creating config file at: %s", EmulatorBase::g_configFilePath );
     doc.SaveFile( EmulatorBase::g_configFilePath );

     // remove from memory
     doc.Clear();
}


void Application::resetConfig()
{
     LOGD("Application::resetConfig()");

     TiXmlDocument doc(EmulatorBase::g_configFilePath);
     doc.LoadFile();

     TiXmlHandle hDoc(&doc);
     TiXmlElement* pElem = NULL;
     TiXmlHandle hRoot(0);
     TiXmlHandle configRoot(0);
     TiXmlHandle keysRoot(0);
     TiXmlHandle touchRoot(0);

     // retrieve the root element
     pElem=hDoc.FirstChildElement().Element();

     // error in xml
     if (!pElem)
     {
          LOGE("Error parsing config.xml!");
          return;
     }

     // root handle, save this for later
     hRoot=TiXmlHandle(pElem);
     configRoot = hRoot.FirstChild("config");

     // set defaults
     configRoot.FirstChild("audio").FirstChild("sampleRate").Element()->SetAttribute("value", 22050);
     configRoot.FirstChild("audio").FirstChild("stretch").Element()->SetAttribute("value", "8");
     configRoot.FirstChild("audio").FirstChild("enabled").Element()->SetAttribute("value", "1");

     configRoot.FirstChild("graphics").FirstChild("maintainAspect").Element()->SetAttribute("value", "1");
     configRoot.FirstChild("graphics").FirstChild("graphicsFilter").Element()->SetAttribute("value", "1");
     configRoot.FirstChild("graphics").FirstChild("orientation").Element()->SetAttribute("value", "0");
     configRoot.FirstChild("graphics").FirstChild("shader").Element()->SetAttribute("value", "");

     configRoot.FirstChild("emulation").FirstChild("frameSkip").Element()->SetAttribute("value", "1");
     configRoot.FirstChild("emulation").FirstChild("autoSave").Element()->SetAttribute("value", "0");
     configRoot.FirstChild("emulation").FirstChild("gameGenie").Element()->SetAttribute("value", "0");
     configRoot.FirstChild("emulation").FirstChild("fastForward").Element()->SetAttribute("value", "1");
     configRoot.FirstChild("emulation").FirstChild("rewind").Element()->SetAttribute("value", "0");

     // build path defaults
     strcpy(EmulatorBase::g_romsDir, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_romsDir, PATH_ROMS);

     strcpy(EmulatorBase::g_statesDir, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_statesDir, PATH_STATES);

     strcpy(EmulatorBase::g_savesDir, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_savesDir, PATH_SAVES);

     strcpy(EmulatorBase::g_cheatsDir, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_cheatsDir, PATH_CHEATS);

     strcpy(EmulatorBase::g_shadersDir, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_shadersDir, PATH_SHADERS);

     strcpy(EmulatorBase::g_tempDir, Base::g_externalStoragePath);
     strcat(EmulatorBase::g_tempDir, PATH_TEMP);

     configRoot.FirstChild("directories").FirstChild("roms").Element()->SetAttribute("value", EmulatorBase::g_romsDir);
     configRoot.FirstChild("directories").FirstChild("saves").Element()->SetAttribute("value", EmulatorBase::g_savesDir);
     configRoot.FirstChild("directories").FirstChild("states").Element()->SetAttribute("value", EmulatorBase::g_statesDir);
     configRoot.FirstChild("directories").FirstChild("cheats").Element()->SetAttribute("value", EmulatorBase::g_cheatsDir);
     configRoot.FirstChild("directories").FirstChild("shaders").Element()->SetAttribute("value", EmulatorBase::g_shadersDir);
     configRoot.FirstChild("directories").FirstChild("temp").Element()->SetAttribute("value", EmulatorBase::g_tempDir);

     // save to disk
     LOGD("Saving config file");
     doc.SaveFile( EmulatorBase::g_configFilePath );

     // remove from memory
     LOGD("Clear config file from memory");
     doc.Clear();
}


void Application::resetInputConfig()
{
     LOGD("resetInputConfig()");

     TiXmlDocument doc(EmulatorBase::g_configFilePath);
     doc.LoadFile();

     TiXmlHandle hDoc(&doc);
     TiXmlElement* pElem = NULL;
     TiXmlHandle hRoot(0);
     TiXmlHandle configRoot(0);
     TiXmlHandle keysRoot(0);
     TiXmlHandle touchRoot(0);

     // retrieve the root element
     pElem=hDoc.FirstChildElement().Element();

     // error in xml
     if (!pElem)
     {
          LOGE("Error parsing config.xml!");
          return;
     }

     // root handle, save this for later
     hRoot=TiXmlHandle(pElem);
     configRoot = hRoot.FirstChild("config");
     keysRoot = configRoot.FirstChild("input").FirstChild("keys");
     touchRoot = configRoot.FirstChild("input").FirstChild("touch");

     // default visible
     configRoot.FirstChild("input").Element()->SetAttribute("showTouch", 1);
     configRoot.FirstChild("input").Element()->SetDoubleAttribute("alpha", 0.65);

     // default button codes - using Xperia Play
     int aCode = 4; // X
     int bCode = 23; // Circle
     int xCode = 100; // Triangle
     int yCode = 99; // Square
     int lCode = 102; // L1
     int rCode = 103; // R1
     int startCode = 108; // Start
     int selectCode = 109; // Select
     int rewindCode = 0; //
     int forwardCode = 0; //
     int leftCode = 21;
     int upCode = 19;
     int rightCode = 22;
     int downCode = 20;

     int noCode = 0;

     // key defaults
     // player 1
     TiXmlElement* padElem = keysRoot.FirstChild("pad").Element();
     padElem->SetAttribute("id", "1");

     TiXmlElement* keyElem = padElem->FirstChildElement();
     keyElem->SetAttribute("keycode", aCode);
     keyElem->SetAttribute("id", "A");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", bCode);
     keyElem->SetAttribute("id", "B");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", xCode);
     keyElem->SetAttribute("id", "X");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", yCode);
     keyElem->SetAttribute("id", "Y");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", lCode);
     keyElem->SetAttribute("id", "L");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", rCode);
     keyElem->SetAttribute("id", "R");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", upCode);
     keyElem->SetAttribute("id", "Up");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", downCode);
     keyElem->SetAttribute("id", "Down");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", leftCode);
     keyElem->SetAttribute("id", "Left");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", rightCode);
     keyElem->SetAttribute("id", "Right");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", startCode);
     keyElem->SetAttribute("id", "Start");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", selectCode);
     keyElem->SetAttribute("id", "Select");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", 0);
     keyElem->SetAttribute("id", "Save");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", 0);
     keyElem->SetAttribute("id", "Load");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", forwardCode);
     keyElem->SetAttribute("id", "Forward");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", rewindCode);
     keyElem->SetAttribute("id", "Rewind");
     keyElem = keyElem->NextSiblingElement();

     // player 2
     padElem = padElem->NextSiblingElement();
     padElem->SetAttribute("id", "2");

     keyElem = padElem->FirstChildElement();
     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "A");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "B");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "X");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Y");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "L");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "R");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Up");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Down");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Left");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Right");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Start");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Select");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Save");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Load");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Forward");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Rewind");
     keyElem = keyElem->NextSiblingElement();

     // player 3
     padElem = padElem->NextSiblingElement();
     padElem->SetAttribute("id", "3");

     keyElem = padElem->FirstChildElement();
     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "A");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "B");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "X");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Y");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "L");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "R");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Up");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Down");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Left");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Right");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Start");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Select");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Save");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Load");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Forward");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Rewind");
     keyElem = keyElem->NextSiblingElement();

     // player 4
     padElem = padElem->NextSiblingElement();
     padElem->SetAttribute("id", "4");

     keyElem = padElem->FirstChildElement();
     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "A");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "B");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "X");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Y");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "L");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "R");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Up");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Down");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Left");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Right");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Start");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Select");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Save");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Load");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Forward");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Rewind");
     keyElem = keyElem->NextSiblingElement();

     // player 5
     padElem = padElem->NextSiblingElement();
     padElem->SetAttribute("id", "5");

     keyElem = padElem->FirstChildElement();
     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "A");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "B");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "X");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Y");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "L");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "R");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Up");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Down");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Left");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Right");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Start");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Select");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Save");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Load");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Forward");
     keyElem = keyElem->NextSiblingElement();

     keyElem->SetAttribute("keycode", noCode);
     keyElem->SetAttribute("id", "Rewind");
     keyElem = keyElem->NextSiblingElement();

     // touch input defaults
     float maxSize = (_screenWidth > _screenHeight ? _screenWidth : _screenHeight);

     float dpadWidth = maxSize * 0.20f;

     // tablet, reduce dpad defaults
     if (_screenType >= 4)
     {
          dpadWidth = maxSize * 0.10f;
     }
     float dpadHeight = dpadWidth;

     float dPadX = 0 + maxSize * 0.01f;
     float dPadY = _screenHeight - dpadHeight - (maxSize * 0.01f);

     float buttonsWidth = maxSize * 0.085f;
     float buttonsHeight = maxSize * 0.085f;
     float buttonSep = maxSize * 0.010f;

     // tablet, reduce button defaults
     if (_screenType >= 4)
     {
          buttonsWidth = maxSize * 0.045f;
          buttonsHeight = maxSize * 0.045f;
          buttonSep = maxSize * 0.005f;
     }

     float yButtonX = _screenWidth - ((buttonsWidth * 3) + buttonSep + (maxSize *0.015f));
     float yButtonY = _screenHeight - (buttonsHeight * 2 + (maxSize * 0.01f));

     float aButtonX = yButtonX + (buttonsWidth * 2) + (maxSize *0.015f);
     float aButtonY = yButtonY;

     float bButtonX = ((aButtonX + yButtonX) / 2.0);
     float bButtonY = yButtonY + (buttonsHeight);

     float xButtonX = bButtonX;
     float xButtonY = yButtonY - (buttonsHeight);

     float lButtonX = yButtonX;
     float lButtonY = yButtonY - (buttonsHeight + ((maxSize *0.02f)));

     float rButtonX = aButtonX;
     float rButtonY = lButtonY;

     float startButtonWidth = maxSize * 0.075f;
     float startButtonHeight = maxSize * 0.04f;

     float startButtonX = (_screenWidth / 2) + buttonSep;
     float startButtonY = _screenHeight - startButtonHeight - (maxSize * 0.015f);

     float selectButtonX = (_screenWidth / 2) - (startButtonWidth) - (buttonSep);
     float selectButtonY = startButtonY;

     float ffButtonWidth = maxSize * 0.05f;
     float ffButtonHeight = maxSize * 0.025f;
     float ffButtonX = _screenWidth - (ffButtonWidth*2 + (maxSize * 0.015f));
     float ffButtonY = 0 + ffButtonHeight + (maxSize * 0.015f);

     float rrButtonWidth = maxSize * 0.05f;
     float rrButtonHeight = maxSize * 0.025f;
     float rrButtonX = 0 + rrButtonWidth + (maxSize * 0.015f);
     float rrButtonY = 0 + rrButtonHeight + (maxSize * 0.015f);

     // touch defaults
     TiXmlElement* touchElem = touchRoot.FirstChild("button").Element();
     touchElem->SetDoubleAttribute("x", aButtonX);
     touchElem->SetDoubleAttribute("y", aButtonY);
     touchElem->SetDoubleAttribute("w", buttonsWidth);
     touchElem->SetDoubleAttribute("h", buttonsHeight);
     touchElem->SetAttribute("texture", "Textures/ButtonA.png");
     touchElem->SetAttribute("id", "A");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", bButtonX);
     touchElem->SetDoubleAttribute("y", bButtonY);
     touchElem->SetDoubleAttribute("w", buttonsWidth);
     touchElem->SetDoubleAttribute("h", buttonsHeight);
     touchElem->SetAttribute("texture", "Textures/ButtonB.png");
     touchElem->SetAttribute("id", "B");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", xButtonX);
     touchElem->SetDoubleAttribute("y", xButtonY);
     touchElem->SetDoubleAttribute("w", buttonsWidth);
     touchElem->SetDoubleAttribute("h", buttonsHeight);
     touchElem->SetAttribute("texture", "Textures/ButtonX.png");
     touchElem->SetAttribute("id", "X");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", yButtonX);
     touchElem->SetDoubleAttribute("y", yButtonY);
     touchElem->SetDoubleAttribute("w", buttonsWidth);
     touchElem->SetDoubleAttribute("h", buttonsHeight);
     touchElem->SetAttribute("texture", "Textures/ButtonY.png");
     touchElem->SetAttribute("id", "Y");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", lButtonX);
     touchElem->SetDoubleAttribute("y", lButtonY);
     touchElem->SetDoubleAttribute("w", buttonsWidth);
     touchElem->SetDoubleAttribute("h", buttonsHeight);
     touchElem->SetAttribute("texture", "Textures/ButtonL.png");
     touchElem->SetAttribute("id", "L");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", rButtonX);
     touchElem->SetDoubleAttribute("y", rButtonY);
     touchElem->SetDoubleAttribute("w", buttonsWidth);
     touchElem->SetDoubleAttribute("h", buttonsHeight);
     touchElem->SetAttribute("texture", "Textures/ButtonR.png");
     touchElem->SetAttribute("id", "R");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", startButtonX);
     touchElem->SetDoubleAttribute("y", startButtonY);
     touchElem->SetDoubleAttribute("w", startButtonWidth);
     touchElem->SetDoubleAttribute("h", startButtonHeight);
     touchElem->SetAttribute("texture", "Textures/StartButton.png");
     touchElem->SetAttribute("id", "Start");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", selectButtonX);
     touchElem->SetDoubleAttribute("y", selectButtonY);
     touchElem->SetDoubleAttribute("w", startButtonWidth);
     touchElem->SetDoubleAttribute("h", startButtonHeight);
     touchElem->SetAttribute("texture", "Textures/SelectButton.png");
     touchElem->SetAttribute("id", "Select");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", ffButtonX);
     touchElem->SetDoubleAttribute("y", ffButtonY);
     touchElem->SetDoubleAttribute("w", ffButtonWidth);
     touchElem->SetDoubleAttribute("h", ffButtonHeight);
     touchElem->SetAttribute("texture", "Textures/FastForward.png");
     touchElem->SetAttribute("id", "Forward");
     touchElem = touchElem->NextSiblingElement();

     touchElem->SetDoubleAttribute("x", rrButtonX);
     touchElem->SetDoubleAttribute("y", rrButtonY);
     touchElem->SetDoubleAttribute("w", rrButtonWidth);
     touchElem->SetDoubleAttribute("h", rrButtonHeight);
     touchElem->SetAttribute("texture", "Textures/Rewind.png");
     touchElem->SetAttribute("id", "Rewind");
     touchElem = touchElem->NextSiblingElement();

     touchElem = touchRoot.FirstChild("analog").Element();
     touchElem->SetDoubleAttribute("x", dPadX);
     touchElem->SetDoubleAttribute("y", dPadY);
     touchElem->SetDoubleAttribute("w", dpadWidth);
     touchElem->SetDoubleAttribute("h", dpadHeight);
     touchElem->SetDoubleAttribute("xSensitivity", 0.35);
     touchElem->SetDoubleAttribute("ySensitivity", 0.35);
     touchElem->SetAttribute("texture", "Textures/DirectionalPad.png");
     touchElem->SetAttribute("id", "0");

     // save to disk
     LOGD("Saving config file");
     doc.SaveFile( EmulatorBase::g_configFilePath );

     // remove from memory
     LOGD("Clear config file from memory");
     doc.Clear();
}


///
/// Requires valid OPENGL context
///
void Application::processGraphicsConfig()
{
     LOGD("processGraphicsConfig()");

     TiXmlDocument doc(EmulatorBase::g_configFilePath);
     doc.LoadFile();

     TiXmlHandle hDoc(&doc);
     TiXmlElement* pElem = NULL;
     TiXmlHandle hRoot(0);
     TiXmlHandle configRoot(0);
     TiXmlHandle keysRoot(0);
     TiXmlHandle touchRoot(0);

     // retrieve the root element
     pElem=hDoc.FirstChildElement().Element();

     // error in xml
     if (!pElem)
     {
          LOGE("Error parsing config.xml!");
          return;
     }

     // root handle, save this for later
     hRoot=TiXmlHandle(pElem);
     configRoot = hRoot.FirstChild("config");

     // graphics
     int maintainAspect = 0;
     configRoot.FirstChild("graphics").FirstChild("maintainAspect").Element()->Attribute("value", &maintainAspect);
     int graphicsFilter = 0;
     configRoot.FirstChild("graphics").FirstChild("graphicsFilter").Element()->Attribute("value", &graphicsFilter);
     const char* shader = configRoot.FirstChild("graphics").FirstChild("shader").Element()->Attribute("value");

     // graphics
     if (maintainAspect)
     {
          Graphics.SetAspectRatio(4.0f / 3.0f);
     }
     else
     {
          Graphics.SetAspectRatio((float)_screenWidth / (float)_screenHeight);
     }

     if (graphicsFilter)
     {
          Graphics.SetSmooth(true);
     }
     else
     {
          Graphics.SetSmooth(false);
     }

#if (defined(ANDROID))
     file_open_apk();
#endif
     if (shader != NULL && strlen(shader) > 1)
     {
          Graphics.InitEmuShader(shader);
     }
     else
     {
          Graphics.InitEmuShader(NULL);
     }
#if (defined(ANDROID))
     file_close_apk();
#endif

     doc.Clear();
}


void Application::processConfig()
{
     LOGD("processConfig()");

     TiXmlDocument doc(EmulatorBase::g_configFilePath);
     doc.LoadFile();

     TiXmlHandle hDoc(&doc);
     TiXmlElement* pElem = NULL;
     TiXmlHandle hRoot(0);
     TiXmlHandle configRoot(0);
     TiXmlHandle keysRoot(0);
     TiXmlHandle touchRoot(0);

     // retrieve the root element
     pElem=hDoc.FirstChildElement().Element();

     // error in xml
     if (!pElem)
     {
          LOGE("Error parsing config.xml!");
          return;
     }

     // root handle, save this for later
     hRoot=TiXmlHandle(pElem);
     configRoot = hRoot.FirstChild("config");
     keysRoot = configRoot.FirstChild("input").FirstChild("keys");
     touchRoot = configRoot.FirstChild("input").FirstChild("touch");

     // audio settings
     int sampleRate = 0;
     configRoot.FirstChild("audio").FirstChild("sampleRate").Element()->Attribute("value", &sampleRate);
     int stretch = 0;
     configRoot.FirstChild("audio").FirstChild("stretch").Element()->Attribute("value", &stretch);
     int useAudio = 0;
     configRoot.FirstChild("audio").FirstChild("enabled").Element()->Attribute("value", &useAudio);

     // emulation
     configRoot.FirstChild("emulation").FirstChild("frameSkip").Element()->Attribute("value", &_frameSkip);
     int autoSave = 0;
     configRoot.FirstChild("emulation").FirstChild("autoSave").Element()->Attribute("value", &autoSave);
     int fastForward = 0;
     configRoot.FirstChild("emulation").FirstChild("fastForward").Element()->Attribute("value", &fastForward);
     int rewind = 0;
     configRoot.FirstChild("emulation").FirstChild("rewind").Element()->Attribute("value", &rewind);
     int gameGenie = 0;
     configRoot.FirstChild("emulation").FirstChild("gameGenie").Element()->Attribute("value", &gameGenie);

     // orientation
     int orientation = 0;
     configRoot.FirstChild("graphics").FirstChild("orientation").Element()->Attribute("value", &orientation);

     // input visibility
     int showTouchInput = 0;
     configRoot.FirstChild("input").Element()->Attribute("showTouch", &showTouchInput);

     // calc aspect ratio data
     float aspectRatio = max(_screenWidth, _screenHeight) / min(_screenWidth, _screenHeight);
     float aspectRatioInv = 1.0f / aspectRatio;

     LOGD("AspectRatio: %f", aspectRatio);
     LOGD("aspectRatioInv: %f", aspectRatioInv);
     LOGD("orientation: %d", orientation);

     //
     // process config
     //
     // test if we need to hard reset the ROM
     bool hardReset = false;

     float alpha = 0.65f;
     configRoot.FirstChild("input").Element()->QueryFloatAttribute("alpha", &alpha);

     // touch init
     int buttonCount = touchRoot.Element()->ChildElementCount("button");
     int analogCount = touchRoot.Element()->ChildElementCount("analog");
     TouchInput.resizeArrays(buttonCount, analogCount);

     // touch init input, buttons
     int touchCount = 0;
     TiXmlElement* pAnalogElem = touchRoot.FirstChild("analog").Element();
     pElem = touchRoot.FirstChildElement("button").Element();
     for( ; pElem && (pElem != pAnalogElem) ; pElem=pElem->NextSiblingElement())
     {
          float x = 0.0f;
          float y = 0.0f;
          float w = 0.0f;
          float h = 0.0f;

          const char* id = pElem->Attribute("id");
          //const char* texture = pElem->Attribute("texture");
          pElem->QueryFloatAttribute("x", &x);
          pElem->QueryFloatAttribute("y", &y);
          pElem->QueryFloatAttribute("w", &w);
          pElem->QueryFloatAttribute("h", &h);

          if (orientation == 1)
          {
               x = x * aspectRatioInv;
               y = y * aspectRatio;

               w = w * aspectRatioInv;
               h = h * aspectRatio;

               w = min(w , h);
               h = w;
          }

          //LOGD("id: %s, texture: %s, x: %f, y: %f, w: %f, h: %f", id, texture, x, y, w, h);

          _touchInputMapping.map[id] = touchCount;

          TouchInput.setButton(touchCount, x, y, w, h, alpha, true);

          touchCount++;
     }

     // touch input, analogs
     touchCount = 0;
     pElem = pAnalogElem;
     for( ; pElem ; pElem=pElem->NextSiblingElement())
     {
          float x = 0.0f;
          float y = 0.0f;
          float w = 0.0f;
          float h = 0.0f;

          float xSens = 0.0f;
          float ySens = 0.0f;

          const char* id = pElem->Attribute("id");
          const char* texture = pElem->Attribute("texture");
          pElem->QueryFloatAttribute("x", &x);
          pElem->QueryFloatAttribute("y", &y);
          pElem->QueryFloatAttribute("w", &w);
          pElem->QueryFloatAttribute("h", &h);

          pElem->QueryFloatAttribute("xSensitivity", &xSens);
          pElem->QueryFloatAttribute("ySensitivity", &ySens);

          if (orientation == 1)
          {
               x = x * aspectRatioInv;
               y = y * aspectRatio;

               w = w * aspectRatioInv;
               h = h * aspectRatio;

               w = min(w , h);
               h = w;
          }

          //LOGD("id: %s, texture: %s, x: %f, y: %f, w: %f, h: %f, xSens: %f, ySens: %f", id, texture, x, y, w, h, xSens, ySens);

          TouchInput.setAnalog(touchCount, x, y, w, h, alpha, true);
          TouchInput.setXSensitivity(xSens);
          TouchInput.setYSensitivity(ySens);

          touchCount++;
     }

     // key input
     int padCount = 0;
     TiXmlElement* pPadElem = keysRoot.FirstChild("pad").Element();
     for (; pPadElem; pPadElem=pPadElem->NextSiblingElement())
     {
          pElem = pPadElem->FirstChildElement("key");
          for( ; pElem ; pElem=pElem->NextSiblingElement())
          {
               int keycode = 0;
               const char* id = pElem->Attribute("id");
               pElem->QueryIntAttribute("keycode", &keycode);

               //LOGD("pad: %d, id: %s, keycode: %d", padCount, id, keycode);

               _keyInputMapping[padCount].map[id] = keycode;
          }
          padCount++;
     }

     // audio
     if (useAudio)
     {
          float fStretch = 1.0f + (stretch / 100.0f);
          int newSampleRate = sampleRate * fStretch;

          if (_sampleRate != newSampleRate)
          {
               hardReset = true;
          }

          _sampleRate = newSampleRate;
          Settings.SoundPlaybackRate = newSampleRate;// * (42660./44100.);

          LOGD("SAMPLERATE: %d", Settings.SoundPlaybackRate);
          LOGD("stretch: %d", stretch);

          // init the audio
          SandstormAudio::openPcm(sampleRate, 2);
     }
     else
     {
          Settings.SoundPlaybackRate = 0;
          SandstormAudio::closePcm();
     }

     // handle rewind visibility
     _rewindEnabled = rewind;
     if (!_rewindEnabled)
     {
          TouchInput.setButtonVisibility(_touchInputMapping.map["Rewind"], false);
     }

     // fast forward visibility
     if (!fastForward)
     {
          TouchInput.setButtonVisibility(_touchInputMapping.map["Forward"], false);
     }

     if (!showTouchInput)
     {
          for (int i = 0; i < buttonCount; i++)
          {
               TouchInput.setButtonVisibility(i, false);
          }
          for (int i = 0; i < analogCount; i++)
          {
               TouchInput.setAnalogVisibility(i, false);
          }
     }

     // perform hard reset
     if (_romLoaded && hardReset)
     {
          LOGD("HARD RESET DUE TO CONFIG CHANGES");

          // TODO: wont catch auto saves
          loadROM(_currentRom);
     }

     doc.Clear();

     // load the paths
     EmulatorBase::loadPaths();
}


CallResult Application::loadContent()
{
	LOGD("loadContent()");

     bool loadInternal = false;
#if (defined(ANDROID))
     loadInternal = true;
     file_open_apk();
#endif
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/DirectionalPad.png"), "Textures/DirectionalPad", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/StartButton.png"), "Textures/StartButton", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/SelectButton.png"), "Textures/SelectButton", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/ButtonX.png"), "Textures/ButtonX", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/ButtonY.png"), "Textures/ButtonY", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/ButtonA.png"), "Textures/ButtonA", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/ButtonB.png"), "Textures/ButtonB", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/ButtonL.png"), "Textures/ButtonL", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/ButtonR.png"), "Textures/ButtonR", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/Rewind.png"), "Textures/Rewind", loadInternal, true);
     ResourceManager::add<Texture>(FILE_GET_ASSETS_PATH("Textures/FastForward.png"), "Textures/FastForward", loadInternal, true);

     //ResourceManager::add<Font>(FILE_GET_ASSETS_PATH("fonts/DroidSansMono.ttf"), "Fonts/Main", loadInternal, true);

#if (defined(ANDROID))
     file_close_apk();
#endif

     TouchInput.setAnalogTexture(0, ResourceManager::get<Texture>("Textures/DirectionalPad")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["X"], ResourceManager::get<Texture>("Textures/ButtonX")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["Y"], ResourceManager::get<Texture>("Textures/ButtonY")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["L"], ResourceManager::get<Texture>("Textures/ButtonL")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["R"], ResourceManager::get<Texture>("Textures/ButtonR")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["B"], ResourceManager::get<Texture>("Textures/ButtonB")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["A"], ResourceManager::get<Texture>("Textures/ButtonA")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["Start"], ResourceManager::get<Texture>("Textures/StartButton")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["Select"], ResourceManager::get<Texture>("Textures/SelectButton")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["Rewind"], ResourceManager::get<Texture>("Textures/Rewind")->getId());
     TouchInput.setButtonTexture(_touchInputMapping.map["Forward"], ResourceManager::get<Texture>("Textures/FastForward")->getId());

     LOGD("Done Loading Emulator Content");

     return NATIVE_OK;
}


CallResult Application::onDeviceCreate()
{
	LOGD("onDeviceCreate(%d, %d, %d)", Sandstorm::engine_getScreenInfo().w,
										Sandstorm::engine_getScreenInfo().h,
										Sandstorm::engine_getScreenInfo().screenType);

    // store screen info
    _screenWidth = Sandstorm::engine_getScreenInfo().w;
    _screenHeight = Sandstorm::engine_getScreenInfo().h;
    _screenType = Sandstorm::engine_getScreenInfo().screenType;

#if (defined(ANDROID))
     file_open_apk();
#endif

     ResourceManager::removeAll();

     Graphics.Init( FILE_GET_ASSETS_PATH("Shaders/stock_gui"),
                    SCREEN_RENDER_TEXTURE_WIDTH,
                    GL_RGB,
                    GL_UNSIGNED_SHORT_5_6_5);

     Graphics.Clear();

     processGraphicsConfig();

     Graphics.SetDimensions(Sandstorm::engine_getScreenInfo().w, Sandstorm::engine_getScreenInfo().h);

#if (defined(ANDROID))
     file_close_apk();
#endif

     return NATIVE_OK;
}


void Application::onShutdown()
{

}


void Application::initEmulator()
{
     __builtin_memset(&Settings, 0, sizeof(Settings));

     Settings.Crosshair = 1;
     Settings.CartAName[0] = 0;
     Settings.CartBName[0] = 0;
     Settings.FrameTimePAL = 20000;
     Settings.FrameTimeNTSC = 16667;

     Settings.SoundPlaybackRate = 44100;
     Settings.SoundInputRate = 31950;//32000;

     Settings.AccessoryAutoDetection =  ACCESSORY_AUTODETECTION_CONFIRM;

     Settings.HDMATimingHack = 100;
     Settings.BlockInvalidVRAMAccessMaster = true;
     Settings.ForceNTSC = true;
     //CPU.Flags = 0;

     S9xSetSamplesAvailableCallback(S9xAudioCallback);
     //S9xUnmapAllControls();
     _emuInitialized = true;
}


void Application::resetROM()
{
	S9xReset();
}


int Application::loadROM(const char* filename)
{
	LOGD("NDK:LoadingRom: %s", filename);

	if (_romLoaded)
	{
          // save sram
          saveSRam();
	}

	// de init s9x engine
	if (_romLoaded)
	{
	     Deinit();
	     S9xDeinitAPU();
	}

     // consider rom terminated
     _romLoaded = false;

     // process config
     processConfig();

	if (!Init())
	{
	     LOGE("Init() Failed!");
	     return NATIVE_ERROR;
	}

     if (!S9xInitAPU())
     {
          LOGE("S9xInitAPU() Failed!");
          return NATIVE_ERROR;
     }

     if (!S9xInitSound(64,0))
     {
          LOGE("S9xInitSound() Failed!");
          return NATIVE_ERROR;
     }

     // load ROM file
     if (!LoadROM(filename))
     {
          LOGE("LoadROM(%s) FAILED", filename);
          return NATIVE_ERROR;
     }

     // store current rom
     strcpy(_currentRom, filename);

     // load sram
     loadSRam();

     // init graphics
     if (GFX.Screen != NULL)
     {
          free(GFX.Screen);
     }

     GFX.Pitch = 512;
     GFX.Screen = (uint16_t *)malloc(1024 * 478);
     memset(GFX.Screen, 0, 1024*478);

     if (_romLoaded)
     {
          S9xGraphicsDeinit();
          S9xControlsSoftReset();
     }

     if (!S9xGraphicsInit())
     {
          LOGE("S9xGraphicsInit() Failed!");
          return NATIVE_ERROR;
     }

     Graphics.ReshapeEmuTexture(SNES_RENDER_TEXTURE_WIDTH,
                                  SNES_RENDER_TEXTURE_HEIGHT,
                                  SCREEN_RENDER_TEXTURE_WIDTH);

     //
     // setup input
     //
     if (Settings.CurrentROMisMultitapCompatible)
     {
          LOGD("MultiTap ROM");
          _controllerType = ControllerType::MULTITAP;
          _emuControllerCount = 5;
     }
     else
     {
          LOGD("Two Player ROM");
          _controllerType = ControllerType::TWO_JOYSTICKS;
          _emuControllerCount = 2;
     }
     S9xUnmapAllControls();

     switch (_controllerType)
     {
          case ControllerType::TWO_JOYSTICKS:
               map_snes9x_standard_controls(PAD_1);
               map_snes9x_standard_controls(PAD_2);
               break;
          case ControllerType::MULTITAP:
               map_snes9x_standard_controls(PAD_1);
               map_snes9x_standard_controls(PAD_2);
               map_snes9x_standard_controls(PAD_3);
               map_snes9x_standard_controls(PAD_4);
               map_snes9x_standard_controls(PAD_5);
               break;
     }

     switch (_controllerType)
     {
          case ControllerType::TWO_JOYSTICKS:
               S9xSetController(0, CTL_JOYPAD, 0, 0, 0, 0);
               S9xSetController(1, CTL_JOYPAD, 1, 0, 0, 0);
               break;
          case MULTITAP:
               S9xSetController(0, CTL_JOYPAD, 0,   0,   0,   0);
               S9xSetController(1, CTL_MP5, 1,   2,   3,   4);
               break;
     }

     S9xVerifyControllers();
   	/*if (_rewindEnabled)
   	{
   	     LOGD("Initializing rewinder!");
   	     _rewindBuf = (uint8 *)memalign(32,STATE_SIZE);

   	     int state_size = state_save(_rewindBuf, 0);

   	     _rewindState = state_manager_new(STATE_SIZE, 10<<20, _rewindBuf);
   	}*/

    _romLoaded = true;

    LOGD("SUCCESSFULLY LOADED ROM: %s", _currentRom);

     return NATIVE_OK;
}


void Application::saveState(const int i)
{
     LOGD("saveState(%d)", i);
     int temp = _curState;
     _curState = i;
     S9xFreezeGame(S9xChooseFilename(FALSE));
     _curState = temp;
}


void Application::loadState(const int i)
{
     LOGD("loadState(%d)", i);
     int temp = _curState;
     _curState = i;
     S9xUnfreezeGame(S9xChooseFilename(TRUE));
     _curState = temp;
}


void Application::selectState(const int i)
{
     LOGD("selectState(%d)", i);
     _curState = i;
}


void Application::loadSRam()
{
     LOGD("loadSRam()");
     LoadSRAM(S9xGetFilename(".srm", SRAM_DIR));
}


void Application::saveSRam()
{
     LOGD("saveSRam()");
     SaveSRAM(S9xGetFilename(".srm", SRAM_DIR));
}


int Application::makePath(const char* dirName, char* out)
{
	LOGD("makeDir(%s)", dirName);

	char* dotIndex = strrchr(_currentRom, '.');
	char* slashIndex = strrchr(_currentRom, '/') + 1;

	if (dotIndex == NULL || slashIndex == NULL)
	{
		return NATIVE_ERROR;
	}

	*dotIndex = '\0';

	sprintf(out, "%s/%s", dirName, slashIndex);

	*dotIndex = '.';

	return NATIVE_OK;
}


void Application::handleInput(const Sandstorm::Timer &timer)
{
     uint64_t special_action_to_execute = 0;
     _rewindTouched = false;

     // check player one key and touch input separately
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_A), BTN_A, TouchInput.isTouchButtonDown(_touchInputMapping.map["A"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["A"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_B), BTN_B, TouchInput.isTouchButtonDown(_touchInputMapping.map["B"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["B"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_X), BTN_X, TouchInput.isTouchButtonDown(_touchInputMapping.map["X"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["X"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_Y), BTN_Y, TouchInput.isTouchButtonDown(_touchInputMapping.map["Y"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Y"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_SELECT), BTN_SELECT, TouchInput.isTouchButtonDown(_touchInputMapping.map["Select"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Select"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_START), BTN_START, TouchInput.isTouchButtonDown(_touchInputMapping.map["Start"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Start"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_L), BTN_L, TouchInput.isTouchButtonDown(_touchInputMapping.map["L"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["L"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_R), BTN_R, TouchInput.isTouchButtonDown(_touchInputMapping.map["R"]) ||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["R"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_UP), BTN_UP, TouchInput.getAnalogY(0) < -TouchInput.getYSensitivity()||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Up"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_DOWN), BTN_DOWN, TouchInput.getAnalogY(0) > TouchInput.getYSensitivity()||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Down"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_LEFT), BTN_LEFT, TouchInput.getAnalogX(0) < -TouchInput.getXSensitivity()||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Left"]));
     S9xReportButton(0, MAKE_BUTTON(PAD_1, BTN_RIGHT), BTN_RIGHT, TouchInput.getAnalogX(0) > TouchInput.getXSensitivity()||
                                                TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Right"]));
     if (TouchInput.isTouchButtonDown(_touchInputMapping.map["Forward"]) ||
               TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Forward"]))
     {
          IPPU.RenderThisFrame = FALSE;
          for (int i = 0; i < TURBO_FRAMES_TO_EMULATE; i++)
          {
               S9xMainLoop();
          }
          IPPU.RenderThisFrame = TRUE;
     }
     if (_rewindEnabled && (
               TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Rewind"]) ||
               TouchInput.isTouchButtonDown(_touchInputMapping.map["Rewind"]) ))
     {
         void* tmp_buf = _rewindBuf;
         if (state_manager_pop(_rewindState, &tmp_buf))
         {
              //state_load((unsigned char*)tmp_buf, 0);
              _rewindTouched = true;
         }
     }
     if (TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Save"]))
     {
          saveState(_curState);
     }
     if (TouchInput.isKeyButtonDown(_keyInputMapping[0].map["Load"]))
     {
          loadState(_curState);
     }


     // check all other players
     for (int i = 1; i < _emuControllerCount; i++)
     {
          special_action_to_execute = 0;
          int padId = i+1;
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_A), BTN_A, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["A"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_B), BTN_B, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["B"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_X), BTN_X, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["X"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_Y), BTN_Y, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Y"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_SELECT), BTN_SELECT, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Select"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_START), BTN_START, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Start"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_L), BTN_L, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["L"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_R), BTN_R, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["R"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_UP), BTN_UP, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Up"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_DOWN), BTN_DOWN, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Down"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_LEFT), BTN_LEFT, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Left"]));
          S9xReportButton(i, MAKE_BUTTON(padId, BTN_RIGHT), BTN_RIGHT, TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Right"]));

          if (TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Save"]))
          {
               saveState(_curState);
          }
          if (TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Load"]))
          {
               loadState(_curState);
          }
          if (TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Forward"]))
          {
               int frameskip = _frameSkip;
               _frameSkip = 999;
               for (int i = 0; i < 120; i++)
               {
                    S9xMainLoop();
                    S9xClearSamples();
               }
               _frameSkip = frameskip;
          }
          if (_rewindEnabled && TouchInput.isKeyButtonDown(_keyInputMapping[i].map["Rewind"]))
          {
              void* tmp_buf = _rewindBuf;
              if (state_manager_pop(_rewindState, &tmp_buf))
              {
                   //state_load((unsigned char*)tmp_buf, 0);
                   _rewindTouched = true;
              }
          }
     }

     TouchInput.update();
}


void Application::update(const Sandstorm::Timer& timer)
{
     //LOGD("STEP");

     // calculate frameskipping
     static int fskipc = 0;
     fskipc=(fskipc+1)%(this->getFrameSkip()+1);
     IPPU.RenderThisFrame = TRUE;
     if (fskipc)
     {
          IPPU.RenderThisFrame = FALSE;
     }

     /*if (_rewindTouched)
     {
          _frameSkipCount = 0;
     }
     else
     {
          _frameSkipCount=(_frameSkipCount+1)%(_frameSkip+1);
     }*/

     S9xMainLoop();
     //processInput();

     /*if (_rewindEnabled && !_rewindTouched)
     {
          int state_size = state_save(_rewindBuf, 0);

          state_manager_push(_rewindState, _rewindBuf);
     }*/

    //LOGD("DONE STEP");
}


void Application::draw(const Sandstorm::Timer& timer)
{
	//LOGD("draw()");

    Graphics.Clear();

    Graphics.Draw();

    TouchInput.draw(Graphics);
}
