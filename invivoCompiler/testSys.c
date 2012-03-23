//学生成绩管理系统
/*-------------1-------------*/
#include<bios.h> 
#include<dos.h>  /*头文件*/
#include<conio.h>
#include<ctype.h>
#include<process.h>
#include<stdlib.h>
#include<stdio.h>
#include<string.h>
  
#define NULL 0
#define ESC 0x001b  /* 退出 */
#define  F1  0x3b00  /* 查看帮助信息，调用HelpMassage()函数 */
#define  F2  0x3c00  /*输入学生成绩*/
#define  F3  0x3d00 /*按学号查找*/
#define  F4  0x3e00  /*按姓名查找*/
#define  F5  0x3f00  /*列出所有学生成绩*/
#define  F6  0x4000 /*统计*/

struct stuType  /*定义结构体变量*/
{
   char NO[11];  /*学号长度为10*/ 
   char XM[10];
   float CJ[4];  /*包含4门成绩*/
};

/*-------------2-------------*/
int JY_NO(char *stu_num,FILE *fp)    /*检验学号的正确性*/
{  struct stuType stud;
   int NO;
   char *p=stu_num;
   if(strcmp(stu_num,"#")==0) return 1;    /*若输入"#"返回真值,不再循环输入*/
   while(*p!='{ARTICLE_CONTENT}')      /*学号必须是数字，否则返回重新输入*/
       {    NO=(int)*p;
      if(NO<48||NO>57)
  {   puts("\t\t\t非法学号!请重新输入!\n");
      return 0;
   }
      else p++;      /*指针加1*/
        }
   if(strlen(stu_num)!=10)     /*若学号长度不为10,则返回重新输入*/
 {  puts("\t\t\t学号长度不对!\n");
    return 0;
  }
   if(getchar()!='\n')      /*若学号后面的字符不是回车符，则学号长度大于10*/
 {    printf("\t\t\t学号长度大于10个!请重新输入!\n");
      do{}while(getchar()!='\n');   /*用getchar接收多余的字符*/
      return 0;       
  } 

   else
     {
    rewind(fp);      /*使文件指针指向头*/
    while(!feof(fp))     /*若文件指针未到结尾,就继续执行下面的循环,feof遇到文件结束符返回非零值,否则返回0*/
  {   fread(&stud,sizeof(struct stuType),1,fp); /*读取一定长度的数据*/
             if(strcmp(stu_num,stud.NO)==0)  /*学号的唯一性*/
   {  printf("\t\t\t学号重复，请重新输入!\n");
         printf("\t\t\t该学生成绩如下：\n");  
         printf("\t\t\t语文：%.1f\n",stud.CJ[0]);
         printf("\t\t\t数学：%.1f\n",stud.CJ[1]);
         printf("\t\t\t英语：%.1f\n",stud.CJ[2]);
         printf("\t\t\t总评：%.1f\n",stud.CJ[3]);
       return 0;
           }
  }
    }
   return 1;
  
}

/*-------------3-------------*/
int JY_NO2(char *stu_num)   /*检验学号*/
{    int NO;
     char *p=stu_num;         
     if(strcmp(stu_num,"#")==0)return 1; /*若输入“#”，则返回真值结束*/
     if(strlen(stu_num)!=10)   /*学号长度为10*/
 {   puts("\t\t\t学号长度不对!\n");
     return 0;
  }
     while(*p!='{ARTICLE_CONTENT}')    /*学号必须用数字,若包含有字母，或其它字符则返回假值重新输入*/
      {      NO=(int)*p;
      if(NO<48||NO>57)
  {   puts("\t\t\t非法学号!请重新输入!\n");
      return 0;
   }
      else p++;    /*指针加1*/
 }
     if(getchar()!='\n')   /*检验学号长度是否大于10，并把多余的字符去掉*/
 {    printf("\t\t\t学号长度大于10个!请重新输入!\n"); 
      do{}while(getchar()!='\n');
      return 0;       
 } 
     return 1;
}

/*-------------4-------------*/
int JY_XM(char *stu_XM)    /*检验姓名*/
{  int PD;
   char *p;
   p=stu_XM;
   while(*p!='{ARTICLE_CONTENT}')    /*姓名只能用中文*/
   {  
      PD=(int)*p;
      if(PD>0)
 {  puts("\t\t\t姓名只能用中文，请重新输入！\n");
    return 0;
  }
      else p++;     /*使指针加1，指向下一汉字*/
    }
   if(getchar()!='\n')    /*姓名长度不得大于5个*/
 {    printf("\t\t\t姓名长度大于5个!请重新输入!\n");
      do{}while(getchar()!='\n');
      return 0;       
 } 
   return 1;     /*字符串全为汉字返回真*/

}

/*-------------5-------------*/
int JY_CJ(float stu_CJ)    /*学生成绩只能在0~100之间*/
{   
     if(stu_CJ<0||stu_CJ>100)
 {  printf("\t\t\t输入错误，成绩只能在0~100之间!\n");
    return 0;
  }
    return 1;
}

/*-------------6-------------*/
void CreatFile()     /*输入文件*/
{  FILE *fp;
   struct stuType stu,stu0={"","",};   /*对stu0先赋值*/
   fp=fopen("stu.dat","wb+");    /*打开或创建一个二进制文件,打开时将原来的内容删除*/
   if(fp==NULL)
 {  printf("\t\t\t文件打开失败!\n\t\t\t按任意键返回...");
    getch();
    return;
  }
   else
   {   while(1)
 {   stu=stu0;
     do{  printf("\n\t\t\t请输入学号:");  /*输入学号并检验其正确性*/
   scanf("%10s",stu.NO);
        }while(!JY_NO(stu.NO,fp));
     if(strcmp(stu.NO,"#")==0)break;
     do{  printf("\n\t\t\t请输入姓名:");  /*输入姓名并检验其正确性*/
   scanf("%10s",stu.XM);
        }while(!JY_XM(stu.XM));
     do{  printf("\n\t\t\t请输入语文成绩:"); /*输入成绩并检验其正确性*/
   scanf("%f",&stu.CJ[0]);
        }while(!JY_CJ(stu.CJ[0]));
     do{  printf("\n\t\t\t请输入数学成绩:"); /*同上*/
   scanf("%f",&stu.CJ[1]);
        }while(!JY_CJ(stu.CJ[1]));
     do{  printf("\n\t\t\t请输入英语成绩:");
   scanf("%f",&stu.CJ[2]);
        }while(!JY_CJ(stu.CJ[2]));
     do{  printf("\n\t\t\t请输入总评成绩:");
   scanf("%f",&stu.CJ[3]);
        }while(!JY_CJ(stu.CJ[3]));
     fwrite(&stu,sizeof(struct stuType),1,fp); /*写文件*/
 }

   }
   fclose(fp);      /*关闭文件*/

}

/*-------------7-------------*/
void Search_Xuehao()       /*按学号查询*/
{  FILE *fp;
  int flag;
   struct stuType stu,stud;
   fp=fopen("stu.dat","rb");
   if(fp==NULL)        /*若文件打不开则输出下面的信息*/
 {  printf("\t\t\t文件打开失败!\n\t\t\t按任意键返回...");
    getch();
    return;
  }
   else
    {   do{  puts("\n\t\t\t输入“#”结束查询");
      do{   printf("\t\t\t请输入要查询的学号:");
     scanf("%10s",stu.NO);
  }while(!JY_NO2(stu.NO));
      if(strcmp(stu.NO,"#")==0)break;         /*若输入“#”则结束循环*/
      flag=0;
      rewind(fp);
      while(fread(&stud,sizeof(struct stuType),1,fp))      /*检查文件指针结束*/
  {    if(strcmp(stu.NO,stud.NO)==0)   /*比较学号*/
   {  puts("\t\t\t该学生成绩如下：");
      printf("\t\t\t学号:%s\n",stud.NO);
      printf("\t\t\t姓名:%s\n",stud.XM);
      printf("\t\t\t语文:%.1f\n",stud.CJ[0]);
      printf("\t\t\t数学:%.1f\n",stud.CJ[1]);
      printf("\t\t\t英语:%.1f\n",stud.CJ[2]);
      printf("\t\t\t总评:%.1f\n",stud.CJ[3]);
      flag=1;     /*记录学号是否查到*/
    }
   }
     if(flag==0)puts("\t\t\t无此学号!");
  }while(strcmp(stu.NO,"#")!=0);

    }   
   fclose(fp);        /*关闭文件*/
      
}

/*-------------8-------------*/
void Search_Xingming()       /*按姓名查找*/
{   FILE  *fp;
    int flag=0;
    struct stuType stu,stud;
    fp=fopen("stu.dat","rb");
    if(fp==NULL)
 {   printf("\t\t\t文件打开失败!\n\t\t\t按任意键返回...");
     getch();
     return;
  }
    else
     {   do{
        do{   printf("\t\t\t请输入要查询的学生姓名:");
               scanf("%10s",stu.XM);
            }while(!JY_XM(stu.XM));
  rewind(fp);      /*文件指针指向头*/
    while(fread(&stud,sizeof(struct stuType),1,fp))
      {    if(strcmp(stu.XM,stud.XM)==0)   /*比较姓名是否相同*/
       {  puts("\t\t\t该学生姓名如下:");
            printf("\t\t\t学号：%s\n",stud.NO);
             printf("\t\t\t姓名：%s\n",stud.XM);
             printf("\t\t\t语文：%.1f\n",stud.CJ[0]);
             printf("\t\t\t数学：%.1f\n",stud.CJ[1]);
             printf("\t\t\t英语：%.1f\n",stud.CJ[2]);
             printf("\t\t\t总评：%.1f\n",stud.CJ[3]);
             flag=1;     /*记录姓名是否被查到*/
           }
           }
   if(flag==0)puts("\n\t\t\t无此学生!");
  puts("\t\t\t是否继续(y--继续，其他返回)?");
     }while(getch()=='y');
      }
   fclose(fp);   
  /* puts("\t\t\t请按任意键继续...");*/
  /* getch();*/

}

/*-------------9-------------*/
int ListFile(void)       /*输出文件,列出所有学生成绩*/
{   FILE *fp;
    int REC=0;        /*记录学生人数*/
    struct stuType stu;
    fp=fopen("stu.dat","rb");
    if(fp==NULL)
 {  printf("\t\t\t文件打开失败!\n\t\t\t按任意键返回...");
    getch();
    return 1;
  }
    else{   printf("\t\t\t学生成绩如下：\n");
     printf("\t\t\t学号\t\t姓名\t语文\t数学\t英语\t总评\n");
     rewind(fp);
     while(fread(&stu,sizeof(struct stuType),1,fp))  
  {          /*每读取一个长度的数据就输出*/
      printf("\t\t\t%s",stu.NO);
      printf("\t%s",stu.XM);    
      printf("\t%.1f",stu.CJ[0]);      
      printf("\t%.1f",stu.CJ[1]);    
      printf("\t%.1f",stu.CJ[2]);
      printf("\t%.1f",stu.CJ[3]);
      printf("\n");
      REC++;
      if(REC%20==0)     /*每输出20个学生成绩，停一下*/
   {   printf("\t\t\t请按任意键继续...\n");
       getch();
    }
   }
  }
    fclose(fp);        /*关闭文件*/
    printf("\t\t\t请按任意键继续...");
    getch();
        
}

/*-------------10-------------*/
void Statistics()       /*统计及格和优秀人数*/
{   FILE *fp;
    int REC=0,pass[4]={0},good[4]={0};     /*REC--记录个数,即人数,pass--及格人数,good--优秀人数*/
    float highest[4]={0},score[4]={0};     /*highest--最高分,score--总分*/
    struct stuType stu;
    fp=fopen("stu.dat","rb");
    if(fp==NULL)
 {  printf("\t\t\t文件打开失败!\n\t\t\t按任意键返回...");
    getch();
    return;
  }
    else {   rewind(fp);
      while(fread(&stu,sizeof(struct stuType),1,fp))
  {   REC++;
      score[0]=score[0]+stu.CJ[0];   /*语文*/
      if(stu.CJ[0]>=60)pass[0]++;
      if(stu.CJ[0]>=80)good[0]++;
      if(highest[0]<stu.CJ[0])highest[0]=stu.CJ[0]; 
      score[1]=score[1]+stu.CJ[1];   /*数学*/
      if(stu.CJ[1]>=60)pass[1]++;
      if(stu.CJ[1]>=80)good[1]++;
      if(highest[1]<stu.CJ[1])highest[1]=stu.CJ[1];
      score[2]=score[2]+stu.CJ[2];   /*英语*/
      if(stu.CJ[2]>=60)pass[2]++;
      if(stu.CJ[2]>=80)good[2]++;
      if(highest[2]<stu.CJ[2])highest[2]=stu.CJ[2];
      score[3]=score[3]+stu.CJ[3];   /*总评*/
      if(stu.CJ[3]>=60)pass[3]++;
      if(stu.CJ[3]>=80)good[3]++;
      if(highest[3]<stu.CJ[3])highest[3]=stu.CJ[3];
   }
      if(REC==0)       /*可以防止记录为0是REC作除数而造成的错误*/
  {    printf("\t\t\t未输入学生记录！按任意键返回...");
       getch();
       return;
   }
      else{
        printf("\t\t\t\t  语文\t  数学\t  英语\t  总评\n"); /*输出统计信息*/
        printf("\t\t\t平均分:   %.1f\t  %.1f\t  %.1f\t  %.1f\n",score[0]/REC,score[1]/REC,score[2]/REC,score[3]/REC);
          printf("\t\t\t最高分：  %.1f\t  %.1f\t  %.1f\t  %.1f\n",highest[0],highest[1],highest[2],highest[3]);
        printf("\t\t\t优秀人数：%d\t  %d\t  %d\t  %d\n",good[0],good[1],good[2],good[3]);
        printf("\t\t\t及格人数：%d\t  %d\t  %d\t  %d\n",pass[0],pass[1],pass[2],pass[3]);        
                  }
   }
    fclose(fp);
    printf("\n\t\t\t请按任意键继续...");
    getch();
   
}

/*-------------11-------------*/
void HelpMessage()  
{ clrscr();
 printf("\n\n\n\n\n     此系统是应老师所布置的作业编制而成，该系统具有存贮学生数据，按学号、姓名查询，列出学生成绩和统计功能。\n    \n     使用方法：系统输入数据后，将在当前目录中建立一个名为stu.dat文件，用于保存输入的数据。学号输入只能用数字输入，并且学号只能是10位。姓名输入符合中国人的姓名，只能用中文,且最长为5个汉字。\n        此程序在Turbo C2.0下运行通过\n\n     由于是初学者，水平有限此系统还有许多不够完整和严密性，敬请指正！");
 getch();
}

/*-------------12-------------*/
int GetKey(void)     /*此函数返回一个按键的数值*/
{   int key; 
    key=bioskey(0);     /*bioskey为调用BIOS键盘接口*/
    if(key<<8)      /*位移*/
    {
        key=key&0x00ff;
      
     }
      return key;     /*返回按键*/
}

/*-------------13-------------*/
void main()
{ int key;
  struct date d;     /*定义时间结构体*/
  getdate(&d);      /*读取系统日期并把它放到结构体d中*/
  clrscr();      /*清除屏幕*/
  printf("\n\n\n\n\n");    
  printf("\t\t\t****************************\n"); /*版本信息*/
  printf("\t\t\t    学生成绩管理系统1.0     \n");
  printf("\t\t\t****************************\n");
  printf("\t\t\t    制作群：  404+1工作室   \n");
  printf("\t\t\t    指导老师：---     \n");
  printf("\t\t\t    制作时间：2004年5月     \n");
  printf("\t\t\t****************************\n");
  printf("\t\t\t请按任意键继续...");
  /*while(!kbhit());*/
  getch();      /*从键盘读取一个字符,但不显示于屏幕*/ 
  system("cls");     /*调用DOS的清屏函数,TC中可用clrscr代替*/
    while(1)      /*主菜单*/
     {    
   printf("\n\n\n\n\n");
   printf("\t\t\t************************************\n");
   printf("\t\t\t**\tF1 --帮助    **\n");
   printf("\t\t\t**\tF2 --输入数据并存入文件   **\n");
   printf("\t\t\t**\tF3 --根据学号查询成绩   **\n");
   printf("\t\t\t**\tF4 --根据姓名查询成绩   **\n");
   printf("\t\t\t**\tF5 --输出文件内容     **\n");
   printf("\t\t\t**\tF6 --统计及格和优秀人数   **\n");
   printf("\t\t\t**\tESC--退出系统    **\n");
   printf("\t\t\t************************************\n");
   printf("\n\t\t\t请输入选项\t\t%d年%d月%d日\n\n",d.da_year,d.da_mon,d.da_day);  /*提示信息,并显示当前系统日期*/
   key=GetKey();     /*调用自定义函数,读取一个键*/
   switch(key)
   {
       case F1: HelpMessage();   break;
       case F2: CreatFile(); break;
       case F3: Search_Xuehao(); break;
       case F4: Search_Xingming();break;
       case F5: ListFile(); break;
       case F6: Statistics(); break;
       case ESC:exit(1);  break;
       /*default: puts("\t\t\t输入错误选项!");
         printf("\t\t\t按任意键返回...");
         getch();*/
     }
   clrscr();     /*每执行完一项功能后,自动清屏*/
       }
 

