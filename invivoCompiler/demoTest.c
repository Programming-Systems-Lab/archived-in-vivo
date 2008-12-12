#include <stdio.h>

int methodA(int a);
int _methodA(int a);
int methodB(int b);
int _methodB(int b);
int methodC(int c);
int _methodC(int c);

int main()
{
	int x = 0;

	x += methodA(1);
	x += methodB(2);
	x += methodC(3);

	printf("output: %d\n", x);

	return 0;
}

int _methodA(int a)
{
	int retVal = a * 1;
	return retVal;
}

void preMethodA(int a)
{
	int x = 1+1;
}

void postMethodA(int a)
{
	int x = 1+1;
}

int methodA(int a)
{
	preMethodA(a);
	int retVal = _methodA(a);
	postMethodA(a);
	return retVal;
}

int _methodB(int b)
{
	int retVal = b * 2;
	return retVal;
}

void preMethodB(int b)
{
	int x = 1+1;
}

int methodB(int b)
{
	preMethodB(b);
	int retVal = _methodB(b);
	return retVal;
}

int _methodC(int c)
{
	int retVal = c * 3;
	return retVal;
}

void postMethodC(int c)
{
	int x = 1+1;
}

int methodC(int c)
{
	int retVal = _methodC(c);
	postMethodC(c);
	return retVal;
}


