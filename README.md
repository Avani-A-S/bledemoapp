# bledemoapp
Demo app using btleplug for BLE communication for android

# Directory Structure
android - 
Demo app with 2 buttons. On clicking scan it will call function from rust library to start scanning bluethooth devices.

rust-android -
Using jni to build dependencies.

rust-core -
My rust library that uses btleplug for implementing BLE communication related functions.

scripts/install-android.bat will build the libraries and copy the SO files for android.
