package javafx;

import com.sun.xml.internal.ws.api.pipe.Engine;
import engine.*;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.primary.bottom.BottomController;
import javafx.primary.center.CenterController;
import javafx.fxml.FXML;
import javafx.primary.left.LeftController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.primary.top.TopController;
import javafx.stage.Stage;
import mypackage.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class AppController
{
    private Stage m_PrimaryStage;
    @FXML private TextField m_RepositoryNameTitleTextField;
    private final String MAGIT_TITLE = "M.A Git - ";
    // ------ CONTROLLERS AND COMPONENTS ------

    @FXML private VBox m_TopComponent;
    @FXML private TopController m_TopComponentController;
    @FXML private ScrollPane m_LeftComponent;
    @FXML private LeftController m_LeftComponentController;
    @FXML private BorderPane m_CenterComponent;
    @FXML private CenterController m_CenterComponentController;
    @FXML private BorderPane m_BottomComponent;
    @FXML private BottomController m_BottomComponentController;

    // ------ CONTROLLERS AND COMPONENTS ------

    private EngineManager m_Engine = new EngineManager();

    @FXML
    public void initialize()
    {
        if(m_BottomComponentController != null && m_CenterComponentController != null
        && m_LeftComponentController != null && m_TopComponentController != null)
        {
            m_BottomComponentController.setMainController(this);
            m_CenterComponentController.setMainController(this);
            m_LeftComponentController.setMainController(this);
            m_TopComponentController.setMainController(this);
        }

        m_RepositoryNameTitleTextField = new TextField();
        m_RepositoryNameTitleTextField.textProperty().addListener((obs, oldTitle, newTitle) -> updateTitle(MAGIT_TITLE + newTitle));
    }

    public void createNewBranch(String i_BranchName, String i_CommitSHA1) throws IOException { m_Engine.createNewBranch(i_BranchName, i_CommitSHA1); }

    public boolean commit(String i_Message, String i_SecondParentSHA1) throws IOException { return m_Engine.commit(i_Message, i_SecondParentSHA1); }

    public void checkout(String i_BranchName) throws IOException { m_Engine.checkout(i_BranchName); }

    public void deleteBranch(String i_BranchName) throws IOException { m_Engine.deleteBranch(i_BranchName); }

    public boolean isRepository(Path i_UserInputPath)
    {
        return m_Engine.isRepository(i_UserInputPath);
    }

    public void stashRepository(Path i_UserInputPath) throws IOException { m_Engine.stashRepository(i_UserInputPath); }

    public void createNewRepository(Path i_UserInputPath, String i_UserInputRepoName) throws IOException { m_Engine.createRepository(i_UserInputPath, i_UserInputRepoName); }

    public boolean isRootFolderEmpty() throws IOException { return m_Engine.isRootFolderEmpty(); }

    public boolean isRepositoryNull() { return m_Engine.isRepositoryNull(); }

    public boolean isBranchExists(String i_BranchName) { return m_Engine.isBranchExists(i_BranchName); }

    public boolean isBranchNameEqualsHead(String i_BranchName) { return m_Engine.isBranchNameEqualsHead(i_BranchName); }

    public OpenChanges getFileSystemStatus() throws IOException { return m_Engine.getFileSystemStatus(); }

    public boolean isFileSystemDirty(OpenChanges i_OpenChanges) { return m_Engine.isFileSystemDirty(i_OpenChanges); }

    public void setPrimaryStage(Stage i_PrimaryStage) { this.m_PrimaryStage = i_PrimaryStage; }

    public void setActiveBranchName(String i_BranchName) throws IOException
    {
        m_Engine.setActiveBranchName(i_BranchName);
    }

    public void addNewestCommitToTableView()
    {
        Commit newestCommit = m_Engine.getNewestCommitByItDate();

        m_CenterComponentController.addCommitToObservableList(newestCommit);
    }

    public boolean isCommitExists(String i_CommitSHA1) { return m_Engine.isCommitSHA1Exists(i_CommitSHA1);}

    public String getCommitMessage(String i_CommitSHA1) { return m_Engine.getCommitMessage(i_CommitSHA1);}

    public boolean isRepositoryEmpty(Path userInputPath) throws IOException
    {
        return m_Engine.isRepositoryEmpty(userInputPath);
    }

    public void loadEmptyRepository(Path userInputPath) throws IOException
    {
        m_Engine.loadEmptyRepository(userInputPath);
    }

    public void loadRepositoryByPath(Path userInputPath) throws IOException, ParseException
    {
        m_Engine.loadRepositoryByPath(userInputPath);
    }

    public boolean isDirectory(Path i_Path) { return m_Engine.isDirectory(i_Path);}

    public String getRepositoryName()
    {
        return m_Engine.getRepositoryName();
    }

    public Map<String, Commit> getCommits() { return m_Engine.getCommits();}

    public boolean isBranchPointedCommitSHA1Empty(String i_ActiveBranchName) { return m_Engine.isBranchPointedCommitSHA1Empty(i_ActiveBranchName);}

    public SortedSet<String> getActiveBranchHistory() { return m_Engine.getActiveBranchHistory();}

    public MagitRepository createXMLRepository(Path i_XMLFilePath) throws JAXBException, FileNotFoundException { return m_Engine.createXMLRepository(i_XMLFilePath); }

    public Map<String, Branch> getBranches() { return m_Engine.getBranches();}

    public Head getHead() { return m_Engine.getHead();}

    public void loadXMLRepoToMagitMaps(MagitRepository i_XMLRepo)
    {
        m_Engine.loadXMLRepoToMagitMaps(i_XMLRepo);
    }

    public NodeMaps getNodeMaps() { return m_Engine.getNodeMaps();}

    public Branch getActiveBranch() { return m_Engine.getActiveBranch();}

    public void createEmptyRepository(Path i_XMLRepositoryLocation, String i_RepositoryName) throws IOException
    {
        m_Engine.createEmptyRepository(i_XMLRepositoryLocation, i_RepositoryName);
    }

    public void createRepository(Path i_RepositoryPath, String i_RepositoryName) throws IOException
    {
        m_Engine.createRepository(i_RepositoryPath,i_RepositoryName);
    }

    public Map<String, MagitSingleFolder> getMagitSingleFolderByID()
    {
        return m_Engine.getMagitSingleFolderByID();
    }

    public boolean areIDsValid(MagitRepository i_XMLRepo)
    {
        return m_Engine.areIDsValid(i_XMLRepo);
    }

    public boolean areFoldersReferencesValid(MagitFolders magitFolders, MagitBlobs magitBlobs)
    {
        return m_Engine.areFoldersReferencesValid(magitFolders,magitBlobs);
    }

    public boolean areCommitsReferencesAreValid(MagitCommits magitCommits, Map<String, MagitSingleFolder> i_magitFolderByID)
    {
        return m_Engine.areCommitsReferencesAreValid(magitCommits, i_magitFolderByID);
    }

    public boolean areBranchesReferencesAreValid(MagitBranches magitBranches, MagitCommits magitCommits)
    {
        return m_Engine.areBranchesReferencesAreValid(magitBranches, magitCommits);
    }

    public boolean isHeadReferenceValid(MagitBranches magitBranches, String head)
    {
        return m_Engine.isHeadReferenceValid(magitBranches, head);
    }

    public void readRepositoryFromXMLFile(MagitRepository i_xmlRepository, XMLMagitMaps i_xmlMagitMaps) throws IOException, ParseException
    {
        m_Engine.readRepositoryFromXMLFile(i_xmlRepository, i_xmlMagitMaps);
    }

    public XMLMagitMaps getXMLMagitMaps()
    {
        return m_Engine.getXMLMagitMaps();
    }

    public boolean isDirectoryEmpty(Path xmlRepositoryLocation) throws IOException
    {
        return m_Engine.isDirectoryEmpty(xmlRepositoryLocation);
    }

    public void updateUsername(String i_Username) { EngineManager.setUserName(i_Username);}

    public boolean isXMLRepositoryEmpty(MagitRepository xmlRepo)
    {
        return m_Engine.isXMLRepositoryEmpty(xmlRepo);
    }

    public void addCommitsToTableView()
    {
        Map<String, Commit> commits = m_Engine.getCommits();
        m_CenterComponentController.addAllCommitsToTableView(commits);
    }

    public boolean isBranchNameRepresentsHead(String i_BranchName)
    {
        return m_Engine.isBranchNameRepresentsHead(i_BranchName);
    }

    public void changeActiveBranchPointedCommit(String i_CommitSHA1) throws IOException
    {
        m_Engine.changeActiveBranchPointedCommit(i_CommitSHA1);
    }

    public String getActiveBranchName()
    {
        return m_Engine.getActiveBranchName();
    }

    public void newCommitSelectedOnCenterTableView(Commit i_NewValue, String i_CommitSHA1) throws IOException
    {
        if(m_Engine.getLazyLoadedNodeMapsByCommitSHA1(i_CommitSHA1) == null)
        {
            m_Engine.lazyLoadCommitFromFileSystem(i_NewValue, i_CommitSHA1);
        }

        m_BottomComponentController.setBottomTabsDetails(i_NewValue, i_CommitSHA1);
    }

    public List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        return m_Engine.getContainedBranches(i_CommitSHA1);
    }

    public Folder getFolderBySHA1(String i_FolderSHA1)
    {
        return m_Engine.getFolderBySHA1(i_FolderSHA1);
    }

    public Node getLazyLoadedNodeBySHA1(String i_CommitSHA1, String i_RootFolderSHA1)
    {
        return m_Engine.getLazyLoadedNodeMapsByCommitSHA1(i_CommitSHA1).getNodeBySHA1().get(i_RootFolderSHA1);
    }

    public MergeNodeMaps merge(String i_TheirBranchName) throws IOException
    {
        return m_Engine.merge(i_TheirBranchName);
    }

    public boolean isPathExists(Path i_XmlRepositoryLocation)
    {
        return m_Engine.isPathExists(i_XmlRepositoryLocation);
    }

    public void createRepositoryPathDirectories(Path i_XmlRepositoryLocation) throws IOException
    {
        m_Engine.createRepositoryPathDirectories(i_XmlRepositoryLocation);
    }

    public void clearCommitTableViewAndTreeView()
    {
        m_CenterComponentController.clearCommitTableView();
        m_BottomComponentController.clearTreeView();
    }

    public String getSelectedCommitFromTableView()
    {
        return m_CenterComponentController.getSelectedCommitFromTableView();
    }

    public String getPointedCommitSHA1(String i_PointedBranch)
    {
        return m_Engine.getPointedCommitSHA1(i_PointedBranch);
    }

    public void deleteFile(Path i_PathToDelete) throws IOException
    {
        m_Engine.deleteFile(i_PathToDelete);
    }

    public void createPathToFile(Path i_PathToFile) throws IOException
    {
        m_Engine.createPathToFile(i_PathToFile);
    }

    public void createAndWriteTxtFile(Path i_PathToFile, String i_Content) throws IOException
    {
        m_Engine.createAndWriteTxtFile(i_PathToFile, i_Content);
    }

    public void removeEmptyDirectories() throws IOException
    {
        m_Engine.removeEmptyDirectories();
    }

    public Path getRootFolderPath()
    {
        return m_Engine.getRootFolderPath();
    }

    public int getNumberOfSubNodes(Path i_Path) throws IOException
    {
        return m_Engine.getNumberOfSubNodes(i_Path);
    }

    public void setActiveBranchPointedCommit(String i_BranchNameToCopyPointedCommit) throws IOException
    {
        m_Engine.setActiveBranchPointedCommit(i_BranchNameToCopyPointedCommit);
    }

    public boolean isFastForwardMerge(String i_TheirBranchName)
    {
        return m_Engine.isFastForwardMerge(i_TheirBranchName);
    }

    public boolean isOursContainsTheir(String i_TheirBranchName)
    {
        return m_Engine.isOursContainsTheir(i_TheirBranchName);
    }

    public List<Commit> getOrderedCommitsByDate()
    {
        return m_Engine.getOrderedCommitsByDate();
    }

    public boolean isCommitFather(String i_FatherSHA1, String i_ChildSHA1)
    {
        return m_Engine.isCommitFather(i_FatherSHA1, i_ChildSHA1);
    }

    public List<Commit> getAllCommitsWithTwoParents()
    {
        return m_Engine.getAllCommitsWithTwoParents();
    }

    public Commit getCommit(String i_CommitSHA1)
    {
        return m_Engine.getCommit(i_CommitSHA1);
    }

    public void updateCommitTree()
    {
        m_LeftComponentController.updateCommitTree();
    }

    public void commitNodeTreeSelected(String i_CommitSHA1)
    {
        m_CenterComponentController.commitNodeTreeSelected(i_CommitSHA1);
    }

    public OpenChanges getDelta(Commit i_FirstCommit, Commit i_SecondCommit) throws IOException
    {
        return m_Engine.getDelta(i_FirstCommit, i_SecondCommit);
    }

    public void cloneRepository(Path i_SourceDirectory, Path i_DestinationDirectory, String i_RepositoryName) throws IOException, ParseException
    {
        m_Engine.cloneRepository(i_SourceDirectory, i_DestinationDirectory, i_RepositoryName);
    }

    public boolean isRBBranch(String i_BranchName)
    {
        return m_Engine.isRBBranch(i_BranchName);
    }

    public void createNewRTB(String i_RemoteBranchName) throws IOException
    {
        m_Engine.createNewRTB(i_RemoteBranchName);
    }

    public boolean isRRExists()
    {
        return m_Engine.isRRExists();
    }

    public void fetch() throws IOException, ParseException
    {
        m_Engine.fetch();
    }

    public boolean isPathRepresentsMAGitRepository(String i_PathToCheck)
    {
        return m_Engine.isRepository(Paths.get(i_PathToCheck));
    }

    public String getRBNameFromCommitSHA1(String i_CommitSHA1Selected)
    {
        return m_Engine.getRBNameFromCommitSHA1(i_CommitSHA1Selected);
    }

    public String getTrackingBranchName(String i_RbName)
    {
        return m_Engine.getTrackingBranchName(i_RbName);
    }

    public void stashDirectory(Path i_Path) throws IOException
    {
        m_Engine.stashDirectory(i_Path);
    }

    public ProgressBar getProgressBar()
    {
        return m_BottomComponentController.getProgressBar();
    }

    public boolean isMagitRemoteReferenceValid(MagitRepository i_XmlRepo)
    {
        return m_Engine.isMagitRemoteReferenceValid(i_XmlRepo);
    }

    public boolean areBranchesTrackingAfterAreValid(MagitBranches i_MagitBranches)
    {
        return m_Engine.areBranchesTrackingAfterAreValid(i_MagitBranches);
    }

    private void updateTitle(String i_Title)
    {
        if (m_PrimaryStage != null)
        {
            m_PrimaryStage.setTitle(i_Title);
        }
        else
        {
            System.out.println("Warning: null stage");
        }
    }

    public void updatePrimaryStageTitle()
    {
        m_RepositoryNameTitleTextField.setText(m_Engine.getRepositoryName());
    }

    public boolean isPushRequired() throws IOException
    {
        return m_Engine.isPushRequired();
    }

    public void pull() throws IOException, ParseException
    {
        m_Engine.pull();
    }
}
