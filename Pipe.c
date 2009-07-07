#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

// the stuff for the pipe
int fd[2];

JNIEXPORT jint JNICALL
Java_Pipe_createPipe ( JNIEnv* env, jobject obj ) 
{
  pipe(fd);
  return 0;
}


JNIEXPORT jint JNICALL
Java_Pipe_writePipe ( JNIEnv* env, jobject obj ) 
{
  close(fd[0]);
  //printf("About to write\n");
  write(fd[1], "Writing to pipe\n", 16);
  //printf("Wrote to pipe\n");
  return 0;
}


JNIEXPORT jint JNICALL
Java_Pipe_readPipe ( JNIEnv* env, jobject obj ) 
{
  close(fd[1]);
  //printf("About to read\n");
  char buf[100];
  int n = read(fd[0], buf, 100);
  //printf("The value of n is %d\n", n);
  //printf("The value in the buffer is %s\n", buf);
  return n;
}


