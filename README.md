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

# Latest changes

1. Checked out jni-utils-rs and btleplug locally.
2. Opened jni-utils-rs in Android Studio and build libs at path "jni-utils-rs\target\debug\java\libs" with following commands.

  $ cargo build
  $ cd java
  $ ./gradlew build

3. Opened btleplug/src/droidplug/java in Android Studio. Updated build.gradle for above generated lib dependencies.

  dependencies {
    compile fileTree(dir: '/jni-utils-rs/target/debug/java/libs', include: ['*.jar'])
  }

4. Build -> Rebuild Project. AAR generated at 'btleplug\src\droidplug\java\build\outputs\aar\droidplug-debug.aar'
5. Placed above generated .aar at my android app location 'android\app\libs'
6. Added .aar file dependencies in build.gradle.

  implementation fileTree(include: ['*.aar'], dir: 'libs')

7. Run the app, clicked on scan button and still facing same error when trying to get the adapter.
