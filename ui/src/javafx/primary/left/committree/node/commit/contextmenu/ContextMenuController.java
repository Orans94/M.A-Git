package javafx.primary.left.committree.node.commit.contextmenu;

import engine.Branch;
import engine.Commit;
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
    @FXML private ChoiceBox<String> branchChoiceBox;
    @FXML private TextField branchNameTextField;
    private CommitTreeManager m_CommitTreeManager;
    private TopController m_TopController;

    public void setCommitTreeManager(CommitTreeManager i_CommitTreeManager) { this.m_CommitTreeManager = i_CommitTreeManager; }

    public void setCommitSHA1(String i_CommitSHA1) { this.m_CommitSHA1 = i_CommitSHA1; }

    @FXML
    void createNewBranchMenuItemAction(ActionEvent event) throws IOException
    {
        String commit = m_CommitSHA1;
        Stage stage = StageUtilities.createPopupStage("Create new branch", ENTER_BRANCH_NAME_FXML_RESOURCE, m_TopController);
        stage.setOnCloseRequest(evt -> {
            // prevent window from closing
            evt.consume();
        });
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

        //branchChoiceBox.getItems().clear();
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
            for(Branch branch : containedBranches)
            {
                branchChoiceBox.getItems().add(branch.getName());
            }
            branchChoiceBox.getSelectionModel().select(0);
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
        branchChoiceBox.getItems().clear();
        List<Branch> branchNames;
 
        branchNames = m_CommitTreeManager.getContainedBranches(m_CommitSHA1);
        if(branchNames.size() == 1)
        {
            m_TopController.merge(branchNames.get(0).getName());
            m_TopController.updateUIComponents();
            AlertFactory.createInformationAlert("Merge", "Merged successfully").showAndWait();
        }
        else if(branchNames.size() > 1)
        {
            for(Branch branch : branchNames)
            {
                branchChoiceBox.getItems().add(branch.getName());
            }
            branchChoiceBox.getSelectionModel().select(1);
            chooseBranchLabel.setText("In order to merge , please choose a branch from the list below");
            Stage stage = StageUtilities.createPopupStage("Choose Branch", CHOOSE_BRANCH_FXML_RESOURCE, m_TopController);
            stage.setOnCloseRequest(evt -> {
                // prevent window from closing
                evt.consume();
            });
            stage.showAndWait();

            m_TopController.merge(branchChoiceBox.getSelectionModel().getSelectedItem());
            m_TopController.updateUIComponents();
            AlertFactory.createInformationAlert("Merge", "Merged successfully").showAndWait();
        }
        else
        {
            AlertFactory.createErrorAlert("Merge", "There are no branches pointing on the current commit")
                    .showAndWait();
        }

        branchChoiceBox.getItems().clear();
    }

    @FXML
    void resetHeadBranchMenuItemAction(ActionEvent event) throws IOException
    {
        if(!m_CommitTreeManager.getActiveBranchPointedCommit().equals(m_CommitSHA1))
        {
            m_CommitTreeManager.resetHeadBranch(m_CommitSHA1);
            m_TopController.updateUIComponents();
            AlertFactory.createInformationAlert("Reset branch", "Head branch reset successful").showAndWait();
        }
        else
        {
            AlertFactory.createErrorAlert("Reset branch", "Head branch is already pointing on that commit")
                    .showAndWait();
        }
    }

    @FXML
    void chooseBranchAction(ActionEvent event) throws IOException
    {
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    @FXML
    void createBranchAction(ActionEvent event) throws IOException
    {
        if(!m_TopController.getBranches().containsKey(branchNameTextField.getText()) && !branchNameTextField.getText().equals(EMPTY_STRING))
        {
            m_TopController.createNewBranch(branchNameTextField.getText(), m_CommitSHA1);
            m_TopController.updateUIComponents();
            AlertFactory.createInformationAlert("Create new branch", "Branch " + branchNameTextField.getText() + " created successfully")
                    .showAndWait();
        }
        else
        {
            AlertFactory.createErrorAlert("Create new branch", "Branch " + branchNameTextField.getText() + " already exists").showAndWait();
        }

        branchNameTextField.clear();
        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    public void addToCheckBox()
    {

        bindBranchesToChoiceBox();
    }

    public void bindBranchesToChoiceBox()
    {
        Map<String, Branch> branches = m_TopController.getBranches();
        addAllBranchesToChoiceBox(branches);
    }

    public void addAllBranchesToChoiceBox(Map<String, Branch> i_Branches)
    {
        Branch activeBranch = m_TopController.getActiveBranch();

        for(Branch branch : i_Branches.values())
        {
            branchChoiceBox.getItems().add(branch.getName());
        }

        branchChoiceBox.setValue(activeBranch.getName());
    }

    @Override
    public void setTopController(TopController i_TopController)
    {
        m_TopController = i_TopController;
    }

}
