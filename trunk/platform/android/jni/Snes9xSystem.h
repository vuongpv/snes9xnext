/*
 * Snes9xImplementation.h
 *
 *  Created on: 2011-05-23
 *      Author: halsafar
 */

#ifndef SNES9XIMPLEMENTATION_H_
#define SNES9XIMPLEMENTATION_H_

extern "C" {
     #include "snes9x.h"
     #include "memmap.h"
     #include "cpuexec.h"
     #include "apu.h"
     #include "ppu.h"
     #include "controls.h"
     #include "cheats.h"
     #include "display.h"
     #include "snapshot.h"
     #include "port.h"
}

extern const char * S9xGetFilename (const char * in, uint32_t dirtype);
void S9xAudioCallback();

#endif /* SNES9XIMPLEMENTATION_H_ */
