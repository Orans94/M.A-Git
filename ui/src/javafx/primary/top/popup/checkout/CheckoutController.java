package javafx.primary.top.popup.checkout;

import engine.Branch;
import engine.OpenChanges;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.Map;

public class CheckoutController implements PopupController
{
    @FXML private ChoiceBox<String> branchNamesChoiceBox;
    @FXML private Button checkoutButton;
    private TopController m_TopController;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController; }

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

    @FXML void checkoutAction(ActionEvent event) throws IOException
    {
        String branchName = branchNamesChoiceBox.getValue();
        if(!m_TopController.isRBBranch(branchName))
        {
            if (!m_TopController.isBranchPointedCommitSHA1Empty(branchName))
            {

                OpenChanges openChanges = m_TopController.getFileSystemStatus();
                if (m_TopController.isFileSystemDirty(openChanges))
                {
                    boolean toCommit = AlertFactory.createYesNoAlert("Checkout", "WC status is dirty, would you like to commit before checkout?")
                            .showAndWait().get().getText().equals("Yes");
                    if (toCommit)
                    {
                        m_TopController.showForcedCommitScene(event, null);
                    }
                }

                m_TopController.checkout(branchName);
                m_TopController.updateCommitTree();
                AlertFactory.createInformationAlert("Checkout", "Checkout made successfully").showAndWait();
            }
            else
            {
                String message = "Branch " + branchName + " is not pointing on a commit, the system did not checked out";
                AlertFactory.createErrorAlert("Checkout", message).showAndWait();
            }
        }
        else
        {
            String message = "Cannot checkout to remote branch." + System.lineSeparator()
            + "Would you like to create a new remote tracking branch and checkout to him?";
            boolean toCreateRTB = AlertFactory.createYesNoAlert("Clone repository", message)
                    .showAndWait().get().getText().equals("Yes");;
            if(toCreateRTB)
            {
                m_TopController.createNewRTB(branchName);
                m_TopController.checkout(branchName);
            }
            else
            {
                AlertFactory.createInformationAlert("Clone repository", "The system did not checked out")
                        .showAndWait();
            }
        }
        StageUtilities.closeOpenSceneByActionEvent(event);
    }
}
