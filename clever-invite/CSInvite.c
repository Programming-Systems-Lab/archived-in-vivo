/*
  Include The Header File.
*/
#include "CSInvite.h"

/*
  Author:       Waseem Ilahi
  Purpose:      To find the variables (in the current file) that a given function/test depends upon.
  Description:  This "Program" takes a "compiled without errors/warnings" c file as its argument
                and parses it in such a way that for each function definition in the file, this program
				prints out the "external/non-local" variables this function depends
                upon. This is a small part of a bigger project. These variables are then used by the other
                parts of the project to do various tasks. The program hasn't been integrated with other
                components, therefore, for now it only prints out the variables, but those variables are
                stored inside a structure and can be passed along to any thing to be used.
  Use Case: 	The user gives the program a c file as the argument. After that the program goes into 
				the array of saved function definitions and prints out all the known dependencies for
				each function. This process happens for each function "sequencially".
  Limitations:  As you may have figured out, this parser only works on compilable c code. 
                Other than that there are a few other limitations. First of, the number of
                max global vairables and the function definations in each file is fixed to 128.
                Also, the program requires the length of each function defination to be less than 2048
                characters. This is excluding the comments spaces, tabs and new lines.
  Note:		Code was formatted with emacs on a linux machine.
*/

/*
  The Main Function.
*/
int main(int argc, char **argv)
{
  
  /* Check the arguments. */  
  if(argc != 2){
    fprintf(stderr, "\n Usage: get_var file_name.\n\n");
    exit(1);
  }
  
  /* Variable declarations. */
  
  /* The helper variables. */
  int i,j;
  int k = 0;	
 
  /* Total Statements in the file. */ 
  int statement_number;
  /* Total Constants in the file. */
  int total_constants = 0;
  /* Total Variables in the file. */
  int total_globals = 0;
  /* Total Functions in the file. */
  int total_functions = 0;
  int function_number = 0;
  /* Total Parameters in the function. (changes with each function the user wants to check).*/
  int total_params = 0;
  /* Total Stateents in the function definition. */
  int total_scoped_statements = 0;
  /* Total Local Variables. */
  int total_declared_local_variables = 0;
  /* Total Variables (used) inside the definition. */
  int total_dependent_variables = 0;
  /* Total Variables the function depends upon. */
  int total_variables = 0;
  int total_unique_variables = 0;
  
  /* The statemetns "read" from the file. */  
  char statements[MAX_NUMBER][MAX_LENGTH];	
  /* Glocal variables. */
  GlobalVar global_variables[MAX_NUMBER];
  /* Parameters. */
  Parameter parameters[MAX_NUMBER];
  /* Variables the function depends upon. */
  Variable variables[MAX_NUMBER];
  Variable unique_variables[MAX_NUMBER];
  /* Scoped statements of each function definition. */
  Scoped_Statements function_scoped_statements[MAX_NUMBER];
  /* Local variables inside each definition. */
  LocalVar declared_local_variables[MAX_NUMBER];
  /* Variables used inside the function definition. */
  char dependent_variables[MAX_NUMBER][VAR_LENGTH];	
  
  /* Initioalize the Variables. */	
  for(i = 0; i < MAX_NUMBER; i++){
    strcpy(global_variables[i].type, "");
    strcpy(global_variables[i].vars ,"");
    strcpy(parameters[i].type, "");
    strcpy(parameters[i].vars ,"");
    strcpy(variables[i].type, "");
    strcpy(variables[i].name ,"");
    strcpy(unique_variables[i].type, "");
    strcpy(unique_variables[i].name ,"");		
    strcpy(function_scoped_statements[i].statements,"");
    function_scoped_statements[i].scope = -1;
    function_scoped_statements[i].number = -1;
    strcpy(declared_local_variables[i].type,"");
    strcpy(declared_local_variables[i].name,"");
    declared_local_variables[i].scope = -1;
    declared_local_variables[i].number = -1;
    for(j =0; j < MAX_LENGTH; j++){
      statements[i][j] = '\0';
    }
  }
  for(i = 0; i < MAX_NUMBER; i++)
    for(j = 0; j < VAR_LENGTH; j++)
      dependent_variables[i][j] = '\0';
  
  /* Get the input from the file. (into the "statements" structure)*/
  if((statement_number = get_input(statements,argv[1])) == -1)exit(1);
  
  /* Find out the max constants in the file. */
  int possible_constants = get_possible_constants(statements, statement_number);
  
  if(possible_constants == 0)possible_constants++;
  
  /* Declare the global constant array, with size = max possible constants. */
  char global_constants[possible_constants][MAX_LENGTH];
  
  /* Initialize the array. */
  for( i = 0; i < possible_constants; i++)
    for( j = 0; j < MAX_LENGTH; j++)
      global_constants[i][j]='\0';
  
  /* Set the Global Constants and Variables. */
  total_constants = set_global_constants(statements,global_constants,statement_number);
  total_globals = set_global_variables(statements,global_variables,statement_number);
  
  /* Total "Possible" Funcion definitions. */
  for(i = 0; i < statement_number; i++)
    if(strlen(statements[i]) > 5)total_functions++;
  
  /* No Functions in the file. */
  if(total_functions == 0){
    fprintf(stderr, "\n No Function Definitions in the File. \n");
    exit(0);
  }	
  
  /* The data structure for holding the function names and their definition. */
  Functions function_list[total_functions];
  
  /* Initialize the structure to empty. */
  for(i = 0; i < total_functions; i++){
    strcpy(function_list[i].name, "");
    strcpy(function_list[i].definition, "");
  }
  
  /* Set the function names and the definition. */
  function_number = set_functions(statements,function_list,statement_number);
  
  /* The array that holds the names of the functions that have been checked
     during each pass. (This is used to avoid infinite recursion, when a function calls
     the function that calls it or itself.
  */
  char done_func[function_number][VAR_LENGTH];
  int total_done = 0;
  
  for(i = 0; i < function_number; i++)
    for(j = 0; j < VAR_LENGTH; j++)
      done_func[i][j] = '\0';
  
  if(function_number == 1){
    fprintf(stdout,"\n\n There is %d function definition in this file. \n",function_number);
  }
  else if(function_number > 1){
    fprintf(stdout,"\n\n There are %d function definitions in this file. \n",function_number);
  }
  fprintf(stdout, "\n ......................................................................\n\n");
  
  /* The main loop. */
  for(i = 0; i < function_number; i++){			
    
    fprintf(stdout,"\n\n Looking into the function \'%s\' \n\n",function_list[i].name);
    int i5;
    /* Reset the parameters. */
    for(i5 = 0; i5 < MAX_NUMBER; i5++){
      strcpy(parameters[i5].type, "");
      strcpy(parameters[i5].vars ,"");
    }
    
    /* Get the parameters for this function. */
    total_params = set_parameters(function_list[i].definition, parameters);
    
    /* Set the Scoped Statements array. */
    if( (total_scoped_statements = set_statement_scopes(function_list[i].definition, function_scoped_statements)) == 0)
      {
	/* Empty definition: continue */
	fprintf(stderr,"\n Empty Function Definition. \n");
	continue ;
      }			
    
    /* get the redeclared variables + their scopes (not all locals, but the ones that were redeclared). */
    total_declared_local_variables = set_declared_local_variables(function_scoped_statements, total_scoped_statements, declared_local_variables,parameters, total_params, global_variables, total_globals, global_constants,total_constants);
    
    /* Get the "dependent_variables" for each of the scoped statement. */
    for(j = 0; j < total_scoped_statements; j++)
      {
	k =  set_dependency(total_dependent_variables,function_scoped_statements[j].statements,function_scoped_statements[j].scope,function_scoped_statements[j].number,total_declared_local_variables,declared_local_variables,dependent_variables,function_list, parameters, global_variables, global_constants );					
	total_dependent_variables = total_dependent_variables + k;
      }
    
    /* add the function name to the done_func array. */
    strcpy(done_func[total_done],function_list[i].name);
    total_done++;					
    
    /* Get all the variables the function depends upon. */
    total_variables = set_variables(done_func,total_done,0,function_number,function_list,total_params, parameters,total_globals,global_variables,total_constants,global_constants,total_dependent_variables,dependent_variables,variables);					
    
    int var_found;
    
    /* Get the unique variables. assign them to unique_variables arraty. */	
    for(k = 0; k < total_variables; k++){
      var_found = 0;
      for(j = 0; j < total_unique_variables; j++){
	if( (strcmp(variables[k].name,unique_variables[j].name)) == 0){
	  var_found = 1;
	  break;
	}
      }
      if(var_found == 1)continue;
      else {
	strcpy(unique_variables[total_unique_variables].name , variables[k].name);
	strcpy(unique_variables[total_unique_variables].type , variables[k].type);
	total_unique_variables++;
      }
    }					
    /* Set the array length for variables. To "output the actual variables, we need to "convert" them back to original state
       from their stored staate.*/
    int i6,i7;
    int sub_size;
    for(i6 = 0; i6 < total_unique_variables; i6++){
      if( (findsubstr(unique_variables[i6].type,"*") == 1) && (findsubstr(unique_variables[i6].type,"[") == 1) ){
	strcat(unique_variables[i6].name,strchr(unique_variables[i6].type,'['));
	sub_size = strlen(unique_variables[i6].type) - strlen(strchr(unique_variables[i6].type,'['));
	for(i7 = sub_size; i7 <= strlen(unique_variables[i6].type); i7++){
	  unique_variables[i6].type[i7] = '\0';
	}
      }
    }
    
    /* Print the Variables, the Function depends upon. */
    if(total_variables > 0){
      fprintf(stdout, "\n\n Variables the function depends upon: \n\n");
      print_variables(unique_variables,total_unique_variables);					
    }
    else{ 
      fprintf(stdout,"\n\n No Known Dependency. \n\n");
    }
    
    fprintf(stdout, "\n ......................................................................\n\n");
    
    /* Reset all the variables that will be reused. */
    for(j = 0; j < MAX_NUMBER; j++){
      strcpy(variables[j].type , "");
      strcpy(variables[j].name , "");
      strcpy(unique_variables[j].type , "");
      strcpy(unique_variables[j].name , "");			
      strcpy(function_scoped_statements[i].statements,"");
      function_scoped_statements[i].scope = -1;
      function_scoped_statements[i].number = -1;
      strcpy(declared_local_variables[i].type,"");
      strcpy(declared_local_variables[i].name,"");
      declared_local_variables[i].scope = -1;
      declared_local_variables[i].number = -1;	
      for(k = 0; k < VAR_LENGTH; k++)
	dependent_variables[j][k] = '\0';
    }
    
    total_dependent_variables = 0;
    total_variables = 0;
    total_unique_variables = 0;
    
    for(k = 0; k < function_number; k++)
      for(j = 0; j < VAR_LENGTH; j++)
	done_func[k][j] = '\0';
    
    total_done = 0;		
    
    k = 0;
    total_dependent_variables = 0;
    
  }/* End for. */
  
  fprintf(stdout, "\n");
  
  return 0;
}
