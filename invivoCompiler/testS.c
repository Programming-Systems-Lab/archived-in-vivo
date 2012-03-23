//文曲星小游戏
/*这是文曲星里的小游戏，本人用C语言将其编出并添加破纪录功能。游戏规则：随机产生4个数字，由玩家输入4个不重复的数字（如：8 3 1 2）按回车后电脑给出提示，其形式为"?A?B".你所输入的四位数中，位置和数字都正确则为A，数字相同但位置不相同时为B，你只可以猜10次，若你所用的次数小于纪录保持者，则为破纪录。小作一篇，请多请教！
广东工业大学 廖龙彪QQ:316688817   http://russia.e63.cn (俄罗斯记事本！欢迎访问！！）*/ 
 
#include<stdlib.h> 
#include<time.h> 
#include<stdio.h> 

struct player /*创立结构体*/ 
{ 
    char name[20]; 
    int score ; 
} 
player ; 
int a[5],b[5],n1,n2,n3,i,j ;/*n1计算位置数字相同的数字个数n2计算数字相同但位置不相同的数字个数n3计算所猜次数*/ 
char ch ; 
main() 
{ 
    goread();/*调用读取破纪录者函数*/ 
    loop1 : 
    system("cls");/*清屏*/ 
    n1=0 ; 
    n3=0; 
    getnumber();/*调用随机产生4个数的函数*/ 
    while(n1<4&&n3<10) 
    { 
        ++n3 ; 
        n1=0 ; 
        n2=0 ; 
        printf("The top player:%s by only %i times Guess 4 numbers:",player.name,player.score); 
        for(i=1;i<=4;i++) 
        scanf("%d",&b[i]); 
        for(i=1;i<=4;i++) 
        { 
            if(a[i]==b[i]) 
            { 
                n1++; 
                continue ; 
            } 
            for(j=1;j<=4;j++) 
            if(b[i]==a[j])n2++; 
        } 
printf("%dA%dB you have guessed %d times\n",n1,n2,n3); 
    } 
    if(n3>10)printf("you blew it,the answer is %d%d%d%d",a[1],a[2],a[3],a[4]); 
    else 
    { 
        printf("Congratulastions!you finish it by %d times\n",n3); 
        if(n3<player.score) 
        breakrecord();/*调用新纪录者函数*/ 
    } 
    getchar(); 
    printf("Do you wanna play it again?(y/n)\n"); 
    ch=getchar(); 
    if(ch=='y')goto loop1 ; 
} 
goread()/*读取破纪录者函数*/ 
{ 
    FILE*fp ; 
    if((fp=fopen("player.txt","rb"))==NULL) 
    { 
        newset();/*调用初始化纪录者函数*/ 
        fp=fopen("player.txt","rb"); 
    } 
    if(fread(&player,sizeof(struct player),1,fp)!=1) 
    printf("file write error"); 
    fclose(fp); 
} 
newset()/*初始化纪录者函数*/ 
{ 
    struct player 
    { 
        char name[20]; 
        int score ; 
    } 
    
    player= 
    { 
        "along",10 
    } 
    ; 
    FILE*fp ; 
    if((fp=fopen("player.txt","wb"))==NULL) 
    { 
        printf("file open error\n"); 
        return ; 
    } 
    if(fwrite(&player,sizeof(struct player),1,fp)!=1) 
    printf("file write error"); 
    fclose(fp); 
    
} 
getnumber()/*随机产生4个数的函数*/ 
{ 
    random(); 
    for(i=1;i<=4;i++) 
    loop2 : 
    { 
        a[i]=rand()%10 ; 
        for(j=i-1;j>=1;j--) 
        if(a[i]==a[j])goto loop2 ; 
        
    } 
} 
breakrecord()/*新纪录者函数*/ 
{ 
    printf("What is you name?"); 
    scanf("%s",&player.name); 
    player.score=n3 ; 
    { 
        FILE*fp ; 
        if((fp=fopen("player.txt","wb"))==NULL) 
        { 
            printf("file open error\n"); 
            return ; 
        } 
        if(fwrite(&player,sizeof(struct player),1,fp)!=1) 
        printf("file write error"); 
        fclose(fp); 
    } 
} 
