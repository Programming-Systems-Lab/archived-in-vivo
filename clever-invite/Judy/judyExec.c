#include <stdio.h>
#include <Judy.h>
#define UNKNOWN_NUMB -1; 

int main(void){
	Word_t   Index;                     // array index
	Word_t   Value;                     // array element value
	Word_t * PValue;                    // pointer to array element value
	long a = 1111; 
	long b = 8888; 

	Pvoid_t  PJLArray = (Pvoid_t)NULL; // initialize JudyL array

	/* Insertions */
	Index = a; 
    	JLI(PValue, PJLArray, Index);
	if (PValue == PJERR) return -1;
	*PValue = a;                 // store new value
	Index = b; 
    	JLI(PValue, PJLArray, Index);
	if (PValue == PJERR) return -1;
	*PValue = b;                 // store new value

	/* Successful Lookup*/ 
	Index = a;
	JLG(PValue, PJLArray, Index);
	if (PValue != NULL)
	{
	    printf("%lu %lu\n", Index, *PValue);
		printf("Printing A \n"); 
	}

	Index = b;
	JLG(PValue, PJLArray, Index);
	printf("Evaling B \n"); 

	if (PValue != NULL)
	{
	    printf("%lu %lu\n", Index, *PValue);
		printf("Printing B \n"); 

	}

	/* Failing Lookup*/ 
	Index = UNKNOWN_NUMB;
	JLG(PValue, PJLArray, Index);
	printf("Evaling B \n"); 

	if (PValue == NULL)
	{
		printf("UNKNOWN_NUMB returns a NULL success \n"); 

	}

	return 0;
}
