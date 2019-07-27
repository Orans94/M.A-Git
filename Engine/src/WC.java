import java.nio.file.Path;

public class WC
{
    private Path m_WorkingCopyDir;
    private Branch m_Head;

    public WC(Path i_Path)
    {
        m_WorkingCopyDir = i_Path;
        m_Head = new Branch(m_WorkingCopyDir.resolve(".magit"),"HEAD","master");
    }

    public String Status()
    {//TODO implement this method(check if clean or dirty)
        return "Clean";
    }

    public void SHA1()
    {

    }
}
