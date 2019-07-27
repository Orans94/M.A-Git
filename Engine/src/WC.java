import java.nio.file.Path;

public class WC
{
    // First way
    private Path m_WorkingCopyDir;
    private Branch m_Head;

    public WC(Path i_Path)
    {
        m_Head = null;
        m_WorkingCopyDir = i_Path;
    }
}
