//���һ������ʮ���
/*һ���˿ˣ���ȥ��������һ����������ȡһ���ƣ���֮�����߸����Լ������ܵĵ���
ѡ�����Ҫ�ƻ�Ƚϴ�С��(��ҿ���ѡ��Ҫ�ƣ���һ��ѡ��Ҫ�ƣ��Ժ�Ͳ�����Ҫ�ƣ�
��ҵĵ���һ������10��5��������Ҫ�ơ� 
j q k��0��5�㡣 ������ܵĵ���������10��5 ��С��10��5������Ӯ��
���һ������10��5����һ��С��10��5��С��10��5��ʤ��*/
/*2005-4-9  ������*/ 
#include<stdio.h>
#include<stdlib.h>
#include<time.h>
struct book{
   int color;
   char number;
   float num;
} Card[52];/*�����洢�ƵĻ�ɫ�����ּ�������ĵ���*/
 
void Build_Show(int HuaSe[], float DianShu[], char ShuZi[]);/*����һ�����ƣ�����ʾ����Ϸ�߼��*/ 
void Wash_Card(void);/*ϴ��*/ 
void Get_Card(int n) ;/*��˳���Ʋ��������*/ 
void DuoRen(int n);
int main(void)
{
   int  flag, n;
   int HuaSe[4]={3,4,5,6};/*�洢��ɫ*/ 
   char ShuZi[13]={'A','K','Q','J','0','9','8','7','6','5','4','3','2'};/*�洢����*/ 
   float DianShu[13]={1,0.5,0.5,0.5,10,9,8,7,6,5,4,3,2};/*�洢����*/ 
   
   Build_Show(HuaSe, DianShu, ShuZi);/*����һ�����ƣ�����ʾ����Ϸ�߼��*/
   do{
      Wash_Card();/*ϴ��*/ 
   a: puts("\n please input number of players��");
      scanf("%d", &n);
          
  switch(n)
      {
         case 1:  puts("��͵�����");
           break;
         case 2:  puts("������������");
            break;
        case 3:  puts("������������");
           break; 
         case 4:  puts("�����ĸ�����");
           break;
         case 5:  puts("�����������");
           break;
         case 6:  puts("������������");
           break;
         case 7:  puts("�����߸�����");
           break;
         case 8:  puts("���ǰ˸�����");
           break;
       default : puts("���̫����");
          goto a;
      }   
      Get_Card(n);/*���Ʋ��������*/ 
      do{
         puts("\n�㻹Ҫ��������1��ʾҪ������0��ʾ��Ҫ��");
       fflush(stdin);
       scanf("%d", &flag);
      }  while(flag != 1 && flag != 0);
   } while(flag == 1);   
   getchar();
   return 0;
}  

void Build_Show(int HuaSe[], float DianShu[], char ShuZi[])
{
   int i, j;
   for(i=0; i<4; i++)/*����һ������*/
    for(j=0; j<13; j++)
    {
       Card[j+13*i].color = HuaSe[i];
       Card[j+13*i].number = ShuZi[j];
       Card[j+13*i].num = DianShu[j];
    } 
    puts("����һ�����ƣ�"); /*��ʾ�����Ƹ���Ϸ�߼��*/
    for(i=0,j=0; i<52; i++,j++)
    {
      if(!(j%13))
       printf("\n");
      printf("  %c%c", Card[i].color , Card[i].number);
    }
} 

void Wash_Card(void)
{
 int i, j, hua;
 char dian;
 float shu;
 
   srand( (unsigned) time(NULL)); 
   for(i=0; i<52; i++)
   {
      j=rand()%(52-i);/*������ƣ����Ƶ�˳�����*/ 
      hua=Card[j].color;
      Card[j].color=Card[51-i].color;
      Card[51-i].color=hua;
      
      dian=Card[j].number;
      Card[j].number=Card[51-i].number;
      Card[51-i].number=dian;
      
      shu=Card[j].num;
      Card[j].num=Card[51-i].num;
      Card[51-i].num=shu;
   }   
}  

void Get_Card(int n) /*��˳���Ʋ��������*/ 
{
   int i=0, j=0, k=0, flag=1, flag2=1;
   float sum_m=0, sum_c=0;
   struct book Man[52], Computer[52];
   if(n == 1)
   {
      while(flag == 1 && i < 52)
      {
         Man[j++]=Card[i++];/*��������*/ 
         sum_m += Man[j-1].num;/*�ۼ����õ����ܵ���*/
         if(flag2 == 1 && i < 52 && (sum_m <= 10.5 && sum_c <= 10.5 && (sum_c + Card[i].num) <= 10.5
          || sum_m > 10.5 && (sum_c + Card[i].num) >= sum_m))  /*�õ��Կ���֪����һ���ƣ��Ա�����Ƿ�Ҫ��*/ 
         {
            Computer[k++]=Card[i++];/*��������*/ 
            sum_c += Computer[k-1].num;/*�ۼƵ����õ����ܵ���*/
         } 
         else
          flag=0;  
          
         printf("\n");
         puts("�������õ�����Ϊ��"); 
         for(int i=0; i<j; i++)
          printf("  %c%c", Man[i].color , Man[i].number);
         printf("\n�������õ����ܵ���Ϊ��%f\n", sum_m);
         
         puts("���������õ�����Ϊ��"); 
         for(int i=0; i<k; i++)
          printf("  %c%c", Computer[i].color , Computer[i].number);
         printf("\n���������õ����ܵ���Ϊ��%f\n", sum_c);
         
         if(sum_m <= 10.5)
         {
            do{
             puts("�㻹Ҫ��������1��ʾҪ������0��ʾ��Ҫ��");
             fflush(stdin);
             scanf("%d", &flag);
            }  while(flag != 1 && flag != 0);
            if(i == 52)
            {
               puts("���Ѿ��ù��ˣ�");  
               system("pause");
            } 
         }
         else
          break;      
      }   /*�˲�Ҫ�ƺ󣬵��Ը�����Ҫ���������ƣ�ֱ������10.5*/ 
      while(flag2 == 1 && i < 52 && (sum_m <= 10.5 && sum_c <= 10.5 && (sum_c + Card[i].num) <= 10.5
        || sum_m > 10.5 && (sum_c + Card[i].num) >= sum_m))  /*�õ��Կ���֪����һ���ƣ��Ա�����Ƿ�Ҫ��*/ 
      {
         Computer[k++]=Card[i++];
         sum_c += Computer[k-1].num;
         if( sum_c > 10.5)
          break;
      } 
      puts("�ã����ڹ��������"); 
      puts("�������õ�����Ϊ��"); 
      for(int i=0; i<j; i++)
       printf("  %c%c", Man[i].color , Man[i].number);
      printf("\n�������õ����ܵ���Ϊ��%f\n", sum_m);
      puts("���������õ�����Ϊ��"); 
      for(int i=0; i<k; i++)
       printf("  %c%c", Computer[i].color , Computer[i].number);
      printf("\n���������õ����ܵ���Ϊ��%f\n", sum_c);
      if((sum_m > 10.5 && sum_c > 10.5)||(sum_m <= 10.5 && sum_c <= 10.5))
         if(sum_m > sum_c)
          puts("�㾹ȻӮ�˵��ԣ����ǲ���˼�飡");
         else if(sum_m == sum_c)
          puts("�͵��Դ����ƽ�֣����ȷʵ����");
         else 
          puts("������˵��ԣ�������Ҫ�����ϰ��");
      else if(sum_m <= 10.5 && sum_c > 10.5)
       puts("�㾹ȻӮ�˵��ԣ����ǲ���˼�飡");
      else
       puts("������˵��ԣ�������Ҫ�����ϰ��");
    } 
    else
     DuoRen(n); 
}    

void DuoRen(int n)
{
   struct book Man[n][52];
   int i, a[8]={0}, k=0, flag[8], flag2=1, s=0;
   float sum[8]={0};
 
   for(i=0; i<n; i++)/*ÿ���˶�Ҫ�õ�һ����*/ 
   {
      printf("\n�� %d �������� \t", i+1);   
      Man[i][a[i]++] = Card[k++];
      sum[i] += Man[i][a[i]-1].num;
      flag[i]=1;
      printf("�� %d �������ϵ���Ϊ:\t", i+1);
      for(int m=0; m<a[i]; m++)
      printf("  %c%c", Man[i][m].color , Man[i][m].number);
      printf("\n�� %d �����õ����ܵ���Ϊ��%f\n", i+1, sum[i]);
   }
   while(flag2 == 1)/*�����е��˶���������ʱѭ������*/ 
   {
      for(i=0; i<n; i++)   
      {
         if(k == 52)
         {
            puts("���Ѿ��ù��ˣ�"); 
            flag2=0; 
            break;
         } 
         else if(flag[i] == 1 && sum[i] <= 10.5)/*��������ʸ�����ʱ��ѡ���Ƿ�����*/ 
         {
            printf("\n\n�� %d �������� \t", i+1);
            do{
               printf("����ܵ���Ϊ��%f\n", sum[i]);
               puts("��Ҫ��������1��ʾҪ������0��ʾ��Ҫ��");
               fflush(stdin);
               scanf("%d", &flag[i]);
            }  while(flag[i] != 1 && flag[i] != 0);
            if(flag[i] == 1)
            {
               Man[i][a[i]++] = Card[k++];
               sum[i] += Man[i][a[i]-1].num;
               printf("�� %d �������ϵ���Ϊ:\t", i+1);
               for(int m=0; m<a[i]; m++)
                  printf("  %c%c", Man[i][m].color , Man[i][m].number);
               printf("\n�� %d �����õ����ܵ���Ϊ��%f\n", i+1, sum[i]);         
            }   
         } 
         if(sum[i] > 10.5)
          flag[i]=0;
      }    
      s=0;     
      for(i=0; i<n; i++) /*���Ƿ����е���Ҷ����ʸ�����*/ 
         s += flag[i];
      if(s == 0)
         flag2=0;
   }  
   puts("\n\n�ã����ڹ��������"); 
   for(i=0; i<n; i++)
   { 
      printf("�� %d �������ϵ���Ϊ:\t", i+1);
      for(int m=0; m<a[i]; m++)
       printf("  %c%c", Man[i][m].color , Man[i][m].number);
      printf("\n�� %d �����õ����ܵ���Ϊ��%f\n", i+1, sum[i]); 
   }        
}      
