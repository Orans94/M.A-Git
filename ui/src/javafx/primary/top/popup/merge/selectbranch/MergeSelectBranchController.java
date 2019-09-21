package javafx.primary.top.popup.merge.selectbranch;

import engine.Branch;
import engine.MergeNodeMaps;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.Map;

public class MergeSelectBranchController implements PopupController
{

    @FXML private TopController m_TopController;
    @FXML private ChoiceBox<String> branchNamesChoiceBox;

    @FXML private Button startMergeButton;

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }

    @FXML void startMergeButtonAction(ActionEvent event) throws IOException
    {
        // getting their branch name
        String theirBranchName = branchNamesChoiceBox.getValue();

        // merge process
        if(m_TopController.isFastForwardMerge(theirBranchName))
        {
            if(m_TopController.isOursContainsTheirs(theirBranchName))
            {
                AlertFactory.createInformationAlert("Merge", "Fast forward merge, nothing to merge")
                .showAndWait();
            }
            else
            { // their contains ours
                m_TopController.setActiveBranchPointedCommit(theirBranchName);
                AlertFactory.createInformationAlert("Merge", "Fast forward merge, active branch points to " + theirBranchName + "pointed commit")
                        .showAndWait();
            }
        }
        else
        {
            MergeNodeMaps mergeNodeMapsResult = m_TopController.merge(theirBranchName);

            // solve conflicts if exists
            if (mergeNodeMapsResult.getConflicts().size() > 0)
            {
                m_TopController.showMergeSolveConflictsScene(mergeNodeMapsResult);
            }

            // commit the merge
            String theirParentCommitSHA1 = m_TopController.getPointedCommitSHA1(theirBranchName);
            m_TopController.showForcedCommitScene(event, theirParentCommitSHA1);
        }
        // TODO - changed from line num 2 to line num 1
        m_TopController.updateUIComponents();
        //m_TopController.updateCommitTree();

        // close stage
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    public void bindBranchesToChoiceBox()
    {
        Map<String, Branch> branches = m_TopController.getBranches();
        addAllBranchesExceptActiveToChoiceBox(branches);
    }

    public void addAllBranchesExceptActiveToChoiceBox(Map<String, Branch> i_Branches)
    {
        Branch activeBranch = m_TopController.getActiveBranch();

        for(Branch branch : i_Branches.values())
        {
            if (!m_TopController.getActiveBranch().equals(branch))
            {
                branchNamesChoiceBox.getItems().add(branch.getName());
            }
        }

        branchNamesChoiceBox.setValue(activeBranch.getName());
    }


}
