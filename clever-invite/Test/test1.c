/* The first Test Function. */
# include "test.h"

void first_test(int a, int b, int c){

  int temp = 0;

  temp = a + b + c + temp;

  fprintf(stdout, "\n 'A + B + C' = %d \n", temp);

  return ;
}
