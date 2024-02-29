#include <jni.h>
#include "tgnet/BuffersStorage.h"
#include "tgnet/NativeByteBuffer.h"

JavaVM *java;
jlong getFreeBuffer(JNIEnv *env, jclass c, jint length) {
    return (jlong) (intptr_t) BuffersStorage::getInstance().getFreeBuffer((uint32_t) length);
}

jint limit(JNIEnv *env, jclass c, jlong address) {
    NativeByteBuffer *buffer = (NativeByteBuffer *) (intptr_t) address;
    return buffer->limit();
}

jint position(JNIEnv *env, jclass c, jlong address) {
    NativeByteBuffer *buffer = (NativeByteBuffer *) (intptr_t) address;
    return buffer->position();
}

void reuse(JNIEnv *env, jclass c, jlong address) {
    NativeByteBuffer *buffer = (NativeByteBuffer *) (intptr_t) address;
    buffer->reuse();
}

jobject getJavaByteBuffer(JNIEnv *env, jclass c, jlong address) {
    NativeByteBuffer *buffer = (NativeByteBuffer *) (intptr_t) address;
    if (buffer == nullptr) {
        return nullptr;
    }
    return buffer->getJavaByteBuffer();
}
static const char *NativeByteBufferClassPathName = "com/example/nativesqlite/tgnet/NativeByteBuffer";
static JNINativeMethod NativeByteBufferMethods[] = {
        {"native_getFreeBuffer", "(I)J", (void *) getFreeBuffer},
        {"native_limit", "(J)I", (void *) limit},
        {"native_position", "(J)I", (void *) position},
        {"native_reuse", "(J)V", (void *) reuse},
        {"native_getJavaByteBuffer", "(J)Ljava/nio/ByteBuffer;", (void *) getJavaByteBuffer}
};

inline int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods, int methodsCount) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, methodsCount) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

extern "C" int registerNativeTgNetFunctions(JavaVM *vm, JNIEnv *env) {
    java = vm;

    if (!registerNativeMethods(env, NativeByteBufferClassPathName, NativeByteBufferMethods, sizeof(NativeByteBufferMethods) / sizeof(NativeByteBufferMethods[0]))) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}



JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    jclass clazz;

    //JNI允许我们提供一个函数映射表，注册给Java虚拟机，这样JVM就可以用函数映射表来调用相应的函数。这样就可以不必通过函数名来查找需要调用的函数了
    //Java与JNI通过JNINativeMethod的结构来建立联系，它被定义在jni.h中
//    第一个变量name，代表的是Java中的函数名
//    第二个变量signature，代表的是Java中的参数和返回值
//    第三个变量fnPtr，代表的是的指向C函数的函数指针
    registerNativeTgNetFunctions(vm,env);
    return JNI_VERSION_1_6;
}