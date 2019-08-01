import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WalkFileSystemResult
{
    private OpenChanges m_OpenChanges;
    private NodeMaps m_UnchangedNodes;
    private NodeMaps m_NewLoadedNodes;

    public NodeMaps getUnchangedNodes() { return m_UnchangedNodes; }

    public NodeMaps getNewLoadedNodes() { return m_NewLoadedNodes; }

    public OpenChanges getOpenChanges() { return m_OpenChanges; }

    public WalkFileSystemResult()
    {
        m_OpenChanges = new OpenChanges();
        m_UnchangedNodes = new NodeMaps();
        m_NewLoadedNodes = new NodeMaps();
    }
}
