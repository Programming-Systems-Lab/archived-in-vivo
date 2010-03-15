#include <stdio.h>
#include <stdlib.h>
#include <Judy.h>
#define UNKNOWN_NUMB -1; 
#define SUCC 0;
#define ERR -1;

Pvoid_t  PLFArray = (Pvoid_t)NULL; // initialize JudyL array
Pvoid_t  PLGArray = (Pvoid_t)NULL; // initialize JudyL array

// MOSES: put any initialization stuff here
void init()
{

}

/*
 *The specific implementation chosen for bijective Mapping
 *is the famous bijective Cantor Pairing Function. 
 */
float bijectiveMapping(float k1, float k2){ 
   float result = (1.0/2.0)*(k1 + k2)*(k1 + k2 + 1) + k2;
   //  printf("ReSULT : %f \n", result);
   return result; 
}
/*
*Insert value as index into judy array
*@return: 0 for success and -1 for failure
*/
int judyInsert(long val, Pvoid_t *PJLArray){ 
  Word_t   Index;                     // array index 
  Word_t   Value;                     // array element value 
  Word_t * PValue;                    // pointer to array element value 
  // Insertions  
  Index = val;  
  JLI(PValue, *PJLArray, Index); 
  if (PValue == PJERR) return ERR; 
  
  /* store new value into this index 
   * We could use this as a hash but  
   * not in this version  
   */
  *PValue = val;                  
  return SUCC; 
}

/* 
 *Search for value as index in judy array 
 *@return: 0 for success and -1 for failure 
 */
int judyMember(long val, Pvoid_t  *PJLArray){ 
  Word_t   Index;                     // array index 
  Word_t   Value;                     // array element value 
  Word_t * PValue;                    // pointer to array element value 
  Index = val; 
  JLG(PValue, *PJLArray, Index); 
  if (PValue == NULL){ 
    return ERR; 
  } 
  return SUCC; 
} 

float paramEncoding_I(int arr[], int length){
   int j;
   float cantVal;
   for(j=1;j<length;j++){
      if(j == 1){
	cantVal = bijectiveMapping((float) arr[0],(float) arr[1]);
      }else{
	cantVal = bijectiveMapping(cantVal, (float) arr[j]);
      }
   }
   return cantVal; 
}


float paramEncoding(float arr[], int length){
   int j;
   float cantVal;
   for(j=1;j<length;j++){
      if(j == 1){
	cantVal = bijectiveMapping((float) arr[0],(float) arr[1]);
      }else{
	cantVal = bijectiveMapping(cantVal, (float) arr[j]);
      }
   }
   return cantVal; 
}


int check_gptr(int *testVals, int testLength, int z){
	float cantVal;
	cantVal = paramEncoding_I(testVals, testLength);  
	cantVal = bijectiveMapping(cantVal,z);

  // see if those values have already been seen
	if(judyMember((long) cantVal, &PLFArray) == -1){ 
		/*If not found in array then add to the corresponding array */ 
	  if(judyInsert((long) cantVal, &PLFArray) == -1){ 
		    printf("Judy Insertion Error"); 
		    return ERR; 
		  } 
		return 1; 
  	} 
	return 0; 
}

int check_fptr(int *testVals, int testLength){
	float cantVal;
	//	float testVals[3] = {x,y,z};
	//      int testLength = sizeof(testVals)/sizeof(float);


	cantVal = paramEncoding_I(testVals, testLength);  

  // see if those values have already been seen
	if(judyMember((long) cantVal, &PLFArray) == -1){ 
		/*If not found in array then add to the corresponding array */ 
		  if(judyInsert((long) cantVal, &PLFArray) == -1){ 
		    printf("Judy Insertion Error"); 
		    return ERR; 
		  } 
		return 1; 
  	} 
	return 0; 
}

int check_f(float x, float y, float z){
	float cantVal;
	float testVals[3] = {x,y,z};
	int testLength = sizeof(testVals)/sizeof(float);
  // get a distinct hash for x, y, and z
/* 
* Could potentially create a hash for the values 
*the array before we make a cantor mapping. 
*/ 
	cantVal = paramEncoding(testVals, testLength);  

  // see if those values have already been seen
	if(judyMember((long) cantVal, &PLFArray) == -1){ 
		/*If not found in array then add to the corresponding array */ 
		  if(judyInsert((long) cantVal, &PLFArray) == -1){ 
		    printf("Judy Insertion Error"); 
		    return ERR; 
		  } 
		return 1; 
  	} 
	return 0; 
}


int check_g(float w, float x, float y, float z){
	float cantVal;
	float testVals[4] = {w,x,y,z};
	int testLength = sizeof(testVals)/sizeof(float);
  // get a distinct hash for x, y, and z
/* 
* Could potentially create a hash for the values 
*the array before we make a cantor mapping. 
*/ 
	cantVal = paramEncoding(testVals, testLength);  

// see if those values have already been seen
	if(judyMember((long) cantVal,&PLGArray) == -1){ 
		/*If not found in array then add to the corresponding array */ 
		  if(judyInsert((long) cantVal, &PLGArray) == -1){ 
		    printf("Judy Insertion Error"); 
		    return ERR; 
		  } 
		return 1; 
  	} 
	return 0; 
}

/*
========================================================
You should not need to modify anything below this line!!!
=========================================================
*/


float k = 5.0; // this is a global var on which the function will depend


// this is the original (wrapped function)
// we'll assume we know it depends on a, b, and k
float _f(float a, float b)
{
  return a + b + k;
}

// this is the wrapper function
float f(float a, float b)
{
  if (check_f(a, b, k))
  {
    int pid = fork();
    if (pid == 0)
    {
      printf("Running test for f! a=%f b=%f k=%f\n", a, b, k);
      exit(0);
    }
  }
  return _f(a, b);
}


// this is the original (wrapped function)
// we'll assume we know it depends on its four inputs 
float _g(float a, float b, float c, float d)
{
  return a + b - c * d;
}

// this is the wrapper function
float g(float a, float b, float c, float d)
{
  if (check_g(a, b, c, d))
  {
    int pid = fork();
    if (pid == 0)
    {
      printf("Running test for g! a=%f b=%f c=%f d=%f\n", a, b, c, d);
      exit(0);
    }
  }
  return _g(a, b, c, d);
}



int main()
{
  // in case you need to do any initialization
  init();
  
  // this should result in a test being run
  f(1.0, 2.0);

  // this should also result in a test being run
  f(4.0, 5.0);

  // this should NOT result in a test being run
  f(1.0, 2.0);


  // this should result in a test being run
  g(2.5, 4.5, 3.4, 6.3);

  // this should also result in a test being run
  g(2.5, 5.5, 3.4, 6.3);

  // this should NOT result in a test being run
  g(2.5, 4.5, 3.4, 6.3);



}

