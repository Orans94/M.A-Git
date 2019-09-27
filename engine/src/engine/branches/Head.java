package engine.branches;

import engine.utils.FileUtilities;

import java.io.IOException;
import java.nio.file.Path;

public class Head
{
    private Branch m_ActiveBranch;
    
    public Head(Branch i_ActiveBranch, Path i_Path) throws IOException
    {
        m_ActiveBranch = i_ActiveBranch;
        FileUtilities.createAndWriteTxtFile(i_Path.resolve("branches").resolve("HEAD.txt"), i_ActiveBranch.getName());
    }

    public Head() { }

    public Branch getActiveBranch() { return m_ActiveBranch; }

    public void setActiveBranch(Branch i_ActiveBranch) { m_ActiveBranch = i_ActiveBranch; }
}
