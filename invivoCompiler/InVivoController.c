#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/time.h>

/*
 * This function determines whether or not a test should be run for the function
 * provided as the argument. It returns 0 if a test should be run.
 * 
 * This function needs to keep track of the total number of concurrent processes
 * and limit that number. Will probably need a separate thread to do that.
 * 
 * Will also need to allow for a probabilistic execution of tests, based on a setting
 * for each function.
 */

static int MAX_CONCURRENT = 10;
static int MAX_OVERHEAD = 200;
static int volatile concurrentTests = 0;

static struct timeval inviteStartTime;
static long totalTestTime;
 
void initialize();
int should_run_test(char* function, double p);
void *waitForFinishedTest(void *file);
void write_to_pipe(int file);

extern int checkAffinity(int cpu);
extern int setAffinity(int cpu);

static int initialized = 0;

int timeval_subtract(struct timeval *result,
		     const struct timeval *x,
		     const struct timeval *y)
{
	struct timeval tmp;
	tmp.tv_sec = y->tv_sec;
	tmp.tv_usec = y->tv_usec;

	/* Perform the carry for the later subtraction */
	if (x->tv_usec < y->tv_usec) {
		int nsec = (y->tv_usec - x->tv_usec) / 1000000 + 1;
		tmp.tv_usec -= 1000000 * nsec;
		tmp.tv_sec += nsec;
	}
	if (x->tv_usec - y->tv_usec > 1000000) {
		int nsec = (x->tv_usec - y->tv_usec) / 1000000;
		tmp.tv_usec += 1000000 * nsec;
		tmp.tv_sec -= nsec;
	}
	/* Compute the time remaining to wait.
	   tv_usec is certainly positive. */
	result->tv_sec = x->tv_sec - tmp.tv_sec;
	result->tv_usec = x->tv_usec - tmp.tv_usec;
	/* Return 1 if result is negative. */
	return x->tv_sec < tmp.tv_sec;
}


void initialize()
{

	gettimeofday(&inviteStartTime, NULL);
	
	/* seed random */
	time_t seconds;
	time(&seconds);
	srand((unsigned int) seconds);
	totalTestTime = 0;

	initialized = 1;
}

int should_run_test(char* function, double p)
{

	// debugging
	//printf("function is %s\n", function);
	//printf("start: %d\n", inviteStartTime);
	
	

	if(!initialized)
		initialize();
	
	// secondary test to ensure we're not exceeding maximum specified overhead
	struct timeval currentTime, result;	
	gettimeofday(&currentTime, NULL);
	timeval_subtract(&result, &currentTime, &inviteStartTime);
	long runningTime = result.tv_sec*1000000 + result.tv_usec;
	float overhead = (float)totalTestTime * 100 / ((float)runningTime - (float)totalTestTime);
	
	//printf("overhead: %f\n", overhead);
	
	//since this is now being driven by overhead, no need for max concurrent tests?
	//if(concurrentTests < MAX_CONCURRENT)
	if(overhead < MAX_OVERHEAD)
	{
		//no need to compute this if we can easily check that we have too many tests first
		//float go = (float) rand() / (float) 0x7fffffff;
		//printf("%f => %f\n", go, p);
		//if(go < p)
			return 0;
	}
	return 1;
}

void *waitForFinishedTest(void *file)
{
	FILE *stream;
	int c;
	int *filep = (int *) file;
	stream = fdopen((int)file, "r");
	while((c = fgetc(stream)) != EOF)
	{
		putchar(c);
	}
	fclose(stream);
	//printf("test--\n");
	concurrentTests--;
}

void* getThread()
{
	return waitForFinishedTest;
}

void write_to_pipe(int file)
{
	FILE *stream;
	stream = fdopen(file, "w");
	fprintf(stream, "finished\n");
	fclose(stream);
}

void increaseTests()
{
	concurrentTests++;
	//printf("running %d tests\n", concurrentTests);
}

void updateTotalTestTime(struct timeval result)
{
	totalTestTime += (result.tv_sec*1000000 + result.tv_usec);
}
