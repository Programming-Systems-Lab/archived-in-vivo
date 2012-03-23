/*
  This file has the functions that get called the most.
  These functions take what we have so far and figure
  out the variables the function depends upon and set the
  data structure that holds these variables.
*/
# include "CSInvite.h"

/*
  This function gets the variable names that are used inside the current statement in a way, that
  the function's behavior depends upon the value they have at the moment.
*/
int set_dependency(int total_dependent_variables,char *statement, int scope ,int number, int total_local_variables, LocalVar local_variables[],char (*dependent_variables)[VAR_LENGTH],Functions function_list[],Parameter parameters[],GlobalVar global_variables[],char (*global_constants)[MAX_LENGTH])
{
  /* if any of the following is not in the statements then don't bother looking for anything. */
  if( (findsubstr(statement, "return") == 0) && (findsubstr(statement, ">") == 0) &&(findsubstr(statement, "<") == 0) &&(findsubstr(statement, "=") == 0) && (findsubstr(statement, "(") == 0) && (findsubstr(statement, "case ") == 0)  )return 0;
  if( strcmp(statement, "else") == 0)return 0;
  
  /* helper variables. */
  int i;
  char *running;
  char *token;
  char *tmpt;	
  int condition = 0;
  char tmp_dec[MAX_LENGTH];	
  int statement_number = 0;
  
  /* If the statement is a conditional or the loop. */
  if( (findsubstr(statement, "while(") == 1) || (findsubstr(statement, "for(") == 1) || (findsubstr(statement, "if(") == 1) || (findsubstr(statement, "switch(") ==  1)
      || (findsubstr(statement, "else(") == 1) ){
    
    /* set the flag. */
    condition = 1;
    
    running = strdup(statement);
    
    token = strtok(running,"("); 
    
    token = strtok(NULL,"");
    
    while( (token != NULL) &&  ((strcmp(token , " ") == 0) || (strlen(token) == 0))  )
      token = strtok(NULL,"(");		
    
    tmpt = token;
    
    for(i = 0; i < MAX_LENGTH; i++)
      {
	tmp_dec[i] = '\0';
      }
    
    if(token != NULL){
      int tmpy = 0;
      while(*tmpt != '\0'){
	tmp_dec[tmpy]=*tmpt;
	tmpy++;
	tmpt++;
      }
    }
  }
  else strcpy(tmp_dec, statement);/* simply copy the statement. */
  
  char key1[] = "=";
  char key2[] = "()"; 
  /* if the statement is not a condition or loop. */
  if(condition == 0  && ( strcspn(tmp_dec,key1) < strcspn(tmp_dec,key2)) ){
    
    running = strdup(tmp_dec);
    
    /* get everything from the right of the equal sign. */
    token = strtok(running, "=");		
    token = strtok(NULL, "=");
    
    tmpt = token;
    
    for(i = 0; i < MAX_LENGTH; i++)
      {
	tmp_dec[i] = '\0';
      }
    
    if(token != NULL){
      int tmpy = 0;
      while(*tmpt != '\0'){
	tmp_dec[tmpy]=*tmpt;
	tmpy++;
	tmpt++;
      }
    }		
  }
  
  running = strdup(tmp_dec);
  
  token = strtok(running, "=*+-%/!><&|(),[] ");
  
  /* get all the variables. */
  while(token != NULL)
    {	
      tmpt = token;
      
      for(i = 0; i < MAX_LENGTH; i++)
	{
	  tmp_dec[i] = '\0';
	}
      
      if(token != NULL){
	int tmpy = 0;
	while(*tmpt != '\0'){
	  tmp_dec[tmpy]=*tmpt;
	  tmpy++;
	  tmpt++;
	}
      }
      
      if( ((strlen(tmp_dec) > 1) || (strcmp(tmp_dec, " ") != 0) ) && (strcmp("return",tmp_dec) != 0)
	  && (strcmp("stdout",tmp_dec) != 0)&& (strcmp("stderr",tmp_dec) != 0)&& (strcmp("stdin",tmp_dec) != 0)){
	if(findsubstr(tmp_dec,".") == 1){
	  char key3[] = ".";
	  char tmp_tmp[MAX_LENGTH];	
	  
	  for(i = 0; i < MAX_LENGTH; i++)
	    {
	      tmp_tmp[i] = '\0';
	    }
	  
	  strcpy(tmp_tmp,tmp_dec);			
	  
	  for(i = 0; i < MAX_LENGTH; i++)
	    {
	      tmp_dec[i] = '\0';
	    }
	  
	  for(i = 0; i < (strlen(tmp_tmp) - (strlen(tmp_tmp) - strcspn(tmp_tmp,key3) ) );i++){
	    tmp_dec[i] = tmp_tmp[i];
	  }
	  
	}
	char *ptr = trimwhitespace(tmp_dec);
	int ii;
	int varFound = 0;
	/* look for a "local variable" and eliminate that variable if found. */
	for( ii = 0; ii < total_local_variables; ii++){
	  if((strcmp(ptr , local_variables[ii].name) == 0) && (local_variables[ii].scope <= scope) &&  (local_variables[ii].number <= number) ){
	    varFound = 1;
	    break;
	  }
	}
	if(varFound == 0){
	  strcpy(dependent_variables[total_dependent_variables + statement_number++],ptr);
	}
	
	
      }
      /* continue to the next. */
      token = strtok(NULL,"=*+-%/!><&|(),[]");
    }
  
  return statement_number;
  
}

/*
  This function sets the actual variables that the function depends upon. 
  The variables gotton from the set_dependency function are compared against globals and parameters to 
  finalize this choice. 
*/
int set_variables(char (*done_func)[VAR_LENGTH], int total_done,int total_variables,int function_number, Functions function_list[],int total_params,Parameter parameters[],int total_globals,GlobalVar global_variables[],int total_constants,char (*global_constants)[MAX_LENGTH],int total_dependent_variables,char (*dependent_variables)[VAR_LENGTH],Variable variables[])
{
  
  int j,k,l = 0, done,m;
  int var_found = 0;
  
  /* for each variable gotton from set_dependency function. */
  for(j = 0; j < total_dependent_variables; j++){
    /* check all the parameters and then see if it already is in the variable structure, if not then add it. */
    for(k = 0; k < total_params; k++){
      if( strcmp(dependent_variables[j] , parameters[k].vars) == 0){
	var_found = 0;
	for(l = 0; l < total_variables; l++){
	  if( strcmp(variables[l].name , dependent_variables[j]) == 0){
	    var_found = 1;
	    break;
	  }										
	}
	
	if(var_found == 1){
	  break;
	}
	else {
	  strcpy(variables[total_variables].type , parameters[k].type);
	  strcpy(variables[total_variables].name , parameters[k].vars);
	  total_variables++;
	  break;
	}
      }
    }
    
    /* Check the Globals Variables Now. */					
    for(k = 0; k < total_globals; k++){
      if( strcmp(dependent_variables[j] , global_variables[k].vars) == 0){
	var_found = 0;
	for(l = 0; l < total_variables; l++){
	  if( strcmp(variables[l].name , dependent_variables[j]) == 0){
	    var_found = 1;
	    break;
	  }										
	}
	
	if(var_found == 1){
	  break;
	}
	else {
	  strcpy(variables[total_variables].type , global_variables[k].type);
	  strcpy(variables[total_variables].name , global_variables[k].vars);
	  total_variables++;
	  break;
	}
      }
    }
    
    /* Check the Globals Constants Now. */						
    for(k = 0; k < total_constants; k++){
      if( strcmp(dependent_variables[j] , global_constants[k]) == 0){
	var_found = 0;
	for(l = 0; l < total_variables; l++){
	  if( strcmp(variables[l].name , dependent_variables[j]) == 0){
	    var_found = 1;
	    break;
	  }										
	}
	
	if(var_found == 1){
	  break;
	}
	else {
	  strcpy(variables[total_variables].type , "constant");
	  strcpy(variables[total_variables].name , global_constants[k]);
	  total_variables++;
	  break;
	}
      }
    }
    
    /* Find out whether the variable is a function or not. */
    k = find_function(function_number, dependent_variables[j], function_list);
    
    if( k == -1){
      continue;
    }
    else{
      
      /* the variable was a function name from the file we are looking at, check if it was already
	 "checked", if not, call "get_func_vars" to check the definition for that fnction. 
      */
      
      done = 0;
      for(m = 0; m < total_done; m++)
	if( strcmp(done_func[m],dependent_variables[j]) == 0){
	  done = 1;
	}
      
      if(done == 1)continue;
      
      strcpy(done_func[total_done++],dependent_variables[j]);
      
      /* this function goes into the "function named: dependent_variables[j]" and gets the "varaibles" from it. */
      int tmp_num = get_func_vars(done_func,total_done,dependent_variables[j],function_number,total_globals,total_constants,function_list[k].definition,total_dependent_variables,dependent_variables,total_variables,variables,function_list,global_variables,global_constants);
      total_variables += tmp_num;	
    }						
  }
  
  return total_variables;
  
}

/*
  This function gets called if there is a function call inside the definition and the function is one of the
  functions defined in the same file. This function goes through all the steps the calling function went through.
*/
int get_func_vars(char (*done_func)[VAR_LENGTH], int total_done,char *func,int function_number,int total_globals,int total_constants,char *definition,int total_dependent_variables,char (*dependent_variables)[VAR_LENGTH],int total_variables,Variable variables[],Functions function_list[],GlobalVar global_variables[],char (*global_constants)[MAX_LENGTH])
{
  /* helpers. */
  int total_params = 0;
  int new_variables = 0;
  int i,j,k;
  
  /* just so that we have somethng to send to the called functions (we don't care for them.)*/
  Parameter parameters[5];
  int total_function_statements = 0;
  int total_declared_local_variables = 0;
  /* variables for this function's definition. */
  Scoped_Statements function_statements[MAX_NUMBER];
  LocalVar declared_local_variables[MAX_NUMBER];
  char new_dependents[MAX_NUMBER][VAR_LENGTH];
  int new_total_dependent = 0;
  
  /* initialize to zero. */	
  for(i = 0; i < MAX_NUMBER; i++)
    for(k = 0; k < VAR_LENGTH; k++)
      new_dependents[i][k] = '\0';
  
  for(i = 0; i < MAX_NUMBER; i++){
    strcpy(function_statements[i].statements , "");
    function_statements[i].scope = -1;
    strcpy(declared_local_variables[i].type,"");
    strcpy(declared_local_variables[i].name,"");
    declared_local_variables[i].scope = -1;
  }
	
  for(i = 0; i < 5; i++){
    strcpy(parameters[i].type,"");
    strcpy(parameters[i].vars,"");
  }
  
  for(j = 0; j < total_dependent_variables; j++){
    if(strcmp(func,dependent_variables[j]) == 0)break;
  }
  j = j + 1;		
  
  /* Set the statement scopes for the current function definition. */
  if( (total_function_statements = set_statement_scopes(definition,function_statements)) == 0)
    {
      return 0;
    }
  
  /* get the redeclared variables */
  total_declared_local_variables = set_declared_local_variables(function_statements, total_function_statements, declared_local_variables,parameters, total_params, global_variables, total_globals, global_constants,total_constants);
  
  /* Set dependency for each scoped statement. */
  for(j = 0; j < total_function_statements; j++)
    {
      k =  set_dependency(new_total_dependent,function_statements[j].statements , function_statements[j].scope,function_statements[j].number, total_declared_local_variables,declared_local_variables,new_dependents,function_list, parameters, global_variables, global_constants );					
      new_total_dependent = new_total_dependent + k;
    }
  
  /* Get all the variables the function depends upon. */
  if(new_total_dependent > 0){
    new_variables = set_variables(done_func,total_done,total_variables,function_number,function_list,total_params, parameters,total_globals,global_variables,total_constants,global_constants,new_total_dependent,new_dependents,variables);					
  }				
  return new_variables;
}
