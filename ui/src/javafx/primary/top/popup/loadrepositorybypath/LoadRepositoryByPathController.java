package javafx.primary.top.popup.loadrepositorybypath;

import javafx.AlertFactory;
import javafx.BrowseManager;
import javafx.StageUtilities;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

public class LoadRepositoryByPathController implements PopupController
{
    @FXML private TopController m_TopController;

    @FXML private VBox createNewRepositoryComponent;
    @FXML private TextField directoryTextField;
    @FXML private Button browseButton;
    @FXML private Button loadButton;

    @FXML
    public void initialize()
    {
        // binding Create button to directory text field
        BooleanBinding isTextFieldEmpty = Bindings.isEmpty(directoryTextField.textProperty());
        loadButton.disableProperty().bind(isTextFieldEmpty);
    }


    @FXML
    void browseButtonAction(ActionEvent event)
    {
        BrowseManager browseManager = new BrowseManager();
        File selectedDirectory = browseManager.openDirectoryChooser(event);

        // if user chose a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            directoryTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void loadButtonAction(ActionEvent event) throws IOException, ParseException
    {
        Path userInputPath;

        userInputPath = Paths.get(directoryTextField.getText());


        if (m_TopController.isRepository(userInputPath))
        {
            if (m_TopController.isRepositoryEmpty(userInputPath))
            {
                m_TopController.loadEmptyRepository(userInputPath);
                notifyRepositoryHasBeenLoaded();
            }
            else
            {
                m_TopController.loadRepositoryByPath(userInputPath);
                notifyRepositoryHasBeenLoaded();
            }
            StageUtilities.closeOpenSceneByActionEvent(event);
        }
        else
        {
            notifyThePathIsntRepository(userInputPath);
        }
    }

    private void notifyThePathIsntRepository(Path i_UserInputPath)
    {
        String message = "The given path: " + i_UserInputPath + " not represents a M.A Git repository ";

        AlertFactory.createInformationAlert("Load repository", message)
                .showAndWait();

    }

    private void notifyRepositoryHasBeenLoaded()
    {
        String repositoryName = m_TopController.getRepositoryName();
        String message = "Repository " + repositoryName + " has been loaded";

        AlertFactory.createInformationAlert("Load repository", message)
                .showAndWait();
    }

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }
}
