package invite.demo;

public class SuperSimple 
{
    public static void main(String[] args)
    {
        System.out.println("Hi!!!");
        new SuperSimple().go();
    }

    public void go()
    {
        add(2, 5);
        //multiply(4, 5);
        //multiply(4, add(5, 1));

        /*
        Sorter sorter = new Sorter();

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
        */
    }

    public int add(int a, int b) { return a + b; }

    public int multiply(int a, int b) { return a * b; }

    public boolean testAdd()
    {
        System.out.println("Testing Add()!");
        //if (add(5, 4) != 9) return false;
        //if (add(0, 0) != 0) return false;
        return true;
    }

    public boolean testMultiply()
    {
        System.out.println("Testing Multiply()!");
        //if (multiply(5, 4) != 20) return false;
        //if (multiply(0, 0) != 0) return false;
        return true;
    }
}
