import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Repository
{
    private WC m_WorkingCopy;
    private Set<Branch> m_Branches = new HashSet<>();
    private Magit m_Magit;

    public Repository(Path i_RepPath) throws IOException // TODO catch
    {
        createRepositoryDirectories(i_RepPath);
        m_Branches.add(new Branch(i_RepPath.resolve(".magit"),"master",""));
        m_WorkingCopy = new WC(i_RepPath);
        m_Magit = new Magit(i_RepPath.resolve(".magit"));
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

    public Magit getMagit()
    {
        return m_Magit;
    }

    public void setMagit(Magit i_Magit)
    {
        this.m_Magit = i_Magit;
    }
}
