#![cfg(target_os = "android")]
#![allow(non_snake_case)]

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use rust_core::{core_test, core_scan, core_connect};

use btleplug::platform::*;

#[no_mangle]
pub extern "C" fn Java_com_example_mybleapp_MainActivity_hello(env: JNIEnv, _class: JClass, input: JString) -> jstring {
    // First, we have to get the string out of java. Check out the `strings`
    // module for more info on how this works.
    let input: String = env
        .get_string(input)
        .expect("Couldn't get java string!")
        .into();

    // Then we have to create a new java string to return. Again, more info
    // in the `strings` module.
    let output = env
        .new_string(format!("Hi {}", core_test(input)))
        .expect("Couldn't create java string!");
    // Finally, extract the raw pointer to return.
    output.into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_example_mybleapp_MainActivity_scan(env: JNIEnv, _class: JClass) -> jstring {

    let output = env
    .new_string(format!("{}", core_scan()))
    .expect("Couldn't create java string!");

    output.into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_example_mybleapp_MainActivity_connect(env: JNIEnv, _class: JClass, device: JString) -> jstring  {

    let device: String = env
    .get_string(device)
    .expect("Couldn't get java string!")
    .into();
    let output = env
        .new_string(format!("{}", core_connect(device)))
        .expect("Couldn't create java string!");

    output.into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_example_mybleapp_MainActivity_jnionload(env: JNIEnv) {
    btleplug::platform::init(&env).unwrap();
}