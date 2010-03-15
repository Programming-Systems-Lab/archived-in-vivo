/* The Second Test Function. */
# include "test.h"

# define Global_Var 24

int second_test(int a, int b, int c){

  int temp = 0 ;
  
  temp = a + b + c + temp + Global_Var ;

  return temp;
}
