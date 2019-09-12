package javafx.primary.top.popup.commit;

import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CommitController implements PopupController
{
    @FXML private TextField commitMessageTextField;
    @FXML private Button commitButton;
    @FXML private TopController m_TopController;
    private String m_SecondParentSHA1;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML void commitAction(ActionEvent event) throws IOException
    {
        if(m_TopController.isRepositoryNull())
        {
            AlertFactory.createErrorAlert("Commit" , "Repository have to be loaded or initialized before making this operation")
                    .showAndWait();
        }
        else
        {
            boolean isWCDirty;
            if (!m_TopController.isRootFolderEmpty())
            {
                String commitMessage = commitMessageTextField.getText();
                isWCDirty = m_TopController.commit(commitMessageTextField.getText(), m_SecondParentSHA1);
                if (!isWCDirty && m_SecondParentSHA1 == null)
                { // there is nothing to commit if WC is clean AND we aren't in merging process
                    AlertFactory.createInformationAlert("Commit", "There is nothing to commit, WC status is clean")
                            .showAndWait();
                }
                else
                {
                    AlertFactory.createInformationAlert("Commit", "Commited successfully").showAndWait();
                    m_TopController.addNewestCommitToTableView();
                    m_TopController.updateCommitTree();
                }
            }
            else
            {
                AlertFactory.createErrorAlert("Commit", "Root folder is empty, there is nothing to commit")
                        .showAndWait();
            }
        }

        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    public String getSecondParentSHA1()
    {
        return m_SecondParentSHA1;
    }

    public void setSecondParentSHA1(String i_SecondParentSHA1)
    {
        this.m_SecondParentSHA1 = i_SecondParentSHA1;
    }
}
