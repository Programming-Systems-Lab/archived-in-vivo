package invite.net.message;

public class Id extends AMessage
{
    private int uid;

    public Id(int uid)
    {
        super();
        this.uid = uid;
    }

    public int getUid()
    {
        return this.uid;
    }

    public int hashCode()
    {
        return this.uid;
    }

    public boolean equals(Object o)
    {
        if (! (o instanceof Id)) return false;

        return this.uid == ((Id) o).uid;
    }

    public String toString()
    {
        return "(" + "uid = " + uid + ")";
    }
}
