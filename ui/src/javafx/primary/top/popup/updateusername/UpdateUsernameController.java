package javafx.primary.top.popup.updateusername;

import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class UpdateUsernameController implements PopupController
{
    private TopController m_TopController;
    @FXML private TextField usernameTextField;
    @FXML private Button updateUsernameButton;

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController;}

    @FXML void updateUsernameAction(ActionEvent event)
    {
        m_TopController.updateUsername(usernameTextField.getText());
        AlertFactory.createInformationAlert("Update username", "Username " + usernameTextField.getText() + " has been updated successfully")
                .showAndWait();
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

}
