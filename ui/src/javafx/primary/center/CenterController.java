package javafx.primary.center;

import javafx.AppController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class CenterController
{
    private AppController m_MainController;
    @FXML private TableView<?> commitsTableView;

    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    @FXML
    public void initialize()
    {
        preventColumnReordering(commitsTableView);
    }


    public static <T> void preventColumnReordering(TableView<T> tableView) {
        Platform.runLater(() -> {
            for (Node header : tableView.lookupAll(".column-header")) {
                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }
}
