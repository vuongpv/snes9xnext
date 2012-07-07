#ifndef __MENU_H
#define __MENU_H


#include <util/common.h>
#include <libemu/GraphicsDriver.h>
#include <libemu/TouchInputHandler.h>
#include <engine/IState.h>
#include <timer/timer.h>
#include <gui/RocketSystemInterface.h>


class Menu : public Sandstorm::IState
{
public:
    // ISTATE INTERFACE
    int init();
    void deinit();

    CallResult onDeviceCreate();
    CallResult loadContent();

    void onShutdown();

    void handleInput(const Sandstorm::Timer &time);
    void update(const Sandstorm::Timer &time);
    void draw(const Sandstorm::Timer &time);


private:
};

#endif
