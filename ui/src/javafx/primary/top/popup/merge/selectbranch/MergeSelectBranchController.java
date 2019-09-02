package javafx.primary.top.popup.merge.selectbranch;

import engine.Branch;
import engine.Commit;
import engine.NodeMaps;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.nio.file.Path;
import java.util.List;
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
        // getting their branch name
        String theirBranchName = branchNamesChoiceBox.getValue();

        // getting our, their branches
        Branch ourBranch = m_TopController.getActiveBranch();
        Branch theirBranch = m_TopController.getBranches().get(theirBranchName);

        // getting our and their actual commits
        Commit ourCommit = m_TopController.getCommits().get(ourBranch.getCommitSHA1());
        Commit theirCommit = m_TopController.getCommits().get(theirBranch.getCommitSHA1());

        // getting ancestor commit
        Commit ancestorCommit = m_TopController.getCommitAncestor(ourCommit, theirCommit);



        m_TopController.merge(theirBranchName);
        // conflicts
        List<Path> mergeConflictsFilesList = m_TopController.getMergeConflicts(ancestorCommit, ourCommit, theirCommit);
        ///////////////////////////////////////////////////////////////////

        // for each file (path)
/*        for (Path conflictedFilePath : mergeConflictsFilesList)
        {
            // open merge solve conflict scene and solve conflict
        }*/

        //commit




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
