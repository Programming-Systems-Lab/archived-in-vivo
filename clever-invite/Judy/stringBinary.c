#include <stdio.h>
#include <string.h>
#include <stdlib.h>

static void pr_hexbytes(const unsigned char *bytes, int nbytes)
/* Print bytes in hex format + newline */
{
   int i;
   for (i = 0; i < nbytes; i++)
   	printf("%02X ", bytes[i]);
   printf("\n");
}

int main()
{
   char szStr[] = "Hello world!";
   unsigned char *lpData;
   long nbytes;
   char *lpszCopy;

   /* Convert string to bytes */
   /* (a) simply re-cast */
   lpData = (unsigned char*)szStr;
   nbytes = strlen(szStr);
   pr_hexbytes(lpData, nbytes);

   /* (b) make a copy */
   lpData = malloc(nbytes);
   memcpy(lpData, (unsigned char*)szStr, nbytes);
   pr_hexbytes(lpData, nbytes);

   /* Convert bytes to a zero-terminated string */
   lpszCopy = malloc(nbytes + 1);
   memcpy(lpszCopy, lpData, nbytes);
   lpszCopy[nbytes] = '\0';
   printf("'%s'\n", lpszCopy);

   free(lpData);
   free(lpszCopy);

   return 0;
}

