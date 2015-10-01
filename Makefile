PACKAGE = de.markusfisch.android.wavelines
APK = WaveLinesWallpaper/build/outputs/apk/WaveLinesWallpaper-debug.apk

all: apk install

apk:
	./gradlew build

install:
	adb $(TARGET) install -rk $(APK)

uninstall:
	adb $(TARGET) uninstall $(PACKAGE)

images:
	svg/update.sh

clean:
	./gradlew clean
