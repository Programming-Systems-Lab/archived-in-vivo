#include <stdio.h> 

/*
 *The specific implementation chosen for bijective Mapping
 *is the famous bijective Cantor Pairing Function. 
 */
float bijectiveMapping(float k1, float k2){ 
   float result = (1.0/2.0)*(k1 + k2)*(k1 + k2 + 1) + k2;
   printf("ReSULT : %f \n", result);
   return result; 
}
int bijectiveMapping_i(int k1, int k2){ 
   int result = (1.0/2.0)*(k1 + k2)*(k1 + k2 + 1) + k2;
   printf("ReSULT : %d \n", result);
   return result; 
}

int calculate_hash(int* array, int length) {
  int hash = 9; // arbitrary seed value
  int multiplier = 13; // arbitrary multiplier value
  int i;
  for (i = 0; i < length; i++) {
    hash = (hash * (multiplier+i)) + array[i];
  }
  return hash;
}

/*
*Calculate mapping for n-element array using the formula
*of getting array hash--then the collective mapping 
* of the first; last; and hashVal
*/
int arr_paramEncoding(int arr[], int length){
  int j;
  int cantVal;
  int hashVal = calculate_hash(arr, sizeof(arr)/sizeof(int));
  printf("array length is %d \n", length);
  printf("HashVal for array is %d \n", hashVal);
  //get mapping of first && last  && hash Value
  cantVal = bijectiveMapping(arr[0], arr[length - 1]);
  cantVal = bijectiveMapping(cantVal,hashVal);
  return cantVal; 
}

int main(){
  float testVals[10] = {1.3, 2.5, 3.6, 4.8, 5.0, 6.32, 7, 8.3, 9.7, 10.03};
  int testLength = sizeof(testVals)/sizeof(float);
  int testVals_i[10] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
  int testLength_i = sizeof(testVals_i)/sizeof(int);

  float  k1, k2, cantorVal;
 // paramEncoding(testVals, testLength);
/*Testing for n size arrays */
/*
*int arrMapping = arr_paramEncoding(testVals_i,testLength_i);
*printf("Final Value is %d \n", arrMapping); 
*/

/*
*Use to test the uniqueness of the cantor Function 
*/

  printf("Reading integers from standard input\n");
  printf("------------------------------------\n\n");
  printf("Enter two numbers separated by space: ");
  scanf("%f%f", &k1, &k2);
  printf("The input displayed as decimal integers is: %f %f\n",  k1, k2);
  printf("The bijective Mapping of these two is:    %f \n", bijectiveMapping(k1,k2));  

  //paramEncoding(testVals, testLength);
  return 0;
 
}

