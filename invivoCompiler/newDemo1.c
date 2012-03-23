//大家一起来玩十点半
/*一副扑克（除去王），第一轮玩者依次取一张牌，。之后玩者根据自己的牌总的点数
选择继续要牌或比较大小。(玩家可以选择不要牌，但一旦选择不要牌，以后就不能再要牌）
玩家的点数一旦超过10。5，不能再要牌。 
j q k算0。5点。 如果牌总的点数都大于10。5 或都小于10。5，大者赢；
如果一个大于10。5而另一个小于10。5，小于10。5者胜。*/
/*2005-4-9  梁见斌*/ 
#include<stdio.h>
#include<stdlib.h>
#include<time.h>
struct book{
   int color;
   char number;
   float num;
} Card[52];/*用来存储牌的花色，数字及所代表的点数*/
 
void Build_Show(int HuaSe[], float DianShu[], char ShuZi[]);/*建立一副新牌，并显示给游戏者检查*/ 
void Wash_Card(void);/*洗牌*/ 
void Get_Card(int n) ;/*按顺序发牌并公布结果*/ 
void DuoRen(int n);
int main(void)
{
   int  flag, n;
   int HuaSe[4]={3,4,5,6};/*存储花色*/ 
   char ShuZi[13]={'A','K','Q','J','0','9','8','7','6','5','4','3','2'};/*存储数字*/ 
   float DianShu[13]={1,0.5,0.5,0.5,10,9,8,7,6,5,4,3,2};/*存储点数*/ 
   
   Build_Show(HuaSe, DianShu, ShuZi);/*建立一副新牌，并显示给游戏者检查*/
   do{
      Wash_Card();/*洗牌*/ 
   a: puts("\n please input number of players：");
      scanf("%d", &n);
          
  switch(n)
      {
         case 1:  puts("你和电脑玩");
           break;
         case 2:  puts("你们两个人玩");
            break;
        case 3:  puts("你们三个人玩");
           break; 
         case 4:  puts("你们四个人玩");
           break;
         case 5:  puts("你们五个人玩");
           break;
         case 6:  puts("你们六个人玩");
           break;
         case 7:  puts("你们七个人玩");
           break;
         case 8:  puts("你们八个人玩");
           break;
       default : puts("玩家太多了");
          goto a;
      }   
      Get_Card(n);/*发牌并公布结果*/ 
      do{
         puts("\n你还要玩吗？输入1表示要，输入0表示不要：");
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
   for(i=0; i<4; i++)/*建立一副新牌*/
    for(j=0; j<13; j++)
    {
       Card[j+13*i].color = HuaSe[i];
       Card[j+13*i].number = ShuZi[j];
       Card[j+13*i].num = DianShu[j];
    } 
    puts("这是一付新牌："); /*显示整副牌给游戏者检查*/
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
      j=rand()%(52-i);/*随机换牌，把牌的顺序打乱*/ 
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

void Get_Card(int n) /*按顺序发牌并公布结果*/ 
{
   int i=0, j=0, k=0, flag=1, flag2=1;
   float sum_m=0, sum_c=0;
   struct book Man[52], Computer[52];
   if(n == 1)
   {
      while(flag == 1 && i < 52)
      {
         Man[j++]=Card[i++];/*人先拿牌*/ 
         sum_m += Man[j-1].num;/*累计人拿到的总点数*/
         if(flag2 == 1 && i < 52 && (sum_m <= 10.5 && sum_c <= 10.5 && (sum_c + Card[i].num) <= 10.5
          || sum_m > 10.5 && (sum_c + Card[i].num) >= sum_m))  /*让电脑可以知道下一张牌，以便决定是否要牌*/ 
         {
            Computer[k++]=Card[i++];/*电脑拿牌*/ 
            sum_c += Computer[k-1].num;/*累计电脑拿到的总点数*/
         } 
         else
          flag=0;  
          
         printf("\n");
         puts("你现在拿到的牌为："); 
         for(int i=0; i<j; i++)
          printf("  %c%c", Man[i].color , Man[i].number);
         printf("\n你现在拿到的总点数为：%f\n", sum_m);
         
         puts("电脑现在拿到的牌为："); 
         for(int i=0; i<k; i++)
          printf("  %c%c", Computer[i].color , Computer[i].number);
         printf("\n电脑现在拿到的总点数为：%f\n", sum_c);
         
         if(sum_m <= 10.5)
         {
            do{
             puts("你还要牌吗？输入1表示要，输入0表示不要：");
             fflush(stdin);
             scanf("%d", &flag);
            }  while(flag != 1 && flag != 0);
            if(i == 52)
            {
               puts("牌已经拿光了！");  
               system("pause");
            } 
         }
         else
          break;      
      }   /*人不要牌后，电脑根据需要可以再拿牌，直到超过10.5*/ 
      while(flag2 == 1 && i < 52 && (sum_m <= 10.5 && sum_c <= 10.5 && (sum_c + Card[i].num) <= 10.5
        || sum_m > 10.5 && (sum_c + Card[i].num) >= sum_m))  /*让电脑可以知道下一张牌，以便决定是否要牌*/ 
      {
         Computer[k++]=Card[i++];
         sum_c += Computer[k-1].num;
         if( sum_c > 10.5)
          break;
      } 
      puts("好，现在公布结果："); 
      puts("你现在拿到的牌为："); 
      for(int i=0; i<j; i++)
       printf("  %c%c", Man[i].color , Man[i].number);
      printf("\n你现在拿到的总点数为：%f\n", sum_m);
      puts("电脑现在拿到的牌为："); 
      for(int i=0; i<k; i++)
       printf("  %c%c", Computer[i].color , Computer[i].number);
      printf("\n电脑现在拿到的总点数为：%f\n", sum_c);
      if((sum_m > 10.5 && sum_c > 10.5)||(sum_m <= 10.5 && sum_c <= 10.5))
         if(sum_m > sum_c)
          puts("你竟然赢了电脑！真是不可思议！");
         else if(sum_m == sum_c)
          puts("和电脑打成了平手，你的确实不错");
         else 
          puts("你输给了电脑！看来需要多加练习！");
      else if(sum_m <= 10.5 && sum_c > 10.5)
       puts("你竟然赢了电脑！真是不可思议！");
      else
       puts("你输给了电脑！看来需要多加练习！");
    } 
    else
     DuoRen(n); 
}    

void DuoRen(int n)
{
   struct book Man[n][52];
   int i, a[8]={0}, k=0, flag[8], flag2=1, s=0;
   float sum[8]={0};
 
   for(i=0; i<n; i++)/*每个人都要拿第一轮牌*/ 
   {
      printf("\n第 %d 个人拿牌 \t", i+1);   
      Man[i][a[i]++] = Card[k++];
      sum[i] += Man[i][a[i]-1].num;
      flag[i]=1;
      printf("第 %d 个人手上的牌为:\t", i+1);
      for(int m=0; m<a[i]; m++)
      printf("  %c%c", Man[i][m].color , Man[i][m].number);
      printf("\n第 %d 个人拿到的总点数为：%f\n", i+1, sum[i]);
   }
   while(flag2 == 1)/*当所有的人都不能拿牌时循环结束*/ 
   {
      for(i=0; i<n; i++)   
      {
         if(k == 52)
         {
            puts("牌已经拿光了！"); 
            flag2=0; 
            break;
         } 
         else if(flag[i] == 1 && sum[i] <= 10.5)/*当玩家有资格拿牌时可选择是否拿牌*/ 
         {
            printf("\n\n第 %d 个人拿牌 \t", i+1);
            do{
               printf("你的总点数为：%f\n", sum[i]);
               puts("你要牌吗？输入1表示要，输入0表示不要：");
               fflush(stdin);
               scanf("%d", &flag[i]);
            }  while(flag[i] != 1 && flag[i] != 0);
            if(flag[i] == 1)
            {
               Man[i][a[i]++] = Card[k++];
               sum[i] += Man[i][a[i]-1].num;
               printf("第 %d 个人手上的牌为:\t", i+1);
               for(int m=0; m<a[i]; m++)
                  printf("  %c%c", Man[i][m].color , Man[i][m].number);
               printf("\n第 %d 个人拿到的总点数为：%f\n", i+1, sum[i]);         
            }   
         } 
         if(sum[i] > 10.5)
          flag[i]=0;
      }    
      s=0;     
      for(i=0; i<n; i++) /*看是否所有的玩家都有资格拿牌*/ 
         s += flag[i];
      if(s == 0)
         flag2=0;
   }  
   puts("\n\n好，现在公布结果："); 
   for(i=0; i<n; i++)
   { 
      printf("第 %d 个人手上的牌为:\t", i+1);
      for(int m=0; m<a[i]; m++)
       printf("  %c%c", Man[i][m].color , Man[i][m].number);
      printf("\n第 %d 个人拿到的总点数为：%f\n", i+1, sum[i]); 
   }        
}      
