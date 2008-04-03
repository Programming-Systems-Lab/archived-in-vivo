package invite.etc;

import java.io.*;
import java.util.*;

public class Config
{
    private static Map<String, String> map = new HashMap<String, String>();

    public static void init(String fileName)
    {
        try
        {
            Scanner scanner = new Scanner(
                new BufferedReader(
                    new FileReader(
                        fileName
                        )
                    )
                );

            while (scanner.hasNext())
            {
                String s = scanner.next();

                String[] ss = s.split("=");

                map.put(ss[0], ss[1]);
            }

            scanner.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static boolean getBoolean(String s)
    {
        return Boolean.parseBoolean(map.get(s));
    }

    public static int getInt(String s)
    {
        return Integer.parseInt(map.get(s));
    }

    public static double getDouble(String s)
    {
        return Double.parseDouble(map.get(s));
    }

    public static String get(String s)
    {
        return map.get(s);
    }
}
