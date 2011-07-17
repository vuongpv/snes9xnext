/**
 * GENPlusDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */
#include "unistd.h"

#include "Application.h"

#include "libzip/zip.h"

#include "snes9x.h"
#include "memmap.h"
#include "controls.h"
#include "apu/apu.h"
#include "snapshot.h"
#include "display.h"


Application::Application():
		Input(BUTTON_INDEX_COUNT, 1)
{
     _initialized = false;
     _romLoaded = false;
     _fceuInitialized = false;
     _audioInitialized = false;
     _ssize = 0;
     _sampleRate = 22050;
     _curState = 0;

     // paths
     _apkPath = (char*)malloc(MAX_PATH * sizeof(char));
     _stateDir = (char*)malloc(MAX_PATH * sizeof(char));
     _sramDir = (char*)malloc(MAX_PATH * sizeof(char));
     _currentRom = (char*)malloc(MAX_PATH * sizeof(char));

     _rewindState = NULL;
     _rewindBuf = NULL;

     GFX.Screen = NULL;
}


Application::~Application()
{
	LOGD("Application::Destructor()");

     destroy();
}


void Application::destroy()
{
     LOGD("Start Application.destroy()");

     // shutdown snes
     if (_romLoaded)
     {
          saveSRam(_currentRom);
     }
     S9xGraphicsDeinit();
     S9xDeinitAPU();
     Memory.Deinit();

     // free rewinder
     /*if (_rewindState != NULL)
     {
          state_manager_free(_rewindState);
          _rewindState = NULL;
     }*/

     // set to false
     _romLoaded = false;

     LOGD("Finished Application.destroy()");
}


void Application::setRewind(bool b)
{
     _rewindEnabled = b;
}


int Application::init(JNIEnv *env, const char * apkAbsolutePath, const char * externalStorageDir,
		const char * stateDir, const char * sramDir)
{
     LOGD("APK_PATH: %s", apkAbsolutePath);
     LOGD("EXTERNAL_STORAGE_DIR: %s", externalStorageDir);
     LOGD("STATE DIR: %s", stateDir);
     LOGD("SRAM DIR: %s", sramDir);

     // TODO: setup genesis directories

     // copy dirs for storage
     if (apkAbsolutePath == NULL || strlen(apkAbsolutePath) >= MAX_PATH)
     {
    	 return NATIVE_ERROR;
     }

     strcpy(_apkPath, apkAbsolutePath);

     if (stateDir == NULL || strlen(stateDir) >= MAX_PATH)
     {
    	 return NATIVE_ERROR;
     }

     strcpy(_stateDir, stateDir);

     if (sramDir == NULL || strlen(sramDir) >= MAX_PATH)
     {
    	 return NATIVE_ERROR;
     }

     strcpy(_sramDir, sramDir);


     if (!_fceuInitialized)
     {
          __builtin_memset(&Settings, 0, sizeof(Settings));

          Settings.MouseMaster = false;
          Settings.SuperScopeMaster = false;
          Settings.JustifierMaster = false;
          Settings.MultiPlayer5Master = false;
          Settings.FrameTimePAL = 20000;
          Settings.FrameTimeNTSC = 16667;
          Settings.SixteenBitSound = true;
          Settings.Stereo = true;
          Settings.SoundPlaybackRate = 22050 * 1.08;
          Settings.SoundInputRate = 32000;
          Settings.SupportHiRes = true;
          Settings.Transparency = true;
          Settings.AutoDisplayMessages = true;
          Settings.InitialInfoStringTimeout = 120;
          Settings.HDMATimingHack = 100;
          Settings.BlockInvalidVRAMAccessMaster = true;

          Settings.OpenGLEnable = true;

          //Settings.DisplayFrameRate = true;

          _fceuInitialized = true;
     }

     _initialized = true;

     return NATIVE_OK;
}


int Application::initAudioBuffers(const int sizeInSamples)
{
     LOGD("initAudioBuffers(%d)", sizeInSamples);


     _audioBufferSize = sizeInSamples;
     _audioInitialized = true;

     return NATIVE_OK;
}


int Application::initGraphics()
{
     Graphics.Init();

     Graphics.InitEmuShader(NULL, NULL);
     Graphics.Clear();

     LOGI("Loading APK %s", _apkPath);
     zip* APKArchive = zip_open(_apkPath, 0, NULL);
     if (APKArchive == NULL)
     {
          LOGE("Error loading APK");
          return NATIVE_ERROR;
     }

     int dPadTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/DirectionalPad.png", &dPadTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int startTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/StartButton.png", &startTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int selectTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/SelectButton.png", &selectTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int xTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/ButtonX.png", &xTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int yTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/ButtonY.png", &yTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }


     int lTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/ButtonL.png", &lTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }


     int rTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/ButtonR.png", &rTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }


     int bTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/ButtonB.png", &bTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int aTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/ButtonA.png", &aTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int rewindTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/Rewind.png", &rewindTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     int fastForwardTex = 0;
     if (Graphics.loadPNGTexture(APKArchive, "assets/Textures/FastForward.png", &fastForwardTex) != GRAPHICS_OK)
     {
          return NATIVE_ERROR;
     }

     zip_close(APKArchive);

     Input.setAnalogTexture(0, dPadTex);
     Input.setButtonTexture(BUTTON_INDEX_X, xTex);
     Input.setButtonTexture(BUTTON_INDEX_Y, yTex);
     Input.setButtonTexture(BUTTON_INDEX_L, lTex);
     Input.setButtonTexture(BUTTON_INDEX_R, rTex);
     Input.setButtonTexture(BUTTON_INDEX_B, bTex);
     Input.setButtonTexture(BUTTON_INDEX_A, aTex);
     Input.setButtonTexture(BUTTON_INDEX_START, startTex);
     Input.setButtonTexture(BUTTON_INDEX_SELECT, selectTex);
     Input.setButtonTexture(BUTTON_INDEX_REWIND, rewindTex);
     Input.setButtonTexture(BUTTON_INDEX_FORWARD, fastForwardTex);


     return NATIVE_OK;
}


void Application::setAudioSampleRate(int rate)
{
	bool hardReset = false;
	if (_sampleRate != rate)
	{
		hardReset = true;
	}

	_sampleRate = rate;

	if (_romLoaded && hardReset)
	{
		// TODO: wont catch auto saves
		loadROM(_currentRom);
	}
}


void Application::setAudioEnabled(const bool b)
{
     if (b)
     {


          _audioInitialized = true;
     }
     else
     {


          _audioInitialized = false;
     }
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
          saveSRam(_sramDir);

          _ssize = 0;
	     /*Memory.Deinit();
	     S9xDeinitAPU();
	     S9xGraphicsDeinit();

		_romLoaded = false;*/
	}

	// init
	if (_romLoaded)
	{
	     Memory.Deinit();
	}
     Memory.Init();

     //if (_romLoaded)
     {
          S9xDeinitAPU();
     }
     S9xInitAPU();

     if (!S9xInitSound(64, 0))
     {
          LOGE("S9xInitSound() Failed!");
          return NATIVE_ERROR;
     }
     S9xSetRenderPixelFormat(RGB565);

     // load ROM file
     if (!Memory.LoadROM(filename))
     {
          LOGE("LoadROM(%s) FAILED", filename);
          return NATIVE_ERROR;
     }

     // load sram
     loadSRam(_currentRom);

     // init graphics
     if (GFX.Screen != NULL)
     {
          free(GFX.Screen);
     }
     GFX.Pitch = 1024;
     GFX.Screen = (uint16_t *)malloc(1024 * 478);
     memset(GFX.Screen, 0, 1024*478);
     if (_romLoaded)
     {
          S9xGraphicsDeinit();
     }

     if (!S9xGraphicsInit())
     {
          LOGE("S9xGraphicsInit() Failed!");
          return NATIVE_ERROR;
     }

     Graphics.ReshapeEmuTexture(256, 224);

   	// store current rom
   	strcpy(_currentRom, filename);

   	// controllers options
   	S9xSetController(0, CTL_JOYPAD, 0, 0, 0, 0);
     #define MAKE_BUTTON(pad, btn) (((pad)<<4)|(btn))
     #define MAP_BUTTON(id, name) S9xMapButton((id), S9xGetCommandT((name)), false)
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_A), "Joypad1 A");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_B), "Joypad1 B");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_X), "Joypad1 X");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_Y), "Joypad1 Y");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_SELECT), "Joypad1 Select");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_START), "Joypad1 Start");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_L), "Joypad1 L");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_R), "Joypad1 R");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_LEFT), "Joypad1 Left");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_RIGHT), "Joypad1 Right");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_UP), "Joypad1 Up");
     MAP_BUTTON(MAKE_BUTTON(PAD_1, BTN_DOWN), "Joypad1 Down");

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
     S9xFreezeGame(S9xChooseFilename(FALSE));
}


void Application::loadState(const int i)
{
     S9xUnfreezeGame(S9xChooseFilename(TRUE));
}


void Application::selectState(const int i)
{
     _curState = i;
}


void Application::loadSRam(const char* romName)
{
     Memory.LoadSRAM(S9xGetFilename(".srm", SRAM_DIR));
}


void Application::saveSRam(const char* romName)
{
     Memory.SaveSRAM(S9xGetFilename(".srm", SRAM_DIR));
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


// consumer, java driven
size_t Application::readAudio(jshort* in)
{
     if (_ssize >= 0 && _ssize <= SB_SIZE)
     {
          memcpy(in, (int16_t*)_audioBuffer, _ssize * sizeof(short));
     }

     return _ssize;
}


void Application::processInput()
{
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_A), Input.isButtonDown(BUTTON_INDEX_A));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_B), Input.isButtonDown(BUTTON_INDEX_B));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_X), Input.isButtonDown(BUTTON_INDEX_X));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_Y), Input.isButtonDown(BUTTON_INDEX_Y));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_SELECT), Input.isButtonDown(BUTTON_INDEX_SELECT));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_START), Input.isButtonDown(BUTTON_INDEX_START));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_L), Input.isButtonDown(BUTTON_INDEX_L));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_R), Input.isButtonDown(BUTTON_INDEX_R));
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_UP), Input.isButtonDown(BUTTON_INDEX_UP) ||
                                                 Input.getAnalogY(0) < -Input.getYSensitivity());
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_DOWN), Input.isButtonDown(BUTTON_INDEX_DOWN) ||
                                                  Input.getAnalogY(0) > Input.getYSensitivity());
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_LEFT), Input.isButtonDown(BUTTON_INDEX_LEFT) ||
                                                  Input.getAnalogX(0) < -Input.getXSensitivity());
     S9xReportButton(MAKE_BUTTON(PAD_1, BTN_RIGHT), Input.isButtonDown(BUTTON_INDEX_RIGHT) ||
                                                  Input.getAnalogX(0) > Input.getXSensitivity());


     if (Input.isButtonDown(BUTTON_INDEX_FORWARD))
     {
          //_frameSkipCount=(_frameSkipCount+1)%(18+1);
          int frameskip = _frameSkip;
          _frameSkip = 999;
          for (int i = 0; i < TURBO_FRAMES_TO_EMULATE; i++)
          {
               if (S9xSyncSound())
               {
                    S9xMainLoop();
               }
          }
          _frameSkip = frameskip;
     }

 /*    _rewindTouched = false;
     if (Input.isButtonDown(BUTTON_INDEX_REWIND))
     {
          void* tmp_buf = _rewindBuf;
          if (state_manager_pop(_rewindState, &tmp_buf))
          {
               state_load((unsigned char*)tmp_buf, 0);
               //usleep(60000);

               _rewindTouched = true;
          }
     }
     */
     Input.update();
}


void Application::step(const JNIEnv *env)
{
     // quick exit if no rom loaded to step
     /*if (!_romLoaded)
     {
          return;
     }*/

     processInput();

     // calculate frameskipping
     /*if (_rewindTouched)
     {
          _frameSkipCount = 0;
     }
     else
     {
          _frameSkipCount=(_frameSkipCount+1)%(_frameSkip+1);
     }*/

     // calc framerate
     /*_timeStart = _timeEnd;
     _timeEnd = now_ms();
     _timeDelta = (_timeEnd - _timeStart);
     FCEUI_printf("FPS: %g", _timeDelta);
     */

     _ssize = S9xGetSampleCount();
     //LOGD("Samples: %d", _ssize);

     S9xMixSamples(_audioBuffer,  _ssize);

     // emulation step
     if (S9xSyncSound())
     {
          S9xMainLoop();
     }

     /*if (_rewindEnabled && !_rewindTouched)
     {
          int state_size = state_save(_rewindBuf, 0);

          state_manager_push(_rewindState, _rewindBuf);
     }*/

    //LOGD("DONE STEP");
}


void Application::draw(const JNIEnv* env)
{
    Graphics.Clear();

    Graphics.Draw();

    Input.draw(Graphics);
}
