package javafx.primary.top;

import engine.OpenChanges;
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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

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

    public void setCreateNewRepositoryComponent(Parent i_CreateNewRepositoryComponent)
    {
        this.m_CreateNewRepositoryComponent = (VBox)i_CreateNewRepositoryComponent;
    }

    public void setCreateNewRepositoryComponentController(PopupController i_CreateNewRepositoryComponentController)
    {
        this.m_CreateNewRepositoryComponentController = (CreateNewRepositoryController)i_CreateNewRepositoryComponentController;
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


    }


    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    @FXML
    public void createNewRepositoryButtonAction(ActionEvent actionEvent) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Create new repository", CREATE_NEW_REPOSITORY_FXML_RESOURCE, this);
        stage.setResizable(false);
        stage.showAndWait();
    }

    @FXML
    void createBranchAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Create new branch", CREATE_NEW_BRANCH_FXML_RESOURCE,this);
        stage.setResizable(false);
        stage.showAndWait();

    }

    @FXML
    private void commitAction(ActionEvent event) throws IOException
    {
        Stage stage = StageUtilities.createPopupStage("Commit", COMMIT_FXML_RESOURCE, this);
        stage.setResizable(false);
        stage.showAndWait();
    }

    public void createNewBranch(String i_BranchName) throws IOException
    {
        m_MainController.createNewBranch(i_BranchName);
    }

    public boolean commit(String i_Message) throws IOException
    { return m_MainController.commit(i_Message); }

    public void checkout(String i_BranchName) throws IOException
    { m_MainController.checkout(i_BranchName);}

    public void deleteBranch(String i_BranchName) throws IOException
    { m_MainController.deleteBranch(i_BranchName); }

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
}