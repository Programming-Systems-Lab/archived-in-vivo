//������С��Ϸ
/*�������������С��Ϸ��������C���Խ�����������Ƽ�¼���ܡ���Ϸ�����������4�����֣����������4�����ظ������֣��磺8 3 1 2�����س�����Ը�����ʾ������ʽΪ"?A?B".�����������λ���У�λ�ú����ֶ���ȷ��ΪA��������ͬ��λ�ò���ͬʱΪB����ֻ���Բ�10�Σ��������õĴ���С�ڼ�¼�����ߣ���Ϊ�Ƽ�¼��С��һƪ�������̣�
�㶫��ҵ��ѧ ������QQ:316688817   http://russia.e63.cn (����˹���±�����ӭ���ʣ�����*/ 
 
#include<stdlib.h> 
#include<time.h> 
#include<stdio.h> 

struct player /*�����ṹ��*/ 
{ 
    char name[20]; 
    int score ; 
} 
player ; 
int a[5],b[5],n1,n2,n3,i,j ;/*n1����λ��������ͬ�����ָ���n2����������ͬ��λ�ò���ͬ�����ָ���n3�������´���*/ 
char ch ; 
main() 
{ 
    goread();/*���ö�ȡ�Ƽ�¼�ߺ���*/ 
    loop1 : 
    system("cls");/*����*/ 
    n1=0 ; 
    n3=0; 
    getnumber();/*�����������4�����ĺ���*/ 
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
        breakrecord();/*�����¼�¼�ߺ���*/ 
    } 
    getchar(); 
    printf("Do you wanna play it again?(y/n)\n"); 
    ch=getchar(); 
    if(ch=='y')goto loop1 ; 
} 
goread()/*��ȡ�Ƽ�¼�ߺ���*/ 
{ 
    FILE*fp ; 
    if((fp=fopen("player.txt","rb"))==NULL) 
    { 
        newset();/*���ó�ʼ����¼�ߺ���*/ 
        fp=fopen("player.txt","rb"); 
    } 
    if(fread(&player,sizeof(struct player),1,fp)!=1) 
    printf("file write error"); 
    fclose(fp); 
} 
newset()/*��ʼ����¼�ߺ���*/ 
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
getnumber()/*�������4�����ĺ���*/ 
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
breakrecord()/*�¼�¼�ߺ���*/ 
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
