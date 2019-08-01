import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Magit
{
    private Map<String, Commit> m_Commits;
    private Map<String, Branch> m_Branches;
    private static Path m_MagitDir = Paths.get("");
    private Head m_Head;
    
    public Magit(Path i_MagitPath)
    {
        Branch master = new Branch(i_MagitPath, "master", "");
        m_Commits = new HashMap<>();
        m_Branches = new HashMap<>();
        m_MagitDir = i_MagitPath;
        m_Head = new Head(master, i_MagitPath);
        m_Branches.put(master.getName(), master);
    }

    public static Path getMagitDir() { return m_MagitDir; }

    public void setMagitDir(Path i_MagitDir) { m_MagitDir = i_MagitDir; }
    public Map<String, Commit> getCommits() { return m_Commits; }

    public void setCommits(Map<String, Commit> i_Commits) { m_Commits = i_Commits; }

    public Map<String, Branch> getBranches() { return m_Branches; }

    public void setBranches(Map<String, Branch> i_Branches) { this.m_Branches = i_Branches; }

    public Head getHead() { return m_Head; }

    public void setHead(Head i_Head) { this.m_Head = i_Head; }

    public String handleNewCommit(String i_RootFolderSha1, String i_ParentSHA1, String i_CommitMessage)
    {
        String commitSHA1 = createCommit(i_RootFolderSha1, i_ParentSHA1, i_CommitMessage);
        setActiveBranchToNewCommit(commitSHA1);
        Commit commit = m_Commits.get(commitSHA1);
        commit.Zip(commitSHA1);

        return commitSHA1;
    }

    private void setActiveBranchToNewCommit(String i_CommitSHA1)
    {
        //set active branch content to new commit sha1
        m_Head.getActiveBranch().setCommitSHA1(i_CommitSHA1);
        //change file content in file system
        FileUtils.modifyTxtFile(m_MagitDir.resolve("branches").resolve(m_Head.getActiveBranch().getName() + ".txt"), i_CommitSHA1);
    }

    private String createCommit(String i_RootFolderSha1, String i_ParentSHA1, String i_CommitMessage)
    {
        Commit commit = new Commit(i_RootFolderSha1,i_ParentSHA1,i_CommitMessage);
        String commitSHA1 = commit.SHA1();
        m_Commits.put(commitSHA1, commit);

        return commitSHA1;
    }
}
