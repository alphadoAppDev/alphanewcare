//
// Created by dodo on 2019-08-03.
//

#ifndef URSDETECTOR_ANDROID_URSDEFINE_H
#define URSDETECTOR_ANDROID_URSDEFINE_H

#include <jni.h>
#include <android/log.h>

#ifndef DEBUG_MODE
#define DEBUG_MODE 1
#endif

#define LOG_TAG "URSDetector"
#define LOGV(...) if (DEBUG_MODE) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGI(...) if (DEBUG_MODE) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) if (DEBUG_MODE) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGW(...) if (DEBUG_MODE) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) if (DEBUG_MODE) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif //URSDETECTOR_ANDROID_URSDEFINE_H
