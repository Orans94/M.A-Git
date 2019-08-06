public class WalkFileSystemResult
{
    private OpenChanges m_OpenChanges;
    private NodeMaps m_UnchangedNodes;
    private NodeMaps m_ToZipNodes;

    public NodeMaps getUnchangedNodes() { return m_UnchangedNodes; }

    public NodeMaps getToZipNodes() { return m_ToZipNodes; }

    public OpenChanges getOpenChanges() { return m_OpenChanges; }

    public WalkFileSystemResult()
    {
        m_OpenChanges = new OpenChanges();
        m_UnchangedNodes = new NodeMaps();
        m_ToZipNodes = new NodeMaps();
    }
}
