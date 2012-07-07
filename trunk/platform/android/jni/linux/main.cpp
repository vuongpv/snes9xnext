/**
 * Tester
 *
 * Stephen Damm
 */


// C includes
#include <stdio.h>
#include <unistd.h>

#include <util/basicGraphics.h>

#include <GL/glfw.h>

// Sandstorm Library Includes
#include <util/common.h>
#include <engine/engine.h>
#include <input/input.h>
#include <audio/interface.h>
#include <gui/RocketSystemInterface.h>
#include "Application.h"
#include "Menu.h"

#include "main.h"

// default resolution
#define RESOLUTION_X	800
#define RESOLUTION_Y	600


// window variables
int g_windowHandle = 0;

// running vars
bool g_isRunning = false;

// pre processor
int openWindow(bool fullscreen);


// linux platform additions to sandstorm namespace
namespace Sandstorm
{
	bool g_isFullScreen = false;

	void shutdown()
	{
		glfwCloseWindow();
		g_isRunning = false;
	}

	void toggleFullScreen()
	{
		LOGD("toggleFullScreen()");

		g_isFullScreen = !g_isFullScreen;

		//glfwTerminate();
		glfwCloseWindow();

		openWindow(g_isFullScreen);

		input_resetStates();
		engine_unloadContent();

		engine_loadContent();
	}
}


Application Emulator;


namespace Base
{

// APK and EXTERNAL PATHS
char g_apkFileName[MAX_PATH_LENGTH];
char g_externalStoragePath[MAX_PATH_LENGTH];

}


namespace EmulatorBase
{
	extern void loadPaths();
}


//---------------------------------------------------------------------------
// GLFW CALLBACKS
//---------------------------------------------------------------------------
void GLFWCALL glfwResize(int w, int h)
{
	Sandstorm::ScreenPacket packet;
	packet.w = w;
	packet.h = h;
	packet.x = 0;
	packet.y = 0;
	packet.screenType = 0;

	Emulator.loadROM("/home/halsafar/devel/personal/snesdroid/roms/Test.smc");

	Sandstorm::engine_initGraphics(packet);
    Sandstorm::engine_loadContent();
}


int GLFWCALL glfwWindowClosed()
{
	LOGD("glfwWindowClosed()");

	return GL_TRUE;
}


void GLFWCALL glfwKeyboard (int key, int state)
{
	if (state)
	{
		Sandstorm::input_onKeyDown(key);
	}
	else
	{
		Sandstorm::input_onKeyUp(key);
	}
}


void GLFWCALL glfwMouseInput(int button, int state)
{
	int translatedButton = button;
	switch (button)
	{
	case GLFW_MOUSE_BUTTON_LEFT:
		translatedButton = Sandstorm::MOUSE_LEFT;
		break;
	case GLFW_MOUSE_BUTTON_RIGHT:
		translatedButton = Sandstorm::MOUSE_RIGHT;
		break;
	case GLFW_MOUSE_BUTTON_MIDDLE:
		translatedButton = Sandstorm::MOUSE_MIDDLE;
		break;
	default:
		return;
	}

	if (state)
	{
		Sandstorm::input_onMouseDown(translatedButton);
		Sandstorm::RocketSystemInterface::getInstance().onMouseDown(translatedButton);
	}
	else
	{
		Sandstorm::input_onMouseUp(translatedButton);
		Sandstorm::RocketSystemInterface::getInstance().onMouseUp(translatedButton);
	}
}


void GLFWCALL glfwMouseMove(int x, int y)
{
	Sandstorm::input_onMouseMove(x, y);
	Sandstorm::RocketSystemInterface::getInstance().onMouseMove(x, y);
}


void GLFWCALL glfwMouseWheel(int wheel)
{
	//LOGD("Mouse Wheel: %d", wheel);
}


void mainLoop()
{
	g_isRunning = true;
	while (g_isRunning)
	{
		Sandstorm::engine_step();
		Sandstorm::engine_draw();
		//Emulator.step();
		//Emulator.draw();

		glfwSwapInterval(1);
		glfwSwapBuffers();

		g_isRunning = g_isRunning && glfwGetWindowParam( GLFW_OPENED );
	}
}


int openWindow(bool fullscreen)
{
	unsigned long windowFlag = (fullscreen ? GLFW_FULLSCREEN : GLFW_WINDOW);

	// load GLFW
	if (glfwInit() != GL_TRUE)
	{
		fprintf(stderr, "ERROR: Could not initialize GLFW\n");
		return EXIT_FAILURE;
	}

	// set core profile OpenGL 3.3+
	//glfwOpenWindowHint(GLFW_OPENGL_VERSION_MAJOR, 3); // Set OpenGL version to 3.3+
	//glfwOpenWindowHint(GLFW_OPENGL_VERSION_MINOR, 3);
	//glfwOpenWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Request core profile
	//glfwOpenWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // Disable legacy

	// Initialize the window & create
	if (glfwOpenWindow(RESOLUTION_X, RESOLUTION_Y,
			8, 8, 8, 8, // RGBA bits
			16, 0, // depth, stencil bits
			windowFlag) != GL_TRUE)
	{
		fprintf(stderr, "ERROR: Could not open window\n");
		glfwTerminate();
		return EXIT_FAILURE;
	}

	glfwSetWindowTitle(APP_TITLE);

	// Set up callbacks
	glfwSetKeyCallback(glfwKeyboard);
	glfwSetMousePosCallback(glfwMouseMove);
	glfwSetMouseButtonCallback(glfwMouseInput);
	glfwSetMouseWheelCallback(glfwMouseWheel);

	glfwSetWindowSizeCallback(glfwResize);
	glfwSetWindowCloseCallback(glfwWindowClosed);

	return 0;
}


int main(int argc, char** argv)
{
	LOGD("WELCOME TO SNESDROID");

	SandstormAudio::init();

	// init non graphics engine stuff
	Sandstorm::input_init(512, 5, 1);

	/// Init the engine
	Sandstorm::engine_init();

	// add our states
    memset(Base::g_externalStoragePath, 0, MAX_PATH_SIZE);
    strcpy(Base::g_externalStoragePath, "./");

	Emulator.init();
	Emulator.buildConfigFile();
	Emulator.resetConfig();
	Emulator.resetInputConfig();
	EmulatorBase::loadPaths();

	Menu* menu = new Menu();
	menu->init();

	Sandstorm::engine_addState("a", menu);
	Sandstorm::engine_addState("main", &Emulator);


	// open glfw window
	if (openWindow(false) != 0)
	{
		LOGE("Cannot open GLFW Window()");
		return EXIT_FAILURE;
	}

	// Init GLEW
#if (defined(WIN32))
	GLenum GlewInitResult;
	GlewInitResult = glewInit();

	if (GLEW_OK != GlewInitResult)
	{
		LOGE("ERROR: %s\n", glewGetErrorString(GlewInitResult));
		exit(EXIT_FAILURE);
	}
#endif

	// Report success
	LOGI("INFO: OpenGL Version: %s\n", glGetString(GL_VERSION));

	// init graphics stuff
	//Sandstorm::engine_loadContent();

	// enter main loop
	mainLoop();

	// unload content, deinit engine
	Sandstorm::engine_unloadContent();
	Sandstorm::engine_deinit();
	Sandstorm::input_destroy();

	return EXIT_SUCCESS;
}
