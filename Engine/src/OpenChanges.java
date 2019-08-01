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

    public void setDeletedNodes(List<Path> i_DeletedNodes) { this.m_DeletedNodes = i_DeletedNodes; }

    public List<Path> getNewNodes() { return m_NewNodes; }

    public void setNewNodes(List<Path> i_NewNodes) { this.m_NewNodes = i_NewNodes; }

    public List<Path> getModifiedNodes() { return m_ModifiedNodes; }

    public void setModifiedNodes(List<Path> i_ModifiedNodes) { this.m_ModifiedNodes = i_ModifiedNodes; }

    public boolean isFileSystemClean() { return m_DeletedNodes.isEmpty() && m_ModifiedNodes.isEmpty() && m_NewNodes.isEmpty(); }
}
