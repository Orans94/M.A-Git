package javafx.primary.top.popup.commit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;

public class CommitController
{
    @FXML private TextField commitMessageTextField;
    @FXML private Button commitButton;
    @FXML private Button cancelButton;
    @FXML private TopController m_TopController;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML void commitAction(ActionEvent event) { m_TopController.commit(commitMessageTextField.getText()); }
}
