#S9XZSNESFX=1
#S9XZSNESC4=1
#S9XDEBUGGER=1
#S9XNETPLAY=1
S9XZIP=1
S9XJMA=1

# Fairly good and special-char-safe descriptor of the os being built on.
OS         = `uname -s -r -m|sed \"s/ /-/g\"|tr \"[A-Z]\" \"[a-z]\"|tr \"/()\" \"___\"`
BUILDDIR   = .
SNES9X_DIR = ../../src
OBJS_DIR   = $(SNES9X_DIR)

OBJECTS    = $(OBJS_DIR)/apu/apu.o $(OBJS_DIR)/apu/SNES_SPC.o $(OBJS_DIR)/apu/SNES_SPC_misc.o $(OBJS_DIR)/apu/SNES_SPC_state.o $(OBJS_DIR)/apu/SPC_DSP.o $(OBJS_DIR)/apu/SPC_Filter.o $(OBJS_DIR)/bsx.o $(OBJS_DIR)/cheats.o $(OBJS_DIR)/cheats2.o $(OBJS_DIR)/clip.o $(OBJS_DIR)/conffile.o $(OBJS_DIR)/controls.o $(OBJS_DIR)/cpu.o $(OBJS_DIR)/cpuexec.o $(OBJS_DIR)/cpuops.o $(OBJS_DIR)/crosshairs.o $(OBJS_DIR)/dma.o $(OBJS_DIR)/dsp.o $(OBJS_DIR)/dsp1.o $(OBJS_DIR)/dsp2.o $(OBJS_DIR)/dsp3.o $(OBJS_DIR)/dsp4.o $(OBJS_DIR)/gfx.o $(OBJS_DIR)/globals.o $(OBJS_DIR)/logger.o $(OBJS_DIR)/memmap.o $(OBJS_DIR)/movie.o $(OBJS_DIR)/obc1.o $(OBJS_DIR)/ppu.o $(OBJS_DIR)/reader.o $(OBJS_DIR)/sa1.o $(OBJS_DIR)/sa1cpu.o $(OBJS_DIR)/screenshot.o $(OBJS_DIR)/sdd1.o $(OBJS_DIR)/sdd1emu.o $(OBJS_DIR)/seta.o $(OBJS_DIR)/seta010.o $(OBJS_DIR)/seta011.o $(OBJS_DIR)/seta018.o $(OBJS_DIR)/snapshot.o $(OBJS_DIR)/snes9x.o $(OBJS_DIR)/spc7110.o $(OBJS_DIR)/srtc.o $(OBJS_DIR)/tile.o $(OBJS_DIR)/filter/2xsai.o $(OBJS_DIR)/filter/blit.o $(OBJS_DIR)/filter/epx.o $(OBJS_DIR)/filter/hq2x.o $(OBJS_DIR)/filter/snes_ntsc.o unix.o x11.o
DEFS       = -DMITSHM -DCPU_SHUTDOWN -DSPC700_SHUTDOWN -DCORRECT_VRAM_READS -DDONT_USE_EXE_MULTI

ifdef S9XZSNESFX
OBJECTS   += $(OBJS_DIR)/i386/fxemu2.o $(OBJS_DIR)/i386/fxemu2b.o $(OBJS_DIR)/i386/fxemu2c.o $(OBJS_DIR)/i386/fxtable.o $(OBJS_DIR)/i386/sfxproc.o $(OBJS_DIR)/i386/zsnes.o
else
OBJECTS   += $(OBJS_DIR)/fxinst.o $(OBJS_DIR)/fxemu.o
ifdef S9XDEBUGGER
OBJECTS   += $(OBJS_DIR)/fxdbg.o
endif
endif

ifdef S9XZSNESC4
OBJECTS   += $(OBJS_DIR)/i386/c4.o $(OBJS_DIR)/i386/zsnesc4.o $(OBJS_DIR)/c4.o
else
OBJECTS   += $(OBJS_DIR)/c4.o $(OBJS_DIR)/c4emu.o
endif

ifdef S9XDEBUGGER
OBJECTS   += $(OBJS_DIR)/debug.o
endif

ifdef S9XNETPLAY
OBJECTS   += $(OBJS_DIR)/netplay.o $(OBJS_DIR)/server.o
endif

ifdef S9XZIP
OBJECTS   += $(OBJS_DIR)/loadzip.o $(OBJS_DIR)/unzip/ioapi.o $(OBJS_DIR)/unzip/unzip.o
endif

ifdef S9XJMA
OBJECTS   += $(OBJS_DIR)/jma/7zlzma.o $(OBJS_DIR)/jma/crc32.o $(OBJS_DIR)/jma/iiostrm.o $(OBJS_DIR)/jma/inbyte.o $(OBJS_DIR)/jma/jma.o $(OBJS_DIR)/jma/lzma.o $(OBJS_DIR)/jma/lzmadec.o $(OBJS_DIR)/jma/s9x-jma.o $(OBJS_DIR)/jma/winout.o
endif

CCC        = g++
CC         = gcc
NASM       = 
GASM       = g++
INCLUDES   = -I. -I$(SNES9X_DIR) -I$(SNES9X_DIR)/apu/ -I$(SNES9X_DIR)/unzip/ -I$(SNES9X_DIR)/jma/ -I$(SNES9X_DIR)/filter/

CCFLAGS    = -O3 -fomit-frame-pointer -fno-exceptions -fno-rtti -pedantic -Wall -W -Wno-unused-parameter  -DJOYSTICK_SUPPORT -DZLIB -DUNZIP_SUPPORT -DJMA_SUPPORT -DHAVE_LIBPNG -DHAVE_MKSTEMP -DHAVE_STRINGS_H -DHAVE_SYS_IOCTL_H -DHAVE_STDINT_H -DRIGHTSHIFT_IS_SAR -DUSE_THREADS $(DEFS)
CFLAGS     = $(CCFLAGS)

.SUFFIXES: .o .cpp .c .cc .h .m .i .s .asm .obj

all: Makefile configure snes9x

Makefile: configure Makefile.in
	@echo "Makefile is older than configure or in-file. Run configure or touch Makefile."
	exit 1

configure: configure.ac
	@echo "configure is older than in-file. Run autoconf or touch configure."
	exit 1

snes9x: $(OBJECTS)
	$(CCC) $(INCLUDES) -o $@ $(OBJECTS) -lm -lz -lpng -lSM -lICE -lX11 -lXext -lpthread

../jma/s9x-jma.o: $(SNES9X_DIR)/jma/s9x-jma.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/7zlzma.o: $(SNES9X_DIR)/jma/7zlzma.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/crc32.o: $(SNES9X_DIR)/jma/crc32.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/iiostrm.o: $(SNES9X_DIR)/jma/iiostrm.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/inbyte.o: $(SNES9X_DIR)/jma/inbyte.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/jma.o: $(SNES9X_DIR)/jma/jma.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/lzma.o: $(SNES9X_DIR)/jma/lzma.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/lzmadec.o: $(SNES9X_DIR)/jma/lzmadec.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@
../jma/winout.o: $(SNES9X_DIR)/jma/winout.cpp
	$(CCC) $(INCLUDES) -c $(CCFLAGS) -fexceptions $*.cpp -o $@

.cpp.o:
	$(CCC) $(INCLUDES) -c $(CCFLAGS) $*.cpp -o $@

.c.o:
	$(CC) $(INCLUDES) -c $(CCFLAGS) $*.c -o $@

.cpp.S:
	$(GASM) $(INCLUDES) -S $(CCFLAGS) $*.cpp -o $@

.cpp.i:
	$(GASM) $(INCLUDES) -E $(CCFLAGS) $*.cpp -o $@

.S.o:
	$(GASM) $(INCLUDES) -c $(CCFLAGS) $*.S -o $@

.S.i:
	$(GASM) $(INCLUDES) -c -E $(CCFLAGS) $*.S -o $@

.s.o:
	@echo Compiling $*.s
	sh-elf-as -little $*.s -o $@

.asm.o:
	$(NASM) -f elf -DELF -DJOYSTICK_SUPPORT -DZLIB -DUNZIP_SUPPORT -DJMA_SUPPORT -DHAVE_LIBPNG -DHAVE_MKSTEMP -DHAVE_STRINGS_H -DHAVE_SYS_IOCTL_H -DHAVE_STDINT_H -DRIGHTSHIFT_IS_SAR -DUSE_THREADS -i$(SNES9X_DIR)/i386/ -o $@ $*.asm

.obj.o:
	cp $*.obj $*.o

clean:
	rm -f $(OBJECTS)
