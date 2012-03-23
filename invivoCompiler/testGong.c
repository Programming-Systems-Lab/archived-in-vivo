//ͼ��ģʽ�µİ��˹�
//������tc3.0�±���ͨ��. �س���

//ѡ��,��ֻ��������, r�����¿�ʼ

//p���ڲ�,ֻ�ܻ��岽��.���������

//��#define STEPMAX��ֵ

#define  MAX  2   /*��Ϸ���ܹ���*/
#define  STARTX   180
#define  STARTY    80
#define  BKCOLOR  BLACK
#define  MANCOLOR RED
#define  OBJECTCOLOR  YELLOW
#define TIMEINT   2
#define STARNUM  300
#define STEPMAX  5
#include<string.h>
#include<bios.h>
#include<stdio.h>
#include<dos.h>
#include<graphics.h>
#include<conio.h>
#include<time.h>
#include<stdlib.h>
#include<stdio.h>
#define Key_R 0x1372
#define Key_Up  0x4800
#define Key_Enter 0x1c0d
#define Key_Down  0x5000
#define Key_P     0x1970
#define Key_Esc   0x11b
#define Key_Right  0x4d00
#define Key_Left  0x4b00

typedef struct star
{
int x;
int y;
int c;
}Star;
Star s[STARNUM];

typedef struct record
{
char name[20];
int second;
struct time t;
struct date d;
}Record;
Record r[MAX];

typedef struct c
{
int x;
int y;
}Add;

typedef struct a
{
int x;
int y;
}Player;
Player p;

char name[20]; /*������Ϸʱ��¼��ҵ�����*/
time_t t1,t2;  /*��Ϸ����ʱ��ʱ��*/
int Ide;          /*��ʼ������Ϸʱ,ѡ��˵�ʱ���Ĺ��ܺ�(1,2,3)*/
int MissionNum;   /*���������Ĺ���*/
int BoxNum;    /*Ŀ�ĵصĸ���*/
int Key;          /*��Ұ���*/
int map[10][10];  /*��ͼ.(��=0),(��=1),(����=2),(ǽ=3),(Ŀ�ĵ�=4),(��+Ŀ�ĵ�=5),(����+Ŀ�ĵ�=6)*/
int StepNum;
int DirectionKey;            /*������ʾ��������һ�ΰ���*/
int BoxMove[STEPMAX];
int Step[STEPMAX];

/*��������*/
void InputName();
void Init();
void MainMenu();
void JudgeRecord();
void WriteRecord();
void JudgeIde();
void DrawMenu(int );
void Game();
void InitMission(int );
void NextMission();
void InitPic(int ,int ,int );
int  Move(Add );
void DrawWall(int ,int );
void DrawBack(int ,int );
void DrawBox(int ,int );
void DrawObject(int ,int );
void DrawMan(int ,int );
void DrawStar();
int JudgeWin();
void InitMission1();
void InitMission2();
int  TimeCome();
void ChangeStar();
void InputName();
void ViewRecords();
void DeleteRecords();
void RegisterStep();
void ReverselyMove();
void MoveBack(Add );

void main()
{
InputName();
Init();       /*�����Կ�*/
srand(time(&t1));
MainMenu();   /*����ʼ�˵�(1.start game 2.view records 3.delete records 4.exit game)*/
}

void InputName()
{
char c;
clrscr();
  do
    {
    printf("\n\nPlease input your name:");
    scanf("%s",name);
    printf("Are you sure the name right(Y/N):");
      do
 {
 c=getch();
 }while(c!='Y'&&c!='y'&&c!='N'&&c!='n');
    }while(c!='Y'&&c!='y');
}
void Init()
{
int gd=DETECT,gm;
initgraph(&gd,&gm,"c:\tc");
}


void MainMenu()
{
setbkcolor(BKCOLOR);
cleardevice();
/*DrawStar();   /*����ʼ�˵��ı�������*/
DrawStar();
Ide=0,Key=0;
DrawMenu(Ide);
do
  {
  if(bioskey(1))   /*�м�����������*/
       {
       Key=bioskey(0);
       switch(Key)
     {
     case Key_Down:  {Ide++;Ide=Ide%4;DrawMenu(Ide);break;}
     case Key_Up:    {Ide--;Ide=(Ide+4)%4;DrawMenu(Ide);break;}
     }
       }
  else {if(TimeCome()) ChangeStar();}            /*�ı䱳��*/
  }while(Key!=Key_Enter);
JudgeIde();         /*����Ide���в�ͬ�ĳ���*/
}

void JudgeIde()
{
switch(Ide)
   {
   case 0:Game();break;
   case 1:{ViewRecords();bioskey(0);MainMenu();break;}
   case 2:{DeleteRecords();ViewRecords();bioskey(0);MainMenu();break;}
   case 3:exit(0);
   }
}

void Game()
{
int i,j,flag;
Add ad;
MissionNum=0;
NextMission();
do
   {
   flag=0;
   Key=bioskey(0);
   switch(Key)
  {
   case Key_Enter:{NextMission();time(&t1);break;}
   case Key_Up:{ad.x=-1;ad.y=0;flag=1;DirectionKey=Key;break;}
   case Key_Down:{ad.x=1;ad.y=0;flag=1;DirectionKey=Key;break;}
   case Key_Left:{ad.x=0;ad.y=-1;flag=1;DirectionKey=Key;break;}
   case Key_Right:{ad.x=0;ad.y=1;flag=1;DirectionKey=Key;break;}
   case Key_R:{MissionNum--;NextMission();break;}
   case Key_Esc:{MainMenu();break;}
          case Key_P:{ReverselyMove();break;}
  }
   if(flag==1)
       {if(Move(ad)) {RegisterStep(); if(JudgeWin()) {JudgeRecord();NextMission();}}}
   }while(1);
}

void InitMission(int n)
{
int i,j;
for(i=0;i<10;i++)
  for(j=0;j<10;j++)
    map[i][j]=0;
switch(n)
   {
    case 1:InitMission1();break;  /*��һ��*/
    case 2:InitMission2();break;  /*�ڶ���*/
   }
}

void InitPic(int n,int i,int j)
{
switch(n)
   {
   case 0:DrawBack(i,j);break;
   case 1:DrawMan(i,j);break;
   case 2:DrawBox(i,j);break;
   case 3:DrawWall(i,j);break;
   case 4:DrawObject(i,j);break;
   case 5:DrawMan(i,j);break;
   case 6:DrawBox(i,j);break;
   }
}

void NextMission()
{
int i,j;
if(MissionNum+1>MAX)  MissionNum=1;
else MissionNum++;
InitMission(MissionNum);
setbkcolor(BKCOLOR);
cleardevice();
for(i=0;i<10;i++)
  for(j=0;j<10;j++)
    InitPic(map[i][j],i,j);
switch(MissionNum)
  {
  case 1:outtextxy(200,230,"Mission 1");break;
  case 2:outtextxy(200,230,"Mission 2");break;
  }
time(&t1);
for(i=0;i<STEPMAX;i++)
  {Step[i]=BoxMove[i]=0;}
StepNum=0;
}


int Move(Add a)
{
int flag;
int i=StepNum%STEPMAX;
switch(map[p.x+a.x][p.y+a.y])  /*����һλ��Ϊʲô*/
    {
    case 0:{map[p.x][p.y]-=1;InitPic(map[p.x][p.y],p.x,p.y);
    p.x=p.x+a.x;p.y=p.y+a.y;
    map[p.x][p.y]+=1;InitPic(map[p.x][p.y],p.x,p.y);flag=1;break;}
    case 2:{if(map[p.x+2*a.x][p.y+2*a.y]==0||map[p.x+2*a.x][p.y+2*a.y]==4)
       {map[p.x][p.y]-=1;map[p.x+a.x][p.y+a.y]=1;map[p.x+2*a.x][p.y+2*a.y]+=2;
       InitPic(map[p.x][p.y],p.x,p.y);
       InitPic(map[p.x+a.x][p.y+a.y],p.x+a.x,p.y+a.y);
       InitPic(map[p.x+2*a.x][p.y+2*a.y],p.x+2*a.x,p.y+2*a.y);
       p.x=p.x+a.x;p.y=p.y+a.y;flag=1;BoxMove[i]=1;}
     else flag=0;
     break;}
    case 3:flag=0;break;
    case 4:{map[p.x][p.y]-=1;InitPic(map[p.x][p.y],p.x,p.y);
    p.x=p.x+a.x;p.y=p.y+a.y;
    map[p.x][p.y]+=1;InitPic(map[p.x][p.y],p.x,p.y);flag=1;break;}
    case 6:{if(map[p.x+2*a.x][p.y+2*a.y]==0||map[p.x+2*a.x][p.y+2*a.y]==4)
       {map[p.x][p.y]-=1;map[p.x+a.x][p.y+a.y]=5;map[p.x+2*a.x][p.y+2*a.y]+=2;
       InitPic(map[p.x][p.y],p.x,p.y);
       InitPic(map[p.x+a.x][p.y+a.y],p.x+a.x,p.y+a.y);
       InitPic(map[p.x+2*a.x][p.y+2*a.y],p.x+2*a.x,p.y+2*a.y);
       p.x=p.x+a.x;p.y=p.y+a.y;flag=1;BoxMove[i]=1;}
     else flag=0;
     break;}
    }
return flag;
}

void DrawWall(int i,int j)
{
DrawBack(i,j);
setfillstyle(9,1);
bar(STARTX+20*j-9,STARTY+20*i-9,STARTX+20*j+9,STARTY+20*i+9);
}

void DrawMan(int i,int j)
{
DrawBack(i,j);
setcolor(MANCOLOR);
circle(STARTX+20*j,STARTY+20*i,9);
arc(STARTX+20*j-3,STARTY+20*i-2,20,160,3);
arc(STARTX+20*j+4,STARTY+20*i-2,20,160,3);
arc(STARTX+20*j,STARTY+20*i-2,220,320,7);
}

void DrawBack(int i,int j)
{
setfillstyle(1,BKCOLOR);
bar(STARTX+20*j-9,STARTY+20*i-9,STARTX+20*j+9,STARTY+20*i+9);
}

void DrawObject(int i,int j)
{
DrawBack(i,j);
setcolor(OBJECTCOLOR);
line(STARTX+20*j-9,STARTY+20*i,STARTX+20*j+9,STARTY+20*i);
line(STARTX+20*j-9,STARTY+20*i-9,STARTX+20*j+9,STARTY+20*i+9);
line(STARTX+20*j-9,STARTY+20*i+9,STARTX+20*j+9,STARTY+20*i-9);
}

void DrawBox(int i,int j)
{
DrawBack(i,j);
setfillstyle(9,3);
bar(STARTX+20*j-9,STARTY+20*i-9,STARTX+20*j+9,STARTY+20*i+9);
}

void DrawMenu(int j)
{
int n;
char *s[4]={"1.Start Game","2.View Records","3.Delete Records","4.Exit Game"};
settextstyle(0,0,1);
setcolor(GREEN);
for(n=0;n<4;n++)
outtextxy(250,170+n*20,s[n]);
setcolor(RED);
outtextxy(250,170+j*20,s[j]);
}

void DrawStar()
{
int w,h,i,dotx,doty,color,maxcolor;
w=getmaxx();
h=getmaxy();
maxcolor=getmaxcolor();
for(i=0;i<STARNUM;i++)
  {
  s[i].x=1+random(w-1);
  s[i].y=1+random(h-1);
  s[i].c=random(maxcolor);
  putpixel(s[i].x,s[i].y,s[i].c);
  }
}
void ChangeStar()
{
int i,maxcolor;
maxcolor=getmaxcolor();
for(i=0;i<STARNUM;i++)
  {
   s[i].c=random(maxcolor);
   putpixel(s[i].x,s[i].y,s[i].c);
  }
}

int TimeCome()
 {

  static long tm, old;
  tm=biostime(0,tm);
  if(tm-old<TIMEINT) return 0;
  else
  {
   old=tm; return 1;
  }
 }

int JudgeWin()
{
int n=0,i,j;
for(i=0;i<10;i++)
  for(j=0;j<10;j++)
    if(map[i][j]==6) n++;
if(n==BoxNum)  return 1;
else return 0;
}


void InitMission1()  /*�ھŹ�*/
{
int i,j;
for(i=0;i<10;i++)
  for(j=0;j<10;j++)
     map[i][j]=0;
for(i=0;i<=5;i++)
  map[0][i]=3;
for(i=5;i<=7;i++)
  {map[2][i]=map[i-1][1]=3;}
for(i=1;i<=4;i++)
  {map[6][i]=map[5][i+3]=map[i][0]=3;}
map[3][7]=map[4][7]=map[1][5]=3;
for(i=2;i<=4;i++)
   map[2][i]=2;
map[3][4]=map[4][5]=2;
for(i=2;i<=3;i++)
   {map[3][i]=map[4][i]=4;}
map[4][4]=4;
p.x=3;p.y=5;
map[3][5]=1;
BoxNum=5;
}

void InitMission2()
{
int i,j;
for(i=0;i<10;i++)
  for(j=0;j<10;j++)
     map[i][j]=0;
for(i=1;i<=5;i++)
   {map[0][i]=map[6][i]=3;}
for(i=2;i<=4;i++)
   {map[1][i+3]=map[i][7]=map[i+2][5]=map[i][0]=3;}
map[1][1]=map[2][1]=map[5][0]=map[5][5]=map[4][6]=map[6][0]=3;
map[2][4]=map[3][3]=map[4][2]=map[4][3]=2;
map[2][3]=map[3][2]=map[3][4]=map[4][4]=4;
p.x=1;p.y=3;
map[1][3]=1;
BoxNum=4;
}


void ViewRecords()
{
FILE *fp;
int i;
setbkcolor(BKCOLOR);
cleardevice();
if((fp=fopen("record","r"))==NULL)
  {
  printf("\nerror on open file!");
  getch();
  exit(1);
  }
gotoxy(1,1);
printf("\n\t\t\tRecord Information\n");
printf("Record-holder  Achievement(s)\t   Time(h:m:s)\t\tDate(y/m/d)");
for(i=0;i<MAX;i++)
  {fseek(fp,i*sizeof(Record),0);
  fread(&r[i],sizeof(Record),1,fp);
  printf("\n%-10s\t%d\t\t   %02d:%02d:%02d\t\t%02d/%02d/%02d",r[i].name,r[i].second,r[i].t.ti_hour,r[i].t.ti_min,r[i].t.ti_sec,r[i].d.da_year,r[i].d.da_mon,r[i].d.da_day);}
fclose(fp);
gotoxy(10,25);
printf("Press any key to return mainmenu...");
}

void DeleteRecords()
{
int i;
FILE *fp;
fp=fopen("record","w");
for(i=0;i<MAX;i++)
 {
 strcpy(r[i].name,"nameless");
 r[i].second=0;
 gettime(&r[i].t);
 getdate(&r[i].d);
 }
for(i=0;i<MAX;i++)
 fwrite(&r[i],sizeof(Record),1,fp);
fclose(fp);
}

void JudgeRecord()
{
int i=MissionNum-1;
time(&t2);
if(r[i].second==0||difftime(t2,t1)<r[i].second)
   {
    gotoxy(10,3);printf("\t\tYou have broken the record");
    r[i].second=difftime(t2,t1);
    strcpy(r[i].name,name);
    gettime(&r[i].t);
    getdate(&r[i].d);
    WriteRecord();
    }
else
  {gotoxy(10,3);printf("\t\tYou have pass this mission");}
gotoxy(10,4);
printf("\t\tpress any key continue...");
getch();
getch();
}

void WriteRecord()
{
FILE *fp;
int i=MissionNum-1;
fp=fopen("record","rt+");
fseek(fp,i*sizeof(Record),0);
fwrite(&r[MissionNum-1],sizeof(Record),1,fp);
fclose(fp);
}

void RegisterStep()
{
int i;
StepNum++;
i=(StepNum-1)%STEPMAX;
Step[i]=DirectionKey;
}


void ReverselyMove()
{
int i;
Add ad;
i=(StepNum-1)%STEPMAX;
if(Step[i]==0)  return;
else 
   {
   switch(Step[i])
  {
   case Key_Up:{ad.x=1;ad.y=0;MoveBack(ad);break;}
   case Key_Down:{ad.x=-1;ad.y=0;MoveBack(ad);break;}
   case Key_Left:{ad.x=0;ad.y=1;MoveBack(ad);break;}
   case Key_Right:{ad.x=0;ad.y=-1;MoveBack(ad);break;}
         }
   StepNum--;Step[i]=0;BoxMove[i]=0;
   }
}


void MoveBack(Add a)   /*һ�������ƶ�*/
{
int i=(StepNum-1)%STEPMAX;
if(BoxMove[i]==0) 
   {
    map[p.x][p.y]-=1;InitPic(map[p.x][p.y],p.x,p.y);
    p.x=p.x+a.x;p.y=p.y+a.y;
    map[p.x][p.y]+=1;InitPic(map[p.x][p.y],p.x,p.y);
   }
else if(BoxMove[i]==1)
   {
   map[p.x-a.x][p.y-a.y]-=2;InitPic(map[p.x-a.x][p.y-a.y],p.x-a.x,p.y-a.y);
   map[p.x][p.y]+=1;InitPic(map[p.x][p.y],p.x,p.y);
   p.x=p.x+a.x;p.y=p.y+a.y;
   map[p.x][p.y]+=1;InitPic(map[p.x][p.y],p.x,p.y); 
   }
}


