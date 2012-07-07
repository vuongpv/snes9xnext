/**
 * GENPlusDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 */

#include <png.h>

#include <util/common.h>
#include <libemu/GraphicsDriver.h>
#include <libemu/TouchInputHandler.h>
#include <engine/IState.h>
#include <timer/timer.h>
#include "Snes9xSystem.h"

extern "C"
{
#include "rewind.h"
}

#include <stdint.h>

// fast forward frame skip count
#define TURBO_FRAMES_TO_EMULATE 32

// emulator screen max
#define SNES_RENDER_TEXTURE_WIDTH  256.0
#define SNES_RENDER_TEXTURE_HEIGHT 224.0
#define SCREEN_RENDER_TEXTURE_WIDTH  512.0
#define SCREEN_RENDER_TEXTURE_HEIGHT 512.0
#define SCREEN_RENDER_BYTE_BY_PIXEL  4

// default paths
#define PATH_ROMS "/roms/"
#define PATH_STATES "/states/"
#define PATH_SAVES "/roms/"
#define PATH_CHEATS "/cheats/"
#define PATH_SHADERS  "/shaders/"
#define PATH_TEMP "/temp/"

#define CONFIG_FILE_NAME "/config.xml"

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
#define PAD_2 2
#define PAD_3 3
#define PAD_4 4
#define PAD_5 5
#define PAD_MAX 5

#define BUTTON_INDEX_COUNT 16
#define TOUCH_BUTTON_COUNT 10


//#define MAKE_BUTTON(pad, btn) (((pad)<<4)|(btn))
#define MAKE_BUTTON(pad, btn)  (((pad)<<6)+btn)
#define MAP_BUTTON(id, name) S9xMapButton((id), S9xGetCommandT((name)))

#define joypad_string(padno, string) "Joypad"#padno" "#string""

#define map_snes9x_standard_controls(padno) \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_A), joypad_string(padno, A)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_B), joypad_string(padno, B)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_X), joypad_string(padno, X)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_Y), joypad_string(padno, Y)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_SELECT), joypad_string(padno, Select)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_START), joypad_string(padno, Start)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_L), joypad_string(padno, L)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_R), joypad_string(padno, R)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_LEFT), joypad_string(padno, Left)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_RIGHT), joypad_string(padno, Right)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_UP), joypad_string(padno, Up)); \
   MAP_BUTTON(MAKE_BUTTON(padno, BTN_DOWN), joypad_string(padno, Down));

extern s9xcommand_t keymap[1024];
extern uint16_t joypad[8];
#define S9xReportButton(i, id, action, pressed) \
      /*if(keymap[id].type == S9xButtonJoypad)*/ \
      joypad[i] = ((joypad[i] | keymap[id].commandunion.button.joypad) & (((pressed) | -(pressed)) >> 31)) | ((joypad[i] & ~keymap[id].commandunion.button.joypad) & ~(((pressed) | -(pressed)) >> 31)); \
      /*else if(pressed) \
         special_action_to_execute = action*/;

enum ControllerType
{
     TWO_JOYSTICKS,
     MULTITAP
};

#include <unordered_map>
// "A" : keycode for A key
struct InputMapping
{
     std::unordered_map<std::string, int> map;
};

using namespace SandstormEmu;

class Application  : public Sandstorm::IState
{
public:
     Application();
     ~Application();

     // GAME STATE
     int init();
     void deinit();

     CallResult onDeviceCreate();
     CallResult loadContent();

     void onShutdown();

     void handleInput(const Sandstorm::Timer &time);
     void update(const Sandstorm::Timer &time);
     void draw(const Sandstorm::Timer &time);

     // EMULATOR
     void initEmulator();

     void buildConfigFile();
     void resetConfig();
     void resetInputConfig();
     void processConfig();
     void processGraphicsConfig();

     int loadROM(const char* filename);
     void closeROM();
     void resetROM();

     void saveSRam();
     void loadSRam();
     void saveState(const int i);
     void loadState(const int i);
     void selectState(const int i);

     uint32_t getCurrentSaveSlot() { return _curState; }

     int getFrameSkip()
     {
          return _frameSkip;
     }

     bool isRomLoaded() { return _romLoaded; }
     bool isInitialized() { return _initialized; }

     GraphicsDriver Graphics;
     TouchInputHandler TouchInput;
private:
     // init vars
     bool _initialized;
     bool _romLoaded;
     bool _emuInitialized;

     // input mappings
     InputMapping _touchInputMapping;
     InputMapping _keyInputMapping[PAD_MAX];
     int _emuControllerCount;
     ControllerType _controllerType;

     // timestamps
     double _timeStart;
     double _timeEnd;
     double _timeDelta;

     // screen info
     float _screenWidth;
     float _screenHeight;
     int _screenType;

     // frameskip counters
     int _frameSkip;
     //int _frameSkipCount;

     // hold paths
     char* _currentRom;

     // state save/load
     uint32_t _curState;

     // audio
     int _sampleRate;

     // rewinding
     bool _rewindTouched;
     bool _rewindEnabled;
     uint8_t *_rewindBuf;
     state_manager_t* _rewindState;

     int makePath(const char* dirName, char* outBuffer);
};
