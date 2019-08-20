package javafx.primary.top;

import javafx.AppController;
import javafx.StageFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.popup.checkout.CheckoutController;
import javafx.primary.top.popup.commit.CommitController;
import javafx.primary.top.popup.createnewbranch.CreateNewBranchController;
import javafx.primary.top.popup.createnewrepository.CreateNewRepositoryController;
import javafx.primary.top.popup.deletebranch.DeleteBranchController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class TopController
{
    private AppController m_MainController;

    //---------new--------
    @FXML private CreateNewBranchController createNewBranchComponentController;
    @FXML private CommitController commitComponentController;
    @FXML private DeleteBranchController deleteBranchComponentController;
    @FXML private CheckoutController checkoutComponentController;
    @FXML private CreateNewRepositoryController createNewRepositoryComponentController;

    //---------new--------


    //-------old-----------
    @FXML private MenuItem createNewRepository;
    @FXML private MenuItem loadRepositoryByPath;
    @FXML private MenuItem loadRepositoryFromXML;
    @FXML private MenuItem cloneRepository;
    @FXML private RadioMenuItem changeThemeToLight;
    @FXML private ToggleGroup themes;
    @FXML private RadioMenuItem changeThemeToDark;
    @FXML private RadioMenuItem changeThemeToStadium;
    @FXML private RadioMenuItem setBackgroundImage;
    @FXML private MenuItem commit;
    @FXML private MenuItem pull;
    @FXML private MenuItem push;
    @FXML private MenuItem createNewBranch;
    @FXML private MenuItem deleteBranch;
    @FXML private MenuItem checkout;
    @FXML private MenuItem merge;
    @FXML private MenuItem resetBranch;
    @FXML private MenuItem showAllBranches;
    @FXML private MenuItem showCurrentBranchHistory;
    @FXML private MenuItem showStatus;
    @FXML private MenuItem showCurrentCommitDetails;
    @FXML private MenuItem contactUs;
    @FXML private MenuItem about;
    @FXML private Button refresh;
    @FXML private TextField directoryTextField;
    @FXML private TextField repositoryNameTextField;


    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    public void createNewRepositoryAction(ActionEvent actionEvent)
    {
        try
        {
            StageFactory stageFactory = new StageFactory();
            Stage stage = stageFactory.createStage("Create new repository", CREATE_NEW_REPOSITORY_FXML_RESOURCE, Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        m_MainController.createNewRepository(actionEvent);
    }



    public File browseDirectory(ActionEvent actionEvent)
    {
        Node source = (Node)actionEvent.getSource();
        Window theStage = source.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(theStage);


        return selectedDirectory;
    }

    public void createNewRepositoryBrowseButtonAction(ActionEvent actionEvent)
    {
        File filePath = browseDirectory(actionEvent);
        String pathString = filePath != null ? filePath.toString() : "";
        directoryTextField.setText(pathString);
    }

    public void createNewBranch(CheckBox i_CheckoutAfterCreateCheckbox, String i_BranchName)
    {
        m_MainController.createNewBranch(i_CheckoutAfterCreateCheckbox, i_BranchName);
    }

    public void commit(String i_Message) { m_MainController.commit(i_Message); }

    public void checkout(String i_BranchName) { m_MainController.checkout(i_BranchName);}

    public void deleteBranch(String i_BranchName) { m_MainController.deleteBranch(i_BranchName); }
}