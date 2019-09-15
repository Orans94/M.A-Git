package javafx.primary.top.popup.createnewbranch;

import engine.Commit;
import engine.OpenChanges;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateNewBranchController implements PopupController
{
    @FXML private TopController m_TopController;
    private ObservableList<Commit> m_CommitsObservableList = FXCollections.observableArrayList();
    @FXML private TableView<Commit> commitsTableView;
    @FXML private TableColumn<Commit, String> messageTableColumn;
    @FXML private TableColumn<Commit, String> authorTableColumn;
    @FXML private TableColumn<Commit, String> dateTableColumn;
    @FXML private TableColumn<Commit, String> sha1TableColumn;
    @FXML private TextField branchNameTextField;
    @FXML private CheckBox checkoutAfterCreateCheckbox;
    @FXML private Button createNewBranchButton;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML
    public void initialize()
    {
        preventColumnReordering(commitsTableView);

        messageTableColumn.setCellValueFactory(new PropertyValueFactory<>("Message"));
        authorTableColumn.setCellValueFactory(new PropertyValueFactory<>("CommitAuthor"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("CommitDate"));
        sha1TableColumn.setCellValueFactory(new PropertyValueFactory<>("SHA1"));

        // disabling reset branch button if commit is not chosen(selected).
        BooleanBinding isCommitSelected = Bindings.isEmpty(commitsTableView.getSelectionModel().getSelectedCells());
        BooleanBinding isTextFieldEmpty = Bindings.isEmpty(branchNameTextField.textProperty());
        BooleanBinding isInfoMissing = Bindings.or(isCommitSelected, isTextFieldEmpty);

        createNewBranchButton.disableProperty().bind(isInfoMissing);

    }
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
            String commitSHA1Selected = sha1TableColumn.getCellData(commitsTableView.getSelectionModel().getSelectedIndex());

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
                    m_TopController.createNewBranch(branchName, commitSHA1Selected);
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
                    m_TopController.updateCommitTree();
                }
            }
        }

        StageUtilities.closeOpenSceneByActionEvent(event);
    }


    private void tableViewLoad() { commitsTableView.setItems(m_CommitsObservableList); }

    private void addAllCommitsToTableView(Map<String, Commit> i_Commits)
    {
        List<Commit> sortedCommitsByDate = i_Commits.values().stream().sorted(Comparator.comparing(Commit::getCommitDate)).collect(Collectors.toList());
        Collections.reverse(sortedCommitsByDate);
        m_CommitsObservableList.addAll(sortedCommitsByDate);
        tableViewLoad();
    }

    private <T> void preventColumnReordering(TableView<T> tableView)
    {
        Platform.runLater(() ->
        {
            for (Node header : tableView.lookupAll(".column-header"))
            {
                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }

    public void bindCommitsToTableView()
    {
        Map<String, Commit> commits = m_TopController.getCommits();
        addAllCommitsToTableView(commits);
    }
}
