package javafx.primary.top.popup.deletebranch;

import engine.Branch;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class DeleteBranchController implements PopupController
{
    @FXML private TopController m_TopController;
    @FXML private Button deleteBranchButton;
    @FXML private ChoiceBox<String> branchNamesChoiceBox;

    @FXML
    void deleteBranchAction(ActionEvent event) throws IOException
    {
        String branchName = branchNamesChoiceBox.getValue();
        boolean isHeadBranch = m_TopController.isBranchNameRepresentsHead(branchName);
        if (isHeadBranch)
        {
            AlertFactory.createErrorAlert("Delete branch", "Cannot delete active branch").showAndWait();
        }
        else
        {
            AlertFactory.createInformationAlert("Delete branch", "Branch " + branchName + " deleted successfully")
                    .showAndWait();
            m_TopController.deleteBranch(branchName);
            m_TopController.updateCommitTree();
        }
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    public void bindBranchesToChoiceBox(ActionEvent event)
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
