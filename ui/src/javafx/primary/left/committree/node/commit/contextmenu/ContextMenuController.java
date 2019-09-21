package javafx.primary.left.committree.node.commit.contextmenu;

import engine.Branch;
import engine.Commit;
import engine.MergeNodeMaps;
import engine.OpenChanges;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.primary.left.committree.CommitTreeManager;
import javafx.primary.left.committree.node.commit.CommitNodeController;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.primary.top.popup.merge.selectbranch.MergeSelectBranchController;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static engine.StringFinals.EMPTY_STRING;
import static javafx.CommonResourcesPaths.*;

public class ContextMenuController implements PopupController
{
    private String m_CommitSHA1;
    @FXML private ContextMenu commitContextMenu;
    @FXML private MenuItem createNewBranchMenuItem;
    @FXML private MenuItem resetHeadBranchMenuItem;
    @FXML private MenuItem mergeHeadBranchMenuItem;
    @FXML private MenuItem deleteBranchMenuItem;
    @FXML private Label chooseBranchLabel;
    @FXML private ChoiceBox<String> branchChoiceBox = new ChoiceBox<>();
    @FXML private TextField branchNameTextField;
    private CommitTreeManager m_CommitTreeManager;
    private TopController m_TopController;
    @FXML private CheckBox toCheckout = new CheckBox();

    public void setCommitTreeManager(CommitTreeManager i_CommitTreeManager) { this.m_CommitTreeManager = i_CommitTreeManager; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }

    private void handleCreateNewBranch(String i_BranchName) throws IOException
    {
        String branchName = i_BranchName;
        String commitSHA1Selected = m_CommitSHA1;

        boolean isBranchExists = m_TopController.isBranchExists(branchName);
        if (isBranchExists)
        {
            AlertFactory.createErrorAlert("Create New Branch", "Branch " + branchName + " already exists")
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
                String rbName = m_TopController.getRBNameFromCommitSHA1(commitSHA1Selected);

                if (rbName != null)
                {
                    // the selected commit is pointed by RB
                    String messageRTBIssue = "The commit you choose recognized as a commit that pointed by RB." + System.lineSeparator() +
                            "Would you like to create the new branch as an RTB?" + System.lineSeparator() +
                            "*Note: if you chose to create it as RTB, the branch name will be the same as the RB.";
                    boolean createBranchAsRTB = AlertFactory.createYesNoAlert("Create new branch", messageRTBIssue)
                            .showAndWait().get().getText().equals("Yes");
                    String createdBranchName;
                    if (createBranchAsRTB)
                    {
                        rbName = m_TopController.getTrackingBranchName(rbName);
                        createdBranchName = rbName;
                        m_TopController.createNewRTB(rbName);
                    }
                    else
                    {
                        createdBranchName = branchName;
                        m_TopController.createNewBranch(branchName, commitSHA1Selected);
                    }
                    handleCheckoutUserDecision(createdBranchName);
                }
                else
                {
                    m_TopController.createNewBranch(branchName, commitSHA1Selected);
                    handleCheckoutUserDecision(branchName);
                }
                m_TopController.updateCommitTree();
            }
        }
    }

    private void handleCheckoutUserDecision(String i_BranchName) throws IOException
    {
        boolean isCheckout = toCheckout.isSelected();
        if (isCheckout)
        {
            OpenChanges openChanges = m_TopController.getFileSystemStatus();
            if (m_TopController.isFileSystemDirty(openChanges))
            {
                // WC dirty - didnt checkout
                AlertFactory.createInformationAlert("Create New Branch", "Branch " + i_BranchName + " created successfully"
                        + System.lineSeparator() + "The WC status is dirty, the system did not checked out")
                        .showAndWait();
            }
            else
            {
                // WC clean - we can checkout
                m_TopController.setActiveBranchName(i_BranchName);
                AlertFactory.createInformationAlert("Create New Branch", "Branch " + i_BranchName + " created successfully"
                        + System.lineSeparator() + "Checkout to branch " + i_BranchName + " has been made successfully")
                        .showAndWait();
            }
        }
        else
        {
            AlertFactory.createInformationAlert("Create New Branch", "Branch " + i_BranchName + " created successfully")
                    .showAndWait();
        }
    }

    @FXML
    void createNewBranchMenuItemAction(ActionEvent event) throws IOException
    {
        String commit = m_CommitSHA1;
        Stage stage = StageUtilities.createPopupStage("Create new branch", ENTER_BRANCH_NAME_FXML_RESOURCE, m_TopController);
        ContextMenuController contextMenuController = getContextMenuController(stage);
        contextMenuController.m_CommitSHA1 = commit;
        stage.showAndWait();
    }

    @FXML
    void deleteBranchMenuItemAction(ActionEvent event) throws IOException
    {
        // -------------------- SAME TRICK --------------------------
        ChoiceBox<String> savedChoiceBox = branchChoiceBox;
        // -------------------- SAME TRICK --------------------------

        List<Branch> containedBranches;
        containedBranches = m_CommitTreeManager.getContainedBranches(m_CommitSHA1);
        if(containedBranches.size() == 1)
        {
            if(!containedBranches.get(0).getName().equals(m_TopController.getActiveBranchName()))
            {
                m_TopController.deleteBranch(containedBranches.get(0).getName());
                m_TopController.updateUIComponents();
                AlertFactory.createInformationAlert("Delete branch", "Branch " + containedBranches.get(0).getName() + " deleted successfully").showAndWait();
            }
            else
            {
                AlertFactory.createErrorAlert("Delete branch", "Cant delete active branch").showAndWait();
            }
        }
        else if(containedBranches.size() > 1)
        {

            //chooseBranchLabel.setText("In order to delete branch , please choose a branch from the list below");
            Stage stage = StageUtilities.createPopupStage("Choose Branch", CHOOSE_BRANCH_FXML_RESOURCE, m_TopController);
            stage.setOnCloseRequest(evt -> {
                // prevent window from closing
                evt.consume();
            });

            // -------------------- SAME TRICK --------------------------
            ContextMenuController contextMenuController = getContextMenuController(stage);
            contextMenuController.branchChoiceBox.setItems(savedChoiceBox.getItems());
            // -------------------- SAME TRICK --------------------------

            stage.showAndWait();

            m_TopController.deleteBranch(branchChoiceBox.getSelectionModel().getSelectedItem());
            m_TopController.updateUIComponents();
            AlertFactory.createInformationAlert("Delete branch", "Branch " + branchChoiceBox.getSelectionModel().getSelectedItem() + " deleted successfully").showAndWait();
        }
        else
        {
            AlertFactory.createErrorAlert("Delete branch", "There are no branches pointing on the current commit")
                    .showAndWait();
        }
    }

    private ContextMenuController getContextMenuController(Stage i_CreatedStage)
    {
        return ((FXMLLoader) i_CreatedStage.getScene().getUserData()).getController();
    }

    @FXML
    void mergeHeadBranchMenuItemAction(ActionEvent event) throws IOException
    {
        List<Branch> branchNames;
        ChoiceBox<String> savedChoiceBox = branchChoiceBox;

        branchNames = m_CommitTreeManager.getContainedBranches(m_CommitSHA1);
        if(branchNames.size() == 1)
        {
            handleMerge(event, branchNames.get(0).getName());
        }
        else if(branchNames.size() > 1)
        {
            chooseBranchLabel.setText("In order to merge , please choose a branch from the list below");
            Stage stage = StageUtilities.createPopupStage("Choose Branch", CHOOSE_BRANCH_FXML_RESOURCE, m_TopController);
            ContextMenuController contextMenuController = getContextMenuController(stage);
            contextMenuController.branchChoiceBox.setItems(savedChoiceBox.getItems());
            stage.setOnCloseRequest(evt -> {
                // prevent window from closing
                evt.consume();
            });
            stage.showAndWait();
            handleMerge(event, branchChoiceBox.getSelectionModel().getSelectedItem());
        }
        else
        {
            AlertFactory.createErrorAlert("Merge", "There are no branches pointing on the current commit")
                    .showAndWait();
        }
    }

    @FXML
    void resetHeadBranchMenuItemAction(ActionEvent event) throws IOException
    {
        if(!m_CommitTreeManager.getActiveBranchPointedCommit().equals(m_CommitSHA1))
        {
            resetBranchAnimateIfSelected(m_CommitSHA1);
            m_TopController.changeActiveBranchPointedCommit(m_CommitSHA1);
            m_TopController.checkout(m_TopController.getActiveBranchName());
            m_TopController.showDetailsOfCurrentCommitScene(event);
            m_TopController.updateUIComponents();
            AlertFactory.createInformationAlert("Reset branch", "The active branch is now pointing on commit " + m_CommitSHA1)
                    .showAndWait();

        }
        else
        {
            AlertFactory.createErrorAlert("Reset branch", "Head branch is already pointing on that commit")
                    .showAndWait();
        }
    }

    private void resetBranchAnimateIfSelected(String I_CommitSHA1)
    {
        if (m_TopController.getResertBranchAnimationCheckMenuItem().isSelected())
            m_TopController.resetBranchAnimate(I_CommitSHA1);
    }

    @FXML
    void chooseBranchAction(ActionEvent event) throws IOException
    {
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    @FXML
    void createBranchAction(ActionEvent event) throws IOException
    {
        handleCreateNewBranch(branchNameTextField.getText());
        branchNameTextField.clear();
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    public void addToCheckBox()
    {
        for(Branch branch : m_CommitTreeManager.getContainedBranches(m_CommitSHA1))
        {
            if(!branch.getName().equals(m_TopController.getActiveBranchName()))
            {
                branchChoiceBox.getItems().add(branch.getName());
                branchChoiceBox.getSelectionModel().selectFirst();
            }
        }
    }

    @Override
    public void setTopController(TopController i_TopController)
    {
        m_TopController = i_TopController;
    }

    private void handleMerge(ActionEvent event, String i_TheirBranchName) throws IOException
    {
        if(m_TopController.isFastForwardMerge(i_TheirBranchName))
        {
            if(m_TopController.isOursContainsTheirs(i_TheirBranchName))
            {
                AlertFactory.createInformationAlert("Merge", "Fast forward merge, nothing to merge")
                        .showAndWait();
            }
            else
            { // their contains ours
                m_TopController.setActiveBranchPointedCommit(i_TheirBranchName);
                AlertFactory.createInformationAlert("Merge", "Fast forward merge, active branch points to " + i_TheirBranchName + "pointed commit")
                        .showAndWait();
            }
        }
        else
        {
            MergeNodeMaps mergeNodeMapsResult = m_TopController.merge(i_TheirBranchName);

            // solve conflicts if exists
            if (mergeNodeMapsResult.getConflicts().size() > 0)
            {
                m_TopController.showMergeSolveConflictsScene(mergeNodeMapsResult);
            }

            // commit the merge
            String theirParentCommitSHA1 = m_TopController.getPointedCommitSHA1(i_TheirBranchName);
            m_TopController.showForcedCommitScene(event, theirParentCommitSHA1);
        }

        m_TopController.updateUIComponents();
    }
}