package javafx.primary.top.popup.merge.solveconflict;

import engine.MergeNodeMaps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.nio.file.Path;
import java.util.List;

public class MergeSolveConflictController implements PopupController
{
    private TopController m_TopController;
    private ObservableList<Path> m_ConflictList;
    private MergeNodeMaps m_MergeNodeMapsResultFromMerge;

    @FXML private ListView<Path> conflictsListView;
    @FXML private TextArea ourTextArea;
    @FXML private TextArea ancestorTextArea;
    @FXML private TextArea theirTextArea;
    @FXML private TextArea resultTextArea;
    @FXML private Button takeResultVersionButton;

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event) {

    }

    public void updateConflictList()
    {
        m_ConflictList = FXCollections.observableList(m_MergeNodeMapsResultFromMerge.getConflicts());
        conflictsListView.setItems(m_ConflictList);
        conflictsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Path>()
        {
            @Override
            public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue)
            {

            }
        });
    }


    public void setMergeNodeMaps(MergeNodeMaps i_MergeNodeMapsResult)
    {
        m_MergeNodeMapsResultFromMerge = i_MergeNodeMapsResult;
    }
}
