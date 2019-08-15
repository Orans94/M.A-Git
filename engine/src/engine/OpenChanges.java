package engine;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class OpenChanges
{
    private List<Path> m_DeletedNodes;
    private List<Path> m_NewNodes;
    private List<Path> m_ModifiedNodes;

    public OpenChanges()
    {
        m_DeletedNodes = new LinkedList<>();
        m_NewNodes = new LinkedList<>();
        m_ModifiedNodes = new LinkedList<>();
    }

    public List<Path> getDeletedNodes() { return m_DeletedNodes; }

    public List<Path> getNewNodes() { return m_NewNodes; }

    public List<Path> getModifiedNodes() { return m_ModifiedNodes; }

    public boolean isFileSystemClean() { return m_DeletedNodes.isEmpty() && m_ModifiedNodes.isEmpty() && m_NewNodes.isEmpty(); }
}
