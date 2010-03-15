/*
 * Test Program.
 */
# include "test.h" 

int main(int argc , char **argv){  

  int a;
  int b;
  int c;
  int d;

  a =  2 * 2 ;
  b =  a * a ;
  c = b * b ;

  fprintf(stdout, "\n A = %d , B = %d , C = %d\n", a, b, c);

  first_test(a, b, c);
  d = second_test(a, b, c);  
  fprintf(stdout, "\n D = %d \n\n", d);
  return 0;
}
