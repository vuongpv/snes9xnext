/**
 * GENPlusDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */

#include <jni.h>

#include "libpng/png.h"

#include "common.h"
#include "GraphicsDriver.h"
#include "InputHandler.h"

extern "C"
{
#include "rewind.h"
}

#include <stdint.h>

#define TURBO_FRAMES_TO_EMULATE 16

#define SNES_RENDER_TEXTURE_WIDTH  256.0
#define SNES_RENDER_TEXTURE_HEIGHT 224.0
#define SCREEN_RENDER_TEXTURE_WIDTH  512.0
#define SCREEN_RENDER_TEXTURE_HEIGHT 512.0
#define SCREEN_RENDER_BYTE_BY_PIXEL  4

// SNES buttons
#define BTN_A 1
#define BTN_B 2
#define BTN_X 3
#define BTN_Y 4
#define BTN_SELECT 5
#define BTN_START 6
#define BTN_L 7
#define BTN_R 8
#define BTN_LEFT 9
#define BTN_RIGHT 10
#define BTN_UP 11
#define BTN_DOWN 12
#define BTN_QUICKSAVE 13
#define BTN_QUICKLOAD 14

#define PAD_1 1

enum Buttons
{
     BUTTON_INDEX_B = 0,
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
     BUTTON_INDEX_COUNT
};


class Application
{
public:
     Application();
     ~Application();

     int init(JNIEnv *env, const char* apkPath);
     int initGraphics();
     int initAudioBuffers(const int sizeInSamples);
     void setAudioSampleRate(int rate);
     void destroy();

     int setPaths(const char* externalStorageDir, const char* romDir,
                          const char* stateDir, const char* sramDir,
                          const char* cheatsDir);

     void processInput();

     int loadROM(const char* filename);
     void closeROM();
     void resetROM();

     void saveSRam(const char* romName);
     void loadSRam(const char* romName);
     void saveState(const int i);
     void loadState(const int i);
     void selectState(const int i);

     void step(const JNIEnv *env);
     void draw(const JNIEnv *env);

     void setAudioEnabled(const bool b);
     int getCurEmuSamples() { return _ssize; }
     unsigned char* getAudioBuffer() { return _audioBuffer; }
     size_t readAudio(jshort* in);
     size_t getAudioBufferSize() { return _audioBufferSize; }

     uint_t getCurrentSaveSlot() { return _curState; }

     void setFrameSkip(int i)
     {
          if (i < 0) i = 0;
          if (i > 9) i = 9;

          _frameSkip = i;
     }

     int getFrameSkip()
     {
          return _frameSkip;
     }

     void setRewind(bool b);

     bool isRomLoaded() { return _romLoaded; }
     bool isInitialized() { return _initialized; }
     bool isAudioInit() { return _audioInitialized; }

     GraphicsDriver Graphics;
     InputHandler Input;

     jshort* AudioBuf;
private:
     bool _initialized;
     bool _romLoaded;
     bool _fceuInitialized;
     bool _audioInitialized;

     double _timeStart;
     double _timeEnd;
     double _timeDelta;

     int _frameSkip;
     //int _frameSkipCount;

     char* _apkPath;
     char* _stateDir;
     char* _sramDir;
     char* _currentRom;

     uint_t _curState;

     // video
     int _viewPortW;
     int _viewPortH;

     // audio
     int32_t _ssize;
     int _sampleRate;
     size_t _audioBufferSize;
     #define SB_SIZE 2048*2
     unsigned char _audioBuffer[SB_SIZE];

     // rewinding
     bool _rewindTouched;
     bool _rewindEnabled;
     uint8_t *_rewindBuf;
     state_manager_t* _rewindState;

     int makePath(const char* dirName, char* outBuffer);
};
