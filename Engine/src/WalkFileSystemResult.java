import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WalkFileSystemResult
{
    private OpenChanges m_OpenChanges;
    private Map<Path, String> m_UnchangedNodes;
    private Map<String, Node> m_NodeBySHA1;
    private Map<Path, String> m_SHA1ByPath;

    public OpenChanges getOpenChanges() { return m_OpenChanges; }

    public void setOpenChanges(OpenChanges i_OpenChanges) { this.m_OpenChanges = i_OpenChanges; }

    public Map<Path, String> getUnchangedNodes() { return m_UnchangedNodes; }

    public void setUnchangedNodes(Map<Path, String> i_UnchangedNodes) { this.m_UnchangedNodes = i_UnchangedNodes; }

    public Map<String, Node> getNodeBySHA1() { return m_NodeBySHA1; }

    public void setNodeBySHA1(Map<String, Node> i_NodeBySHA1) { this.m_NodeBySHA1 = i_NodeBySHA1; }

    public Map<Path, String> getSHA1ByPath() {return m_SHA1ByPath; }

    public void setSHA1ByPath(Map<Path, String> m_SHA1ByPath) {this.m_SHA1ByPath = m_SHA1ByPath; }

    public WalkFileSystemResult()
    {
        this.m_OpenChanges =new OpenChanges();
        this.m_UnchangedNodes = new HashMap<>();
        this.m_NodeBySHA1 = new HashMap<>();
        this.m_SHA1ByPath = new HashMap<>();
    }
}
