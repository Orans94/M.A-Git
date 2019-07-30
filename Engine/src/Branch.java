import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Branch
{
    private String m_Name;
    private String m_CommitSHA1 = null;

    public Branch(Path i_RepoPath, String i_Name, String i_CommitSHA1)
    {
        m_CommitSHA1 = i_CommitSHA1;
        m_Name = i_Name;
        FileUtils.CreateAndWriteTxtFile(i_RepoPath.resolve("branches").resolve(i_Name.concat(".txt")), i_CommitSHA1);
    }

    public String getName()
    {
        return m_Name;
    }

    public void setName(String m_Name)
    {
        this.m_Name = m_Name;
    }

    public String getCommitSHA1() { return m_CommitSHA1; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }
}
