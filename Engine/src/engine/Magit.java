package engine;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Magit
{
    private Map<String, Commit> m_Commits = new HashMap<>();
    private Map<String, Branch> m_Branches = new HashMap<>();
    private static Path m_MagitDir = Paths.get("");
    private Head m_Head;

    public Magit(Path i_MagitPath)
    {
        //ctor for new magit
        Branch master = new Branch("master", "");
        FileUtilities.createAndWriteTxtFile(i_MagitPath.resolve("branches").resolve("master.txt"),"");
        m_MagitDir = i_MagitPath;
        m_Head = new Head(master, i_MagitPath);
        m_Branches.put(master.getName(), master);
    }

    public Magit()
    {
        //ctor for loaded magit
        m_Head = new Head();
    }

    public static Path getMagitDir() { return m_MagitDir; }

    public static void setMagitDir(Path i_MagitDir) { m_MagitDir = i_MagitDir; }

    public Map<String, Commit> getCommits() { return m_Commits; }

    public void setCommits(Map<String, Commit> i_Commits) { m_Commits = i_Commits; }

    public Map<String, Branch> getBranches() { return m_Branches; }

    public void setBranches(Map<String, Branch> i_Branches) { this.m_Branches = i_Branches; }

    public Head getHead() { return m_Head; }

    public void setHead(Head i_Head) { this.m_Head = i_Head; }

    public String handleNewCommit(String i_RootFolderSha1, String i_ParentSHA1, String i_CommitMessage)
    {
        Commit commit = createCommit(i_RootFolderSha1, i_ParentSHA1, i_CommitMessage);
        String commitSHA1 = commit.SHA1();
        m_Commits.put(commitSHA1, commit);
        setActiveBranchToNewCommit(commitSHA1);
        commit.Zip(commitSHA1);

        return commitSHA1;
    }

    private void setActiveBranchToNewCommit(String i_CommitSHA1)
    {
        //set active branch content to new commit sha1
        m_Head.getActiveBranch().setCommitSHA1(i_CommitSHA1);
        //change file content in file system
        FileUtilities.modifyTxtFile(m_MagitDir.resolve("branches").resolve(m_Head.getActiveBranch().getName() + ".txt"), i_CommitSHA1);
    }

    public Commit createCommit(String i_RootFolderSha1, String i_ParentSHA1, String i_CommitMessage)
    {
        return new Commit(i_RootFolderSha1,i_ParentSHA1,i_CommitMessage);
    }

    public void clear()
    {
        m_Commits.clear();
        m_Branches.clear();
    }

    public void load(Path i_repPath) throws IOException
    {
        m_MagitDir = i_repPath.resolve(".magit");
        loadBranches();
        loadHead();
        loadCommits();
    }

    private void loadHead() throws IOException
    {
        Path headPath = m_MagitDir.resolve("branches").resolve("HEAD.txt");
        String headContent = new String(Files.readAllBytes(headPath));
        m_Head.setActiveBranch(m_Branches.get(headContent));
    }

    private void loadCommits() throws IOException
    { // assuming branches is already loaded to m.a git system
        Commit newCommit;
        Branch currentBranch;
        String commitSHA1, parentCommitSHA1, commitContent, rootFolderSHA1, commitMessage, commitAuthor;
        Date commitCreateDate;

        for (Map.Entry<String, Branch> entry: m_Branches.entrySet())
        { // to all branches in objects\\branches
            currentBranch = entry.getValue();
            commitSHA1 = currentBranch.getCommitSHA1();
            loadCommitsToMapsRecursive(commitSHA1);
        }
    }

    private void loadCommitsToMapsRecursive(String i_CommitSHA1) throws IOException
    {
        if (!m_Commits.containsKey(i_CommitSHA1))
        { // the current commit not found in commits map

           addCommitToMapsByObjectsDir(i_CommitSHA1);
        }
        if (m_Commits.get(i_CommitSHA1).getParentSHA1().size() == 2)
        {
            loadCommitsToMapsRecursive(m_Commits.get(i_CommitSHA1).getParentSHA1().get(0));
            loadCommitsToMapsRecursive(m_Commits.get(i_CommitSHA1).getParentSHA1().get(1));
        }
        else if (m_Commits.get(i_CommitSHA1).getParentSHA1().size() == 1)
        {
            loadCommitsToMapsRecursive(m_Commits.get(i_CommitSHA1).getParentSHA1().get(0));
        }
    }

    private void addCommitToMapsByObjectsDir(String i_CommitSHA1) throws IOException
    {
        String commitContent, rootFolderSHA1, commitMessage, commitAuthor;
        List<String> parentsCommitSHA1 = new LinkedList<>();
        Commit newCommit;
        Date commitCreateDate;

        commitContent = FileUtilities.getTxtFromZip(i_CommitSHA1 + ".zip", i_CommitSHA1 + ".txt");
        rootFolderSHA1 = StringUtilities.getCommitInformation(commitContent, 0);
        parentsCommitSHA1.add(StringUtilities.getCommitInformation(commitContent,1));
        parentsCommitSHA1.add(StringUtilities.getCommitInformation(commitContent,2));
        if (parentsCommitSHA1.get(0).equals(""))
        {
            parentsCommitSHA1.remove(0);
        }
        if (parentsCommitSHA1.get(1).equals(""))
        {
            parentsCommitSHA1.remove(1);
        }
        commitMessage = StringUtilities.getCommitInformation(commitContent, 3);
        commitCreateDate = DateUtils.FormatToDate(StringUtilities.getCommitInformation(commitContent, 4));
        commitAuthor = StringUtilities.getCommitInformation(commitContent, 5);

        newCommit = new Commit(rootFolderSHA1, parentsCommitSHA1, commitMessage, commitCreateDate, commitAuthor);
        m_Commits.put(i_CommitSHA1, newCommit);
    }

    private void loadBranches() throws IOException
    {
        String branchName;
        List<Path> branches = Files.walk(m_MagitDir.resolve("branches"), 1)
                .filter(d->!d.toFile().isDirectory())
                .filter(d-> !d.toFile().getName().equals("HEAD.txt"))
                .collect(Collectors.toList());
        for(Path path : branches)
        {
            branchName = FilenameUtils.removeExtension(path.toFile().getName());
            String branchContent = new String(Files.readAllBytes(path));
            m_Branches.put(branchName, new Branch(branchName, branchContent));
        }
    }
}
