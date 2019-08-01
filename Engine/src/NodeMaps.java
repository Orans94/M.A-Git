import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class NodeMaps
{
    private Map<String, Node> m_NodeBySHA1;
    private Map<Path, String> m_SHA1ByPath;

    public NodeMaps()
    {
        m_NodeBySHA1 = new HashMap<>();
        m_SHA1ByPath = new HashMap<>();
    }

    public NodeMaps(NodeMaps i_newNodeMaps)
    {
        m_NodeBySHA1 = MapUtilities.deepClone(i_newNodeMaps.getNodeBySHA1());
        m_SHA1ByPath = MapUtilities.deepClone(i_newNodeMaps.getSHA1ByPath());
    }

    public void clear()
    {
        m_NodeBySHA1.clear();
        m_SHA1ByPath.clear();
    }

    public Map<String, Node> getNodeBySHA1()
    {
        return m_NodeBySHA1;
    }
    public Map<Path, String> getSHA1ByPath() { return m_SHA1ByPath; }
}
