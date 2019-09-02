package engine;

public class MergeNodeMaps
{
    private NodeMaps m_UnionNodeMaps = new NodeMaps();
    private NodeMaps m_AncestorNodeMaps = new NodeMaps();
    private NodeMaps m_OursNodeMaps = new NodeMaps();
    private NodeMaps m_TheirNodeMaps = new NodeMaps();

    public NodeMaps getUnionNodeMaps() { return m_UnionNodeMaps; }
    public void setUnionNodeMaps(NodeMaps m_UnionNodeMaps) { this.m_UnionNodeMaps = m_UnionNodeMaps; }
    public NodeMaps getAncestorNodeMaps() { return m_AncestorNodeMaps; }
    public void setAncestorNodeMaps(NodeMaps m_AncestorNodeMaps) { this.m_AncestorNodeMaps = m_AncestorNodeMaps; }
    public NodeMaps getOursNodeMaps() { return m_OursNodeMaps; }
    public void setOursNodeMaps(NodeMaps m_OursNodeMaps) { this.m_OursNodeMaps = m_OursNodeMaps; }
    public NodeMaps getTheirNodeMaps() { return m_TheirNodeMaps; }
    public void setTheirNodeMaps(NodeMaps m_TheirNodeMaps) { this.m_TheirNodeMaps = m_TheirNodeMaps; }
    public NodeMaps getConflictsNodeMaps() { return m_ConflictsNodeMaps; }
    public void setConflictsNodeMaps(NodeMaps m_ConflictsNodeMaps) { this.m_ConflictsNodeMaps = m_ConflictsNodeMaps; }
    private NodeMaps m_ConflictsNodeMaps = new NodeMaps();
}
