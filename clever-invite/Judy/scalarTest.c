#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <Judy.h>
#include <math.h>
#define UNKNOWN_NUMB -1;
#define SUCC 0;
#define ERR -1;
#define NPRIMES  100000
#define FALSE 0
#define TRUE  1



Pvoid_t  PLFArray = (Pvoid_t)NULL; // initialize JudyL array
Pvoid_t  PLGArray = (Pvoid_t)NULL; // initialize JudyL array


/*
 *The specific implementation chosen for bijective Mapping
 *is the famous bijective Cantor Pairing Function. 
 */
float bijectiveMapping(float k1, float k2){ 
   float result = (1.0/2.0)*(k1 + k2)*(k1 + k2 + 1) + k2;
   //  printf("ReSULT : %f \n", result);
   return result; 
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



long called = 0;
long found = 0;
long functCalled = 0; 
long forked = 0; 
long nonforked = 0; 
int __check_pauc(int x, int y, int z){
  float cantVal;
  int testVals[3] = {x,y,z};
  int testLength = sizeof(testVals)/sizeof(float);
  called++;
  // get a distinct hash for x, y, and z

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
  found++;
  return 0;
}




static double __pauc(int a1, int a2, int a3)
{
  //printf("orderref[0]=%d n=%d\n", orderref[0], n);
  if (0)
  //if (__check_pauc(a1, a2 ,a3))
    {
      int pid = fork();
      forked++;
      if (pid == 0)
	{
	  
	  a1 *= 2;
	  a2 *= 2;
	  a3 *= 2;
	  a3 =  __paucHelper(a1,a2,a3);
	  printf("Here is a3: %i" , a3); 
	  exit(0);
	}
      if(pid == -1){
	perror("fork failed");
	exit(-1);
      }
    }
  
  nonforked++;
  return __paucHelper(a1,a2,a3);
}

//Sieve method to get primes below a number n 
//Reference: http://www.cis.temple.edu/~ingargio/cis71/code/sieve.c
int  __paucHelper(int a1, int a2, int a3){
  int numRun = 10*a1*a2* a3 + 1000;
  int n;
  int i,j;
  int flag;
  int primes[NPRIMES]; /*It will contain the primes smaller than n
                        *that we have already encountered*/
  int level;           /*1+Number of primes currently in PRIMES*/

  n = numRun;
  n = (int)floor(n/a1);
  n = (int)floor(n/a2);
  n = (int)floor(n/a3);
  n = n*n;
  printf("Resulting N is: %d \n", n ); 
  level = 0;

  /*Main body*/
  for(i=2;i<=n;i++) {
    for(j = 0, flag = TRUE; j<level && flag; j++)
      flag = (i%primes[j]);
    if (flag) { /*I is a prime */
      printf("%12d\n", i);
      if (level < NPRIMES)
	primes[level++] = i;
    }
  }
  functCalled++;
  return 0;
}

/*
int main(void){
  // how to fill in -> Ask Chris.
  return 0; 
}
*/
/*
 *Retrieve the inputs from the generated inputs.txt
 */
int main( void ){
  /*Parse inputs*/
  static const char filename[] = "inputs.txt";
  char *token;
  char delims[3] = " \n";
  int index = 0;
  int val, i, loop,runTimes;
  int testVals[3];
  FILE *file;
  runTimes = 10; //Runs runTimes * 10 times

  for(loop = 0; loop < runTimes; loop++ ){
    file = fopen(filename, "r"); 
    printf("LOOOOOOP %d \n", loop);
     if ( file != NULL ){
      char line [128]; 
      while ( fgets (line, sizeof line, file ) != NULL ){
	token = strtok(line, delims);
	while(token != NULL){
	  val = atoi(token);
	  token = strtok(NULL, delims);
	  testVals[index] = val;
	  index++; 
	  if(index == 3){
	    printf("**Testing with: %d - %d - %d on loop %d\n**", 
		   testVals[0],testVals[1], testVals[2], loop);
	  __pauc(testVals[0],testVals[1],testVals[2]);
	  index = 0;
	  }
	}
      }
      
      fclose ( file );
    }
    
    else{
      perror(filename);
    }

  }
  printf("Judy Hit Rate found: %li ~ called: %li \n", found, called);
  printf("Test Function Called : %li \n", functCalled);
  printf("Test Function Called in fork: %li \n", forked);
  printf("Test Function Called out of fork: %li \n", nonforked);
  return 0;
}

