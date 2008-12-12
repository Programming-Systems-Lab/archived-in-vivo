#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdio.h>
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

int _methodA(int a, int b)
{
	int x, retVal;
	for(x = 0; x < 10000; x++)
	{
		retVal += x % a;
	}
	return retVal;
}


void preMethodA(int a, int b)
{

	int x = 1+1;

	printf("preMethodA\n");

}


void postMethodA(int a, int b, int retVal)
{

	int x = 2+2;

	printf("postMethodA\n");

}


int methodA(int a, int b)
{
	
				if (should_run_test("methodA", .1) == 0) 
				{ 
					struct timeval startTime, endTime, result;	
					gettimeofday(&startTime, NULL);
					
					increaseTests();
					pthread_t thread;
		
					int mypipe[2];
					pipe(mypipe);
		
					pthread_create(&thread, NULL, (void*)getThread(), (void *) mypipe[0]);
		
					int pid = fork();
					
					if (pid == 0) 
					{ 
						setAffinity(1);
						preMethodA(a, b); 
						close(mypipe[0]);
						write_to_pipe(mypipe[1]); 
						exit(0); 
					} 
					else 
					{ 					
						close(mypipe[1]);
						signal(SIGCHLD, SIG_IGN); 
					}
					
					gettimeofday(&endTime, NULL);
					timeval_subtract(&result, &endTime, &startTime);
					updateTotalTestTime(result);
				}
	int retVal = _methodA(a, b);
	
				if (should_run_test("methodA", .5) == 0) 
				{ 
					struct timeval startTime, endTime, result;	
					gettimeofday(&startTime, NULL);
					
					increaseTests();
					pthread_t thread;
		
					int mypipe[2];
					pipe(mypipe);
		
					pthread_create(&thread, NULL, (void*)getThread(), (void *) mypipe[0]);
		
					int pid = fork();
					
					if (pid == 0) 
					{ 
						setAffinity(1);
						postMethodA(a, b, retVal); 
						close(mypipe[0]);
						write_to_pipe(mypipe[1]); 
						exit(0); 
					} 
					else 
					{ 					
						close(mypipe[1]);
						signal(SIGCHLD, SIG_IGN); 
					}
					
					gettimeofday(&endTime, NULL);
					timeval_subtract(&result, &endTime, &startTime);
					updateTotalTestTime(result);
				}
	 return retVal;
}

int _methodB(int b)
{
	int retVal = b * 2;
	return retVal;
}


void preMethodB(int b)
{

	int x = 3+3;

	printf("preMethodB\n");

}


int methodB(int b)
{
	
				if (should_run_test("methodB", 1.0) == 0) 
				{ 
					struct timeval startTime, endTime, result;	
					gettimeofday(&startTime, NULL);
					
					increaseTests();
					pthread_t thread;
		
					int mypipe[2];
					pipe(mypipe);
		
					pthread_create(&thread, NULL, (void*)getThread(), (void *) mypipe[0]);
		
					int pid = fork();
					
					if (pid == 0) 
					{ 
						setAffinity(1);
						preMethodB(b); 
						close(mypipe[0]);
						write_to_pipe(mypipe[1]); 
						exit(0); 
					} 
					else 
					{ 					
						close(mypipe[1]);
						signal(SIGCHLD, SIG_IGN); 
					}
					
					gettimeofday(&endTime, NULL);
					timeval_subtract(&result, &endTime, &startTime);
					updateTotalTestTime(result);
				}
	int retVal = _methodB(b);
	 return retVal;
}

int methodC(int c)
{
	int retVal = c * 3;
	return retVal;
}

