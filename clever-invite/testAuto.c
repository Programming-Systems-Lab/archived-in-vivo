#include <stdio.h>

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

