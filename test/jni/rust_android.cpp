#include <string.h>
#include "rust_android.h"

JNIEXPORT jstring JNICALL Java_com_example_mybleapp_MainActivity_hello(JNIEnv *env, jobject obj, jstring input)
{
	return input;
}
