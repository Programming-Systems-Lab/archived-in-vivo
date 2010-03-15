#ifndef CSINVITE_H
#define CSINVITE_H

/* 
   Include all the required header files 
*/
# include <stdio.h>
# include <stdlib.h>
# include <string.h>
# include <stddef.h>
# include <ctype.h>

/*
  Required Macros
*/
# define MAX_NUMBER 128
# define MAX_LENGTH 2048
# define VAR_LENGTH 255

/*
  This structure represents a global variable
*/
typedef struct globals {
  char type[VAR_LENGTH];
  char vars[VAR_LENGTH];
}GlobalVar;

/*
  This structure represents a parameter
*/
typedef struct params {
  char type[VAR_LENGTH];
  char vars[VAR_LENGTH];
}Parameter;

/*
  This structure represents a variable
*/
typedef struct final {
  char type[VAR_LENGTH];
  char name[VAR_LENGTH];
}Variable;

/*
  This structure represents a function
*/
typedef struct funcs{
  char name[MAX_NUMBER];
  char definition[MAX_LENGTH];
}Functions;

/*
  This structure represents a scoped & numbered statement
*/
typedef struct scoped_statements{
  char statements[MAX_LENGTH];
  int scope ;
  int number;
}Scoped_Statements;

/*
  This structure represents a scoped (local) variable
*/
typedef struct scoped_variables{
  char type[VAR_LENGTH];
  char name[VAR_LENGTH];
  int number;
  int scope;
}LocalVar;

/*
  These functions get called once for each time the program executes (defined in CSInvite_set_input.c)
*/
extern int get_input(char (*statements)[MAX_LENGTH], char *filename);
extern int get_possible_constants(char (*statements)[MAX_LENGTH], int statement_number);
extern int set_global_constants(char (*statements)[MAX_LENGTH],char (*global_constants)[MAX_LENGTH], int statement_number);
extern int set_global_variables(char (*statements)[MAX_LENGTH],GlobalVar global_variables[], int statement_number);
extern int set_functions(char (*statements)[MAX_LENGTH],Functions function_list[],int statement_number);

/*
  These Functions are local to each function (defined in CSInvite_set_func.c)
*/
extern int find_function(int function_number,char *funcs, Functions function_list[]);
extern int set_parameters(char *definition, Parameter parameters[]);
extern int set_statement_scopes(char * definition, Scoped_Statements function_scoped_statements[]);
extern int set_declared_local_variables(Scoped_Statements function_scoped_statements[], int total_scoped_statements, LocalVar declared_local_variables[],Parameter parameters[],int total_params, GlobalVar global_variables[], int total_globals, char (*global_constants)[MAX_LENGTH], int total_constants);

/*
  These functions are called for each function that needs to be checked (recursive calls + original) (defined in CSInvite_set_vars.c)
*/
extern int set_dependency(int total_dependent_variables,char *statement,int scope,int number, int total_local_variables, LocalVar local_variables[],char (*dependent_variables)[VAR_LENGTH],Functions function_list[],Parameter parameters[],GlobalVar global_variables[],char (*global_constants)[MAX_LENGTH]);
extern int set_variables(char (*done_func)[VAR_LENGTH], int total_done,int total_variables,int function_number, Functions function_list[],int total_params,Parameter parameters[],int total_globals,GlobalVar global_variables[],int total_constants,char (*global_constants)[MAX_LENGTH],int total_dependent_variables,char (*dependent_variables)[VAR_LENGTH],Variable variables[]);
extern int get_func_vars(char (*done_func)[VAR_LENGTH], int total_done,char *func,int function_number,int total_globals,int total_constants,char *definition,int total_dependent_variables,char (*dependent_variables)[VAR_LENGTH],int total_variables,Variable variables[],Functions function_list[],GlobalVar global_variables[],char (*global_constants)[MAX_LENGTH]);

/*
  String Helper Functions (defined in CSInvite_help.c)
*/
extern char *trimwhitespace(char *str);
extern int findsubstr(char *string , char *substring);
extern int split(char *original, char * split1, char * split2 , int scope);

/*
  The Print Functions (defined in print_strings.c)
*/
extern void print_global_vars(GlobalVar g_vars[], int total);
extern void print_functions(Functions func_list[], int total);
extern void print_Local_variables(LocalVar vars[], int total);
extern void print_params(Parameter parameters[], int total_params);
extern void print_variables(Variable variables[], int total_variables);
extern void print_output(char (*strings)[MAX_LENGTH], int string_number);
extern void print_scoped_statements(Scoped_Statements statements[], int total);
extern void print_dependent_variables(char (*dependent_variables)[VAR_LENGTH],int total_dependent_variables);

#endif
