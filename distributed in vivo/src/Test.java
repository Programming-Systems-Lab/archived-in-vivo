import java.util.*;

public class Test
{
    public static final void main(String[] args)
    {
        System.out.println("Test");

        List<String> list = new ArrayList<String>();
        list.add("Hello");
        list.add("World");

        for (String word : list)
        {
            System.out.println(word);
        }
    }
}
