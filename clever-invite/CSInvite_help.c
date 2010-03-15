/*
  This file contains the helper string functions.
*/
#include "CSInvite.h"

/* The Find Substring Function. (acquired from an online forum
   This function checks if the second char array is a substring 
   of the first. if yes then it returns 1 otherwise it returns 0.*/
int findsubstr( char *str, char *substr)
{
  if ( !*substr )
    {
      return 0;
    }
  for ( ; *str; ++str )
    {
      if ( *str == *substr )
	{
	  const char *h, *n;
	  for ( h = str, n = substr; *h && *n; ++h, ++n )
	    {
	      if ( *h != *n )
		{
		  break;
		}
	    }
	  if ( !*n )return 1;	    
	}
    }
  
  return 0;
}

/* Acquired from online blog. 
   Trims the white spaces from the 
   front and the end of the string.*/
char *trimwhitespace(char *str)
{
  char *end;
  
  /* Trim leading space*/
  while(isspace(*str)) str++;
  
  /* Trim trailing space*/
  end = str + strlen(str) - 1;
  while(end > str && isspace(*end)) end--;
  
  /* Write new null terminator */
  *(end+1) = 0;
  
  return str;
  
}

/* Split the string in two. 
   Takes the original and "splits" it in two smaller
   strings (doesn't affect the original).
   The split depends upon the open and close braces.
   The resulting slices are inside split1 and split2.*/
int split(char *original, char * split1, char * split2 , int scope)
{
  int i = 0;
  int j = 0;
  int flag = 0;
  
  while (original[i] != '\0')
    {
      if (flag == 0)
	{
	  if ((original[i] == '{') || (original[i] == '}'))
	    {
	      flag = 1;
	      split1[i] = '\0';
	      
	      if(original[i] == '{')
		{
		  scope = scope + 1;
		}
	      else if(original[i] == '}')
		{
		  scope = scope - 1;
		}
	    }
	  else
	    {
	      split1[i] = original[i];
	    }
	}
      else
	{
	  split2[j++] = original[i];
	}
      i++;
    }
  if(flag == 0)
    {
      split1[i] = '\0';
      return -1;
    }
  
  return scope;
}
