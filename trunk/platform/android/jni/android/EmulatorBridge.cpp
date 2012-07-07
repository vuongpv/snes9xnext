/**
 * GENPlusDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 *
 */
#include <stdlib.h>
#include <math.h>
#include <assert.h>

#include <android/log.h>

#if !defined(_WIN32) && defined(__GNUC__) && __GNUC__ > 3
#undef JNIEXPORT
#define JNIEXPORT __attribute__ ((visibility("default")))
#endif

#include <compression/compression.h>
#include <util/common.h>
#include <util/basicString.h>
#include <audio/interface.h>
#include <file.h>
#include <libemu/GraphicsDriver.h>
#include <engine/engine.h>
#include <input/input.h>
#include "Application.h"
#include "Menu.h"
#include "EmulatorBridge.h"

Application Emulator;

namespace Base
{

// JNI JVM handlers
JavaVM* g_jVM = 0;
JNIEnv* g_jEnv = 0;

// APK and EXTERNAL PATHS
char g_apkFileName[MAX_PATH_LENGTH];
char g_externalStoragePath[MAX_PATH_LENGTH];

}

namespace EmulatorBase
{
     extern char g_romsDir[MAX_PATH_LENGTH];
     extern char g_statesDir[MAX_PATH_LENGTH];
     extern char g_savesDir[MAX_PATH_LENGTH];
     extern char g_shadersDir[MAX_PATH_LENGTH];
     extern char g_cheatsDir[MAX_PATH_LENGTH];
     extern char g_tempDir[MAX_PATH_LENGTH];
     extern char g_configFilePath[MAX_PATH_LENGTH];

     extern void loadPaths();
}


CLINK JNIEXPORT jint JNICALL LVISIBLE JNI_OnLoad(JavaVM *vm, void*)
{
     LOGD("JNI_OnLoad()");

     // store important jni communication vars
     Base::g_jVM = vm;
     int getEnvRet = vm->GetEnv((void**) &Base::g_jEnv, JNI_VERSION_1_6);

     if (getEnvRet != JNI_OK)
     {
          LOGD("JNI ERROR - Could not receive g_jEnv pointer!");
          exit(-1);
     }

     return JNI_VERSION_1_6;
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_destroy
  (JNIEnv *, jclass)
{
     LOGD("destroy()");

     SandstormAudio::closePcm();

     Sandstorm::engine_deinit();
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_init
  	  	  (JNIEnv* env, jclass cls,
		  jstring apkAbsolutePath,
		  jstring externalStoragePath)
{
     LOGD("Emulator_init()");

     // store APK path
     jboolean isCopy;
     const char * szFilename = env->GetStringUTFChars(apkAbsolutePath, &isCopy);
     memset(Base::g_apkFileName, 0, MAX_PATH_SIZE);
     strcpy(Base::g_apkFileName, szFilename);
     env->ReleaseStringUTFChars(apkAbsolutePath, szFilename);

     const char * szExternalStorage = env->GetStringUTFChars(externalStoragePath, &isCopy);
     memset(Base::g_externalStoragePath, 0, MAX_PATH_SIZE);
     strcpy(Base::g_externalStoragePath, szExternalStorage);
     env->ReleaseStringUTFChars(externalStoragePath, szExternalStorage);

     LOGD("Emulator_init_paths(%s, %s)", Base::g_apkFileName, Base::g_externalStoragePath);

     Sandstorm::input_init(0, 10, 0);
     SandstormAudio::init();

     // init the application
     int retVal = Emulator.init();

     if (!Sandstorm::engine_hasState("main"))
     {
          LOGD("Adding Emulator state to engine");
          Sandstorm::engine_addState("main", &Emulator);
     }
     else
     {
          LOGD("Emulator state already added");
     }

     if (!Sandstorm::engine_hasState("a"))
     {
          LOGD("Adding menu state to engine");
          //Sandstorm::engine_addState("a", new Menu());
     }
     else
     {
          LOGD("Menu state already added");
     }

	return retVal;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_initGraphics
  (JNIEnv* env, jclass cls, jint width, jint height, jint screenType)
{
     Sandstorm::ScreenPacket packet;
     packet.x = 0;
     packet.y = 0;
     packet.w = width;
     packet.h = height;
     packet.screenType = screenType;

     Sandstorm::engine_initGraphics(packet);
     Sandstorm::engine_loadContent();
     return NATIVE_OK;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_resetInputConfig
  (JNIEnv* env, jclass cls)
{
     Emulator.resetInputConfig();

     return NATIVE_OK;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_resetConfig
  (JNIEnv* env, jclass cls)
{
     Emulator.buildConfigFile();
     Emulator.resetConfig();
     Emulator.resetInputConfig();
     EmulatorBase::loadPaths();

     return NATIVE_OK;
}



JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_processConfig
  (JNIEnv* env, jclass cls)
{
     Emulator.processConfig();

     return NATIVE_OK;
}


JNIEXPORT jstring JNICALL Java_ca_halsafar_snesdroid_Emulator_getConfigFileName
  (JNIEnv* env, jclass cls)
{
     return env->NewStringUTF(EmulatorBase::g_configFilePath);

     return NATIVE_OK;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_processGraphicsConfig
  (JNIEnv* env, jclass cls)
{
     Emulator.processGraphicsConfig();

     return NATIVE_OK;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_loadRom
  (JNIEnv *env, jclass obj, jstring fileName)
{
     LOGD("Emulator_loadROM()");

     jboolean isCopy;
     const char * szFilename = env->GetStringUTFChars(fileName, &isCopy);
     int retVal = Emulator.loadROM(szFilename);
     env->ReleaseStringUTFChars(fileName, szFilename);

     return retVal;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_onPause
  (JNIEnv* env, jclass)
{
     LOGD("Emulator_onPause()");

     SandstormAudio::pausePcm();

     Emulator.saveSRam();

     return NATIVE_OK;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_onResume
  (JNIEnv* env, jclass)
{
     LOGD("Emulator_onResume()");

     SandstormAudio::startPcm();

     return NATIVE_OK;
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_saveState
  (JNIEnv *, jclass, jint i)
{
     Emulator.saveState(i);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_loadState
  (JNIEnv *, jclass, jint i)
{
     Emulator.loadState(i);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_selectState
  (JNIEnv *, jclass, jint i)
{
     LOGD("selectState(%d)", i);
     Emulator.selectState(i);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_resetGame
  (JNIEnv *, jclass)
{
	Emulator.resetROM();
}


JNIEXPORT jboolean JNICALL Java_ca_halsafar_snesdroid_Emulator_isRomLoaded
  (JNIEnv *, jclass)
{
     return Emulator.isRomLoaded();
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_parse7Zip
  (JNIEnv* env, jclass, jstring zipFileName, jobjectArray outDirs, jobjectArray outFiles)
{
     jboolean isCopy;
     const char * szZipFile = env->GetStringUTFChars(zipFileName, &isCopy);

     LOGD("Emulator_parse7Zip(%s)", szZipFile);

     int listCount = 0;
     zip_file_t* files;

     SzParse(szZipFile, &files, &listCount);

     //
     int dirCount = 0;
     int fileCount = 0;
     jclass cls = env->FindClass("ca/halsafar/filechooser/ZipFileType");
     for (int i = 0; i < listCount; i++)
     {
          if (files[i].type == FILE_TYPES::Dir)
          {
               env->SetObjectArrayElement(outDirs,dirCount,env->NewStringUTF(files[i].name));
               dirCount++;
          }
          else
          {
               jmethodID cid = env->GetMethodID(cls,"<init>","()V");
               jobject object = env->NewObject(cls, cid);
               jclass clazz = env->GetObjectClass(object);
               jfieldID fid = env->GetFieldID(clazz,"name","Ljava/lang/String;");
               env->SetObjectField(object,fid,env->NewStringUTF(files[i].name));

               fid = env->GetFieldID(clazz,"pos","I");
               env->SetIntField(object,fid,files[i].pos);

               fid = env->GetFieldID(clazz,"size","I");
               env->SetIntField(object,fid,files[i].length);

               env->SetObjectArrayElement(outFiles,fileCount,object);
               fileCount++;
          }

          SAFE_FREE(files[i].name);
     }

     SAFE_FREE(files);

     env->ReleaseStringUTFChars(zipFileName, szZipFile);
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_unzip7ZFile
  (JNIEnv* env, jclass, jstring zipFileName, jint id, jstring extractFileName, jstring outLocation)
{
     LOGD("unzip7ZFile()");
     jboolean isCopy;
     const char * szZipFile = env->GetStringUTFChars(zipFileName, &isCopy);
     const char * szExtractFile = env->GetStringUTFChars(extractFileName, &isCopy);
     const char * szOutLocation = env->GetStringUTFChars(outLocation, &isCopy);

     LOGD("unzip7ZFile(%s, %s, %s)", szZipFile, szExtractFile, szOutLocation);

     // 16 MB buffer, large!
     unsigned char* fileBuffer = (unsigned char*)malloc(sizeof(unsigned char)* 0x1000000);
     int fileSize = SzExtractFile(szZipFile, id, fileBuffer);
     if (fileSize <= 0)
     {
          LOGD("unzip7ZFile() Failed!");
          SAFE_FREE(fileBuffer);
          return NATIVE_ERROR;
     }
     FILE* outFile = fopen(szOutLocation, "w");
     fwrite(fileBuffer, 1, fileSize, outFile);
     fclose(outFile);

     SAFE_FREE(fileBuffer);
     LOGD("unzip7ZFile SUCCESS");

     env->ReleaseStringUTFChars(zipFileName, szZipFile);
     env->ReleaseStringUTFChars(extractFileName, szExtractFile);
     env->ReleaseStringUTFChars(outLocation, szOutLocation);

     return NATIVE_OK;
}


JNIEXPORT jint JNICALL Java_ca_halsafar_snesdroid_Emulator_unzipFile
  (JNIEnv* env, jclass, jstring zipFileName, jstring extractFileName, jstring outLocation)
{
     jboolean isCopy;
     const char * szZipFile = env->GetStringUTFChars(zipFileName, &isCopy);
     const char * szExtractFile = env->GetStringUTFChars(extractFileName, &isCopy);
     const char * szOutLocation = env->GetStringUTFChars(outLocation, &isCopy);

     LOGD("unzipFile(%s, %s, %s)", szZipFile, szExtractFile, szOutLocation);

     char tmp[8092];
     size_t read = 0;

     FILE* outFile = fopen(szOutLocation, "w");
     zip* zipArchive = zip_open(szZipFile, 0, NULL);
     struct zip_file* file = zip_fopen(zipArchive, szExtractFile, 0);
     read = zip_fread(file, tmp, sizeof(tmp));
     while (read > 0)
     {
          fwrite(tmp, sizeof(char), read, outFile);
          read = zip_fread(file, tmp, sizeof(tmp));
     }

     zip_fclose(file);
     fclose(outFile);
     zip_close(zipArchive);

     env->ReleaseStringUTFChars(zipFileName, szZipFile);
     env->ReleaseStringUTFChars(extractFileName, szExtractFile);
     env->ReleaseStringUTFChars(outLocation, szOutLocation);

     return NATIVE_OK;
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_onTouchUp
  (JNIEnv* env, jclass cls, jint finger, jfloat x, jfloat y, jfloat radius)
{
     TouchEvent event;
     event.finger = finger;
     event.x = x;
     event.y = y;

     Emulator.TouchInput.onTouchUp(event);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_onTouchDown
  (JNIEnv* env, jclass cls, jint finger, jfloat x, jfloat y, jfloat radius)
{
     TouchEvent event;
     event.finger = finger;
     event.x = x;
     event.y = y;
     event.radius = radius;

     Emulator.TouchInput.onTouchDown(event);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_onTouchMove
  (JNIEnv* env, jclass cls, jint finger, jfloat x, jfloat y, jfloat radius)
{
     TouchEvent event;
     event.finger = finger;
     event.x = x;
     event.y = y;
     event.radius = radius;

     Emulator.TouchInput.onTouchMove(event);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_onKeyDown
 (JNIEnv *, jclass, jint keycode)
{
     Emulator.TouchInput.onKeyDown(keycode);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_onKeyUp
 (JNIEnv *, jclass, jint keycode)
{
     Emulator.TouchInput.onKeyUp(keycode);
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_step
  (JNIEnv* env, jclass obj)
{
	Sandstorm::engine_step();
}


JNIEXPORT void JNICALL Java_ca_halsafar_snesdroid_Emulator_draw
  (JNIEnv* env, jclass obj)
{
     Sandstorm::engine_draw();
}

