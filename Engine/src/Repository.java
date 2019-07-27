import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Repository
{
    private WC m_WorkingCopy;
    private Path m_MagitDir;
    Set<Branch> m_Branches = new HashSet<>();

    public Repository(Path i_RepPath) throws IOException // TODO catch
    {
        createRepositoryDirectories(i_RepPath);
        m_MagitDir = i_RepPath.resolve(".magit");
        m_Branches.add(new Branch(m_MagitDir,"master",""));
        m_WorkingCopy = new WC(i_RepPath);
    }

    private void createRepositoryDirectories(Path i_RepPath) throws IOException //TODO catch exception
    {
        Files.createDirectory(i_RepPath.resolve(".magit"));
        Files.createDirectory(i_RepPath.resolve(".magit").resolve("branches"));
        Files.createDirectory(i_RepPath.resolve(".magit").resolve("objects"));
    }

    public WC getWorkingCopy() {
        return m_WorkingCopy;
    }

    public void setWorkingCopy(WC i_WorkingCopy) {
        this.m_WorkingCopy = i_WorkingCopy;
    }
}
