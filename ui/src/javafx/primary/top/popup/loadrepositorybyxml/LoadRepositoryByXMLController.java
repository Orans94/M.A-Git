package javafx.primary.top.popup.loadrepositorybypath;

import javafx.BrowseManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;

public class LoadRepositoryByXMLController {

    private TopController m_TopController;

    @FXML private VBox createNewRepositoryComponent;
    @FXML private TextField xmlPathTextField;
    @FXML private Button browseButton;
    @FXML private Button loadButton;

    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }


    @FXML
    public void initialize()
    {
        // binding Create button to directory text field
        BooleanBinding isTextFieldEmpty = Bindings.isEmpty(xmlPathTextField.textProperty());
        loadButton.disableProperty().bind(isTextFieldEmpty);
    }

    @FXML
    void browseButtonAction(ActionEvent event)
    {

        BrowseManager browseManager = new BrowseManager();
        File selectedDirectory = browseManager.openFileChooser(event);

        // if user chose a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            xmlPathTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void loadButtonAction(ActionEvent event)
    {

    }

}

