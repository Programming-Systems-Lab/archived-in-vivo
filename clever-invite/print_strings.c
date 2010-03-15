/*
  This file contains th eoptional print functions used to print various structures.
*/
#include "CSInvite.h"

/* This file contains all the print functions. Anyone of them can be used in the 
   source to check what the corresponding data structure holds at that moment. 
*/

/*
  Generic Two dimensional array with dimensions [string_number][MAX_LENGTH] printer
*/
void print_output(char (*strings)[MAX_LENGTH], int string_number)
{
  int i;
  
  for(i = 0; i < string_number; i++ )
    fprintf(stdout, "%d. %d. %s  \n\n",i,strlen(strings[i]),strings[i]);
  
  return ;
}

/*
  This prints out the Global Variables.
*/
void print_global_vars(GlobalVar g_vars[], int total)
{
  int i;
  
  for(i = 0; i < total; i++){
    fprintf(stdout, "%d. %d. %s  \n\n",i,strlen(g_vars[i].type),g_vars[i].type);
    fprintf(stdout, "%d. %d. %s  \n\n",i,strlen(g_vars[i].vars),g_vars[i].vars);
  }
  
  return ;
}

/*
  This prints out the Local Variables.
*/
void print_Local_variables(LocalVar vars[], int total)
{
  int i;
  
  for(i = 0; i < total; i++){
    fprintf(stdout, "%d. %d. %d. %s  \n\n",vars[i].number,vars[i].scope,strlen(vars[i].type),vars[i].type);
    fprintf(stdout, "%d. %d. %d. %s  \n\n",vars[i].number,vars[i].scope,strlen(vars[i].name),vars[i].name);
  }
  
  return ;
}

/*
  This prints out the scoped statements.
*/
void print_scoped_statements(Scoped_Statements statements[], int total)
{
  int i;
  
  for(i = 0; i < total; i++){
    fprintf(stdout, "%d. %d. %d. %s  \n\n",statements[i].number,statements[i].scope,strlen(statements[i].statements),statements[i].statements);
  }
  
  return ;
}

/*
  This prints out the possible variables that the function depends upon.
*/
void print_dependent_variables(char (*dependent_variables)[VAR_LENGTH],int total_dependent_variables)
{
  
  int i;
  
  for(i = 0; i < total_dependent_variables; i++ )
    fprintf(stdout, "%d. %d. %s  \n\n",i,strlen(dependent_variables[i]),dependent_variables[i]);
  
  return ;
  
}

/*
  This prints out the Parameters.
*/
void print_params(Parameter parameters[], int total_params)
{
  
  int i;
  
  for(i = 0; i < total_params; i++){
    fprintf(stdout, " %d. %d. %s  \n\n",i,strlen(parameters[i].type),parameters[i].type);
    fprintf(stdout, " %d. %d. %s  \n\n",i,strlen(parameters[i].vars),parameters[i].vars);
  }
  
  return ;
  
}

/*
  This prints out the Final Variables the function depends upon.
*/
void print_variables(Variable variables[], int total_variables)
{
  
  int i;
  
  for(i = 0; i < total_variables; i++){
    if( (strlen(variables[i].type) > 0 ) && (strlen(variables[i].name) > 0)){
      fprintf(stdout, " %d. %d. %s  \n\n",i,strlen(variables[i].type),variables[i].type);
      fprintf(stdout, " %d. %d. %s  \n\n",i,strlen(variables[i].name),variables[i].name);
    }
  }
  
  return ;
  
}

/*
  This prints out the Entire function definition as stored in the program.
*/
void print_functions(Functions func_list[], int total)
{
  int i;
  
  for(i = 0; i < total; i++){
    fprintf(stdout, "%d. %d. %s  \n\n",i,strlen(func_list[i].name),func_list[i].name);
    fprintf(stdout, "%d. %d. %s  \n\n",i,strlen(func_list[i].definition),func_list[i].definition);
  }
  
  return ;
}
