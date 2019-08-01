import java.nio.file.Path;

public class Head
{
    private Branch m_ActiveBranch;
    
    public Head(Branch i_ActiveBranch, Path i_Path)
    {
        m_ActiveBranch = i_ActiveBranch;
        FileUtils.CreateAndWriteTxtFile(i_Path.resolve("branches\\HEAD".concat(".txt")), i_ActiveBranch.getName());
    }

    public Branch getActiveBranch() { return m_ActiveBranch; }

    public void setActiveBranch(Branch i_ActiveBranch)
    {
        this.m_ActiveBranch = i_ActiveBranch;
        FileUtils.modifyTxtFile(Magit.getMagitDir().resolve("branches")
                .resolve(i_ActiveBranch.getName() + ".txt"), i_ActiveBranch.getName());
    }
}
