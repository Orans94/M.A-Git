package javafx.primary.top;

import engine.*;
import javafx.AlertFactory;
import javafx.AppController;
import javafx.event.ActionEvent;
import javafx.StageUtilities;
import javafx.fxml.FXML;
import javafx.primary.top.popup.PopupController;
import javafx.primary.top.popup.checkout.CheckoutController;
import javafx.primary.top.popup.clone.CloneRepositoryController;
import javafx.primary.top.popup.commit.CommitController;
import javafx.primary.top.popup.createnewbranch.CreateNewBranchController;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.primary.top.popup.deletebranch.DeleteBranchController;
import javafx.primary.top.popup.loadrepositorybypath.LoadRepositoryByPathController;
import javafx.primary.top.popup.loadrepositorybyxml.LoadRepositoryByXMLController;
import javafx.primary.top.popup.merge.selectbranch.MergeSelectBranchController;
import javafx.primary.top.popup.merge.solveconflict.MergeSolveConflictController;
import javafx.primary.top.popup.resetbranch.ResetBranchController;
import javafx.primary.top.popup.showinformation.*;
import javafx.primary.top.popup.updateusername.UpdateUsernameController;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mypackage.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;
import java.util.SortedSet;

import static javafx.CommonResourcesPaths.*;

public class TopController
{
    private AppController m_MainController;

    // ------ CONTROLLERS AND COMPONENTS ------

    @FXML private VBox m_CreateNewBranchComponent;
    @FXML private CreateNewBranchController m_CreateNewBranchComponentController;
    @FXML private VBox m_DeleteBranchComponent;
    @FXML private DeleteBranchController m_DeleteBranchComponentController;
    @FXML private VBox m_CheckoutComponent;
    @FXML private CheckoutController m_CheckoutComponentController;
    @FXML private VBox m_CommitComponent;
    @FXML private CommitController m_CommitComponentController;
    @FXML private VBox m_CreateNewRepositoryComponent;
    @FXML private CreateNewRepositoryController m_CreateNewRepositoryComponentController;
    @FXML private VBox m_LoadRepositoryByPathComponent;
    @FXML private LoadRepositoryByPathController m_LoadRepositoryByPathComponentController;
    @FXML private VBox m_LoadRepositoryByXMLComponent;
    @FXML private LoadRepositoryByXMLController m_LoadRepositoryByXMLComponentController;
    @FXML private VBox m_ShowInformationComponent;
    @FXML private ShowInformationController m_ShowInformationComponentController;
    @FXML private VBox m_UpdateUsernameComponent;
    @FXML private UpdateUsernameController m_UpdateUsernameComponentController;
    @FXML private ScrollPane m_ResetBranchComponent;
    @FXML private ResetBranchController m_ResetBranchComponentController;
    @FXML private VBox m_MergeSelectBranchComponent;
    @FXML private MergeSelectBranchController m_MergeSelectBranchComponentController;
    @FXML private SplitPane m_MergeSolveConflictComponent;
    @FXML private MergeSolveConflictController m_MergeSolveConflictComponentController;
    @FXML private VBox m_CloneRepositoryComponent;
    @FXML private CloneRepositoryController m_CloneRepositoryComponentController;

    // ------ CONTROLLERS AND COMPONENTS ------

    @FXML private MenuItem fetchMenuItem;
    @FXML private MenuItem createNewRepositoryMenuItem;
    @FXML private MenuItem loadRepositoryByPathMenuItem;
    @FXML private MenuItem loadRepositoryFromXMLMenuItem;
    @FXML private MenuItem cloneRepositoryMenuItem;
    @FXML private RadioMenuItem changeThemeToLightRadioMenuItem;
    @FXML private ToggleGroup themes;
    @FXML private RadioMenuItem changeThemeToDarkRadioMenuItem;
    @FXML private RadioMenuItem changeThemeToStadiumRadioMenuItem;
    @FXML private RadioMenuItem setBackgroundImageRadioMenuItem;

    public MenuItem getCommitMenuItem() { return commitMenuItem; }

    @FXML private MenuItem commitMenuItem;
    @FXML private MenuItem pullMenuItem;
    @FXML private MenuItem pushMenuItem;
    @FXML private MenuItem createNewBranchMenuItem;
    @FXML private MenuItem deleteBranchMenuItem;
    @FXML private MenuItem checkoutMenuItem;
    @FXML private MenuItem mergeMenuItem;
    @FXML private MenuItem resetBranchMenuItem;
    @FXML private MenuItem showAllBranchesMenuItem;
    @FXML private MenuItem showCurrentBranchHistoryMenuItem;
    @FXML private MenuItem showStatusMenuItem;
    @FXML private MenuItem showCurrentCommitDetailsMenuItem;
    @FXML private MenuItem contactUsMenuItem;
    @FXML private MenuItem aboutMenuItem;
    @FXML private Button refreshButton;
    @FXML private Button pushButton;
    @FXML private Button pullButton;
    @FXML private Button commitButton;
    @FXML private Button showStatusButton;
    @FXML private Button createNewBranchButton;
    @FXML private Button deleteBranchButton;
    @FXML private SplitMenuButton repositoryFullPathSplitMenuButton;

    public void setUpdateUsernameComponent(Parent i_UpdateUsernameComponent) { this.m_UpdateUsernameComponent = (VBox) i_UpdateUsernameComponent; }

    public void setUpdateUsernameComponentController(PopupController i_UpdateUsernameComponentController) { this.m_UpdateUsernameComponentController = (UpdateUsernameController) i_UpdateUsernameComponentController; }

    public void setLoadRepositoryByPathComponent(Parent m_LoadRepositoryByPathComponent) { this.m_LoadRepositoryByPathComponent = (VBox) m_LoadRepositoryByPathComponent; }

    public void setLoadRepositoryByPathComponentController(PopupController m_LoadRepositoryByPathComponentController) { this.m_LoadRepositoryByPathComponentController = (LoadRepositoryByPathController) m_LoadRepositoryByPathComponentController; }

    public void setLoadRepositoryByXMLComponent(Parent m_LoadRepositoryByXMLComponent) { this.m_LoadRepositoryByXMLComponent = (VBox) m_LoadRepositoryByXMLComponent; }

    public void setLoadRepositoryByXMLComponentController(PopupController m_LoadRepositoryByXMLComponentController) { this.m_LoadRepositoryByXMLComponentController = (LoadRepositoryByXMLController) m_LoadRepositoryByXMLComponentController; }

    public void setShowInformationComponent(Parent m_ShowInformationComponent) { this.m_ShowInformationComponent = (VBox) m_ShowInformationComponent; }

    public void setShowInformationComponentController(PopupController m_ShowInformationComponentController) { this.m_ShowInformationComponentController = (ShowInformationController) m_ShowInformationComponentController; }

    public void setCreateNewRepositoryComponent(Parent i_CreateNewRepositoryComponent) { this.m_CreateNewRepositoryComponent = (VBox) i_CreateNewRepositoryComponent; }

    public void setCreateNewRepositoryComponentController(PopupController i_CreateNewRepositoryComponentController) { this.m_CreateNewRepositoryComponentController = (CreateNewRepositoryController) i_CreateNewRepositoryComponentController; }

    public void setCreateNewBranchComponent(Parent i_CreateNewBranchComponent) { this.m_CreateNewBranchComponent = (VBox) i_CreateNewBranchComponent; }

    public void setCreateNewBranchComponentController(PopupController i_CreateNewBranchComponentController) { this.m_CreateNewBranchComponentController = (CreateNewBranchController) i_CreateNewBranchComponentController; }

    public void setDeleteBranchComponent(Parent i_DeleteBranchComponent) { this.m_DeleteBranchComponent = (VBox) i_DeleteBranchComponent; }

    public void setDeleteBranchComponentController(PopupController i_DeleteBranchComponentController) { this.m_DeleteBranchComponentController = (DeleteBranchController) i_DeleteBranchComponentController; }

    public void setCheckoutComponent(Parent i_CheckoutComponent) { this.m_CheckoutComponent = (VBox) i_CheckoutComponent; }

    public void setCheckoutComponentController(PopupController i_CheckoutComponentController) { this.m_CheckoutComponentController = (CheckoutController) i_CheckoutComponentController; }

    public void setCommitComponent(Parent i_CommitComponent)
    {
        this.m_CommitComponent = (VBox) i_CommitComponent;
    }

    public void setCommitComponentController(PopupController i_CommitComponentController) { this.m_CommitComponentController = (CommitController) i_CommitComponentController; }

    public void setResetBranchComponent(Parent i_ResetBranchComponent) { this.m_ResetBranchComponent = (ScrollPane) i_ResetBranchComponent; }

    public void setResetBranchComponentController(PopupController i_ResetBranchComponentController) { this.m_ResetBranchComponentController = (ResetBranchController) i_ResetBranchComponentController; }

    public void setMergeSelectBranchComponent(Parent m_MergeSelectBranchComponent) { this.m_MergeSelectBranchComponent = (VBox) m_MergeSelectBranchComponent; }

    public void setMergeSelectBranchComponentController(PopupController m_MergeSelectBranchComponentController) { this.m_MergeSelectBranchComponentController = (MergeSelectBranchController) m_MergeSelectBranchComponentController; }

    public void setMergeSolveConflictComponent(Parent m_MergeSolveConflictComponent) { this.m_MergeSolveConflictComponent = (SplitPane) m_MergeSolveConflictComponent; }

    public void setMergeSolveConflictComponentController(PopupController m_MergeSolveConflictComponentController) { this.m_MergeSolveConflictComponentController = (MergeSolveConflictController) m_MergeSolveConflictComponentController; }

    public void setCloneRepositoryComponent(Parent i_CloneRepositoryComponent) { this.m_CloneRepositoryComponent =(VBox) i_CloneRepositoryComponent; }

    public void setCloneRepositoryComponentController(PopupController i_CloneRepositoryComponentController) { this.m_CloneRepositoryComponentController = (CloneRepositoryController) i_CloneRepositoryComponentController; }

    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    @FXML
    public void createNewRepositoryMenuItemAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Create new repository", CREATE_NEW_REPOSITORY_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    void cloneRepositoryMenuItemAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Clone repository", CLONE_REPOSITORY_FMXL_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    void createBranchMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Create new branch", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Create new branch", CREATE_NEW_BRANCH_FXML_RESOURCE, this);
            m_CreateNewBranchComponentController.bindCommitsToTableView();
            stage.showAndWait();
        }
    }

    @FXML
    private void commitMenuItemAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Commit", COMMIT_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    private void updateUsernameMenuItemAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Update username", UPDATE_USERNAME_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    void checkoutMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Checkout", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Checkout", CHECKOUT_FXML_RESOURCE, this);
            m_CheckoutComponentController.bindBranchesToChoiceBox(event);
            stage.showAndWait();
        }
    }

    @FXML
    void deleteBranchMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Delete branch", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Delete Branch", DELETE_BRANCH_FXML_RESOURCE, this);
            m_DeleteBranchComponentController.bindBranchesToChoiceBox(event);
            stage.showAndWait();
        }
    }

    @FXML
    void fetchMenuItemAction(ActionEvent event) throws IOException, ParseException
    {
        if(!isRepositoryNull())
        {
            if (m_MainController.isRRExists())
            {
                m_MainController.fetch();
                updateUIComponents();
                AlertFactory.createInformationAlert("Fetch", "Fetch has been done successfully").showAndWait();
            }
            else
            {
                AlertFactory.createErrorAlert("Fetch", "There is no remote repository. The system did not fetch");
            }
        }
        else
        {
            AlertFactory.createErrorAlert("Fetch", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
    }

    @FXML
    void resetBranchMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Reset branch", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Reset Branch", RESET_BRANCH_FXML_RESOURCE, this);
            m_ResetBranchComponentController.bindCommitsToTableView();
            stage.showAndWait();
        }
    }

    @FXML
    void showActiveBranchHistoryMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show active branch history", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else if (isBranchPointedCommitSHA1Empty(m_MainController.getActiveBranch().getName()))
        {
            AlertFactory.createErrorAlert("Show active branch history", "Active branch " + m_MainController.getActiveBranch().getName() + " is not pointing on a commit")
                    .showAndWait();
        }
        else
        {
            Branch activeBranch = m_MainController.getActiveBranch();
            Stage stage = StageUtilities.createPopupStage("Show active branch history", SHOW_INFORMATION_FXML_RESOURCE, this);
            m_ShowInformationComponentController.setInformationTextArea(new ShowActiveBranchHistory(m_MainController.getActiveBranch(), m_ShowInformationComponentController));
            stage.showAndWait();
        }
    }

    @FXML
    void showAllBranchesMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show commit history", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Show all branches", SHOW_INFORMATION_FXML_RESOURCE, this);
            m_ShowInformationComponentController.setInformationTextArea(new ShowAllBranches(m_MainController.getBranches(), m_MainController.getHead(), m_ShowInformationComponentController));
            stage.showAndWait();
        }
    }

    @FXML
    void showStatusMenuItemAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show commit history", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else if(!isFileSystemDirty(getFileSystemStatus()))
        {
            AlertFactory.createInformationAlert("Show status", "WC status is clean")
            .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Show status", SHOW_INFORMATION_FXML_RESOURCE, this);
            m_ShowInformationComponentController.setInformationTextArea(new ShowStatus(m_MainController.getFileSystemStatus()));
            stage.showAndWait();
        }
    }

    public void showCurrentCommitDetailsMenuItemAction(ActionEvent actionEvent) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show current commit details", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else if (m_MainController.getCommits().size() == 0)
        {
            // commit haven't been done yet
            AlertFactory.createErrorAlert("Show current commit details", "Commit haven't been done yet")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Show current commit details", SHOW_INFORMATION_FXML_RESOURCE, this);
            m_ShowInformationComponentController.setInformationTextArea(new ShowCurrentCommitDetails(m_MainController.getNodeMaps(), m_ShowInformationComponentController));
            stage.showAndWait();
        }
    }

    @FXML
    public void loadRepositoryByPathMenuItemAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Load repository", LOAD_REPOSITORY_BY_PATH_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    public void loadRepositoryByXMLMenuItemAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Load repository", LOAD_REPOSITORY_BY_XML_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    public void createNewBranch(String i_BranchName, String i_CommitSHA1) throws IOException
    {
        m_MainController.createNewBranch(i_BranchName, i_CommitSHA1);
    }

    public boolean commit(String i_Message, String i_SecondParentSHA1) throws IOException
    {
        return m_MainController.commit(i_Message, i_SecondParentSHA1);
    }

    public void checkout(String i_BranchName) throws IOException
    {
        m_MainController.checkout(i_BranchName);
    }

    public void deleteBranch(String i_BranchName) throws IOException
    {
        m_MainController.deleteBranch(i_BranchName);
    }

    public boolean isRepository(Path i_UserInputPath)
    {
        return m_MainController.isRepository(i_UserInputPath);
    }

    public void stashRepository(Path i_userInputPath) throws IOException
    {
        m_MainController.stashRepository(i_userInputPath);
    }

    public void createNewRepository(Path i_UserInputPath, String i_UserInputRepoName) throws IOException
    {
        setRepositoryFullPathSplitMenuButton(i_UserInputPath);
        m_MainController.createNewRepository(i_UserInputPath, i_UserInputRepoName);
    }

    public void setRepositoryFullPathSplitMenuButton(Path i_UserInputPath)
    {
        repositoryFullPathSplitMenuButton.setText(i_UserInputPath.toString());
    }

    public boolean isRootFolderEmpty() throws IOException
    {
        return m_MainController.isRootFolderEmpty();
    }

    public boolean isRepositoryNull()
    {
        return m_MainController.isRepositoryNull();
    }

    public boolean isBranchExists(String i_BranchName)
    {
        return m_MainController.isBranchExists(i_BranchName);
    }

    public boolean isBranchNameEqualsHead(String i_BranchName)
    {
        return m_MainController.isBranchNameEqualsHead(i_BranchName);
    }

    public OpenChanges getFileSystemStatus() throws IOException
    {
        return m_MainController.getFileSystemStatus();
    }

    public boolean isFileSystemDirty(OpenChanges i_OpenChanges)
    {
        return m_MainController.isFileSystemDirty(i_OpenChanges);
    }

    public void setActiveBranchName(String i_BranchName) throws IOException
    {
        m_MainController.setActiveBranchName(i_BranchName);
    }

    public void addNewestCommitToTableView()
    {
        m_MainController.addNewestCommitToTableView();
    }

    public boolean isRepositoryEmpty(Path userInputPath) throws IOException
    {
        return m_MainController.isRepositoryEmpty(userInputPath);
    }

    public void loadEmptyRepository(Path i_UserInputPath) throws IOException
    {
        m_MainController.loadEmptyRepository(i_UserInputPath);
    }

    public void loadRepositoryByPath(Path i_UserInputPath) throws IOException, ParseException
    {
        m_MainController.loadRepositoryByPath(i_UserInputPath);
    }

    public String getRepositoryName()
    {
        return m_MainController.getRepositoryName();
    }

    public MagitRepository createXMLRepository(Path i_XMLFilePath) throws JAXBException, FileNotFoundException
    {
        return m_MainController.createXMLRepository(i_XMLFilePath);
    }

    public void loadXMLRepoToMagitMaps(MagitRepository i_XMLRepo)
    {
        m_MainController.loadXMLRepoToMagitMaps(i_XMLRepo);
    }

    public void createEmptyRepository(Path i_XMLRepositoryLocation, String i_RepositoryName) throws IOException
    {
        m_MainController.createEmptyRepository(i_XMLRepositoryLocation, i_RepositoryName);
    }

    public void createRepository(Path i_RepositoryPath, String i_RepositoryName) throws IOException
    {
        m_MainController.createRepository(i_RepositoryPath, i_RepositoryName);
    }

    public Map<String, MagitSingleFolder> getMagitSingleFolderByID()
    {
        return m_MainController.getMagitSingleFolderByID();
    }

    public boolean areIDsValid(MagitRepository i_XMLRepo)
    {
        return m_MainController.areIDsValid(i_XMLRepo);
    }

    public boolean areFoldersReferencesValid(MagitFolders magitFolders, MagitBlobs magitBlobs)
    {
        return m_MainController.areFoldersReferencesValid(magitFolders, magitBlobs);
    }

    public boolean areCommitsReferencesAreValid(MagitCommits magitCommits, Map<String, MagitSingleFolder> i_magitFolderByID)
    {
        return m_MainController.areCommitsReferencesAreValid(magitCommits, i_magitFolderByID);
    }

    public boolean areBranchesReferencesAreValid(MagitBranches magitBranches, MagitCommits magitCommits)
    {
        return m_MainController.areBranchesReferencesAreValid(magitBranches, magitCommits);
    }

    public boolean isHeadReferenceValid(MagitBranches magitBranches, String head)
    {
        return m_MainController.isHeadReferenceValid(magitBranches, head);
    }

    public void readRepositoryFromXMLFile(MagitRepository i_XMLRepository, XMLMagitMaps i_XMLMagitMaps) throws IOException, ParseException
    {
        m_MainController.readRepositoryFromXMLFile(i_XMLRepository, i_XMLMagitMaps);
    }

    public XMLMagitMaps getXMLMagitMaps()
    {
        return m_MainController.getXMLMagitMaps();
    }

    public boolean isDirectoryEmpty(Path xmlRepositoryLocation) throws IOException
    {
        return m_MainController.isDirectoryEmpty(xmlRepositoryLocation);
    }

    public boolean isCommitExists(String i_CommitSHA1) { return m_MainController.isCommitExists(i_CommitSHA1);}

    public String getCommitMessage(String i_CommitSHA1) { return m_MainController.getCommitMessage(i_CommitSHA1);}

    public boolean isDirectory(Path i_Path) { return m_MainController.isDirectory(i_Path);}

    public boolean isBranchPointedCommitSHA1Empty(String i_ActiveBranchName)
    {
        return m_MainController.isBranchPointedCommitSHA1Empty(i_ActiveBranchName);
    }

    public SortedSet<String> getActiveBranchHistory()
    {
        return m_MainController.getActiveBranchHistory();
    }

    public Map<String, Commit> getCommits()
    {
        return m_MainController.getCommits();
    }

    public void updateUsername(String i_Username) { m_MainController.updateUsername(i_Username);}

    public boolean isXMLRepositoryEmpty(MagitRepository xmlRepo)
    {
        return m_MainController.isXMLRepositoryEmpty(xmlRepo);
    }

    public void addCommitsToTableView()
    {
        m_MainController.addCommitsToTableView();
    }

    public Map<String, Branch> getBranches()
    {
        return m_MainController.getBranches();
    }

    public Branch getActiveBranch()
    {
        return m_MainController.getActiveBranch();
    }

    public void showForcedCommitScene(ActionEvent event, String i_SecondParentSHA1) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Commit", COMMIT_FXML_RESOURCE, this);
        stage.setOnCloseRequest(evt -> {
            // prevent window from closing
            evt.consume();
        });
        m_CommitComponentController.setSecondParentSHA1(i_SecondParentSHA1);
        stage.showAndWait();
    }

    public boolean isBranchNameRepresentsHead(String i_BranchName)
    {
        return m_MainController.isBranchNameRepresentsHead(i_BranchName);
    }

    public void changeActiveBranchPointedCommit(String i_CommitSHA1) throws IOException
    {
        m_MainController.changeActiveBranchPointedCommit(i_CommitSHA1);
    }

    public String getActiveBranchName()
    {
        return m_MainController.getActiveBranchName();
    }

    public void showDetailsOfCurrentCommitScene(ActionEvent event) throws IOException
    {
        showCurrentCommitDetailsMenuItemAction(event);
    }

    public void mergeMenuItemAction(ActionEvent actionEvent) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Merge", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else if(isFileSystemDirty(getFileSystemStatus()))
        {
            AlertFactory.createInformationAlert("Merge", "WC status is dirty, merge is not allowed")
                    .showAndWait();
        }
        else
        {
            Stage stage = StageUtilities.createPopupStage("Merge", MERGE_SELECT_BRANCH_FXML_RESOURCE, this);
            m_MergeSelectBranchComponentController.bindBranchesToChoiceBox();
            stage.showAndWait();
        }
    }

    public MergeNodeMaps merge(String i_TheirBranchName) throws IOException
    {
        return m_MainController.merge(i_TheirBranchName);
    }

    public boolean isPathExists(Path i_XmlRepositoryLocation)
    {
        return m_MainController.isPathExists(i_XmlRepositoryLocation);
    }

    public void createRepositoryPathDirectories(Path i_XmlRepositoryLocation) throws IOException
    {
        m_MainController.createRepositoryPathDirectories(i_XmlRepositoryLocation);
    }

    public void clearCommitTableViewAndTreeView()
    {
        m_MainController.clearCommitTableViewAndTreeView();
    }

    public void showMergeSolveConflictsScene(MergeNodeMaps i_MergeNodeMapsResult) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Merge Conflict Solver", MERGE_SOLVE_CONFLICT_FXML_RESOURCE, this);
        stage.setOnCloseRequest(evt -> {
            // prevent window from closing
            evt.consume();
        });
        m_MergeSolveConflictComponentController.setMergeNodeMaps(i_MergeNodeMapsResult);
        m_MergeSolveConflictComponentController.updateConflictList();
        stage.showAndWait();
    }

    public String getPointedCommitSHA1(String i_PointedBranch)
    {
        return m_MainController.getPointedCommitSHA1(i_PointedBranch);
    }

    public void deleteFile(Path i_PathToDelete) throws IOException
    {
        m_MainController.deleteFile(i_PathToDelete);
    }

    public void createPathToFile(Path i_PathToFile) throws IOException
    {
        m_MainController.createPathToFile(i_PathToFile);
    }

    public void createAndWriteTxtFile(Path i_PathToFile, String i_Content) throws IOException
    {
        m_MainController.createAndWriteTxtFile(i_PathToFile, i_Content);
    }

    public void removeEmptyDirectories() throws IOException
    {
        m_MainController.removeEmptyDirectories();
    }

    public Path getRootFolderPath()
    {
        return m_MainController.getRootFolderPath();
    }

    public int getNumberOfSubNodes(Path i_Path) throws IOException
    {
        return m_MainController.getNumberOfSubNodes(i_Path);
    }

    public void setActiveBranchPointedCommit(String i_BranchNameToCopyPointedCommit) throws IOException
    {
        m_MainController.setActiveBranchPointedCommit(i_BranchNameToCopyPointedCommit);
    }

    public boolean isFastForwardMerge(String i_TheirBranchName)
    {
        return m_MainController.isFastForwardMerge(i_TheirBranchName);
    }

    public boolean isOursContainsTheirs(String i_TheirsBranchName)
    {
        return m_MainController.isOursContainsTheir(i_TheirsBranchName);
    }

    public void updateCommitTree()
    {
        m_MainController.updateCommitTree();
    }

    public void cloneRepository(Path i_SourceDirectory, Path i_DestinationDirectory, String i_RepositoryName) throws IOException, ParseException
    {
        setRepositoryFullPathSplitMenuButton(i_DestinationDirectory);
        m_MainController.cloneRepository(i_SourceDirectory, i_DestinationDirectory, i_RepositoryName);
    }

    public boolean isRBBranch(String i_BranchName)
    {
        return m_MainController.isRBBranch(i_BranchName);
    }

    public void createNewRTB(String i_RemoteBranchName) throws IOException
    {
        m_MainController.createNewRTB(i_RemoteBranchName);
    }

    public boolean isPathRepresentsMAGitRepository(String i_PathToCheck)
    {
        return m_MainController.isPathRepresentsMAGitRepository(i_PathToCheck);
    }

    public String getRBNameFromCommitSHA1(String i_CommitSHA1Selected)
    {
        return m_MainController.getRBNameFromCommitSHA1(i_CommitSHA1Selected);
    }

    public String getTrackingBranchName(String i_RbName)
    {
        return m_MainController.getTrackingBranchName(i_RbName);
    }

    public void stashDirectory(Path i_Path) throws IOException
    {
        m_MainController.stashDirectory(i_Path);
    }

    public ProgressBar getProgressBar()
    {
        return m_MainController.getProgressBar();
    }

    public boolean isMagitRemoteReferenceValid(MagitRepository i_XmlRepo)
    {
        return m_MainController.isMagitRemoteReferenceValid(i_XmlRepo);
    }

    public boolean areBranchesTrackingAfterAreValid(MagitBranches i_MagitBranches)
    {
        return m_MainController.areBranchesTrackingAfterAreValid(i_MagitBranches);
    }

    public void updatePrimaryStageTitle()
    {
        m_MainController.updatePrimaryStageTitle();
    }

    public void updateUIComponents()
    {
        clearCommitTableViewAndTreeView();
        addCommitsToTableView();
        updateCommitTree();
        updatePrimaryStageTitle();
    }

    public void pullMenuItemOnAction(ActionEvent actionEvent) throws IOException, ParseException
    {
        //TODO disable this menu item of the repository is not tracking after repository
        if (m_MainController.getActiveBranch().getIsTracking())
        {
            // head branch is RTB
            if (!m_MainController.isFileSystemDirty(m_MainController.getFileSystemStatus()))
            {
                // file system is clean
                if (!m_MainController.isPushRequired())
                {
                    // the requirements were validated, pull operation can start
                    m_MainController.pull();
                    updateUIComponents();
                    AlertFactory.createInformationAlert("Pull", "Pulled successfully").showAndWait();
                }
                else
                {
                    // notify user that push operation is required before operate pull
                    AlertFactory.createErrorAlert("Pull", "Push is required before pull").showAndWait();
                }
            }
            else
            {
                // notify user that WC is dirty and pull operation cannot be done
                AlertFactory.createErrorAlert("Pull", "WC status id dirty, cannot pull").showAndWait();
            }
        }
        else
        {
            // notify user that active branch isn't RTB
            AlertFactory.createErrorAlert("Pull", "The active branch is not tracking after a remote branch(not RTB)").showAndWait();
        }
    }
}