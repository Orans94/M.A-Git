package javafx.primary.top.popup.resetbranch;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ResetBranchController implements PopupController
{
    private ObservableList<Commit> m_CommitsObservableList = FXCollections.observableArrayList();
    @FXML private TopController m_TopController;
    @FXML private TableView<Commit> commitsTableView;
    @FXML private TableColumn<Commit, String> messageTableColumn;
    @FXML private TableColumn<Commit, String> authorTableColumn;
    @FXML private TableColumn<Commit, String> dateTableColumn;
    @FXML private TableColumn<Commit, String> sha1TableColumn;
    @FXML private Button resetBranchButton;

    public void setTopController(TopController i_TopController)
    {
        m_TopController = i_TopController;
    }

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
        resetBranchButton.disableProperty().bind(isCommitSelected);
    }

    @FXML
    void resetBranchButtonAction(ActionEvent event) throws IOException
    {
        boolean toContinue;
        OpenChanges openChanges = m_TopController.getFileSystemStatus();
        String commitSHA1 = sha1TableColumn.getCellData(commitsTableView.getSelectionModel().getSelectedIndex());
        if (m_TopController.isFileSystemDirty(openChanges))
        {
            String message = "Please notice, the WC is dirty. if you will continue all changes will be lost."+System.lineSeparator()+"Would you like to continue?";
            Alert alert = AlertFactory.createYesNoAlert("Reset branch", message);
            toContinue = alert.showAndWait().get().getText().equals("Yes");
            if (toContinue)
            {
                resetBranch(commitSHA1, event);
            }
        }
        else
        {
            resetBranch(commitSHA1, event);
        }

        StageUtilities.closeOpenSceneByActionEvent(event);
    }

    private void resetBranchAnimateIfSelected(String I_CommitSHA1)
    {
        if (m_TopController.getResertBranchAnimationCheckMenuItem().isSelected())
            m_TopController.resetBranchAnimate(I_CommitSHA1);
    }


    private void resetBranch(String i_CommitSHA1, ActionEvent event) throws IOException
    {
        resetBranchAnimateIfSelected(i_CommitSHA1);
        m_TopController.changeActiveBranchPointedCommit(i_CommitSHA1);
        AlertFactory.createInformationAlert("Reset branch", "The active branch is now pointing on commit " + i_CommitSHA1)
                .showAndWait();
        m_TopController.checkout(m_TopController.getActiveBranchName());
        m_TopController.showDetailsOfCurrentCommitScene(event);
        m_TopController.updateUIComponents();
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