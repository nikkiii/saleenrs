#ifdef _WIN32
#include <windows.h>
#include <conio.h>
#else
# include <unistd.h>
#endif

#include <stdio.h>
#include <jni.h>
#include "Console.h"

JNIEXPORT void JNICALL
Java_org_saleen_util_log_Console_setTitleWindows(JNIEnv* env, jclass c, jstring s) {
    const jbyte *str;
    str = (*env)->GetStringUTFChars(env, s, NULL);

    SetConsoleTitle(str);

    (*env)->ReleaseStringUTFChars(env, s, str);
};