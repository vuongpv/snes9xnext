APP_BUILD_SCRIPT = $(APP_PROJECT_PATH)/Android.mk

#APP_OPTIM := debug
APP_OPTIM := release

APP_STL := gnustl_static
#APP_STL := stlport_static

TARGET_PLATFORM := android-8

APP_ABI := armeabi armeabi-v7a

#APP_CPPFLAGS += -fexceptions 

compile-s-source = $(eval $(call ev-compile-c-source,$1,$(1:%.s=%.o)))

#JNI_H_INCLUDE = $(APP_PROJECT_PATH)/../common/libnativehelper/include/