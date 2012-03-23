/*
  This file contains the functions that get called
  on the input file. These function set the "globals"
  to be used later on. 
*/
#include "CSInvite.h"

/*
  Get the input and put it into an array of strings.
  This function takes the "statements" structure and the filename.
  Opens the file reads it and "parses" it to the "statements"
  structure. It returns the total number of statements in the file.
*/
int get_input(char (*statements)[MAX_LENGTH], char *filename)
{
  char ch;/* read character by character into this variable. */
  int brace = 0;
  int i;
  char line[MAX_LENGTH];/* represents each line. */
  char d_line[MAX_LENGTH];
  int statement_number = 0;/* count the total. */
  FILE *input;
  
  /* initialize to zero. */
  for(i = 0; i < MAX_LENGTH; i++){
    d_line[i] = '\0';
    line[i] = '\0';
  }
  /* Error opening the file, return error. */
  if((input = fopen( filename, "r" )) == NULL)
    {
      fprintf(stderr, "\n File Could not be opened for reading. \n\n");
      return -1;
    }
  
  /* Get the first character from the file. */
  ch = getc( input );
  
  /* continue until reaching the end of the file. */
  while( ch != EOF ) {
    
    /* Ignore newline. */
    if(ch == '\n'){
      ch = getc(input);
      continue;
    }
    
    /* Start of the macro. Handle it till hitting newline. */
    if(ch == '#'){
      line[strlen(line)] = ch;
      while(ch != '\n'){
	ch = getc( input );  
	line[strlen(line)] = ch;
      }
      if(findsubstr(line,"define")==1){
	
	if(statement_number >= MAX_LENGTH)break;
	
	strcpy(statements[statement_number] , line);
	
	statement_number ++;
		
      }
      /* reset the line array. */
      for(i = 0; i < MAX_LENGTH; i++)
	line[i] = '\0';
      
      ch = getc( input );
      
      continue;
    }
    
    /* If it's a comment ignore it. */
    /* This comment is ignored till the end of the line. */
    if(ch == '/'){
      ch = getc(input);
      if( ch  == '/'){
	while(1){
	  ch = getc( input );
	  if(ch == '\n'){
	    ch = getc(input);
	    break;
	  }
	}
     	continue;
      }/* this comment is ignored till we hit its end */
      else if( ch  == '*'){
	while(1){
	  ch = getc( input );
	  if(ch == '*'){
	    if( (ch = getc(input)) == '/'){
	      ch = getc(input);
	      break;
	    }
	  }
	}
	continue;
      }
    }
    /* ignore tabs. */
    if(ch == '\t'){
      ch = getc(input);
      continue;
    }
    
    /* for our purpose, ignore the string literals. */
    if(ch == '"'){
      while(1){
	ch = getc( input );
	if(ch == '"'){
	  ch = getc(input);
	  break;
	}
      }
      continue;
    }
    
    /* Divide the statements into the blocks "separated by the braces" */
    if(ch == '{')brace++;
    
    if( (ch == '}') || (ch == ';') ){
      if(ch == '}')brace--;
      if( brace != 0){		
	line[strlen(line)] = ch;
	ch = getc( input );
	continue;
      }
      
      if(ch == ';'){
	if((findsubstr(line,"(") == 1) && (findsubstr(line, "=") == 0)){
	  for(i = 0; i < MAX_LENGTH; i++)
	    line[i] = '\0';
	  
	  ch = getc( input );
	  continue;
	}
      }
      
      /* reached the max size , return error. */	
      if(statement_number >= MAX_LENGTH)return -1;
      line[strlen(line)] = ch;
      /* set the statements. */
      strcpy(statements[statement_number] , line);
      
      statement_number ++;
      
      for(i = 0; i < MAX_LENGTH; i++)
	line[i] = '\0';
      
      ch = getc( input );
      
      continue;
    }
    
    line[strlen(line)] = ch;
    
    ch = getc( input );
  }
  
  /* Close the file. */
  fclose(input);
  return statement_number;
}

/* find the max possible constants in the file. and return the number.*/
int get_possible_constants(char (*statements)[MAX_LENGTH], int statement_number)
{
  int total_constants = 0;
  int i;
  
  for(i = 0; i < statement_number; i++)
    if( (findsubstr(statements[i],"define") == 1)  && (strlen(statements[i]) > 2))
      total_constants++;
  
  return total_constants;
  
}

/*
  Takes the "Statements" structure and the "global_constants" strucuture and sets the 
  later with the global constants found inside the statements structure. 
*/
int set_global_constants(char (*statements)[MAX_LENGTH],char (*global_constants)[MAX_LENGTH], int statement_number)
{
  int total_constants = 0;	
  int i;
  char *running;
  char *token;
  char *tmpt;
  
  for(i = 0; i < statement_number; i++){ /* for each statement. */
    if( (findsubstr(statements[i],"define") == 1)  && (strlen(statements[i]) > 2)){/* if it is a macro. */
      running = strdup(statements[i]);/* copy the statement. */
      token = strtok(running , " ");/* this tokenizes the "running" string based on the second argument, which in this case is " " */
      token = strtok(NULL , " ");
      token = strtok(NULL , " ");
      tmpt = token;
      if(token != NULL){
	while(*tmpt != '\0'){
	  /* set the global_constant. */
	  sprintf(global_constants[total_constants],"%s%c",global_constants[total_constants],*tmpt);
	  
	  tmpt++;
	}
      }
      total_constants++;
      /* empty out the statements that held the constant. */
      strcpy(statements[i],"\0");
    }
  }
  
  return total_constants;
  
}

/*
  This function takes the statements structure and global_variable structure,
  finds the global variables, assigns them to the structure and returns the 
  number of variables found. 
*/
int set_global_variables(char (*statements)[MAX_LENGTH],GlobalVar global_variables[], int statement_number)
{
  int i,j;
  char *running;
  char *token;
  char *tmpt;
  int total_globals = 0;
  char tmp_dec[MAX_LENGTH];
  char tmp_dec2[MAX_LENGTH];
  char *each_running;
  int tmpyy = 0;
  char tmp_type[VAR_LENGTH];
  
  /* for every statement. */
  for(i = 0; i < statement_number; i++){
    /* if it's not a function definition. */
    if((findsubstr(statements[i],"{") == 0) && (strlen(statements[i]) > 2)){
      
      /* reset the tmp arrays. */
      strcpy(tmp_dec , "");
      strcpy(tmp_dec2 , "");
	  strcpy(tmp_type , "");
      
      /* dublicate the statements.*/
      running = strdup(statements[i]);
      /* get the first token. */
      token = strtok(running , ";");
      
      tmpt = token;
      
      /* assign the token to the "tmp_dec" temporary array. */
      if(token != NULL){
	int tmpy = 0;
	while(*tmpt != '\0'){
	  tmp_dec[tmpy]=*tmpt;
	  tmpy++;
	  tmpt++;
	}
      }
      
      /* If it is not the multiple variable declaration. */
      if( findsubstr(tmp_dec,",") == 0)
	{
	  /* dublicate. */
	  running = strdup(tmp_dec);
	  
	  /* reset. */
	  for(j = 0; j < MAX_LENGTH; j++)
	    {
	      tmp_dec[j] = '\0';
	    }
	  
	  /* get the token. (removes the right hand side of the equal sign if there is any.)*/
	  token = strtok(running,"=");
	  
	  tmpt = token;
	  
	  /* assign. */
	  if(token != NULL){
	    int tmpy = 0;
	    while(*tmpt != '\0'){
	      tmp_dec[tmpy]=*tmpt;
	      tmpy++;
	      tmpt++;
	    }
	  }
	  
	  /* dublicate the "substring. */
	  running = strdup(tmp_dec);		
	  
	  for(j = 0; j < MAX_LENGTH; j++)
	    {
	      tmp_dec[j] = '\0';
	    }
	  
	  /* get the type. */
	  token = strtok(running," ");
	  
	  tmpt = token;
	  
	  if(token != NULL){
	    int tmpy = 0;
	    while(*tmpt != '\0'){
	      tmp_dec[tmpy]=*tmpt;
	      tmpy++;
	      tmpt++;
	    }
	    tmpyy = tmpy;
	  }
	  
	  /* assign to the structure. */
	  strcpy(global_variables[total_globals].type,tmp_dec);
	  
	  for(j = 0; j < MAX_LENGTH; j++)
	    {
	      tmp_dec[j] = '\0';
	    }
	  /* If the token was one of the following we need to get the next token again for the type of the variable. */
	  if( (strcmp(global_variables[total_globals].type,"struct") == 0) || (strcmp(global_variables[total_globals].type,"unsigned") == 0)
	      || (strcmp(global_variables[total_globals].type,"long") == 0) || (strcmp(global_variables[total_globals].type,"short") == 0))
	    {
	      token = strtok(NULL , " ");
	      
	      if(token != NULL){
		tmpt = token;
		global_variables[total_globals].type[tmpyy++] = ' ';
		while(*tmpt != '\0'){
		  global_variables[total_globals].type[tmpyy]=*tmpt;
		  tmpyy++;
		  tmpt++;
		}				
	      }		
	    }
	  
	  for(j = 0; j < MAX_LENGTH; j++)
	    {
	      tmp_dec[j] = '\0';
	    }
	  /* This is for the variable name. */
	  token = strtok(NULL,"[]()");
	  
	  if(token != NULL){
	    
	    tmpt = token;
	    /* if there are two stars then we need to add them to the type. (this is for the internal storage)
	       it gets changed back before outputing the vasriables to the user. 
	    */
	    if(findsubstr(token,"**") == 1){
	      global_variables[total_globals].type[tmpyy++] = '*';
	      global_variables[total_globals].type[tmpyy++] = '*';
	      tmpt++;
	      tmpt++;
	    }
	    else if(findsubstr(token,"*") == 1){
	      global_variables[total_globals].type[tmpyy++] = '*';
	      tmpt++;
	    }			
	    
	    int tmpy = 0;
	    while(*tmpt != '\0'){
	      tmp_dec[tmpy]=*tmpt;
	      tmpy++;
	      tmpt++;
	    }		
	    
	    /* same thing with the array declarations. */
	    token = strtok(NULL , "[]()");				
	    
	    if( token != NULL){
	      
	      tmpt = token;		
	      global_variables[total_globals].type[tmpyy++] = '[';
	      
	      while(*tmpt != '\0'){
		global_variables[total_globals].type[tmpyy]=*tmpt;
		tmpyy++;
		tmpt++;
	      }
	      
	      global_variables[total_globals].type[tmpyy++] = ']';
	      
	    }
	    
	    token = strtok(NULL , "[]()");				
	    
	    if( (token != NULL) && (findsubstr(token,";") == 0)){
	      
	      tmpt = token;			
	      global_variables[total_globals].type[tmpyy++] = '[';
	      
	      while(*tmpt != '\0'){
		global_variables[total_globals].type[tmpyy]=*tmpt;
		tmpyy++;
		tmpt++;
	      }
	      
	      global_variables[total_globals].type[tmpyy++] = ']';
	      
	    }		
	    /* trim white spaces around the variable, before assigning it. */
	    char *ptr = trimwhitespace(tmp_dec);				
	    strcpy(global_variables[total_globals++].vars,ptr);
	  }
	  
	  tmpyy = 0;
	  
	  for(j = 0; j < MAX_LENGTH; j++)
	    {
	      tmp_dec[j] = '\0';
	    }
	}/* For Multiple Declarations. */
      else if( findsubstr(tmp_dec,",") == 1)
	{
	  /* Flag to check whether it's the first variable or not.(initially set)*/
	  int first = 1;
	  
	  /* create a back up, will need it to loop. */
	  strcpy(tmp_dec2,tmp_dec);
	  
	  /* same old, same old. */
	  running = strdup(tmp_dec);
	  
	  token = strtok(running,",");
	  
	  /* this is different, since it continues, until all the variables have been found. */
	  while(token != NULL) 
	    {
	      tmpt = token;
	      
	      for(j = 0; j < MAX_LENGTH; j++)
		{
		  tmp_dec[j] = '\0';
		}		
	      
	      int tmpy = 0;
	      while(*tmpt != '\0'){
		tmp_dec[tmpy]=*tmpt;
		tmpy++;
		tmpt++;
	      }
	      
	      each_running = strdup(tmp_dec);
	      
	      for(j = 0; j < MAX_LENGTH; j++)
		{
		  tmp_dec[j] = '\0';
		}
	      
	      token = strtok(each_running,"=");
	      
	      tmpt = token;
	      
	      if(token != NULL){
		int tmpy = 0;
		while(*tmpt != '\0'){
		  tmp_dec[tmpy]=*tmpt;
		  tmpy++;
		  tmpt++;
		}
		
	      }
	      
	      /* if it's the first variable, we need to set its type like in the previous section.
		 and then do the rest of the stuff.*/
	      if(first == 1){
		
		each_running = strdup(tmp_dec);
		
		for(j = 0; j < MAX_LENGTH; j++)
		  {
		    tmp_dec[j] = '\0';
		  }
		
		token = strtok(each_running," ");
		
		tmpt = token;
		
		if(token != NULL){
		  int tmpy = 0;
		  while(*tmpt != '\0'){
		    tmp_dec[tmpy]=*tmpt;
		    tmpy++;
		    tmpt++;
		  }
		  tmpyy = tmpy;
		}
		
		strcpy(global_variables[total_globals].type,tmp_dec);
		
		for(j = 0; j < MAX_LENGTH; j++)
		  {
		    tmp_dec[j] = '\0';
		  }
		
		if( (strcmp(global_variables[total_globals].type,"struct") == 0) || (strcmp(global_variables[total_globals].type,"unsigned") == 0)
		    || (strcmp(global_variables[total_globals].type,"long") == 0) || (strcmp(global_variables[total_globals].type,"short") == 0))
		  {
		    
		    token = strtok(NULL , " ");
		    
		    if(token != NULL){
		      //int tmpy = 0;
		      tmpt = token;
		      global_variables[total_globals].type[tmpyy++] = ' ';
		      while(*tmpt != '\0'){
			global_variables[total_globals].type[tmpyy]=*tmpt;
			tmpyy++;
			tmpt++;
		      }
		      
		    }
		    
		  } 
		
			strcpy(tmp_type , global_variables[total_globals].type);
		
		for(j = 0; j < MAX_LENGTH; j++)
		  {
		    tmp_dec[j] = '\0';
		  }
		
		token = strtok(NULL,"[]()");
		
		if(token != NULL){
		  
		  tmpt = token;
		  
		  if(findsubstr(token,"**") == 1){
		    global_variables[total_globals].type[tmpyy++] = '*';
		    global_variables[total_globals].type[tmpyy++] = '*';
		    tmpt++;
		    tmpt++;
		  }
		  else if(findsubstr(token,"*") == 1){
		    global_variables[total_globals].type[tmpyy++] = '*';
		    tmpt++;
		  }			
		  
		  int tmpy = 0;
		  while(*tmpt != '\0'){
		    tmp_dec[tmpy]=*tmpt;
		    tmpy++;
		    tmpt++;
		  }		
		  
		  token = strtok(NULL , "[]()");				
		  
		  if( token != NULL){
		    
		    tmpt = token;		
		    global_variables[total_globals].type[tmpyy++] = '[';
		    
		    while(*tmpt != '\0'){
		      global_variables[total_globals].type[tmpyy]=*tmpt;
		      tmpyy++;
		      tmpt++;
		    }
		    
		    global_variables[total_globals].type[tmpyy++] = ']';
		    
		  }
		  
		  token = strtok(NULL , "[]()");				
		  
		  if( token != NULL){
		    
		    tmpt = token;			
		    global_variables[total_globals].type[tmpyy++] = '[';
		    
		    while(*tmpt != '\0'){
		      global_variables[total_globals].type[tmpyy]=*tmpt;
		      tmpyy++;
		      tmpt++;
		    }
		    
		    global_variables[total_globals].type[tmpyy++] = ']';
		    
		  }		
		  char *ptr = trimwhitespace(tmp_dec);				
		  strcpy(global_variables[total_globals].vars,ptr);
		  total_globals++;
		}
		/* reset the flag. */		
		first = 0;
		/* get the next variable. */
		running = strdup(tmp_dec2);
		token = strtok(running, ",");
		for(j = 0; j < total_globals;j++){
		  if(token != NULL){token = strtok(NULL,",");}
		  else break;
		}
	      }/* if it is not the first variable, we already have its type so continue after that. */
	      else if( first == 0){
		
		strcpy(global_variables[total_globals].type , tmp_type);
				
		each_running = strdup(tmp_dec);
		
		for(j = 0; j < MAX_LENGTH; j++)
		  {
		    tmp_dec[j] = '\0';
		  }
		
		token = strtok(each_running,"[]()");
		
		if(token != NULL){
		  
		  tmpt = token;
		  
		  if(findsubstr(token,"**") == 1){
		    global_variables[total_globals].type[tmpyy++] = '*';
		    global_variables[total_globals].type[tmpyy++] = '*';
		    tmpt++;
		    tmpt++;
		  }
		  else if(findsubstr(token,"*") == 1){
		    global_variables[total_globals].type[tmpyy++] = '*';
		    tmpt++;
		  }			
		  
		  int tmpy = 0;
		  while(*tmpt != '\0'){
		    tmp_dec[tmpy]=*tmpt;
		    tmpy++;
		    tmpt++;
		  }		
		  
		  token = strtok(NULL , "[]()");				
		  
		  if( token != NULL){
		    
		    tmpt = token;		
		    global_variables[total_globals].type[tmpyy++] = '[';
		    
		    while(*tmpt != '\0'){
		      global_variables[total_globals].type[tmpyy]=*tmpt;
		      tmpyy++;
		      tmpt++;
		    }
		    
		    global_variables[total_globals].type[tmpyy++] = ']';
		    
		  }
		  
		  token = strtok(NULL , "[]()");				
		  
		  if( token != NULL){
		    
		    tmpt = token;			
		    global_variables[total_globals].type[tmpyy++] = '[';
		    
		    while(*tmpt != '\0'){
		      global_variables[total_globals].type[tmpyy]=*tmpt;
		      tmpyy++;
		      tmpt++;
		    }
		    
		    global_variables[total_globals].type[tmpyy++] = ']';
		    
		  }		
		  char *ptr = trimwhitespace(tmp_dec);				
		  strcpy(global_variables[total_globals].vars,ptr);
		  total_globals++;
		  
		  /* go to the next variable, if done exit. */
		  running = strdup(tmp_dec2);
		  token = strtok(running, ",");
		  for(j = 0; j < total_globals;j++){
		    if(token != NULL){token = strtok(NULL,",");}
		    else break;
		  }
		}
	      }
	      
	    }		
	  
	  tmpyy = 0;
	  
	  for(j = 0; j < MAX_LENGTH; j++)
	    {
	      tmp_dec[j] = '\0';
	      tmp_dec2[j] = '\0';
	    }
	  
	}
    }
  }
  
  return total_globals;	
}

/* 
   Set the function names and their definitions. 
   into the function_list data structure.
*/
int set_functions(char (*statements)[MAX_LENGTH],Functions function_list[],int statement_number)
{	
  int i;
  char *running;
  char *token;
  char *tmpt;
  int function_number = 0;
  
  for( i = 0 ; i < statement_number; i++){
    
    /* only look into the possible function definitions, (ignore other statements). */
    if( (strlen(statements[i]) > 5) && (findsubstr(statements[i],"(") == 1) && (findsubstr(statements[i], "{") == 1)){
      
      running = strdup(statements[i]);
      
      token = strtok(running , " *(");
      /* if the token is any of the following we need to go one step further. */
      if(token != NULL)
	if((strcmp(token,"struct") == 0) || (strcmp(token,"unsigned") == 0) || (strcmp(token,"long") == 0) || (strcmp(token,"short") == 0) )
	  token = strtok(NULL , " *(");
      if(token != NULL){
	token = strtok(NULL," *(");
      }
      
      /* set the function name. */
      if(token != NULL){
	tmpt = token;
	strcpy(function_list[function_number].name , tmpt);
	
	/* set the function definition. */
	strcpy(function_list[function_number].definition,statements[i]);
	
      }
      function_number++;
    }
    
  }
  return function_number;
}
