import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Branch
{
    private String m_Name;
    private String m_Content = null;

    public Branch(Path i_RepoPath, String i_Name, String i_Content)
    {
        m_Content = i_Content;
        m_Name = i_Name;
        FileUtils.CreateAndWriteTxtFile(i_RepoPath.resolve("branches").resolve(i_Name.concat(".txt")), i_Content, false);
    }

    public String getName()
    {
        return m_Name;
    }

    public void setName(String m_Name)
    {
        this.m_Name = m_Name;
    }
}
