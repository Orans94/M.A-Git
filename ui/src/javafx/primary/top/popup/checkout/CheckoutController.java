package javafx.primary.top.popup.checkout;

import engine.Branch;
import engine.OpenChanges;
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
        if (!m_TopController.isBranchPointedCommitSHA1Empty(branchName))
        {
            OpenChanges openChanges = m_TopController.getFileSystemStatus();
            if (m_TopController.isFileSystemDirty(openChanges))
            {
                boolean toCommit = AlertFactory.createYesNoAlert("Checkout", "WC status is dirty, would you like to commit before checkout?")
                .showAndWait().get().getText().equals("Yes");
                if (toCommit)
                {
                    m_TopController.showForcedCommitScene(event);
                }
            }

            m_TopController.checkout(branchName);
            AlertFactory.createInformationAlert("Checkout", "Checkout made successfully").showAndWait();
        }
        else
        {
            String message = "Branch " + branchName + " is not pointing on a commit, the system did not checked out";
            AlertFactory.createErrorAlert("Checkout", message).showAndWait();
        }
        StageUtilities.closeOpenSceneByActionEvent(event);
    }
}
