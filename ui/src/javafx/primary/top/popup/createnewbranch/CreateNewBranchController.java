package javafx.primary.top.popup.createnewbranch;

import engine.OpenChanges;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateNewBranchController implements PopupController
{
    @FXML private TopController m_TopController;
    @FXML private CheckBox checkoutAfterCreateCheckbox;
    @FXML private Button createNewBranchButton;
    @FXML private TextField branchNameTextField;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML void createNewBranchAction(ActionEvent event) throws IOException
    {
        if(m_TopController.isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Commit" , "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            String branchName = branchNameTextField.getText();
            boolean isBranchExists = m_TopController.isBranchExists(branchName);
            if (isBranchExists)
            {
                AlertFactory.createErrorAlert("Create New Branch", "Branch "+branchName +" already exists")
                        .showAndWait();
            }
            else
            {
                if (m_TopController.isBranchNameEqualsHead(branchName))
                {
                    AlertFactory.createErrorAlert("Create New Branch", "You can not set branch name to HEAD")
                            .showAndWait();
                }
                else
                {
                    m_TopController.createNewBranch(branchName);
                    boolean toCheckout = checkoutAfterCreateCheckbox.isSelected();
                    if (toCheckout)
                    {
                        OpenChanges openChanges = m_TopController.getFileSystemStatus();
                        if (m_TopController.isFileSystemDirty(openChanges))
                        {
                            // Create new branch , WC dirty - didnt checkout
                            AlertFactory.createInformationAlert("Create New Branch", "Branch " + branchName + " created successfully"
                            +System.lineSeparator()+"The WC status is dirty, the system did not checked out")
                                    .showAndWait();
                        }
                        else
                        {
                            // Crate new branch and Checkout
                            m_TopController.setActiveBranchName(branchName);
                            AlertFactory.createInformationAlert("Create New Branch", "Branch " + branchName + " created successfully"
                                    +System.lineSeparator()+ "Checkout to branch " + branchName + " has been made successfully")
                                    .showAndWait();
                        }
                    }
                    else
                    {
                        // create new branch
                        AlertFactory.createInformationAlert("Create New Branch", "Branch " + branchName + " created successfully")
                                .showAndWait();
                    }
                }
            }
        }

        StageUtilities.closeOpenSceneByActionEvent(event);
    }
}
