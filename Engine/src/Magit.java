import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Magit
{
    
    private Map<String, Node> m_Nodes;
    private Map<String, Commit> m_Commits;
    private Map<String, Branch> m_Branches;
    private static Path m_MagitDir;
    private Head m_Head;
    
    public Magit(Path i_MagitPath)
    {
        m_Nodes = new HashMap<>();
        m_Commits = new HashMap<>();
        m_Branches = new HashMap<>();
        m_MagitDir = i_MagitPath;
        m_Head = new Head(i_MagitPath);
    }

    public static Path getMagitDir()
    {
        return m_MagitDir;
    }

    public static void setMagitDir(Path i_MagitDir) {
        Magit.m_MagitDir = i_MagitDir;
    }

    public Map<String, Node> getNodes() 
    {
        return m_Nodes;
    }

    public void setNodes(Map<String, Node> i_Nodes) 
    {
        this.m_Nodes = i_Nodes;
    }

    public Map<String, Commit> getCommits() 
    {
        return m_Commits;
    }

    public void setCommits(Map<String, Commit> i_Commits)
    {
        this.m_Commits = i_Commits;
    }

    public Map<String, Branch> getBranches() 
    {
        return m_Branches;
    }

    public void setBranches(Map<String, Branch> i_Branches) 
    {
        this.m_Branches = i_Branches;
    }

    public Head getHead() 
    {
        return m_Head;
    }

    public void setHead(Head i_Head) 
    {
        this.m_Head = i_Head;
    }

}
