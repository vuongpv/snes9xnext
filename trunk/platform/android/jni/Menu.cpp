

#include <engine/engine.h>
#include <resources/resources.h>

#include "Menu.h"


int Menu::init()
{
	LOGD("init()");

	return NATIVE_OK;
}


void Menu::deinit()
{
	Sandstorm::RocketSystemInterface::getInstance().deinit();
}


CallResult Menu::onDeviceCreate()
{
	LOGD("onDeviceCreate()");

	return NATIVE_OK;
}


CallResult Menu::loadContent()
{
	LOGD("loadContent()");

     bool loadInternal = false;
#if (defined(ANDROID))
     loadInternal = true;
     file_open_apk();
#endif

     Sandstorm::ResourceManager::add<Sandstorm::Shader>("assets/Shaders/menu", "Shaders/menu", loadInternal, true);

 	CallResult retVal = Sandstorm::RocketSystemInterface::getInstance().init("Shaders/menu", Sandstorm::engine_getScreenInfo().w, Sandstorm::engine_getScreenInfo().h);
 	Sandstorm::RocketSystemInterface::getInstance().loadContext("default", "assets/rocket/demo.rml", Sandstorm::engine_getScreenInfo().w, Sandstorm::engine_getScreenInfo().h);


#if (defined(ANDROID))
     file_close_apk();
#endif

     return NATIVE_OK;
}


void Menu::onShutdown()
{

}


void Menu::handleInput(const Sandstorm::Timer &time)
{

}


void Menu::update(const Sandstorm::Timer &time)
{
	Sandstorm::RocketSystemInterface::getInstance().getContext("default")->Update();
}


void Menu::draw(const Sandstorm::Timer &time)
{
	//LOGD("draw()");

	Sandstorm::RocketSystemInterface::getInstance().getContext("default")->Render();
}
