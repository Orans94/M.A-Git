import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Repository
{
    private WC m_WorkingCopy;
    private Path m_MagitDir;
    Set<Branch> m_Branches = new HashSet<>();

    public Repository(Path i_RepPath)
    {
        m_Branches.add(new Branch("master"));
        m_WorkingCopy = new WC(i_RepPath);
        m_MagitDir = i_RepPath.resolve(".magit");
    }
}
