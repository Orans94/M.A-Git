package engine.managers;

import engine.branches.Branch;
import engine.branches.Head;
import engine.core.Magit;
import engine.core.Repository;
import engine.dataobjects.MergeNodeMaps;
import engine.dataobjects.NodeMaps;
import engine.dataobjects.OpenChanges;
import engine.objects.Commit;
import engine.objects.Folder;
import engine.objects.Node;
import engine.utils.FileUtilities;
import engine.xml.SchemaBasedJAXB;
import engine.xml.XMLMagitMaps;
import engine.xml.XMLManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private Map<Path, Repository> m_Repositories = new HashMap<>();
    private Repository m_LoadedRepository;
    private static String m_UserName = "Administrator";
    private XMLManager m_XMLManager = new XMLManager();
    private Map<String, NodeMaps> m_LazyLoadedNodeMapsByCommitSHA1 = new HashMap<>();
    private BooleanProperty m_IsRepositoryLoadedProperty = new SimpleBooleanProperty(false);
    private BooleanProperty m_IsRepositoryClonedProperty = new SimpleBooleanProperty(false);
    public BooleanProperty getIsRepositoryLoadedProperty() { return m_IsRepositoryLoadedProperty; }

    public Map<Path, Repository> getRepositories() { return m_Repositories; }

    public XMLManager getXMLManager() { return m_XMLManager; }

    public static String getUserName()
    {
        return m_UserName;
    }

    public static void setUserName(String i_UserName)
    {
        EngineManager.m_UserName = i_UserName;
    }

    public void createRepository(Path i_RepPath, String i_Name) throws IOException
    {
        m_LoadedRepository = new Repository(i_RepPath, i_Name);
        m_IsRepositoryLoadedProperty.setValue(true);
        m_IsRepositoryClonedProperty.setValue(m_LoadedRepository.getRemoteRepositoryPath() != null);
        m_Repositories.put(i_RepPath, m_LoadedRepository);
    }

    public boolean commit(String i_CommitMessage, String i_SecondParentSHA1) throws IOException
    {
        return m_LoadedRepository.commit(i_CommitMessage, true, i_SecondParentSHA1) != null;
    }

    public boolean isPathExists(Path i_Path)
    {
           return FileUtilities.isExists(i_Path);
    }

    public boolean isRepository(Path i_Path)
    {
        return FileUtilities.isExists(i_Path.resolve(".magit"));
    }

    public boolean isBranchExists(String i_BranchName) { return m_LoadedRepository.getMagit().getBranches().containsKey(i_BranchName); }

    public void createNewBranch(String i_BranchName, String i_CommitSHA1) throws IOException
    {
        m_LoadedRepository.createNewBranch(i_BranchName, i_CommitSHA1);
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

    public MagitRepository createXMLRepository(String i_XMLContent) throws JAXBException, FileNotFoundException
    {
        SchemaBasedJAXB jaxb = new SchemaBasedJAXB();
        return jaxb.createRepositoryFromXML(i_XMLContent);
    }

    public void readRepositoryFromXMLFile(MagitRepository i_XMLRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException {
        m_LoadedRepository = new Repository(Paths.get(i_XMLRepository.getLocation()));
        m_IsRepositoryLoadedProperty.setValue(true);
        m_LoadedRepository.loadXMLRepoToSystem(i_XMLRepository, i_XMLMagitMaps);
        m_LoadedRepository.checkout(m_LoadedRepository.getMagit().getHead().getActiveBranch().getName());
        m_IsRepositoryClonedProperty.setValue(m_LoadedRepository.getRemoteRepositoryPath() != null);
        m_Repositories.put(Paths.get(i_XMLRepository.getLocation()), m_LoadedRepository);
    }

    public OpenChanges getFileSystemStatus() throws IOException { return m_LoadedRepository.getFileSystemStatus(); }

    public boolean isFileSystemDirty(OpenChanges i_OpenChanges) { return !i_OpenChanges.isFileSystemClean(); }

    public void checkout(String i_BranchName) throws IOException { m_LoadedRepository.checkout(i_BranchName); }

    public Repository getLoadedRepository() { return m_LoadedRepository; }

    public void loadRepositoryByPath(Path i_RepoPath) throws IOException, ParseException
    {
        m_LoadedRepository = new Repository(i_RepoPath);
        m_IsRepositoryLoadedProperty.setValue(true);
        m_LoadedRepository.loadRepository(i_RepoPath);
        m_IsRepositoryClonedProperty.setValue(m_LoadedRepository.getRemoteRepositoryPath() != null);
        m_Repositories.put(i_RepoPath, m_LoadedRepository);
    }

    public boolean isDirectory(Path i_DirToCheck) { return FileUtilities.isDirectoryInFileSystem(i_DirToCheck); }

    public boolean isBranchNameRepresentsHead(String i_BranchName)
    {
        Head head = m_LoadedRepository.getMagit().getHead();

        return head.getActiveBranch().getName().equals(i_BranchName);
    }

    public void deleteBranch(String i_BranchName) throws IOException
    {
        m_LoadedRepository.deleteBranch(i_BranchName);
    }

    public void stashRepository(Path i_RepositoryToStash) throws IOException
    {
        Path pathToDelete = i_RepositoryToStash.resolve(".magit");
        FileUtils.cleanDirectory(pathToDelete.toFile());
        Files.delete(pathToDelete);
    }

    public boolean isDirectoryEmpty(Path i_Path) throws IOException { return FileUtilities.getNumberOfSubNodes(i_Path) == 0;}

    public boolean isRepositoryNull() { return m_LoadedRepository == null;}

    public boolean isXMLRepositoryEmpty(MagitRepository i_XMLRepo) { return m_XMLManager.isXMLRepositoryIsEmpty(i_XMLRepo); }

    public boolean isBranchPointedCommitSHA1Empty(String i_BranchName)
    {
        return m_LoadedRepository.getMagit().getBranches().get(i_BranchName).getCommitSHA1() == null ||
                m_LoadedRepository.getMagit().getBranches().get(i_BranchName).getCommitSHA1().equals("");
    }

    public boolean isCommitSHA1Exists(String i_CommitSHA1) { return m_LoadedRepository.getMagit().getCommits().containsKey(i_CommitSHA1); }

    public void changeActiveBranchPointedCommit(String i_CommitSHA1) throws IOException
    { m_LoadedRepository.setActiveBranchPointedCommitByCommitSHA1(i_CommitSHA1); }

    public String getActiveBranchName() { return m_LoadedRepository.getMagit().getHead().getActiveBranch().getName();}

    public void setActiveBranchName(String i_BranchName) throws IOException
    {
        m_LoadedRepository.getMagit().getHead().setActiveBranch(m_LoadedRepository.getMagit().getBranches().get(i_BranchName));
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
        m_LoadedRepository = new Repository(i_RepoPath);
        m_IsRepositoryLoadedProperty.setValue(true);
        m_LoadedRepository.writeRemoteRepositoryPathToFileSystem();
        m_LoadedRepository.loadNameFromFile();
        m_LoadedRepository.getMagit().loadBranches(Magit.getMagitDir().resolve("branches"), null);
        m_LoadedRepository.getMagit().loadHead();
        m_IsRepositoryClonedProperty.setValue(m_LoadedRepository.getRemoteRepositoryPath() != null);
        m_Repositories.put(i_RepoPath, m_LoadedRepository);
    }

    public boolean isRootFolderEmpty() throws IOException
    {
        // assuming the root folder including only .magit folder
        return m_LoadedRepository.isRootFolderEmpty();
    }

    public void createEmptyRepository(Path i_RepoPath, String i_RepoName) throws IOException
    {
        m_LoadedRepository = new Repository(i_RepoPath, i_RepoName);
        m_IsRepositoryLoadedProperty.setValue(true);
        m_IsRepositoryClonedProperty.setValue(m_LoadedRepository.getRemoteRepositoryPath() != null);
        m_Repositories.put(i_RepoPath, m_LoadedRepository);
    }

    public Commit getNewestCommitByItDate()
    {
        return m_LoadedRepository.getNewestCommitByItDate();
    }

    public String getCommitMessage(String i_CommitSHA1) { return m_LoadedRepository.getMagit().getCommits().get(i_CommitSHA1).getMessage();}

    public String getRepositoryName()
    {
        return m_LoadedRepository.getName();
    }

    public Map<String, Commit> getCommits() { return m_LoadedRepository.getMagit().getCommits();}

    public SortedSet<String> getActiveBranchHistory() { return m_LoadedRepository.getActiveBranchHistory();}

    public Map<String, Branch> getBranches() { return m_LoadedRepository.getMagit().getBranches();}

    public Head getHead() { return m_LoadedRepository.getMagit().getHead();}

    public void loadXMLRepoToMagitMaps(MagitRepository i_XMLRepo)
    {
        m_XMLManager.loadXMLRepoToMagitMaps(i_XMLRepo);
    }

    public NodeMaps getNodeMaps() { return m_LoadedRepository.getNodeMaps();}

    public Branch getActiveBranch()
    {
        Head head = m_LoadedRepository.getMagit().getHead();
        return m_LoadedRepository.getMagit().getBranches().get(head.getActiveBranch().getName());
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
        return m_LoadedRepository.getContainedBranches(i_CommitSHA1);
    }

    public Folder getFolderBySHA1(String i_FolderSHA1)
    {
        return m_LoadedRepository.getFolderBySHA1(i_FolderSHA1);
    }

    public Node getNodeBySHA1(String i_ItemSHA1)
    {
        return m_LoadedRepository.getNodeBySHA1(i_ItemSHA1);
    }

    public void lazyLoadCommitFromFileSystem(Path i_RepositoryPath, Commit i_NewValue, String i_CommitSHA1) throws IOException
    {
        NodeMaps commitNodeMaps = new NodeMaps();
        Path rootFolderPath = i_RepositoryPath.resolve(".magit").resolve("objects").resolve(i_NewValue.getRootFolderSHA1() + ".zip");
        commitNodeMaps.getSHA1ByPath().put(rootFolderPath, i_NewValue.getRootFolderSHA1());
        m_LoadedRepository.setNodeMapsByRootFolder(rootFolderPath, m_LoadedRepository.getWorkingCopy().getWorkingCopyDir(), commitNodeMaps, false);
        m_LazyLoadedNodeMapsByCommitSHA1.put(i_CommitSHA1, commitNodeMaps);
    }

    public NodeMaps getLazyLoadedNodeMapsByCommitSHA1(String i_CommitSHA1)
    {
        return m_LazyLoadedNodeMapsByCommitSHA1.getOrDefault(i_CommitSHA1, null);
    }

    public MergeNodeMaps merge(String i_TheirBranchName) throws IOException
    {
        return m_LoadedRepository.merge(i_TheirBranchName);
    }

    public String getPointedCommitSHA1(String i_PointedBranch)
    {
        return m_LoadedRepository.getPointedCommitSHA1(i_PointedBranch);
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
        m_LoadedRepository.removeEmptyDirectories();
    }

    public Path getRootFolderPath()
    {
        return m_LoadedRepository.getRootFolderPath();
    }

    public int getNumberOfSubNodes(Path i_Path) throws IOException
    {
        return FileUtilities.getNumberOfSubNodes(i_Path);
    }

    public void setActiveBranchPointedCommit(String i_BranchNameToCopyPointedCommit) throws IOException
    {
        m_LoadedRepository.setActiveBranchPointedCommitByBranchName(i_BranchNameToCopyPointedCommit);
    }

    public boolean isFastForwardMerge(String i_TheirBranchName)
    {
        return m_LoadedRepository.isFastForwardMerge(i_TheirBranchName);
    }

    public boolean isOursContainsTheir(String i_TheirBranchName)
    {
        return m_LoadedRepository.isOursContainsTheir(i_TheirBranchName);
    }

    public List<Commit> getOrderedCommitsByDate()
    {
        return m_LoadedRepository.getOrderedCommitsByDate();
    }

    public boolean isCommitFather(String i_FatherSHA1, String i_ChildSHA1)
    {
        return m_LoadedRepository.isCommitFather(i_FatherSHA1, i_ChildSHA1);
    }

    public List<Commit> getAllCommitsWithTwoParents()
    {
        return m_LoadedRepository.getAllCommitsWithTwoParents();
    }

    public Commit getCommit(String i_CommitSHA1)
    {
        return m_LoadedRepository.getCommit(i_CommitSHA1);
    }

    public OpenChanges getDelta(Commit i_FirstCommit, Commit i_SecondCommit) throws IOException
    {
        return m_LoadedRepository.delta(i_FirstCommit, i_SecondCommit);
    }

    public void cloneRepository(Path i_Source, Path i_Destination, String i_Name) throws IOException, ParseException
    {
        m_LoadedRepository = new Repository(i_Source, i_Destination, i_Name);
        m_IsRepositoryLoadedProperty.setValue(true);
        m_IsRepositoryClonedProperty.setValue(true);
        m_LoadedRepository.deleteRepositoryNameFile();
        m_LoadedRepository.createRepositoryNameFile();
        m_LoadedRepository.checkout(m_LoadedRepository.getMagit().getHead().getActiveBranch().getName());
        m_Repositories.put(i_Source, m_LoadedRepository);
    }

    public boolean isRBBranch(String i_BranchName)
    {
        return m_LoadedRepository.isRBBranch(i_BranchName);
    }

    public void createNewRTB(String i_RemoteBranchName) throws IOException
    {
        m_LoadedRepository.createNewRTB(i_RemoteBranchName);
    }

    public void fetch() throws IOException, ParseException
    {
        m_LoadedRepository.fetch();
    }

    public boolean isRRExists()
    {
        return m_LoadedRepository.isRRExists();
    }

    public String getRBNameFromCommitSHA1(String i_CommitSHA1Selected)
    {
        return m_LoadedRepository.getRBNameFromCommitSHA1(i_CommitSHA1Selected);
    }

    public String getTrackingBranchName(String i_RbName)
    {
        return m_LoadedRepository.getMagit().getTrackingBranchName(i_RbName);
    }

    public void stashDirectory(Path i_Path) throws IOException
    {
        FileUtilities.cleanDirectory(i_Path);
    }

    public boolean isMagitRemoteReferenceValid(MagitRepository i_XmlRepo)
    {
        return m_XMLManager.isMagitRemoteReferenceValid(i_XmlRepo);
    }

    public boolean areBranchesTrackingAfterAreValid(MagitBranches i_MagitBranches)
    {
        return m_XMLManager.areBranchesTrackingAfterAreValid(i_MagitBranches);
    }

    public boolean isPushRequired() throws IOException
    {
        return m_LoadedRepository.isPushRequired();
    }

    public void pull() throws IOException, ParseException
    {
        m_LoadedRepository.pull();
    }

    public boolean isRRWcIsClean() throws IOException, ParseException
    {
        return m_LoadedRepository.isRRWcIsClean();
    }

    public boolean isHeadRTB()
    {
        return m_LoadedRepository.isHeadRTB();
    }

    public boolean isRBEqualInRRAndLR(String i_TrackingAfter) throws IOException
    {
        return m_LoadedRepository.isRBEqualInRRAndLR(i_TrackingAfter);
    }

    public boolean isRBAndRTBAlreadyTracking(Branch i_Branch)
    {
        return m_LoadedRepository.isRBAndRTBAlreadyTracking(i_Branch);
    }

    public void push() throws IOException, ParseException
    {
        m_LoadedRepository.push();
    }

    public void updateRTBToBeRegularBranch(String i_RBName) throws IOException
    {
        m_LoadedRepository.updateRTBToBeRegularBranch(i_RBName);
    }

    public void pushNotRTB() throws IOException, ParseException
    {
        m_LoadedRepository.pushNotRTB();
    }

    public BooleanProperty getIsRepositoryClonedProperty()
    {
        return m_IsRepositoryClonedProperty;
    }

    public boolean isHeadTrackingAfterRB()
    {
        return m_LoadedRepository.isHeadTrackingAfterRB();
    }

    public List<Commit> getConnectedCommitsByCommitSHA1(String i_CommitSHA1)
    {
        return m_LoadedRepository.getConnectedCommitsByCommitSHA1(i_CommitSHA1);
    }

    public NodeMaps getNodeMapsByCommitSHA1(Path i_RepositoryPath, String i_CommitSHA1) throws IOException
    {
        if(!m_LazyLoadedNodeMapsByCommitSHA1.containsKey(i_CommitSHA1))
        {
            NodeMaps node = m_Repositories.get(i_RepositoryPath).getNodeMapsByCommitSHA1(i_CommitSHA1);
            m_LazyLoadedNodeMapsByCommitSHA1.put(i_CommitSHA1,node);

            return node;
        }
        else
        {
            return m_LazyLoadedNodeMapsByCommitSHA1.get(i_CommitSHA1);
        }
    }

    public void modifyTxtFile(Path i_filePath, String i_fileContent) throws IOException
    {
        FileUtilities.modifyTxtFile(i_filePath, i_fileContent);
    }
}
