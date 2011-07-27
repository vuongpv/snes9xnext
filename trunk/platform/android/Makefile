# fceuDroid Makefile 
# - for eclipse to auto build

all:
	ndk-build -j8

#NDK_DEBUG=1 -debug

clean:
	ndk-build clean
	
headers:
	javah -classpath bin ca.halsafar.snesdroid.Emulator
	mv ca_halsafar_snesdroid_Emulator.h ./jni/EmulatorBridge.h
	
install:
	adb install SNESDroidRelease.apk
	
uninstall:
	adb uninstall ca.halsafar.snesdroid
	
zipalign:
	rm -f SNESDroidRelease.apk
	zipalign -v 4 SNESDroid-unaligned.apk SNESDroidRelease.apk