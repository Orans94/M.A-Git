package javafx.top;

import javafx.AppController;
import javafx.StageFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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

    @FXML
    private TextField directoryTextField;

    @FXML
    private TextField repositoryNameTextField;


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
}