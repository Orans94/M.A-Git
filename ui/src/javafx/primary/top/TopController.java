package javafx.primary.top;

import javafx.AppController;
import javafx.event.ActionEvent;
import javafx.factories.StageFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.primary.top.popup.checkout.CheckoutController;
import javafx.primary.top.popup.commit.CommitController;
import javafx.primary.top.popup.createnewbranch.CreateNewBranchController;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.primary.top.popup.deletebranch.DeleteBranchController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static javafx.CommonResourcesPaths.*;

public class TopController
{
    private AppController m_MainController;

    // ------ CONTROLLERS AND COMPONENTS ------

    @FXML private CreateNewBranchController m_CreateNewBranchComponentController;
    @FXML private DeleteBranchController m_DeleteBranchComponentController;
    @FXML private CheckoutController m_CheckoutComponentController;
    @FXML private CommitController m_CommitComponentController;
    @FXML private VBox m_CreateNewRepositoryComponent;
    @FXML private CreateNewRepositoryController m_CreateNewRepositoryComponentController;

    // ------ CONTROLLERS AND COMPONENTS ------



    //-------old-----------
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
        // create new repo
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(CREATE_NEW_REPOSITORY_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        VBox createNewRepositoryComponent = fxmlLoader.load(url.openStream());
        CreateNewRepositoryController createNewRepositoryController = fxmlLoader.getController();


        // create new branch
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource(CREATE_NEW_BRANCH_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        VBox createNewBranchComponent = fxmlLoader.load(url.openStream());
        CreateNewRepositoryController createNewBranchController = fxmlLoader.getController();

        // commit
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource(COMMIT_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        VBox commitComponent = fxmlLoader.load(url.openStream());
        CommitController commitController = fxmlLoader.getController();

        // checkout
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource(CHECKOUT_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        VBox checkoutComponent = fxmlLoader.load(url.openStream());
        CheckoutController checkoutController = fxmlLoader.getController();



        m_CreateNewRepositoryComponent = createNewRepositoryComponent;

        m_CreateNewRepositoryComponentController = createNewRepositoryController;

        m_CreateNewRepositoryComponentController.setTopController(this);
        /*m_CreateNewBranchComponentController.setTopController(this);
        m_CommitComponentController.setTopController(this);
        m_DeleteBranchComponentController.setTopController(this);
        m_CheckoutComponentController.setTopController(this);*/
    }

    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    public void createNewRepositoryButtonAction(ActionEvent actionEvent)
    {
        StageFactory stageFactory = new StageFactory();
        Stage stage = stageFactory.createPopupStage("Create new repository", m_CreateNewRepositoryComponent, Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void createNewBranch(CheckBox i_CheckoutAfterCreateCheckbox, String i_BranchName) throws IOException
    {
        m_MainController.createNewBranch(i_CheckoutAfterCreateCheckbox, i_BranchName);
    }

    public void commit(String i_Message) throws IOException
    { m_MainController.commit(i_Message); }

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
}