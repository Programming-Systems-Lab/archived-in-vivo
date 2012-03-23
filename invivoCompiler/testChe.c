//�ı�ģʽ�µ�������
//��һ����������
//�������̵����Ͻ�����(MinX,MinY),���½�����(MaxX,MaxY)�����̴�СChessX=MaxX-MinX,ChessY=MaxY-MinY
//typedet struct chess
//{
//int x,y;
//}Position;Position Pos;������¼���ӵ�λ��
//ChessAttribute[ChessX][ChessY]��¼������ÿ�������(�գ������1ռ��,��o��ʾ�������2ռ��,��x��ʾ)
//NowPlayer���ڱ�ʾ��ǰ����

#include<stdio.h>
#include<stdlib.h>
#include<dos.h>
#include<conio.h>
#include<bios.h>
#define KeyW 0x1177
#define KeyS 0x1f73
#define KeyR 0x1372
#define KeyD 0x2064
#define KeyA 0x1e61
#define KeyJ 0x246a
#define KeyEnter 0x1c0d
#define KeyEsc 0x11b
#define KeyUp 0x4800
#define KeyDown 0x5000
#define KeyLeft 0x4b00
#define KeyRight 0x4d00
#define LeftUpPoint  0xda
#define LeftDownPoint 0xc0
#define RightUpPoint 0xbf
#define RightDownPoint 0xd9
#define RightLine 0xb4
#define LeftLine  0xc3
#define UpLine  0xc2
#define DownLine 0xc1
#define CrossPoint 0xc5
#define ChessNull 0
#define ChessPlayer1 'o'
#define ChessPlayer2 'x'

typedef struct chess
{
int x,y;
}Position;

Position Pos;
const int JudgeNum=4;    //��һ����������ִ���ж�ʤ���Ĵ�����'��'����
const int MinX=30,MinY=1,MaxX=81,MaxY=25;     //�������̵����Ͻ�����(MinX,MinY)�����½�����(MaxX,MaxY)
const int ChessX=MaxX-MinX,ChessY=MaxY-MinY;
const int InfoX=MinX+13,InfoY=MaxY;
int ChessAttribute[ChessX][ChessY], NowPlayer,key;

void Init();
void DrawChessboard(int ,int );
void GetHelp();
void ProcessKey();
void ProcessKey2();
void ProcessKey1();
int Win();
int WinLine(int);
int Judge(int ,int );
void Insure();
void Restart();
void SetMode();

void main()
{
SetMode();
GetHelp();
Init();//�����̲���ʼ������
ProcessKey();
}

void SetMode()
{
_AL=3;
_AH=0;
geninterrupt(0x10);
}

void Init()
{
int i,j;

textcolor(GREEN);
for (i=0;i<ChessX;i++)
    for(j=0;j<ChessY;j++)
  DrawChessboard(i,j);
gotoxy(InfoX,InfoY);
cprintf(" Player1 Go!");
for(i=0;i<ChessX;i++)
   for(j=0;j<ChessY;j++)
      ChessAttribute[i][j]=ChessNull;
NowPlayer=1;
Pos.x=MinX;Pos.y=MinY;
gotoxy(Pos.x,Pos.y);
}

void DrawChessboard(int x,int y)
{
gotoxy(MinX+x,MinY+y);
if(x==0&&y==0)
    {putch(LeftUpPoint);return;}
if(x==ChessX-1&&y==0)
    {putch(RightUpPoint);return;}
if(x==0&&y==ChessY-1)
    {putch(LeftDownPoint);return;}
if(x==ChessX-1&&y==ChessY-1)
    {putch(RightDownPoint);return;}
if(x==0)
    {putch(LeftLine);return;}
if(x==ChessX-1)
    {putch(RightLine);return;}
if(y==0)
    {putch(UpLine);return;}
if(y==ChessY-1)
    {putch(DownLine);return;}
putch(CrossPoint);
}

void GetHelp()
{
int i;
textbackground(BLACK);
clrscr();
textcolor(3);
char *Player1Help[6]={"       Player1:","press 'w' to move up","press 's' to move down","press 'd' to move right","press 'a' to move left","press 'j' to ensure"};
for(i=0;i<6;i++)
   {
   gotoxy(1,i+1);
   cprintf("%s",Player1Help[i]);
   }
char *Player2Help[6]={"       Player2:","to move up","to move down","to move right","to move left","press 'enter' to ensure"};
gotoxy(1,9);
cprintf("%s",Player2Help[0]);
for(i=1;i<5;i++)
    {
    gotoxy(1,i+9);
    cprintf("press '%c' %s",23+i,Player2Help[i]);
    }
gotoxy(1,14);
cprintf("%s",Player2Help[5]);
char *Info[6]={"Press 'r' to restart","Press 'esc' to exit game","author:  litigo","time:    10/6/2003","e-mail:  litigo@sohu.com","oicq:    25317747"};
for(i=0;i<2;i++)
    {
    gotoxy(1,i+17);
    cprintf("%s",Info[i]);
    }
for(i=2;i<6;i++)
    {
    gotoxy(1,i+19);
    cprintf("%s",Info[i]);
    }
}
void ProcessKey()
{
do
  {
   switch(NowPlayer)
  {
  case 1:ProcessKey1();break;
  case 2:ProcessKey2();break;
  }
  }while(1);
}

void ProcessKey2()
{
   key=bioskey(0);
   switch(key)
       {
       case KeyUp:
       {
       Pos.y--;
       if(Judge(Pos.x,Pos.y)) Pos.y++;  //�����ǰ���λ�ó������̵ķ�Χ(1)������԰���
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyDown:
       {
       Pos.y++;
       if(Judge(Pos.x,Pos.y)) Pos.y--;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyLeft:
       {
       Pos.x--;
       if(Judge(Pos.x,Pos.y)) Pos.x++;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyRight:
       {
       Pos.x++;
       if(Judge(Pos.x,Pos.y)) Pos.x--;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyEnter:
       {
        if(ChessAttribute[Pos.y-MinY][Pos.x-MinX]!=ChessNull) break;
        ChessAttribute[Pos.y-MinY][Pos.x-MinX]=ChessPlayer2;
        textcolor(RED);                //���2�����ӵ���ɫ
        putch(ChessAttribute[Pos.y-MinY][Pos.x-MinX]);
        if(Win())
       {
       gotoxy(MinX+10,MaxY+2);
       cprintf(" Player2 Win");
       sleep(5);
       textbackground(BLACK);
       Init();
       ProcessKey();
       }
        NowPlayer=1;
        gotoxy(InfoX,InfoY);
        cprintf(" Player%d Go!",NowPlayer);
        gotoxy(Pos.x,Pos.y);
        break;
       }
       case KeyEsc:Insure(); break;
       case KeyR  :Restart();break;
       }
}

void ProcessKey1()
{
   key=bioskey(0);
   switch(key)
       {
       case KeyW:
       {
       Pos.y--;
       if(Judge(Pos.x,Pos.y)) Pos.y++;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyS:
       {
       Pos.y++;
       if(Judge(Pos.x,Pos.y)) Pos.y--;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyA:
       {
       Pos.x--;
       if(Judge(Pos.x,Pos.y)) Pos.x++;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyD:
       {
       Pos.x++;
       if(Judge(Pos.x,Pos.y)) Pos.x--;
       gotoxy(Pos.x,Pos.y);
       break;
       }
       case KeyJ:
       {
        if(ChessAttribute[Pos.y-MinY][Pos.x-MinX]!=ChessNull) break;
        ChessAttribute[Pos.y-MinY][Pos.x-MinX]=ChessPlayer1;
        textcolor(YELLOW);   //���1���ӵ���ɫ
        putch(ChessAttribute[Pos.y-MinY][Pos.x-MinX]);
        if(Win())
       {
       gotoxy(MinX+10,MaxY+2);
       cprintf(" Player1 Win");
       getch();
       textbackground(BLACK);
       Init();
       ProcessKey();
       }
        NowPlayer=2;
        gotoxy(InfoX,InfoY);
        cprintf(" Player%d Go!",NowPlayer);
        gotoxy(Pos.x,Pos.y);
        break;
       }
       case KeyEsc:Insure();break;
       case KeyR  :Restart();break;
       }
}

int Win()
{
int i;
for(i=0;i<JudgeNum;i++)
   if(WinLine(i)) return 1;
return 0;
}

int WinLine(int Direction)
{
int SameCount=0;
Position Add,Move;
switch(Direction)
      {
      case 0:  //��ˮƽλ�������������ͬ����������һ��
      {
      Move.x=Pos.x-4;    //�ѹ������λ����������λ,�ж��Ƿ�ʤ����ֻҪ��һ��������
      Move.y=Pos.y;
      Add.x=1;
      Add.y=0;
      break;
      }
      case 1:  //����ֱλ�������������ͬ����������һ��
      {
      Move.x=Pos.x;     //�ѹ������λ����������λ,�ж��Ƿ�ʤ����ֻҪ��һ��������
      Move.y=Pos.y-4;
      Add.x=0;
      Add.y=1;
      break;
      }
      case 2:  //����бλ�������������ͬ����������һ��
      {
      Move.x=Pos.x-4;  //�ѹ������λ������������λ,�ж��Ƿ�ʤ����ֻҪ��һ��������
      Move.y=Pos.y+4;
      Add.x=1;
      Add.y=-1;
      break;
      }
      case 3:  //����бλ�������������ͬ����������һ��
      {
      Move.x=Pos.x-4;   //�ѹ������λ������������λ,�ж��Ƿ�ʤ����ֻҪ��һ��������
      Move.y=Pos.y-4;
      Add.x=1;
      Add.y=1;
      break;
      }
      }
for(int i=0;i<JudgeNum*2+1;i++)
   {
   if(!Judge(Move.x,Move.y))   //Move.x,Move.y��λ��û�г������̵ķ�Χ
  {
   if(ChessAttribute[Move.y-MinY][Move.x-MinX]==ChessAttribute[Pos.y-MinY][Pos.x-MinX])  

                                //�ж����������Ƿ������µ���ͬ
        {
  SameCount++;
  if(SameCount>=5)  return 1;   //���ͬһ�ߵ���ͬ���ӳ���4������ʤ��
        }
   else
        {
        SameCount=0;
        }

  }
   Move.x+=Add.x;
   Move.y+=Add.y;
   }
return 0;
}

int Judge(int xpos,int ypos)
{
 if(xpos<MinX||ypos<MinY||xpos>=MaxX||ypos>=MaxY)   return 1;
 else return 0;
}

void Insure()
{
int insure;
gotoxy(InfoX,InfoY);
cprintf("   Quit(Y/N)");
while(1)
    {
    insure=getch();
    if(insure=='Y'||insure=='y') exit(1);
    if(insure=='N'||insure=='n')
 {
 gotoxy(InfoX,InfoY);
 cprintf(" Player%d Go!",NowPlayer);
 gotoxy(Pos.x,Pos.y);
 break;
 }
    }
}
void Restart()
{
int restart;
gotoxy(InfoX,InfoY);
cprintf("Restart(Y/N)");
while(1)
    {
    restart=getch();
    if(restart=='Y'||restart=='y')
  {
  Init();
  ProcessKey();
  break;
  }
    if(restart=='N'||restart=='n')
  {
  gotoxy(InfoX,InfoY);
  cprintf(" Player%d Go!",NowPlayer);
  gotoxy(Pos.x,Pos.y);
  break;
  }
    }
}


