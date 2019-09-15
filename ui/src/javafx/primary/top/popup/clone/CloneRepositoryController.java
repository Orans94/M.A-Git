package javafx.primary.top.popup.clone;

import javafx.AlertFactory;
import javafx.BrowseManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

public class CloneRepositoryController implements PopupController
{

    @FXML private TextField sourceDirectoryTextField;
    @FXML private TextField destinationDirectoryTextField;
    @FXML private TextField repositoryNameTextField;
    @FXML private Button cloneButton;
    private TopController m_TopController;

    @FXML
    public void initialize()
    {
        BooleanBinding isTextFieldEmpty = Bindings.or(Bindings.isEmpty(sourceDirectoryTextField.textProperty())
                , Bindings.isEmpty(destinationDirectoryTextField.textProperty()));
        cloneButton.disableProperty().bind(isTextFieldEmpty);
    }

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML
    void browseDestinationDirectoryAction(ActionEvent event)
    {
        BrowseManager browseManager = new BrowseManager();
        File selectedDirectory = browseManager.openDirectoryChooser(event);

        // if user chose a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            destinationDirectoryTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void browseSourceDirectoryAction(ActionEvent event)
    {
        BrowseManager browseManager = new BrowseManager();
        File selectedDirectory = browseManager.openDirectoryChooser(event);

        // if user chose a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            sourceDirectoryTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void cloneButtonAction(ActionEvent event) throws IOException, ParseException
    {
        if(m_TopController.isSourceRepresentsMAGitRepository(sourceDirectoryTextField.getText()))
        {
            m_TopController.cloneRepository(Paths.get(sourceDirectoryTextField.getText())
                    , Paths.get(destinationDirectoryTextField.getText()), repositoryNameTextField.getText());
        }
        else
        {
            AlertFactory.createErrorAlert("Clone repository", "The source directory is not a M.A Git repository")
            .showAndWait();
        }
    }
}