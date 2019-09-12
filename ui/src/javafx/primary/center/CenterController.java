package javafx.primary.center;

import engine.Commit;
import javafx.AppController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CenterController
{
    private AppController m_MainController;
    private ObservableList<Commit> m_CommitsObservableList = FXCollections.observableArrayList();


    @FXML private TableView<Commit> commitsTableView;
    @FXML private TableColumn<Commit, String> messageTableColumn;
    @FXML private TableColumn<Commit, String> authorTableColumn;
    @FXML private TableColumn<Commit, Date> dateTableColumn;
    @FXML private TableColumn<Commit, String> sha1TableColumn;

    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    @FXML
    public void initialize()
    {
        preventColumnReordering(commitsTableView);

        messageTableColumn.setCellValueFactory(new PropertyValueFactory<>("Message"));
        authorTableColumn.setCellValueFactory(new PropertyValueFactory<>("CommitAuthor"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("CommitDate"));
        sha1TableColumn.setCellValueFactory(new PropertyValueFactory<>("SHA1"));

        bindSelectedCommitChangedToMainController();
    }

    private void bindSelectedCommitChangedToMainController()
    {
        commitsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Commit>()
        {
            @Override
            public void changed(ObservableValue<? extends Commit> observable, Commit oldValue, Commit newValue)
            {
                if (newValue != null)
                {
                    String commitSHA1 = sha1TableColumn.getCellData(commitsTableView.getSelectionModel().getSelectedIndex());
                    try
                    {
                        m_MainController.newCommitSelectedOnCenterTableView(newValue, commitSHA1);
                    }
                    catch (IOException e)
                    {
                        //TODO
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void tableViewLoad(ObservableList<Commit> i_CommitsData) { commitsTableView.setItems(getCommitsData()); }

    private ObservableList<Commit> getCommitsData() { return m_CommitsObservableList;}

    public void addCommitToObservableList(Commit i_Commit)
    {
        m_CommitsObservableList.add(0,i_Commit);
        tableViewLoad(m_CommitsObservableList);
    }

    public void addAllCommitsToTableView(Map<String, Commit> i_Commits)
    {
        List<Commit> sortedCommitsByDate = i_Commits.values().stream().sorted(Comparator.comparing(Commit::getCommitDate)).collect(Collectors.toList());
        Collections.reverse(sortedCommitsByDate);
        m_CommitsObservableList.addAll(sortedCommitsByDate);
        tableViewLoad(m_CommitsObservableList);
    }

    private static <T> void preventColumnReordering(TableView<T> tableView)
    {
        Platform.runLater(() ->
        {
            for (Node header : tableView.lookupAll(".column-header"))
            {
                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }

    public void clearCommitTableView()
    {
        m_CommitsObservableList.clear();
        tableViewLoad(m_CommitsObservableList);
    }

    public String getSelectedCommitFromTableView()
    {
       return sha1TableColumn.getCellData(commitsTableView.getSelectionModel().getSelectedIndex());
    }

    public void commitNodeTreeSelected(String i_CommitSHA1)
    {
        commitsTableView.getSelectionModel().select(m_MainController.getCommit(i_CommitSHA1));
    }
}
