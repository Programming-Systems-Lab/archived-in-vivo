#include <jni.h>
#include <stdio.h>
#include "helloNative.h"

JNIEXPORT jint JNICALL
Java_Forker_doFork ( JNIEnv* env, jobject obj ) 
{
  // fork this process and get the id
  jint pid = fork();
  
  // printf("pid %i\n", pid);
  
  // the child will have a pid of 0, but the parent's is unchanged
  return pid;

}


JNIEXPORT void JNICALL
Java_Forker_doExit ( JNIEnv* env, jobject obj ) 
{
  //printf("Buh bye\n");
  exit(0);

}


