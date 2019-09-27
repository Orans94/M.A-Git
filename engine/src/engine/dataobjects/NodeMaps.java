package engine.dataobjects;

import engine.objects.Folder;
import engine.objects.Node;
import engine.utils.MapUtilities;

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

    public boolean isEmpty(){return m_NodeBySHA1.isEmpty() && m_SHA1ByPath.isEmpty();}

    public void putAll(NodeMaps i_NodeMaps)
    {
        m_NodeBySHA1.putAll(i_NodeMaps.getNodeBySHA1());
        m_SHA1ByPath.putAll(i_NodeMaps.getSHA1ByPath());
    }

    public Folder getFolderByPath(Path i_CurrentPath)
    {
        String currentFolderSHA1 = m_SHA1ByPath.get(i_CurrentPath);

        return (Folder) m_NodeBySHA1.get(currentFolderSHA1);
    }

    public void putIfPathDoesntExists(Path i_Path, NodeMaps i_ToPut)
    {
        if(!m_SHA1ByPath.containsKey(i_Path))
        {
            String SHA1 = i_ToPut.getSHA1ByPath().get(i_Path);
            m_SHA1ByPath.put(i_Path, SHA1);
            m_NodeBySHA1.put(SHA1, i_ToPut.getNodeBySHA1().get(SHA1));
        }
    }
}
