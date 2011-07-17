#
# SNESDROID
# Copyright 2011 Stephen Damm (Halsafar) 
# All rights reserved.
# shinhalsafar@gmail.com
#

LOCAL_PATH := $(call my-dir)
ORG_PATH := $(LOCAL_PATH)

# BUILD ALL EXTERNAL LIBS
include $(CLEAR_VARS)
include $(call all-makefiles-under,$(LOCAL_PATH))


# BUILD EMU LIB
include $(CLEAR_VARS)
LOCAL_PATH := $(ORG_PATH)/

LOCAL_MODULE_TAGS := user

LOCAL_ARM_MODE := arm

LOCAL_MODULE := libemu

# Snes9x API DIR
SNES9X_API_DIR := ../../../src

# SNES9x Source 				
LOCAL_SRC_FILES := $(SNES9X_API_DIR)/tile.cpp \
     				$(SNES9X_API_DIR)/cpu.cpp \
                    $(SNES9X_API_DIR)/dma.cpp \
                    $(SNES9X_API_DIR)/ppu.cpp \
                    $(SNES9X_API_DIR)/sa1.cpp \
                    $(SNES9X_API_DIR)/sa1cpu.cpp \
                    $(SNES9X_API_DIR)/gfx.cpp \
                    $(SNES9X_API_DIR)/clip.cpp \
                    $(SNES9X_API_DIR)/screenshot.cpp \
                    $(SNES9X_API_DIR)/fxdbg.cpp \
                    $(SNES9X_API_DIR)/fxemu.cpp \
                    $(SNES9X_API_DIR)/fxinst.cpp \
                    $(SNES9X_API_DIR)/cpuexec.cpp \
                    $(SNES9X_API_DIR)/cpuops.cpp \
                    $(SNES9X_API_DIR)/srtc.cpp \
                    $(SNES9X_API_DIR)/memmap.cpp \
                    $(SNES9X_API_DIR)/apu/apu.cpp \
                    $(SNES9X_API_DIR)/apu/SNES_SPC.cpp \
					$(SNES9X_API_DIR)/apu/SNES_SPC_misc.cpp \
					$(SNES9X_API_DIR)/apu/SNES_SPC_state.cpp \
					$(SNES9X_API_DIR)/apu/SPC_DSP.cpp \
					$(SNES9X_API_DIR)/apu/SPC_Filter.cpp \
                    $(SNES9X_API_DIR)/bsx.cpp \
                    $(SNES9X_API_DIR)/c4.cpp \
                    $(SNES9X_API_DIR)/c4emu.cpp \
                    $(SNES9X_API_DIR)/cheats.cpp \
                    $(SNES9X_API_DIR)/cheats2.cpp \
                    $(SNES9X_API_DIR)/conffile.cpp \
                    $(SNES9X_API_DIR)/controls.cpp \
                    $(SNES9X_API_DIR)/crosshairs.cpp \
                    $(SNES9X_API_DIR)/dsp.cpp \
                    $(SNES9X_API_DIR)/dsp1.cpp \
                    $(SNES9X_API_DIR)/dsp2.cpp \
                    $(SNES9X_API_DIR)/dsp3.cpp \
                    $(SNES9X_API_DIR)/dsp4.cpp \
                    $(SNES9X_API_DIR)/globals.cpp \
                    $(SNES9X_API_DIR)/loadzip.cpp \
                    $(SNES9X_API_DIR)/netplay.cpp \
                    $(SNES9X_API_DIR)/obc1.cpp \
                    $(SNES9X_API_DIR)/reader.cpp \
                    $(SNES9X_API_DIR)/sdd1.cpp \
                    $(SNES9X_API_DIR)/sdd1emu.cpp \
                    $(SNES9X_API_DIR)/server.cpp \
                    $(SNES9X_API_DIR)/seta.cpp \
                    $(SNES9X_API_DIR)/seta010.cpp \
                    $(SNES9X_API_DIR)/seta011.cpp \
                    $(SNES9X_API_DIR)/seta018.cpp \
                    $(SNES9X_API_DIR)/snapshot.cpp \
                    $(SNES9X_API_DIR)/logger.cpp \
                    $(SNES9X_API_DIR)/movie.cpp \
                    $(SNES9X_API_DIR)/snes9x.cpp \
                    $(SNES9X_API_DIR)/spc7110.cpp				
			
#                     $(foreach dir,$(SNES9X_API_DIR)/jma/,$(wildcard $(dir)/*.cpp))

# Emulator Engine source files that we will compile.
LOCAL_SRC_FILES += \
				EmulatorBridge.cpp \
				GraphicsDriver.cpp \
				Application.cpp \
				InputHandler.cpp \
				Quad.cpp \
				Snes9xSystem.cpp
			
				
# bluetooth
 # Bluetooth.cpp
	                  
# Debug
#LOCAL_CFLAGS += -g

# Compiler flags.
LOCAL_CFLAGS := -I. \
				-I$(LOCAL_PATH)/$(SNES9X_API_DIR) \
				-I$(LOCAL_PATH)/$(SNES9X_API_DIR) \
				-I$(LOCAL_PATH)/$(SNES9X_API_DIR)
				
				#-D_STLP_HAS_WCHAR_T -D_GLIBCXX_USE_WCHAR_T
	
# Optimization flags
LOCAL_CFLAGS += -O3 \
				-ffast-math \
				-fomit-frame-pointer \
				-fvisibility=hidden \
				-fno-rtti \
				-funit-at-a-time \
				-finline-functions \
				-ftree-vectorize \
				-fsingle-precision-constant \
				-mfpu=neon \
				-mfloat-abi=softfp
				#-funroll-loops \
				--param inline-unit-growth=1000 \
				--param large-function-growth=5000 \
				--param max-inline-insns-single=2450 \
				

			
# Compiler Warning flags
#LOCAL_CFLAGS += -Winline
LOCAL_CFLAGS += -Wno-write-strings

# Custom Emulator Flags
LOCAL_CFLAGS += -DHALDROID -DANDROID $(MACHDEP)

# FCEU Flags
LOCAL_CFLAGS += -DLSB_FIRST -DUSE_OPENGL

# FCEU Enable Lua 
#LOCAL_CFLAGS += -D_S9XLUA_H -D_USE_FCEU_LUA_ENGINE

# Copy C Flags to CXX flags
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS)
LOCAL_CXXFLAGS := $(LOCAL_CFLAGS)
     

# Native libs to link to
LOCAL_LDLIBS := -lz -llog -lGLESv2
#-lbluedroid

# All of the shared libraries we link against.
LOCAL_SHARED_LIBRARIES := libzip libgnupng 

                   
# Static libraries.              
LOCAL_STATIC_LIBRARIES :=   

# Don't prelink this library.  For more efficient code, you may want
# to add this library to the prelink map and set this to true. However,
# it's difficult to do this for applications that are not supplied as
# part of a system image.
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)


