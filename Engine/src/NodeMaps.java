import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class NodeMaps
{
    private Map<String, Node> m_NodeBySHA1;
    private Map<Path, String> m_SHA1ByPath;

    public NodeMaps(Map<String, Node> i_m1, Map<Path, String> i_m2)
    {
        m_NodeBySHA1 = i_m1;
        m_SHA1ByPath = i_m2;
    }

    public Map<String, Node> getNodeBySHA1()
    {
        return m_NodeBySHA1;
    }
    public Map<Path, String> getSHA1ByPath() { return m_SHA1ByPath; }
}
