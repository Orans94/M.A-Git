package javafx.primary.left.committree.node.commit.contextmenu;

import engine.Commit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.left.committree.CommitTreeManager;
import javafx.primary.left.committree.node.commit.CommitNodeController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ContextMenuController
{
    private String m_CommitSHA1;
    @FXML private ContextMenu commitContextMenu;
    @FXML private MenuItem createNewBranchMenuItem;
    @FXML private MenuItem resetHeadBranchMenuItem;
    @FXML private MenuItem mergeHeadBranchMenuItem;
    @FXML private MenuItem deleteBranchMenuItem;
    private CommitTreeManager m_CommitTreeManager;

    public void setCommitTreeManager(CommitTreeManager i_CommitTreeManager) { this.m_CommitTreeManager = i_CommitTreeManager; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }

    @FXML
    void createNewBranchMenuItemAction(ActionEvent event)
    {
    }

    @FXML
    void deleteBranchMenuItemAction(ActionEvent event)
    {

    }

    @FXML
    void mergeHeadBranchMenuItemAction(ActionEvent event)
    {

    }

    @FXML
    void resetHeadBranchMenuItemAction(ActionEvent event)
    {

    }

}
