package javafx.primary.top.popup.commitselector;

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
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommitSelectorController implements PopupController
{
    private ObservableList<Commit> m_CommitsObservableList = FXCollections.observableArrayList();
    @FXML protected TopController m_TopController;
    @FXML protected TableView<Commit> commitsTableView;
    @FXML protected TableColumn<Commit, String> messageTableColumn;
    @FXML protected TableColumn<Commit, String> authorTableColumn;
    @FXML protected TableColumn<Commit, String> dateTableColumn;
    @FXML protected TableColumn<Commit, String> sha1TableColumn;

    @Override
    public void setTopController(TopController i_TopController)
    {
        m_TopController = i_TopController;
    }

    public Button getSelectButton() { return selectButton; }

    public void setSelectButton(Button selectButton) { this.selectButton = selectButton; }

    @FXML private Button selectButton;

    @FXML
    void selectButtonAction(ActionEvent event) throws IOException
    {

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
        selectButton.disableProperty().bind(isCommitSelected);
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
