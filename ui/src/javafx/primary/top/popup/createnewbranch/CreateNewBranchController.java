package javafx.primary.top.popup.createnewbranch;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateNewBranchController
{
    @FXML private TopController m_TopController;
    @FXML private CheckBox checkoutAfterCreateCheckbox;
    @FXML private Button createNewBranchButton;
    @FXML private TextField branchNameTextField;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML void createNewBranchAction(ActionEvent event) throws IOException
    { m_TopController.createNewBranch(checkoutAfterCreateCheckbox, branchNameTextField.getText()); }
}
