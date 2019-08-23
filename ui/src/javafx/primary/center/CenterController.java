package javafx.primary.center;

import engine.Commit;
import javafx.AppController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.Date;

public class CenterController
{
    private AppController m_MainController;
    private ObservableList<Commit> m_commitsObservableList = FXCollections.observableArrayList();


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
        messageTableColumn.setCellValueFactory(new PropertyValueFactory<>("m_Message"));
        authorTableColumn.setCellValueFactory(new PropertyValueFactory<> ("m_CommitAuthor"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("m_CommitDate"));

        // assign commit sha1 also
        //sha1TableColumn.setCellValueFactory(new PropertyValueFactory<>(""));
    }

    private void tableViewLoad(ObservableList<Commit> i_CommitsData) { commitsTableView.setItems(getInitialTableData()); }

    private ObservableList<Commit> getInitialTableData() {
        return m_commitsObservableList;
    }

    public void addCommitToObservableList(Commit i_Commit)
    {
        m_commitsObservableList.add(i_Commit);
        tableViewLoad(m_commitsObservableList);
    }

    public static <T> void preventColumnReordering(TableView<T> tableView) {
        Platform.runLater(() -> {
            for (Node header : tableView.lookupAll(".column-header")) {
                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }
}
