package javafx.primary.top;

import engine.*;
import javafx.AlertFactory;
import javafx.AppController;
import javafx.ComponentControllerConnector;
import javafx.event.ActionEvent;
import javafx.StageUtilities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.primary.top.popup.PopupController;
import javafx.primary.top.popup.checkout.CheckoutController;
import javafx.primary.top.popup.commit.CommitController;
import javafx.primary.top.popup.createnewbranch.CreateNewBranchController;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.primary.top.popup.deletebranch.DeleteBranchController;
import javafx.primary.top.popup.loadrepositorybypath.LoadRepositoryByPathController;
import javafx.primary.top.popup.loadrepositorybyxml.LoadRepositoryByXMLController;
import javafx.primary.top.popup.showinformation.ShowInformationController;
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
    @FXML private ScrollPane m_ShowInformationComponent;
    @FXML private ShowInformationController m_ShowInformationComponentController;
    @FXML private VBox m_UpdateUsernameComponent;
    @FXML private UpdateUsernameController m_UpdateUsernameComponentController;

    // ------ CONTROLLERS AND COMPONENTS ------

    @FXML private MenuItem createNewRepositoryMenuItem;
    @FXML private MenuItem loadRepositoryByPathMenuItem;
    @FXML private MenuItem loadRepositoryFromXMLMenuItem;
    @FXML private MenuItem cloneRepositoryMenuItem;
    @FXML private RadioMenuItem changeThemeToLightRadioMenuItem;
    @FXML private ToggleGroup themes;
    @FXML private RadioMenuItem changeThemeToDarkRadioMenuItem;
    @FXML private RadioMenuItem changeThemeToStadiumRadioMenuItem;
    @FXML private RadioMenuItem setBackgroundImageRadioMenuItem;
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

    @FXML
    public void initialize() throws IOException
    {
        ComponentControllerConnector connector = new ComponentControllerConnector();

        // connect controllers and components
        FXMLLoader fxmlLoader = connector.getFXMLLoader(CREATE_NEW_REPOSITORY_FXML_RESOURCE);
        m_CreateNewRepositoryComponent = fxmlLoader.getRoot();
        m_CreateNewRepositoryComponentController = fxmlLoader.getController();
        m_CreateNewRepositoryComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(CHECKOUT_FXML_RESOURCE);
        m_CheckoutComponent = fxmlLoader.getRoot();
        m_CheckoutComponentController = fxmlLoader.getController();
        m_CheckoutComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(COMMIT_FXML_RESOURCE);
        m_CommitComponent = fxmlLoader.getRoot();
        m_CommitComponentController = fxmlLoader.getController();
        m_CommitComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(CREATE_NEW_BRANCH_FXML_RESOURCE);
        m_CreateNewBranchComponent = fxmlLoader.getRoot();
        m_CreateNewBranchComponentController = fxmlLoader.getController();
        m_CreateNewBranchComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(DELETE_BRANCH_FXML_RESOURCE);
        m_DeleteBranchComponent = fxmlLoader.getRoot();
        m_DeleteBranchComponentController = fxmlLoader.getController();
        m_DeleteBranchComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(LOAD_REPOSITORY_BY_PATH_FXML_RESOURCE);
        m_LoadRepositoryByPathComponent = fxmlLoader.getRoot();
        m_LoadRepositoryByPathComponentController = fxmlLoader.getController();
        m_LoadRepositoryByPathComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(SHOW_INFORMATION_FXML_RESOURCE);
        m_ShowInformationComponent = fxmlLoader.getRoot();
        m_ShowInformationComponentController = fxmlLoader.getController();
        m_ShowInformationComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(LOAD_REPOSITORY_BY_XML_FXML_RESOURCE);
        m_LoadRepositoryByXMLComponent = fxmlLoader.getRoot();
        m_LoadRepositoryByXMLComponentController = fxmlLoader.getController();
        m_LoadRepositoryByXMLComponentController.setTopController(this);

        fxmlLoader = connector.getFXMLLoader(UPDATE_USERNAME_FXML_RESOURCE);
        m_UpdateUsernameComponent= fxmlLoader.getRoot();
        m_UpdateUsernameComponentController = fxmlLoader.getController();
        m_UpdateUsernameComponentController.setTopController(this);
    }

    public void setUpdateUsernameComponent(Parent i_UpdateUsernameComponent) { this.m_UpdateUsernameComponent = (VBox) i_UpdateUsernameComponent; }

    public void setUpdateUsernameComponentController(PopupController i_UpdateUsernameComponentController) { this.m_UpdateUsernameComponentController = (UpdateUsernameController) i_UpdateUsernameComponentController; }

    public void setLoadRepositoryByPathComponent(Parent m_LoadRepositoryByPathComponent) { this.m_LoadRepositoryByPathComponent = (VBox) m_LoadRepositoryByPathComponent; }

    public void setLoadRepositoryByPathComponentController(PopupController m_LoadRepositoryByPathComponentController) { this.m_LoadRepositoryByPathComponentController = (LoadRepositoryByPathController) m_LoadRepositoryByPathComponentController; }

    public void setLoadRepositoryByXMLComponent(Parent m_LoadRepositoryByXMLComponent) { this.m_LoadRepositoryByXMLComponent = (VBox) m_LoadRepositoryByXMLComponent; }

    public void setLoadRepositoryByXMLComponentController(PopupController m_LoadRepositoryByXMLComponentController) { this.m_LoadRepositoryByXMLComponentController = (LoadRepositoryByXMLController) m_LoadRepositoryByXMLComponentController; }

    public void setShowInformationComponent(Parent m_ShowInformationComponent) { this.m_ShowInformationComponent = (ScrollPane) m_ShowInformationComponent; }

    public void setShowInformationComponentController(PopupController m_ShowInformationComponentController) { this.m_ShowInformationComponentController = (ShowInformationController) m_ShowInformationComponentController; }

    public void setCreateNewRepositoryComponent(Parent i_CreateNewRepositoryComponent)
    {
        this.m_CreateNewRepositoryComponent = (VBox) i_CreateNewRepositoryComponent;
    }

    public void setCreateNewRepositoryComponentController(PopupController i_CreateNewRepositoryComponentController)
    {
        this.m_CreateNewRepositoryComponentController = (CreateNewRepositoryController) i_CreateNewRepositoryComponentController;
    }

    public void setCreateNewBranchComponent(Parent i_CreateNewBranchComponent)
    {
        this.m_CreateNewBranchComponent = (VBox) i_CreateNewBranchComponent;
    }

    public void setCreateNewBranchComponentController(PopupController i_CreateNewBranchComponentController)
    {
        this.m_CreateNewBranchComponentController = (CreateNewBranchController) i_CreateNewBranchComponentController;
    }

    public void setDeleteBranchComponent(Parent i_DeleteBranchComponent)
    {
        this.m_DeleteBranchComponent = (VBox) i_DeleteBranchComponent;
    }

    public void setDeleteBranchComponentController(PopupController i_DeleteBranchComponentController)
    {
        this.m_DeleteBranchComponentController = (DeleteBranchController) i_DeleteBranchComponentController;
    }

    public void setCheckoutComponent(Parent i_CheckoutComponent)
    {
        this.m_CheckoutComponent = (VBox) i_CheckoutComponent;
    }

    public void setCheckoutComponentController(PopupController i_CheckoutComponentController)
    {
        this.m_CheckoutComponentController = (CheckoutController) i_CheckoutComponentController;
    }

    public void setCommitComponent(Parent i_CommitComponent)
    {
        this.m_CommitComponent = (VBox) i_CommitComponent;
    }

    public void setCommitComponentController(PopupController i_CommitComponentController)
    {
        this.m_CommitComponentController = (CommitController) i_CommitComponentController;
    }

    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    @FXML
    public void createNewRepositoryButtonAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Create new repository", CREATE_NEW_REPOSITORY_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    void createBranchAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Create new branch", CREATE_NEW_BRANCH_FXML_RESOURCE, this);
        stage.showAndWait();

    }

    @FXML
    private void commitAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Commit", COMMIT_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    private void updateUsernameAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Update username", UPDATE_USERNAME_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    void showActiveBranchHistoryAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show commit history", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Branch activeBranch = m_MainController.getActiveBranch();
            m_ShowInformationComponentController.showActiveBranchHistory(activeBranch);
            Stage stage = StageUtilities.createPopupStage("Show active branch history", SHOW_INFORMATION_FXML_RESOURCE, this);
            stage.showAndWait();
        }
    }

    @FXML
    void showAllBranchesAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show commit history", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            Head head = m_MainController.getHead();
            Map<String, Branch> branches = m_MainController.getBranches();
            Stage stage = StageUtilities.createPopupStage("Show all Branches", SHOW_INFORMATION_FXML_RESOURCE, this);
            m_ShowInformationComponentController.showAllBranches(branches, head);
            stage.showAndWait();
        }
    }

    @FXML
    void showCommitHistoryAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show commit history", "Repository have to be loaded or initialized before making this operation")
            .showAndWait();
        }
        else
        {
            NodeMaps nodeMaps = m_MainController.getNodeMaps();
            m_ShowInformationComponentController.showDetailsOfCurrentCommit(nodeMaps);
            Stage stage = StageUtilities.createPopupStage("Show commit history", SHOW_INFORMATION_FXML_RESOURCE, this);
            stage.showAndWait();
        }
    }

    @FXML
    void showStatusAction(ActionEvent event) throws IOException
    {
        if(isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Show commit history", "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            m_ShowInformationComponentController.showStatus();
            Stage stage = StageUtilities.createPopupStage("Show status", SHOW_INFORMATION_FXML_RESOURCE, this);
            stage.showAndWait();
        }
    }

    @FXML
    public void updateUserNameAction(ActionEvent actionEvent)
    {
    }

    @FXML
    public void loadRepositoryByPathAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Load repository", LOAD_REPOSITORY_BY_PATH_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    @FXML
    public void loadRepositoryByXMLAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Load repository", LOAD_REPOSITORY_BY_XML_FXML_RESOURCE, this);
        stage.showAndWait();
    }

    public void createNewBranch(String i_BranchName) throws IOException
    {
        m_MainController.createNewBranch(i_BranchName);
    }

    public boolean commit(String i_Message) throws IOException
    {
        return m_MainController.commit(i_Message);
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
        m_MainController.createNewRepository(i_UserInputPath, i_UserInputRepoName);
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

    public void loadEmptyRepository(Path userInputPath) throws IOException
    {
        m_MainController.loadEmptyRepository(userInputPath);
    }

    public void loadRepositoryByPath(Path userInputPath) throws IOException, ParseException
    {
        m_MainController.loadRepositoryByPath(userInputPath);
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
}