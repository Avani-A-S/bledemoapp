@echo off

:: Build the Rust core lib
cd ..\rust-core
cargo clean
cargo build

:: Building the Rust library for Android
cd ..\rust-android
cargo clean
if exist "./target" rd /s /q "./target"
cargo build --target aarch64-linux-android --release
cargo build --target armv7-linux-androideabi --release
cargo build --target i686-linux-android --release
cargo build --target x86_64-linux-android --release

:: Including the Rust library into the Android app
set "JNI_LIBS=.\android\app\src\main\jniLibs"
set "JNI_LIBS_ARM64_V8A=%JNI_LIBS%\arm64-v8a"
set "JNI_LIBS_ARMEABI_V7A=%JNI_LIBS%\armeabi-v7a"
set "JNI_LIBS_X68=%JNI_LIBS%\x86"
set "JNI_LIBS_X86_64=%JNI_LIBS%\x86_64"
set "JNI_LIBS_AARCH64=%JNI_LIBS%\aarch64"

cd ..
if exist "%JNI_LIBS%" rd /s /q "%JNI_LIBS%"
md "%JNI_LIBS%"
md "%JNI_LIBS_ARM64_V8A%"
md "%JNI_LIBS_ARMEABI_V7A%"
md "%JNI_LIBS_X68%"
md "%JNI_LIBS_X86_64%"
md "%JNI_LIBS_AARCH64%"

copy ".\rust-android\target\aarch64-linux-android\release\*.so" "%JNI_LIBS_ARM64_V8A%"
copy ".\rust-android\target\aarch64-linux-android\release\*.so" "%JNI_LIBS_AARCH64%"
copy ".\rust-android\target\armv7-linux-androideabi\release\*.so" "%JNI_LIBS_ARMEABI_V7A%"
copy ".\rust-android\target\i686-linux-android\release\*.so" "%JNI_LIBS_X68%"
copy ".\rust-android\target\x86_64-linux-android\release\*.so" "%JNI_LIBS_X86_64%"
