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

import java.io.IOException;
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

    @FXML void startMergeButtonAction(ActionEvent event) throws IOException
    {
        // getting their branch name
        String theirBranchName = branchNamesChoiceBox.getValue();

        try
        {
            m_TopController.merge(theirBranchName);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        // conflicts
        List<Path> mergeConflictsFilesList = m_TopController.merge(theirBranchName);

/*        for (Path conflictedFilePath : mergeConflictsFilesList)
        {
            // open merge solve conflict scene and solve conflict
        }*/

        //TODO open commit without X and not closeable
        m_TopController.showCommitScene(event);
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
