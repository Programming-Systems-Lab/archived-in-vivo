package invite.demo;

public class Sorting
{
    static int sorts = 0;
    
    int max = 9;

    boolean testing = false;

    public int[] bubbleSort(int[] A)
    {
        if (testing) System.out.println("testing bubbleSort");
        else System.out.println("bubbleSort");

        for (int i = 0; i < A.length; i++)
        {
            for (int j = 0; j < A.length; j++)
            {
                if (A[i] < A[j])
                {
                    int temp = A[i];
                    A[i] = A[j];
                    A[j] = temp;
                }
            }
        }

        //System.out.println("Sorts" + sorts);
        return A;
    }

    public boolean testBubbleSort()
    {
        testing = true;

        System.out.println("testBubbleSort; sorts is " + sorts);
        //int[] test = { 5, 3, 9, 1, 4, 8, 6, 7, 2, 0 };
        int[] test = { 5, 3, 9 };
        
        int[] sorted = bubbleSort(test);
        
        for (int i = 0; i < sorted.length; i++)
        {
            if (i != sorted[i]) return false;
            //System.out.println(sorted[i]);
        }
        
        return true;
    }

    public static final void main(String[] args)
    {
        Sorting sorter = new Sorting();

        // randomly generate 1000 sequences of 1000 random numbers, then sort them
        int[] A = new int[1000];
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++)
        {
            for (int j = 0; j < A.length; j++)
            A[j] = (int)(Math.random() * 100);

            sorter.bubbleSort(A);
        }

        long end = System.currentTimeMillis();
        long time = end - start;

        System.out.println("Time: " + time + "!!");
    }
}
