package javafx.primary.top.popup.deletebranch;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;

public class DeleteBranchController
{
    @FXML private TopController m_TopController;
    @FXML private SplitMenuButton branchNameSplitMenuButton;
    @FXML private Button deleteBranchButton;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML void deleteBranch(ActionEvent event) { m_TopController.deleteBranch(branchNameSplitMenuButton.getText());}
}
