/*
 * This is the source code of tgnet library v. 1.1
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2015-2018.
 */

#include <memory.h>
#include <stdlib.h>
#include <string>
#include <fcntl.h>
#include "ConnectionsManager.h"
#include "NativeByteBuffer.h"
#include "BuffersStorage.h"
#include "ByteArray.h"

#ifdef ANDROID

#include <jni.h>

JavaVM *javaVm = nullptr;
JNIEnv *jniEnv[MAX_ACCOUNT_COUNT];
jclass jclass_ByteBuffer = nullptr;
jmethodID jclass_ByteBuffer_allocateDirect = nullptr;
#endif

static bool done = false;

//ConnectionsManager::ConnectionsManager(int32_t instance) {
//    instanceNum = instance;
//    pthread_mutex_init(&mutex, nullptr);
//}
//
//ConnectionsManager::~ConnectionsManager() {
//    pthread_mutex_destroy(&mutex);
//}

//ConnectionsManager &ConnectionsManager::getInstance(int32_t instanceNum) {
//    switch (instanceNum) {
//        case 0:
//            static ConnectionsManager instance0(0);
//            return instance0;
//        case 1:
//            static ConnectionsManager instance1(1);
//            return instance1;
//        case 2:
//            static ConnectionsManager instance2(2);
//            return instance2;
//        case 3:
//            static ConnectionsManager instance3(3);
//            return instance3;
//        case 4:
//        default:
//            static ConnectionsManager instance4(4);
//            return instance4;
//    }
//}

#ifdef ANDROID

void ConnectionsManager::useJavaVM(JavaVM *vm, bool useJavaByteBuffers) {
    javaVm = vm;
//    if (useJavaByteBuffers) {
//        JNIEnv *env = nullptr;
//        if (javaVm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
//
//            exit(1);
//        }
//        jclass_ByteBuffer = (jclass) env->NewGlobalRef(env->FindClass("java/nio/ByteBuffer"));
//        if (jclass_ByteBuffer == nullptr) {
//
//            exit(1);
//        }
//        jclass_ByteBuffer_allocateDirect = env->GetStaticMethodID(jclass_ByteBuffer,
//                                                                  "allocateDirect",
//                                                                  "(I)Ljava/nio/ByteBuffer;");
//        if (jclass_ByteBuffer_allocateDirect == nullptr) {
//
//            exit(1);
//        }
//
//    }
}

#endif
