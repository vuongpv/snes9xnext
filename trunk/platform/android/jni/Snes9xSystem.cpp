#include <string.h>
#include <unistd.h>

#include <audio/interface.h>

#include "Snes9xSystem.h"
#include "Application.h"

#include "Snes9xSystem.h"


namespace Base
{

// APK and EXTERNAL PATHS
extern char g_apkFileName[MAX_PATH_LENGTH];
extern char g_externalStoragePath[MAX_PATH_LENGTH];

}


namespace EmulatorBase
{
     // Directories
     extern char g_romsDir[MAX_PATH_LENGTH];
     extern char g_statesDir[MAX_PATH_LENGTH];
     extern char g_savesDir[MAX_PATH_LENGTH];
     extern char g_shadersDir[MAX_PATH_LENGTH];
     extern char g_cheatsDir[MAX_PATH_LENGTH];
     extern char g_tempDir[MAX_PATH_LENGTH];
     extern char g_configFilePath[MAX_PATH_LENGTH];
}


extern Application Emulator;

extern const char *S9xGetDirectory (uint32_t dirtype);

static const uint audioMaxFramesPerUpdate = (SandstormAudio::maxRate/49)*2;

void S9xAudioCallback()
{
   //size_t i;
   size_t samples = 0;
   uint frames = 0;
   // Just pick a big buffer. We won't use it all.
   static int16_t audioBuff[4096];

   S9xFinalizeSamples();
   samples = S9xGetSampleCount();
   frames = samples / 2;

   /*(static SandstormAudio::BufferContext *aBuff = 0;
   int16 *audioBuff = 0;
   if ((aBuff = SandstormAudio::getPlayBuffer(frames)))
   {
        audioBuff = (int16*)aBuff->data;
   }
   else
   {
        LOGD("FAIL GETTING PLAY BUFFER");
        return;
   }*/

   //int16 audioBuff[samples];
   S9xMixSamples(audioBuff, samples);

   //SandstormAudio::commitPlayBuffer(aBuff, frames);
   SandstormAudio::writePcm((unsigned char*)audioBuff, frames);

   //LOGD("Audio frames %d", frames);
}


/*void S9xParsePortConfig (ConfigFile &conf, int pass)
{

}
*/

void S9xExtraUsage (void)
{
}


void S9xParseArg (char **argv, int &i, int argc)
{
}


bool S9xPollAxis (uint32_t id, int16_t *value)
{
	return false;
}


bool S9xPollButton (uint32_t id, bool *pressed)
{
	return false;
}


bool S9xPollPointer (uint32_t id, int16_t *x, int16_t *y)
{
	return false;
}


void S9xHandlePortCommand (s9xcommand_t cmd, int16_t data1, int16_t data2)
{
}


const char *S9xStringInput (const char *message)
{
	return NULL;
}


bool8 S9xOpenSnapshotFile (const char *filepath, bool8 read_only, STREAM *file)
{
	if(read_only)
	{
		if((*file = OPEN_STREAM(filepath, "rb")) != 0)
		{
			return (TRUE);
		}
	}
	else
	{
		if((*file = OPEN_STREAM(filepath, "wb")) != 0)
		{
			return (TRUE);
		}
	}
	return (FALSE);
}


void S9xCloseSnapshotFile (STREAM file)
{
	CLOSE_STREAM (file);
}


void S9xExit (void)
{
}


bool8 S9xInitUpdate (void)
{
	return true;
}


static int s_lastWidth = 0;
void S9xDeinitUpdate (int width, int height)
{
     //LOGD("S9xDeinitUpdate(%d, %d)", width, height);
     if (width != s_lastWidth)
     {
          if (width == 512 || height == 448 || height == 478)
          {
               GFX.Pitch = 1024;
          }
          else
          {
               GFX.Pitch = 512;
          }

          s_lastWidth = width;
          Emulator.Graphics.ReshapeEmuTexture(width, height, SCREEN_RENDER_TEXTURE_WIDTH);
     }

	Emulator.Graphics.DrawEMU(GFX.Screen, width, height);
}


bool8 S9xContinueUpdate (int width, int height)
{
	return true;
}


void S9xMessage (int type, int number, char const *message)
{
	LOGD("[%d-%d]: %s", type, number, message);
}


bool8 S9xOpenSoundDevice (void)
{
	return true;
}


const char * S9xGetFilename (const char *ex, uint32_t dirtype)
{
     static char    s[PATH_MAX + 1];
     char      drive[_MAX_DRIVE + 1];
     char      dir[PATH_MAX + 1];
     char      fname[PATH_MAX + 1];
     char      ext[PATH_MAX + 1];

     _splitpath(Memory.ROMFilename, drive, dir, fname, ext);
     snprintf(s, PATH_MAX + 1, "%s%s%s%s", S9xGetDirectory(dirtype), SLASH_STR, fname, ex);

     return (s);
}


const char * S9xGetFilenameInc (const char *ex, enum s9x_getdirtype dirtype)
{
	static char	s[PATH_MAX + 1];
	char		drive[_MAX_DRIVE + 1];
	char		dir[PATH_MAX + 1];
	char		fname[PATH_MAX + 1];
	char		ext[PATH_MAX + 1];

	unsigned int	i = 0;
	const char	*d;

	_splitpath(Memory.ROMFilename, drive, dir, fname, ext);
	d = S9xGetDirectory(dirtype);

	snprintf(s, PATH_MAX + 1, "%s%s%s.%03d%s", d, SLASH_STR, fname, i++, ex);

	return (s);
}


const char * S9xBasename (const char *f)
{
	const char	*p;

	if ((p = strrchr(f, '/')) != NULL || (p = strrchr(f, '\\')) != NULL)
	{
		return (p + 1);
	}

	return (f);
}


const char *S9xGetDirectory (uint32_t dirtype)
{
	static char s[PATH_MAX + 1];

	switch (dirtype)
	{
		case SNAPSHOT_DIR:
			return EmulatorBase::g_statesDir;
			break;
		case CHEAT_DIR:
			return EmulatorBase::g_cheatsDir;
			break;
		case SRAM_DIR:
		     return EmulatorBase::g_savesDir;
		     break;
		case ROMFILENAME_DIR:
			strncpy(s, Memory.ROMFilename, PATH_MAX + 1);
			s[PATH_MAX] = 0;

			for (unsigned int i = strlen(s); i >= 0; --i)
			{
				if (s[i] == SLASH_CHAR)
				{
					s[i] = 0;
					break;
				}
			}

			return s;
			break;
		default:
			return Base::g_externalStoragePath;
	}
}


const char *S9xChooseFilename (bool8 read_only)
{
	static char	filename[PATH_MAX + 1];
	static char	drive[_MAX_DRIVE + 1];
	static char	dir[PATH_MAX + 1];
	static char	def[PATH_MAX + 1];
	static char	ext[PATH_MAX + 1];

	_splitpath(Memory.ROMFilename, drive, dir, def, ext);
	snprintf(filename, PATH_MAX + 1, "%s%s%s.%03d", S9xGetDirectory(SNAPSHOT_DIR), SLASH_STR, def, Emulator.getCurrentSaveSlot());

	LOGD("S9xChooseFilename result: %s", filename);

	return filename;
}


const char *S9xChooseMovieFilename (bool8 read_only)
{
	return NULL;
}


void S9xToggleSoundChannel (int c)
{
}


void S9xSetPalette (void)
{
}


void _splitpath (const char *path, char *drive, char *dir, char *fname, char *ext)
{
	*drive = 0;

	const char	*slash = strrchr(path, SLASH_CHAR);
	const char	*dot   = strrchr(path, '.');

	if (dot && slash && dot < slash)
	{
		dot = NULL;
	}

	if (!slash)
	{
		*dir = 0;

		strcpy(fname, path);

		if (dot)
		{
			fname[dot - path] = 0;
			strcpy(ext, dot + 1);
		}
		else
		{
			*ext = 0;
		}
	}
	else
	{
		strcpy(dir, path);
		dir[slash - path] = 0;

		strcpy(fname, slash + 1);

		if (dot)
		{
			fname[dot - slash - 1] = 0;
			strcpy(ext, dot + 1);
		}
		else
		{
			*ext = 0;
		}
	}
}

void _makepath (char *path, const char *, const char *dir, const char *fname, const char *ext)
{
	if (dir && *dir)
	{
		strcpy(path, dir);
		strcat(path, SLASH_STR);
	}
	else
	{
		*path = 0;
	}

	strcat(path, fname);

	if (ext && *ext)
	{
		strcat(path, ".");
		strcat(path, ext);
	}
}
