#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdio.h>
#define  __USE_GNU

int methodA(int a, int b);

int main(){

     int x = 0, i;
     x += methodA(1,2);

     printf("The result: %d\n", x);

     return x;
}

int methodA(int a, int b){

    int y = a+b;
    return y;
}

