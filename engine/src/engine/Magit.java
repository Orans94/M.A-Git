package engine;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Magit
{
    private Map<String, Commit> m_Commits = new HashMap<>();
    private Map<String, Branch> m_Branches = new HashMap<>();
    private static Path m_MagitDir = Paths.get("");
    private Head m_Head;

    public Magit(Path i_MagitPath) throws IOException
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

    public String handleNewCommit(String i_RootFolderSha1, List<String> i_ParentsSHA1, String i_CommitMessage) throws IOException
    {
        Commit commit = createCommit(i_RootFolderSha1, i_ParentsSHA1, i_CommitMessage);
        String commitSHA1 = commit.getSHA1();
        m_Commits.put(commitSHA1, commit);
        setActiveBranchToNewCommit(commitSHA1);
        commit.Zip(commitSHA1);

        return commitSHA1;
    }

    private void setActiveBranchToNewCommit(String i_CommitSHA1) throws IOException
    {
        //set active branch content to new commit sha1
        m_Head.getActiveBranch().setCommitSHA1(i_CommitSHA1);
        //change file content in file system
        FileUtilities.modifyTxtFile(m_MagitDir.resolve("branches").resolve(m_Head.getActiveBranch().getName() + ".txt"), i_CommitSHA1);
    }

    public Commit createCommit(String i_RootFolderSha1, List<String> i_ParentsSHA1, String i_CommitMessage)
    {
        return new Commit(i_RootFolderSha1,i_ParentsSHA1,i_CommitMessage, new Date(), EngineManager.getUserName());
    }

    public void clear()
    {
        m_Commits.clear();
        m_Branches.clear();
    }

    public void load(Path i_repPath) throws IOException, ParseException
    {
        loadBranches(Magit.getMagitDir().resolve("branches"), null);
        loadHead();
        loadCommits();
    }

    public void loadHead() throws IOException
    {
        Path headPath = m_MagitDir.resolve("branches").resolve("HEAD.txt");
        String headContent = new String(Files.readAllBytes(headPath));
        m_Head.setActiveBranch(m_Branches.get(headContent));
    }

    public void loadCommits() throws IOException, ParseException
    { // assuming branches is already loaded to m.a git system
        Branch currentBranch;
        String commitSHA1;

        for (Map.Entry<String, Branch> entry: m_Branches.entrySet())
        { // to all branches in objects\\branches
            currentBranch = entry.getValue();
            commitSHA1 = currentBranch.getCommitSHA1();
            if(!commitSHA1.equals(""))
            {
                loadCommitsToMapsRecursive(commitSHA1);
            }
        }
    }

    private void loadCommitsToMapsRecursive(String i_CommitSHA1) throws IOException, ParseException {
        if (!m_Commits.containsKey(i_CommitSHA1))
        { // the current commit not found in commits map

            Commit newCommit = createCommitByObjectsDir(i_CommitSHA1, Magit.getMagitDir().resolve("objects").toString());
            m_Commits.put(i_CommitSHA1, newCommit);
            for(String SHA1 : m_Commits.get(i_CommitSHA1).getParentsSHA1())
            {
                loadCommitsToMapsRecursive(SHA1);
            }
        }
    }

    public Commit createCommitByObjectsDir(String i_CommitSHA1, String i_ObjectDir) throws IOException, ParseException {
        String commitContent, rootFolderSHA1, commitMessage, commitAuthor;
        List<String> parentsCommitSHA1 = new LinkedList<>();
        Date commitCreateDate;
        Path objectDirPath = Paths.get(i_ObjectDir);

        commitContent = FileUtilities.getTxtFromZip(objectDirPath.resolve(i_CommitSHA1 + ".zip").toString() , i_CommitSHA1 + ".txt");
        rootFolderSHA1 = StringUtilities.getCommitInformation(commitContent, 0);
        parentsCommitSHA1.add(StringUtilities.getCommitInformation(commitContent,1));
        parentsCommitSHA1.add(StringUtilities.getCommitInformation(commitContent,2));
        parentsCommitSHA1 = parentsCommitSHA1.stream().filter(d->!d.equals("")).collect(Collectors.toList());
        commitMessage = StringUtilities.getCommitInformation(commitContent, 3);
        commitCreateDate = DateUtils.FormatToDate(StringUtilities.getCommitInformation(commitContent, 4));
        commitAuthor = StringUtilities.getCommitInformation(commitContent, 5);

        return new Commit(rootFolderSHA1, parentsCommitSHA1, commitMessage, commitCreateDate, commitAuthor);
    }

    public void loadBranches(Path i_LoadFromPath, String i_RemoteRepositoryName) throws IOException
    {
        // this method is loading branches from the given path.
        // if remote repository name is null - it does not act as loading remote branches.
        // if remote branch is not null - its acting like loading remote branches.
        String branchName, branchContent;
        List<Path> branches = Files.walk(i_LoadFromPath, 1)
                .filter(d-> !d.toFile().isDirectory())
                .filter(d-> !d.toFile().getName().equals("HEAD.txt"))
                .collect(Collectors.toList());
        for(Path path : branches)
        {
            branchName = FilenameUtils.removeExtension(path.toFile().getName());
            if(i_RemoteRepositoryName != null)
            {
                branchName = i_RemoteRepositoryName + "\\" + FilenameUtils.removeExtension(path.toFile().getName());
            }
            branchContent = new String(Files.readAllBytes(path));
            if(!m_Branches.containsKey(branchName))
            {
                m_Branches.put(branchName, new Branch(branchName, branchContent));
            }
            else
            {
                m_Branches.get(branchName).setCommitSHA1(branchContent);
            }
            if(i_RemoteRepositoryName != null)
            {
                m_Branches.get(branchName).setIsRemote(true);
            }
        }
    }

    public Commit getNewestCommitByItDate()
    {
        return m_Commits.values()
                .stream()
                .max(Comparator.comparing(Commit::getCommitDate)).get();
    }

    public List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        // this method return a list of all branches pointed to i_CommitSHA1.
        List<Branch> containedBranches = new LinkedList<>();

        for(Branch branch : m_Branches.values())
        {
            if(branch.getCommitSHA1().equals(i_CommitSHA1))
            {
                containedBranches.add(branch);
            }
        }

        return containedBranches;
    }

    public void changeBranchName(String i_RRName, String i_BranchName)
    {
        String newBranchName = i_RRName+ "\\" +i_BranchName;
        Branch branch = m_Branches.get(i_BranchName);
        branch.setName(newBranchName);
        m_Branches.remove(i_BranchName);
        m_Branches.put(newBranchName, branch);
    }

    public void setIsRemoteBranch(String i_BranchName, boolean i_IsRemote)
    {
        m_Branches.get(i_BranchName).setIsRemote(i_IsRemote);
    }

    public void createNewRTB(String i_RemoteRepositoryName, String i_RemoteBranchName) throws IOException
    {
        String remoteBranchName = i_RemoteRepositoryName +"\\"+i_RemoteBranchName;
        String commitSHA1 = m_Branches.get(remoteBranchName).getCommitSHA1();
        Branch trackingBranch = new Branch(i_RemoteBranchName, commitSHA1);
        trackingBranch.setIsTracking(true);
        trackingBranch.setTrackingAfter(i_RemoteBranchName);
        FileUtilities.createAndWriteTxtFile(Magit.getMagitDir().resolve("branches").resolve(i_RemoteBranchName + ".txt"), commitSHA1);
        m_Branches.put(i_RemoteBranchName, trackingBranch);
    }

    public String getTrackingBranchName(String i_RemoteBranchName)
    {
        // this method gets a remote branch name and return the tracking branch name
        return Paths.get(i_RemoteBranchName).getFileName().toString();
    }

    public void deleteRemoteBranchesFromBranchesMap()
    {
        List<Branch> remoteBranches = m_Branches.values().stream()
                .filter(branch -> branch.getName().contains("\\"))
                .collect(Collectors.toList());
        for(Branch branch : remoteBranches)
        {
            m_Branches.remove(branch.getName());
        }
    }

    public void setActiveBranch(String i_BranchNameToSet, boolean i_SetOnFileSystem) throws IOException
    {
        m_Head.setActiveBranch(getBranches().get(i_BranchNameToSet));

        if (i_SetOnFileSystem)
        {
            FileUtilities.modifyTxtFile(getMagitDir().resolve("branches").resolve("HEAD.txt"), i_BranchNameToSet);
        }
    }
}
