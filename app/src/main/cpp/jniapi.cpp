//
// Created by icespring on 2019-05-08.
//

//
// Copyright 2011 Tero Saarni
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
#define __ANDROID_API__ 21

#include <stdint.h>
#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "logger.h"
#include "renderer.h"

#define LOG_TAG "EglSample"

static ANativeWindow *window = 0;
static Renderer *renderer = 0;

JNIEXPORT void JNICALL
nativeOnStart(JNIEnv *jenv,
              jobject obj) {
    LOG_INFO("nativeOnStart");
    renderer = new Renderer();
    return;
}

JNIEXPORT void JNICALL
nativeOnResume(JNIEnv *jenv,
               jobject obj) {
    LOG_INFO("nativeOnResume");
    renderer->start();
    return;
}

JNIEXPORT void JNICALL
nativeOnPause(JNIEnv *jenv,
              jobject obj) {
    LOG_INFO("nativeOnPause");
    renderer->stop();
    return;
}

JNIEXPORT void JNICALL
nativeOnStop(JNIEnv *jenv,
             jobject obj) {
    LOG_INFO("nativeOnStop");
    delete renderer;
    renderer = 0;
    return;
}

JNIEXPORT void JNICALL
nativeSetSurface(JNIEnv *jenv,
                 jobject obj,
                 jobject surface) {
    if (surface != 0) {
        window = ANativeWindow_fromSurface(jenv, surface);
        LOG_INFO("Got window %p", window);
        renderer->setWindow(window);
    } else {
        LOG_INFO("Releasing window");
        ANativeWindow_release(window);
    }

    return;
}

static JNINativeMethod method_table[] = {
        {"nativeOnStart",    "()V",                       (void *) nativeOnStart},
        {"nativeOnResume",   "()V",                       (void *) nativeOnResume},
        {"nativeOnPause",    "()V",                       (void *) nativeOnPause},
        {"nativeOnStop",     "()V",                       (void *) nativeOnStop},
        {"nativeSetSurface", "(Landroid/view/Surface;)V", (void *) nativeSetSurface},
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->AttachCurrentThread(&env, NULL) == JNI_OK) {
        jclass clazz = env->FindClass("yuan/icespring/openglesdemo/GLESlib");
        if (clazz == NULL) {
            return JNI_ERR;
        }

        if (env->RegisterNatives(clazz, method_table,
                                 sizeof(method_table) / sizeof(method_table[0])) == JNI_OK) {
            return JNI_VERSION_1_6;
        }
    }

    return JNI_ERR;
}
