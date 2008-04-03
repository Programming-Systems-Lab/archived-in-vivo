package invite.net.console;

public class Exit extends AAction
{
    public Exit(String command)
    {
        super(command);
    }

    public void act()
    {
        println("You're leaving me.");
    }
}
