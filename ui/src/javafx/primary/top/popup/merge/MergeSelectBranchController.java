package javafx.primary.top.popup.merge;

import engine.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.util.Map;

public class MergeSelectBranchController implements PopupController
{

    @FXML private TopController m_TopController;
    @FXML private ChoiceBox<String> branchNamesChoiceBox;

    @FXML private Button startMergeButton;

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }

    @FXML void startMergeButtonAction(ActionEvent event)
    {

    }

    public void bindBranchesToChoiceBox()
    {
        Map<String, Branch> branches = m_TopController.getBranches();
        addAllBranchesToChoiceBox(branches);
    }

    public void addAllBranchesToChoiceBox(Map<String, Branch> i_Branches)
    {
        Branch activeBranch = m_TopController.getActiveBranch();

        for(Branch branch : i_Branches.values())
        {
            branchNamesChoiceBox.getItems().add(branch.getName());
        }

        branchNamesChoiceBox.setValue(activeBranch.getName());
    }


}
