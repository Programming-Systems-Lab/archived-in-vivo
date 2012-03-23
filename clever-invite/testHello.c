#include <stdlib.h>
#include <signal.h>
#include <stdio.h>



int main()
{

        int x=0;
        x += methodA(1);
	printf("hello world\n");
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

