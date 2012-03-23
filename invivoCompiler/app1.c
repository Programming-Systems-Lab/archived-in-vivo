#include <stdlib.h>
#include <stdio.h>	
#include <math.h>
#include <time.h>


#define numOfQuestions 10

void main()
{
  srand(1);
  for(int i=0;i<numOfQuestions ;i++)
{
   bool add=0;
add = rand()%2;
   char oper;
oper = '-';
   if(add) oper = '+';
   int a;
a = rand()%100;
   int b;
b = rand()%100;
   if(!add)
   {
       while(b>a)b = rand()%100;
   }
   printf("%d %c %d =?", a, oper, b);
   int answer = 0;
   scanf("%d", &answer);
   bool correct;
correct = false;
   if(add)
   {
       if(answer == (a + b)) correct = true;
   }
   else
   {
      if(answer == (a - b)) correct = true;
   }
   if(correct)printf("correct!\n");
   else printf("wrong!\n");

}
}
