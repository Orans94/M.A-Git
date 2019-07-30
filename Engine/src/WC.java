import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.*;
import java.util.*;

public class WC
{
    private Path m_WorkingCopyDir;
    private Map<String, Node> m_Nodes;

    public WC(Path i_Path)
    {
        m_WorkingCopyDir = i_Path;
        m_Nodes = new HashMap<>();
    }

    public Map<String, Node> getNodes()
    {
        return m_Nodes;
    }

    public void setNodes(Map<String, Node> i_Nodes)
    {
        this.m_Nodes = i_Nodes;
    }

    public String addFolderToMap(Folder i_Folder)
    {
        String folderSHA1 = i_Folder.SHA1();
        m_Nodes.put(folderSHA1, i_Folder);

        return folderSHA1;
    }

    public Path getWorkingCopyDir() { return m_WorkingCopyDir; }

    public void setWorkingCopyDir(Path i_WorkingCopyDir) { this.m_WorkingCopyDir = i_WorkingCopyDir; }
}
