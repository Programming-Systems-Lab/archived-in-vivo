/* The Test File */

# include <stdio.h>
# include <stdlib.h>
# include <string.h>

       #      define     Global_Variable      1
	  
	unsigned int xx = 1 , xxx = 2,xxxx,xxxxx = 55,xxxxxx[122] , xz;
	
	unsigned int *ab[10];
	
	long int it;
		
double   xy   ;

typedef struct params {
	char type[1024];
	char vars[1024];
}Parameter;

Parameter parameters[1024];

int first(int a, double b, char c);
double second(int a, double b, char c);
int third(int d);
void fourth(void);
int fifth(Parameter e);
void sixth();

int first(int a, double b, char c)
{
	int t;
	a = b + c;
	t = a + t;
	t = xz;
	
	int ta = *(ab[2]);

	return ta + a + t +( b * c);

}

double second(int a, double b, char c)
{	
	int i , j;
	for( i = 0; i < xx; i++){
		for(j = 0; j < xx; j++){
			int a;
			fprintf(stdout, "\n T = %d \n",  a );
		}
	}	
	return b;
}

int third(int d)
{
	int a;
	a = first( d, d, d);
	
	char c = 'c';
	second(d,xy,c);
	
	first(d , d , d);
	
	first(xx ,d, xx);
	
	fprintf(stderr, "\n Quit \n");
	
  return a;
}

void fourth(void)
{
 if(0 == 1){
 ;
 }
 else {
	return ;
 }
 
  return ;

}

int fifth(Parameter p)
{
  return strlen(p.vars);

}

void sixth()
{
	fourth();
}

int seventh(int a, int b)
{	
	int i;
	
	for(i = 0; i < 1; i++){
		int a = 0 ,  b = 1;				
		if(a == b){
			int xx = a + b;			
			xx = xx + 1;
			fprintf(stdout, "\n xx = %d\n",xx);
		}		
	}	
	return a;
}
