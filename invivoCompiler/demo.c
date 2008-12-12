#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#define  __USE_GNU

int methodA(int a, int b);
int methodB(int b);
int methodC(int c);

int main()
{

	struct timeval startTime, endTime, result;	
	gettimeofday(&startTime, NULL);


	int x = 0, i;


	x += methodA(1, 2);
	x += methodB(2);
	x += methodC(3);

	for(i = 0; i < 10000; i++)
		methodA(1,2);

	printf("output: %d\n", x);

	gettimeofday(&endTime, NULL);
	timeval_subtract(&result, &endTime, &startTime);
	printf("Total Running Time: %d\n", result.tv_sec*1000000 + result.tv_usec);

	return 0;
}

int methodA(int a, int b)
{
	int x, retVal;
	for(x = 0; x < 10000; x++)
	{
		retVal += x % a;
	}
	return retVal;
}

int methodB(int b)
{
	int retVal = b * 2;
	return retVal;
}

int methodC(int c)
{
	int retVal = c * 3;
	return retVal;
}

