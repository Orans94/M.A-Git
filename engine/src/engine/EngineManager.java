package engine;

import mypackage.*;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class EngineManager
{
    private Repository m_Repository;
    private static String m_UserName = "Administrator";
    private XMLManager m_XMLManager = new XMLManager();
    private Map<String, NodeMaps> m_LazyLoadedNodeMapsByCommitSHA1 = new HashMap<>();

    public XMLManager getXMLManager() { return m_XMLManager; }

    public static String getUserName()
    {
        return m_UserName;
    }

    public static void setUserName(String i_UserName)
    {
        EngineManager.m_UserName = i_UserName;
    }

    public void createRepository(Path i_RepPath, String i_Name) throws IOException { m_Repository = new Repository(i_RepPath, i_Name); }

    public boolean commit(String i_CommitMessage) throws IOException
    {
        return m_Repository.commit(i_CommitMessage, true) != null;
    }

    public boolean isPathExists(Path i_Path)
    {
           return FileUtilities.exists(i_Path);
    }

    public boolean isRepository(Path i_Path)
    {
        return FileUtilities.exists(i_Path.resolve(".magit"));
    }

    public boolean isBranchExists(String i_BranchName) { return m_Repository.getMagit().getBranches().containsKey(i_BranchName); }

    public void createNewBranch(String i_BranchName) throws IOException
    {
        m_Repository.createNewBranch(i_BranchName);
    }

    public boolean isBranchNameEqualsHead(String i_BranchName)
    {
        return i_BranchName.toUpperCase().equals("HEAD");
    }

    public MagitRepository createXMLRepository(Path i_XMLFilePath) throws JAXBException, FileNotFoundException
    {
        SchemaBasedJAXB jaxb = new SchemaBasedJAXB();
        return jaxb.createRepositoryFromXML(i_XMLFilePath);
    }

    public void readRepositoryFromXMLFile(MagitRepository i_XMLRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException {
        m_Repository = new Repository(Paths.get(i_XMLRepository.getLocation()));
        m_Repository.loadXMLRepoToSystem(i_XMLRepository, i_XMLMagitMaps);
        m_Repository.checkout(m_Repository.getMagit().getHead().getActiveBranch().getName());
    }

    public OpenChanges getFileSystemStatus() throws IOException { return m_Repository.getFileSystemStatus(); }

    public boolean isFileSystemDirty(OpenChanges i_OpenChanges) { return !i_OpenChanges.isFileSystemClean(); }

    public void checkout(String i_BranchName) throws IOException { m_Repository.checkout(i_BranchName); }

    public Repository getRepository() { return m_Repository; }

    public void loadRepositoryByPath(Path i_RepoPath) throws IOException, ParseException {
        m_Repository = new Repository(i_RepoPath);
        m_Repository.loadRepository(i_RepoPath);
    }

    public boolean isDirectory(Path i_DirToCheck) { return FileUtilities.isDirectoryInFileSystem(i_DirToCheck); }

    public boolean isBranchNameRepresentsHead(String i_BranchName)
    {
        Head head = m_Repository.getMagit().getHead();

        return head.getActiveBranch().getName().equals(i_BranchName);
    }

    public void deleteBranch(String i_BranchName) throws IOException
    {
        m_Repository.deleteBranch(i_BranchName);
    }

    public void stashRepository(Path i_RepositoryToStash) throws IOException
    {
        Path pathToDelete = i_RepositoryToStash.resolve(".magit");
        FileUtils.cleanDirectory(pathToDelete.toFile());
        Files.delete(pathToDelete);
    }

    public boolean isDirectoryEmpty(Path i_Path) throws IOException { return FileUtilities.getNumberOfSubNodes(i_Path) == 0;}

    public boolean isRepositoryNull() { return m_Repository == null;}

    public boolean isXMLRepositoryEmpty(MagitRepository i_XMLRepo) { return m_XMLManager.isXMLRepositoryIsEmpty(i_XMLRepo); }

    public boolean isBranchPointedCommitSHA1Empty(String i_BranchName)
    {
        return m_Repository.getMagit().getBranches().get(i_BranchName).getCommitSHA1() == null ||
                m_Repository.getMagit().getBranches().get(i_BranchName).getCommitSHA1().equals("");
    }

    public boolean isCommitSHA1Exists(String i_CommitSHA1) { return m_Repository.getMagit().getCommits().containsKey(i_CommitSHA1); }

    public void changeActiveBranchPointedCommit(String i_CommitSHA1) throws IOException
    { m_Repository.changeActiveBranchPointedCommit(i_CommitSHA1); }

    public String getActiveBranchName() { return m_Repository.getMagit().getHead().getActiveBranch().getName();}

    public void setActiveBranchName(String i_BranchName) throws IOException
    {
        m_Repository.getMagit().getHead().setActiveBranch(m_Repository.getMagit().getBranches().get(i_BranchName));
        FileUtilities.modifyTxtFile(Magit.getMagitDir().resolve("branches").resolve("HEAD.txt"), i_BranchName);
    }

    public void createRepositoryPathDirectories(Path i_XmlRepositoryLocation) throws IOException
    {
        Files.createDirectories(i_XmlRepositoryLocation);
    }

    public boolean isRepositoryEmpty(Path i_RepoPath) throws IOException
    {
        // this method return true if objects folder is empty and all the branches not pointing to any commit
        Path branchesDir = i_RepoPath.resolve(".magit").resolve("branches");
        Path objectsDir = i_RepoPath.resolve(".magit").resolve("objects");
        int numberOfSubNodes = FileUtilities.getNumberOfSubNodes(objectsDir);
        String branchContent;


        List<Path> branchesPath = Files.walk(branchesDir)
                .filter(d -> !d.getFileName().toString().equals("HEAD.txt") && !Files.isDirectory(d))
                .collect(Collectors.toList());

        for (Path branchPath : branchesPath)
        {
            branchContent = new String(Files.readAllBytes(branchPath));
            if (!branchContent.equals(""))
            {
                return false;
            }
        }

        return numberOfSubNodes == 0;
    }


    public void loadEmptyRepository(Path i_RepoPath) throws IOException
    {
        m_Repository = new Repository(i_RepoPath);
        m_Repository.loadNameFromFile();
        m_Repository.getMagit().loadBranches();
        m_Repository.getMagit().loadHead();
    }

    public boolean isRootFolderEmpty() throws IOException
    {
        // assuming the root folder including only .magit folder
        return m_Repository.isRootFolderEmpty();
    }

    public void createEmptyRepository(Path i_RepoPath, String i_RepoName) throws IOException
    {
        m_Repository = new Repository(i_RepoPath, i_RepoName);
    }

    public Commit getNewestCommitByItDate()
    {
        return m_Repository.getNewestCommitByItDate();
    }

    public String getCommitMessage(String i_CommitSHA1) { return m_Repository.getMagit().getCommits().get(i_CommitSHA1).getMessage();}

    public String getRepositoryName()
    {
        return m_Repository.getName();
    }

    public Map<String, Commit> getCommits() { return m_Repository.getMagit().getCommits();}

    public SortedSet<String> getActiveBranchHistory() { return m_Repository.getActiveBranchHistory();}

    public Map<String, Branch> getBranches() { return m_Repository.getMagit().getBranches();}

    public Head getHead() { return m_Repository.getMagit().getHead();}

    public void loadXMLRepoToMagitMaps(MagitRepository i_XMLRepo)
    {
        m_XMLManager.loadXMLRepoToMagitMaps(i_XMLRepo);
    }

    public NodeMaps getNodeMaps() { return m_Repository.getNodeMaps();}

    public Branch getActiveBranch()
    {
        Head head = m_Repository.getMagit().getHead();
        return m_Repository.getMagit().getBranches().get(head.getActiveBranch().getName());
    }

    public Map<String, MagitSingleFolder> getMagitSingleFolderByID()
    {
        return m_XMLManager.getMagitSingleFolderByID();
    }

    public boolean areIDsValid(MagitRepository i_XMLRepo)
    {
        return m_XMLManager.areIDsValid(i_XMLRepo);
    }

    public boolean areFoldersReferencesValid(MagitFolders magitFolders, MagitBlobs magitBlobs)
    {
        return m_XMLManager.areFoldersReferencesValid(magitFolders, magitBlobs);
    }

    public boolean areCommitsReferencesAreValid(MagitCommits magitCommits, Map<String, MagitSingleFolder> i_magitFolderByID)
    {
        return m_XMLManager.areCommitsReferencesAreValid(magitCommits, i_magitFolderByID);
    }

    public boolean areBranchesReferencesAreValid(MagitBranches magitBranches, MagitCommits magitCommits)
    {
        return m_XMLManager.areBranchesReferencesAreValid(magitBranches, magitCommits);
    }

    public boolean isHeadReferenceValid(MagitBranches magitBranches, String head)
    {
        return m_XMLManager.isHeadReferenceValid(magitBranches, head);
    }

    public XMLMagitMaps getXMLMagitMaps()
    {
        return m_XMLManager.getXMLMagitMaps();
    }

    public List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        return m_Repository.getContainedBranches(i_CommitSHA1);
    }

    public Folder getFolderBySHA1(String i_FolderSHA1)
    {
        return m_Repository.getFolderBySHA1(i_FolderSHA1);
    }

    public Node getNodeBySHA1(String i_ItemSHA1)
    {
        return m_Repository.getNodeBySHA1(i_ItemSHA1);
    }

    public void lazyLoadCommitFromFileSystem(Commit i_NewValue, String i_CommitSHA1) throws IOException
    {
        NodeMaps commitNodeMaps = new NodeMaps();
        Path rootFolderPath = Magit.getMagitDir().resolve("objects").resolve(i_NewValue.getRootFolderSHA1() + ".zip");
        commitNodeMaps.getSHA1ByPath().put(rootFolderPath, i_NewValue.getRootFolderSHA1());
        m_Repository.setNodeMapsByRootFolder(rootFolderPath, commitNodeMaps, false);
        m_LazyLoadedNodeMapsByCommitSHA1.put(i_CommitSHA1, commitNodeMaps);
    }

    public NodeMaps getLazyLoadedNodeMapsByCommitSHA1(String i_CommitSHA1)
    {
        return m_LazyLoadedNodeMapsByCommitSHA1.getOrDefault(i_CommitSHA1, null);
    }

    public MergeNodeMaps merge(String i_TheirBranchName) throws IOException
    {
        return m_Repository.merge(i_TheirBranchName);
    }

    public void addParentSHAToNewestCommit(String i_PointedBranch)
    {
        m_Repository.addParentSHAToNewestCommit(i_PointedBranch);
    }

    public void deleteFile(Path i_PathToDelete) throws IOException
    {
        FileUtilities.deleteFile(i_PathToDelete);
    }

    public void createPathToFile(Path i_PathToFile) throws IOException
    {
        Path pathToCreate = i_PathToFile.getParent();
        Files.createDirectories(pathToCreate);
    }

    public void createAndWriteTxtFile(Path i_PathToFile, String i_Content) throws IOException
    {
        FileUtilities.createAndWriteTxtFile(i_PathToFile, i_Content);
    }

    public void removeEmptyDirectories() throws IOException
    {
        m_Repository.removeEmptyDirectories();
    }

    public Path getRootFolderPath()
    {
        return m_Repository.getRootFolderPath();
    }

    public int getNumberOfSubNodes(Path i_Path) throws IOException
    {
        return FileUtilities.getNumberOfSubNodes(i_Path);
    }
}
