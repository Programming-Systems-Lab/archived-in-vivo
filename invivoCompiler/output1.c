#include <stdlib.h>
#include <signal.h>
#include <stdio.h>

int methodA(int a, int b);
int methodB(int b);
int methodC(int c);

int main()
{
	int x = 0;

	x += methodA(1, 2);
	x += methodB(2);
	x += methodC(3);

	printf("output: %d\n", x);

	return 0;
}

int _methodA(int a, int b)
{
	int retVal = a * 1;
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
	
				if (should_run_test("methodA") == 0) 
				{ 
					if (fork() == 0) 
					{ 
						preMethodA(a, b); 
						exit(0); 
					} 
					else 
					{ 
						signal(SIGCHLD, SIG_IGN); 
					}
				}
	int retVal = _methodA(a, b);
	
				if (should_run_test("methodA") == 0) 
				{ 
					if (fork() == 0) 
					{ 
						postMethodA(a, b, retVal); 
						exit(0); 
					} 
					else 
					{ 
						signal(SIGCHLD, SIG_IGN); 
					}
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
	
				if (should_run_test("methodB") == 0) 
				{ 
					if (fork() == 0) 
					{ 
						preMethodB(b); 
						exit(0); 
					} 
					else 
					{ 
						signal(SIGCHLD, SIG_IGN); 
					}
				}
	int retVal = _methodB(b);
	 return retVal;
}

int methodC(int c)
{
	int retVal = c * 3;
	return retVal;
}
