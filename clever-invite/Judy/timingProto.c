#include <stdio.h>
#include <unistd.h>     
#include <sys/wait.h>
#include <Judy.h>
#define UNKNOWN_NUMB -1; 
#define SUCC 0;
#define ERR -1;

Pvoid_t  PJLArray = (Pvoid_t)NULL; // initialize JudyL array
struct timeval startExec, endExec; 
struct timeval startJudy, endJudy; 

/*
*The specific implementation chosen for bijective Mapping
*is the famous bijective Cantor Pairing Function. 
*/
int bijectiveMapping(int k1, int k2){ 
  return 1/2*(k1 + k2)*(k1 + k2 + 1) + k2;
}
/*
*Insert value as index into judy array
*@return: 0 for success and -1 for failure
*/
int judyInsert(long val){ 
  Word_t   Index;                     // array index 
  Word_t   Value;                     // array element value 
  Word_t * PValue;                    // pointer to array element value 
  // Insertions  
  Index = val;  
  JLI(PValue, PJLArray, Index); 
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
int judyMember(long val){ 
  Word_t   Index;                     // array index 
  Word_t   Value;                     // array element value 
  Word_t * PValue;                    // pointer to array element value 
  Index = val; 
  JLG(PValue, PJLArray, Index); 
  if (PValue == NULL){ 
    return ERR; 
  } 
  return SUCC; 
} 

/*
 *Dummy Test Case for child to execute 
 */
void dummyTest(){ 
  /*Basic Functionality - Print out 10 fibonacci numbers      */
  int n;        /* The number of fibonacci numbers we will print  */
  int i;        /* The index of fibonacci number to be printed next  */
  int current;  /* The value of the (i)th fibonacci number */
  int next;     /* The value of the (i+1)th fibonacci number  */
  int twoaway;  /* The value of the (i+2)th fibonacci number*/
  
  printf("Child Test Functionality - Fibonacci Numbers "); 
  n = 10; /* Print out 10 Fib numbers */  
  printf("\n\n\tI \t Fibonacci(I) \n\t=====================\n"); 
  next = current = 1; 
  for (i=1; i<=n; i++) { 
    printf("\t%d \t   %d\n", i, current); 
    twoaway = current+next; 
    current = next;
    next    = twoaway;
  }  
  printf("Test case complete :)\n\n\n\n");
}

/*
 *Tests a testcase by forking a process in order to test a testcase
 *@return: 0 for success and -1 for failure 
 */
int timeTestCase(){
  pid_t childpid;
  int retval; 
  int status;
  
  gettimeofday(&startExec, NULL);  
  childpid = fork();    
  if (childpid >= 0){
    if (childpid == 0){   /* child process */
      dummyTest();
      exit(0); 
    }
    else{ /* parent process */
      wait(&status); /* wait for child to complete testing */ 
      gettimeofday(&endExec, NULL); 
    }
  }
  else{ /* fork failure */
    perror("Fork Error"); /* display error message */
    exit(0); 
  } 
  return 0;
}

/*
 *Manipulate Judy Structures and time performance
 *@return: 0 for success and -1 for failure 
 */
int timeJudyManip(){
  int mapValue; 
  int mapValue2; 
  /*
   *Time Judy Operations
   */
  gettimeofday(&startJudy, NULL);
  mapValue = bijectiveMapping(4,2);  
  if(judyInsert(mapValue) == -1){ 
    printf("Judy Insertion Error"); 
    return ERR; 
  } 
  /*Testing identical params to that inserted previously*/
  mapValue2 = bijectiveMapping(4,2); 
  if(judyMember(mapValue2) == -1){ 
    printf("Should of found duplicate entity \n"); 
    return ERR; 
  } 
  /*Testing UNlike value to that inserted */
  mapValue2 = bijectiveMapping(2,4); 
  if(!judyMember(mapValue2)){ 
    printf("Should of NOT found duplicate entity \n"); 
    return ERR; 
  }
  printf("Inserting %d  && %d \n", mapValue, mapValue2); 
  gettimeofday(&endJudy, NULL);
}

/*
 *Calculate and Display Timing Information 
 */ 
void printStatistics(){
  int judyTime; 
  int execTime;
  float percentage; 
  /*
   *Calculate Times
   */
  
  judyTime =  ((endJudy.tv_sec * 1000000 + endJudy.tv_usec)
	       - (startJudy.tv_sec * 1000000 + startJudy.tv_usec));
  execTime =  ((endExec.tv_sec * 1000000 + endExec.tv_usec)
	       - (startExec.tv_sec * 1000000 + startExec.tv_usec));
  
  /*
   *Print Statistical Information
   */
  printf("***************FINAL RESULTS*********************\n\n\n\n");
  printf("Here is Judy Time : %d ms\n", judyTime); 
  printf("Here is Test Case Time : %d ms\n", execTime); 
  percentage = (float) ((float) judyTime/(float)execTime) * 100; 
  printf("Judy is %f percent of the testCase time\n", percentage);

  printf("\n\n\n\n");
  printf("***************FINAL RESULTS*********************\n\n");
  
}



int main(void){
  /*
   *Time Judy Execution
   */
  if(timeJudyManip() == -1){
    printf("judyManip() failed"); 
    return ERR; 
  }
  
  /*
   *Time TestCase Execution using a child
   */

  if(timeTestCase() == -1){
    printf("timeTestCase() failed"); 
    return ERR; 
  }
  
  printStatistics(); 
  return 0;  
  
}



