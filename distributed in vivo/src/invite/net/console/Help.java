package invite.net.console;

public class Help extends AAction
{
    public Help(String command)
    {
        super(command);
    }

    public void act()
    {
        println("This is help.");
    }
}
