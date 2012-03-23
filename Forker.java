/**
 * This class is responsible for invoking a UNIX fork 
 * method through JNI.
 */

public class Forker 
{
    // declaration of native method
    private native int doFork();

    private native void doExit();

    /**
     * This method calls the native doFork method and returns
     * the process id of this particular process. The child will
     * have a pid of 0, the parent's will be unchanged.
     */
    public int fork()
    {
	// call the native method
	int pid = doFork();

	/**
	System.out.println("Got the pid " + pid);

	// if the pid is 0, then you're the child
	if (pid == 0)
	    System.out.println("Child " + pid);
	else
	    System.out.println("Parent " + pid);
	**/

	return pid;

    }

    /**
     * Used to exit the current process.
     */
    public void exit()
    {
	doExit();
    }


    // load the library
    static 
    {
	System.loadLibrary("forker");
    }

    /**
     * Main method just used for testing.
     */
    public static void main(String[] args) 
    {
	new Forker().fork();
    }

}
