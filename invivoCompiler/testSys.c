//ѧ���ɼ�����ϵͳ
/*-------------1-------------*/
#include<bios.h> 
#include<dos.h>  /*ͷ�ļ�*/
#include<conio.h>
#include<ctype.h>
#include<process.h>
#include<stdlib.h>
#include<stdio.h>
#include<string.h>
  
#define NULL 0
#define ESC 0x001b  /* �˳� */
#define  F1  0x3b00  /* �鿴������Ϣ������HelpMassage()���� */
#define  F2  0x3c00  /*����ѧ���ɼ�*/
#define  F3  0x3d00 /*��ѧ�Ų���*/
#define  F4  0x3e00  /*����������*/
#define  F5  0x3f00  /*�г�����ѧ���ɼ�*/
#define  F6  0x4000 /*ͳ��*/

struct stuType  /*����ṹ�����*/
{
   char NO[11];  /*ѧ�ų���Ϊ10*/ 
   char XM[10];
   float CJ[4];  /*����4�ųɼ�*/
};

/*-------------2-------------*/
int JY_NO(char *stu_num,FILE *fp)    /*����ѧ�ŵ���ȷ��*/
{  struct stuType stud;
   int NO;
   char *p=stu_num;
   if(strcmp(stu_num,"#")==0) return 1;    /*������"#"������ֵ,����ѭ������*/
   while(*p!='{ARTICLE_CONTENT}')      /*ѧ�ű��������֣����򷵻���������*/
       {    NO=(int)*p;
      if(NO<48||NO>57)
  {   puts("\t\t\t�Ƿ�ѧ��!����������!\n");
      return 0;
   }
      else p++;      /*ָ���1*/
        }
   if(strlen(stu_num)!=10)     /*��ѧ�ų��Ȳ�Ϊ10,�򷵻���������*/
 {  puts("\t\t\tѧ�ų��Ȳ���!\n");
    return 0;
  }
   if(getchar()!='\n')      /*��ѧ�ź�����ַ����ǻس�������ѧ�ų��ȴ���10*/
 {    printf("\t\t\tѧ�ų��ȴ���10��!����������!\n");
      do{}while(getchar()!='\n');   /*��getchar���ն�����ַ�*/
      return 0;       
  } 

   else
     {
    rewind(fp);      /*ʹ�ļ�ָ��ָ��ͷ*/
    while(!feof(fp))     /*���ļ�ָ��δ����β,�ͼ���ִ�������ѭ��,feof�����ļ����������ط���ֵ,���򷵻�0*/
  {   fread(&stud,sizeof(struct stuType),1,fp); /*��ȡһ�����ȵ�����*/
             if(strcmp(stu_num,stud.NO)==0)  /*ѧ�ŵ�Ψһ��*/
   {  printf("\t\t\tѧ���ظ�������������!\n");
         printf("\t\t\t��ѧ���ɼ����£�\n");  
         printf("\t\t\t���ģ�%.1f\n",stud.CJ[0]);
         printf("\t\t\t��ѧ��%.1f\n",stud.CJ[1]);
         printf("\t\t\tӢ�%.1f\n",stud.CJ[2]);
         printf("\t\t\t������%.1f\n",stud.CJ[3]);
       return 0;
           }
  }
    }
   return 1;
  
}

/*-------------3-------------*/
int JY_NO2(char *stu_num)   /*����ѧ��*/
{    int NO;
     char *p=stu_num;         
     if(strcmp(stu_num,"#")==0)return 1; /*�����롰#�����򷵻���ֵ����*/
     if(strlen(stu_num)!=10)   /*ѧ�ų���Ϊ10*/
 {   puts("\t\t\tѧ�ų��Ȳ���!\n");
     return 0;
  }
     while(*p!='{ARTICLE_CONTENT}')    /*ѧ�ű���������,����������ĸ���������ַ��򷵻ؼ�ֵ��������*/
      {      NO=(int)*p;
      if(NO<48||NO>57)
  {   puts("\t\t\t�Ƿ�ѧ��!����������!\n");
      return 0;
   }
      else p++;    /*ָ���1*/
 }
     if(getchar()!='\n')   /*����ѧ�ų����Ƿ����10�����Ѷ�����ַ�ȥ��*/
 {    printf("\t\t\tѧ�ų��ȴ���10��!����������!\n"); 
      do{}while(getchar()!='\n');
      return 0;       
 } 
     return 1;
}

/*-------------4-------------*/
int JY_XM(char *stu_XM)    /*��������*/
{  int PD;
   char *p;
   p=stu_XM;
   while(*p!='{ARTICLE_CONTENT}')    /*����ֻ��������*/
   {  
      PD=(int)*p;
      if(PD>0)
 {  puts("\t\t\t����ֻ�������ģ����������룡\n");
    return 0;
  }
      else p++;     /*ʹָ���1��ָ����һ����*/
    }
   if(getchar()!='\n')    /*�������Ȳ��ô���5��*/
 {    printf("\t\t\t�������ȴ���5��!����������!\n");
      do{}while(getchar()!='\n');
      return 0;       
 } 
   return 1;     /*�ַ���ȫΪ���ַ�����*/

}

/*-------------5-------------*/
int JY_CJ(float stu_CJ)    /*ѧ���ɼ�ֻ����0~100֮��*/
{   
     if(stu_CJ<0||stu_CJ>100)
 {  printf("\t\t\t������󣬳ɼ�ֻ����0~100֮��!\n");
    return 0;
  }
    return 1;
}

/*-------------6-------------*/
void CreatFile()     /*�����ļ�*/
{  FILE *fp;
   struct stuType stu,stu0={"","",};   /*��stu0�ȸ�ֵ*/
   fp=fopen("stu.dat","wb+");    /*�򿪻򴴽�һ���������ļ�,��ʱ��ԭ��������ɾ��*/
   if(fp==NULL)
 {  printf("\t\t\t�ļ���ʧ��!\n\t\t\t�����������...");
    getch();
    return;
  }
   else
   {   while(1)
 {   stu=stu0;
     do{  printf("\n\t\t\t������ѧ��:");  /*����ѧ�Ų���������ȷ��*/
   scanf("%10s",stu.NO);
        }while(!JY_NO(stu.NO,fp));
     if(strcmp(stu.NO,"#")==0)break;
     do{  printf("\n\t\t\t����������:");  /*������������������ȷ��*/
   scanf("%10s",stu.XM);
        }while(!JY_XM(stu.XM));
     do{  printf("\n\t\t\t���������ĳɼ�:"); /*����ɼ�����������ȷ��*/
   scanf("%f",&stu.CJ[0]);
        }while(!JY_CJ(stu.CJ[0]));
     do{  printf("\n\t\t\t��������ѧ�ɼ�:"); /*ͬ��*/
   scanf("%f",&stu.CJ[1]);
        }while(!JY_CJ(stu.CJ[1]));
     do{  printf("\n\t\t\t������Ӣ��ɼ�:");
   scanf("%f",&stu.CJ[2]);
        }while(!JY_CJ(stu.CJ[2]));
     do{  printf("\n\t\t\t�����������ɼ�:");
   scanf("%f",&stu.CJ[3]);
        }while(!JY_CJ(stu.CJ[3]));
     fwrite(&stu,sizeof(struct stuType),1,fp); /*д�ļ�*/
 }

   }
   fclose(fp);      /*�ر��ļ�*/

}

/*-------------7-------------*/
void Search_Xuehao()       /*��ѧ�Ų�ѯ*/
{  FILE *fp;
  int flag;
   struct stuType stu,stud;
   fp=fopen("stu.dat","rb");
   if(fp==NULL)        /*���ļ��򲻿�������������Ϣ*/
 {  printf("\t\t\t�ļ���ʧ��!\n\t\t\t�����������...");
    getch();
    return;
  }
   else
    {   do{  puts("\n\t\t\t���롰#��������ѯ");
      do{   printf("\t\t\t������Ҫ��ѯ��ѧ��:");
     scanf("%10s",stu.NO);
  }while(!JY_NO2(stu.NO));
      if(strcmp(stu.NO,"#")==0)break;         /*�����롰#�������ѭ��*/
      flag=0;
      rewind(fp);
      while(fread(&stud,sizeof(struct stuType),1,fp))      /*����ļ�ָ�����*/
  {    if(strcmp(stu.NO,stud.NO)==0)   /*�Ƚ�ѧ��*/
   {  puts("\t\t\t��ѧ���ɼ����£�");
      printf("\t\t\tѧ��:%s\n",stud.NO);
      printf("\t\t\t����:%s\n",stud.XM);
      printf("\t\t\t����:%.1f\n",stud.CJ[0]);
      printf("\t\t\t��ѧ:%.1f\n",stud.CJ[1]);
      printf("\t\t\tӢ��:%.1f\n",stud.CJ[2]);
      printf("\t\t\t����:%.1f\n",stud.CJ[3]);
      flag=1;     /*��¼ѧ���Ƿ�鵽*/
    }
   }
     if(flag==0)puts("\t\t\t�޴�ѧ��!");
  }while(strcmp(stu.NO,"#")!=0);

    }   
   fclose(fp);        /*�ر��ļ�*/
      
}

/*-------------8-------------*/
void Search_Xingming()       /*����������*/
{   FILE  *fp;
    int flag=0;
    struct stuType stu,stud;
    fp=fopen("stu.dat","rb");
    if(fp==NULL)
 {   printf("\t\t\t�ļ���ʧ��!\n\t\t\t�����������...");
     getch();
     return;
  }
    else
     {   do{
        do{   printf("\t\t\t������Ҫ��ѯ��ѧ������:");
               scanf("%10s",stu.XM);
            }while(!JY_XM(stu.XM));
  rewind(fp);      /*�ļ�ָ��ָ��ͷ*/
    while(fread(&stud,sizeof(struct stuType),1,fp))
      {    if(strcmp(stu.XM,stud.XM)==0)   /*�Ƚ������Ƿ���ͬ*/
       {  puts("\t\t\t��ѧ����������:");
            printf("\t\t\tѧ�ţ�%s\n",stud.NO);
             printf("\t\t\t������%s\n",stud.XM);
             printf("\t\t\t���ģ�%.1f\n",stud.CJ[0]);
             printf("\t\t\t��ѧ��%.1f\n",stud.CJ[1]);
             printf("\t\t\tӢ�%.1f\n",stud.CJ[2]);
             printf("\t\t\t������%.1f\n",stud.CJ[3]);
             flag=1;     /*��¼�����Ƿ񱻲鵽*/
           }
           }
   if(flag==0)puts("\n\t\t\t�޴�ѧ��!");
  puts("\t\t\t�Ƿ����(y--��������������)?");
     }while(getch()=='y');
      }
   fclose(fp);   
  /* puts("\t\t\t�밴���������...");*/
  /* getch();*/

}

/*-------------9-------------*/
int ListFile(void)       /*����ļ�,�г�����ѧ���ɼ�*/
{   FILE *fp;
    int REC=0;        /*��¼ѧ������*/
    struct stuType stu;
    fp=fopen("stu.dat","rb");
    if(fp==NULL)
 {  printf("\t\t\t�ļ���ʧ��!\n\t\t\t�����������...");
    getch();
    return 1;
  }
    else{   printf("\t\t\tѧ���ɼ����£�\n");
     printf("\t\t\tѧ��\t\t����\t����\t��ѧ\tӢ��\t����\n");
     rewind(fp);
     while(fread(&stu,sizeof(struct stuType),1,fp))  
  {          /*ÿ��ȡһ�����ȵ����ݾ����*/
      printf("\t\t\t%s",stu.NO);
      printf("\t%s",stu.XM);    
      printf("\t%.1f",stu.CJ[0]);      
      printf("\t%.1f",stu.CJ[1]);    
      printf("\t%.1f",stu.CJ[2]);
      printf("\t%.1f",stu.CJ[3]);
      printf("\n");
      REC++;
      if(REC%20==0)     /*ÿ���20��ѧ���ɼ���ͣһ��*/
   {   printf("\t\t\t�밴���������...\n");
       getch();
    }
   }
  }
    fclose(fp);        /*�ر��ļ�*/
    printf("\t\t\t�밴���������...");
    getch();
        
}

/*-------------10-------------*/
void Statistics()       /*ͳ�Ƽ������������*/
{   FILE *fp;
    int REC=0,pass[4]={0},good[4]={0};     /*REC--��¼����,������,pass--��������,good--��������*/
    float highest[4]={0},score[4]={0};     /*highest--��߷�,score--�ܷ�*/
    struct stuType stu;
    fp=fopen("stu.dat","rb");
    if(fp==NULL)
 {  printf("\t\t\t�ļ���ʧ��!\n\t\t\t�����������...");
    getch();
    return;
  }
    else {   rewind(fp);
      while(fread(&stu,sizeof(struct stuType),1,fp))
  {   REC++;
      score[0]=score[0]+stu.CJ[0];   /*����*/
      if(stu.CJ[0]>=60)pass[0]++;
      if(stu.CJ[0]>=80)good[0]++;
      if(highest[0]<stu.CJ[0])highest[0]=stu.CJ[0]; 
      score[1]=score[1]+stu.CJ[1];   /*��ѧ*/
      if(stu.CJ[1]>=60)pass[1]++;
      if(stu.CJ[1]>=80)good[1]++;
      if(highest[1]<stu.CJ[1])highest[1]=stu.CJ[1];
      score[2]=score[2]+stu.CJ[2];   /*Ӣ��*/
      if(stu.CJ[2]>=60)pass[2]++;
      if(stu.CJ[2]>=80)good[2]++;
      if(highest[2]<stu.CJ[2])highest[2]=stu.CJ[2];
      score[3]=score[3]+stu.CJ[3];   /*����*/
      if(stu.CJ[3]>=60)pass[3]++;
      if(stu.CJ[3]>=80)good[3]++;
      if(highest[3]<stu.CJ[3])highest[3]=stu.CJ[3];
   }
      if(REC==0)       /*���Է�ֹ��¼Ϊ0��REC����������ɵĴ���*/
  {    printf("\t\t\tδ����ѧ����¼�������������...");
       getch();
       return;
   }
      else{
        printf("\t\t\t\t  ����\t  ��ѧ\t  Ӣ��\t  ����\n"); /*���ͳ����Ϣ*/
        printf("\t\t\tƽ����:   %.1f\t  %.1f\t  %.1f\t  %.1f\n",score[0]/REC,score[1]/REC,score[2]/REC,score[3]/REC);
          printf("\t\t\t��߷֣�  %.1f\t  %.1f\t  %.1f\t  %.1f\n",highest[0],highest[1],highest[2],highest[3]);
        printf("\t\t\t����������%d\t  %d\t  %d\t  %d\n",good[0],good[1],good[2],good[3]);
        printf("\t\t\t����������%d\t  %d\t  %d\t  %d\n",pass[0],pass[1],pass[2],pass[3]);        
                  }
   }
    fclose(fp);
    printf("\n\t\t\t�밴���������...");
    getch();
   
}

/*-------------11-------------*/
void HelpMessage()  
{ clrscr();
 printf("\n\n\n\n\n     ��ϵͳ��Ӧ��ʦ�����õ���ҵ���ƶ��ɣ���ϵͳ���д���ѧ�����ݣ���ѧ�š�������ѯ���г�ѧ���ɼ���ͳ�ƹ��ܡ�\n    \n     ʹ�÷�����ϵͳ�������ݺ󣬽��ڵ�ǰĿ¼�н���һ����Ϊstu.dat�ļ������ڱ�����������ݡ�ѧ������ֻ�����������룬����ѧ��ֻ����10λ��������������й��˵�������ֻ��������,���Ϊ5�����֡�\n        �˳�����Turbo C2.0������ͨ��\n\n     �����ǳ�ѧ�ߣ�ˮƽ���޴�ϵͳ������಻�������������ԣ�����ָ����");
 getch();
}

/*-------------12-------------*/
int GetKey(void)     /*�˺�������һ����������ֵ*/
{   int key; 
    key=bioskey(0);     /*bioskeyΪ����BIOS���̽ӿ�*/
    if(key<<8)      /*λ��*/
    {
        key=key&0x00ff;
      
     }
      return key;     /*���ذ���*/
}

/*-------------13-------------*/
void main()
{ int key;
  struct date d;     /*����ʱ��ṹ��*/
  getdate(&d);      /*��ȡϵͳ���ڲ������ŵ��ṹ��d��*/
  clrscr();      /*�����Ļ*/
  printf("\n\n\n\n\n");    
  printf("\t\t\t****************************\n"); /*�汾��Ϣ*/
  printf("\t\t\t    ѧ���ɼ�����ϵͳ1.0     \n");
  printf("\t\t\t****************************\n");
  printf("\t\t\t    ����Ⱥ��  404+1������   \n");
  printf("\t\t\t    ָ����ʦ��---     \n");
  printf("\t\t\t    ����ʱ�䣺2004��5��     \n");
  printf("\t\t\t****************************\n");
  printf("\t\t\t�밴���������...");
  /*while(!kbhit());*/
  getch();      /*�Ӽ��̶�ȡһ���ַ�,������ʾ����Ļ*/ 
  system("cls");     /*����DOS����������,TC�п���clrscr����*/
    while(1)      /*���˵�*/
     {    
   printf("\n\n\n\n\n");
   printf("\t\t\t************************************\n");
   printf("\t\t\t**\tF1 --����    **\n");
   printf("\t\t\t**\tF2 --�������ݲ������ļ�   **\n");
   printf("\t\t\t**\tF3 --����ѧ�Ų�ѯ�ɼ�   **\n");
   printf("\t\t\t**\tF4 --����������ѯ�ɼ�   **\n");
   printf("\t\t\t**\tF5 --����ļ�����     **\n");
   printf("\t\t\t**\tF6 --ͳ�Ƽ������������   **\n");
   printf("\t\t\t**\tESC--�˳�ϵͳ    **\n");
   printf("\t\t\t************************************\n");
   printf("\n\t\t\t������ѡ��\t\t%d��%d��%d��\n\n",d.da_year,d.da_mon,d.da_day);  /*��ʾ��Ϣ,����ʾ��ǰϵͳ����*/
   key=GetKey();     /*�����Զ��庯��,��ȡһ����*/
   switch(key)
   {
       case F1: HelpMessage();   break;
       case F2: CreatFile(); break;
       case F3: Search_Xuehao(); break;
       case F4: Search_Xingming();break;
       case F5: ListFile(); break;
       case F6: Statistics(); break;
       case ESC:exit(1);  break;
       /*default: puts("\t\t\t�������ѡ��!");
         printf("\t\t\t�����������...");
         getch();*/
     }
   clrscr();     /*ÿִ����һ��ܺ�,�Զ�����*/
       }
 

