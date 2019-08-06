import java.nio.file.*;
import java.util.*;

public class WC
{
    private Path m_WorkingCopyDir;
    private NodeMaps m_NodeMaps;
    private String m_CommitSHA1 = "";

    public String getCommitSHA1() { return m_CommitSHA1; }

    public void setCommitSHA1(String i_CommitSHA1) { m_CommitSHA1 = i_CommitSHA1; }

    public NodeMaps getNodeMaps() { return m_NodeMaps; }

    public void setNodeMaps(NodeMaps i_NodeMaps) { this.m_NodeMaps = i_NodeMaps; }

    public WC(Path i_Path)
    {
        m_WorkingCopyDir = i_Path;
        m_NodeMaps = new NodeMaps();
    }

    public Path getWorkingCopyDir() { return m_WorkingCopyDir; }

    public void setWorkingCopyDir(Path i_WorkingCopyDir) { this.m_WorkingCopyDir = i_WorkingCopyDir; }

    public void clear() { m_NodeMaps.clear(); }
}
