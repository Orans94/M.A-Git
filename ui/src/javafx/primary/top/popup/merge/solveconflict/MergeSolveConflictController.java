package javafx.primary.top.popup.merge.solveconflict;

import engine.dataobjects.MergeNodeMaps;
import engine.objects.Node;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class MergeSolveConflictController implements PopupController
{
    private TopController m_TopController;
    private ObservableList<Path> m_ConflictListObservableList;
    private MergeNodeMaps m_MergeNodeMapsResultFromMerge;

    @FXML private ListView<Path> conflictsListView;
    @FXML private TextArea ourTextArea;
    @FXML private TextArea ancestorTextArea;
    @FXML private TextArea theirTextArea;
    @FXML private TextArea resultTextArea;
    @FXML private Button takeResultVersionButton;
    @FXML private Button deleteFileButton;

    @Override
    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }

    public void initialize()
    {
        BooleanBinding isResultTextFieldEmpty = Bindings.isEmpty(resultTextArea.textProperty());
        takeResultVersionButton.disableProperty().bind(isResultTextFieldEmpty);

        deleteFileButton.disableProperty().bind(conflictsListView.getSelectionModel().selectedItemProperty().isNull());
        conflictsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        conflictsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Path>()
        {
            @Override
            public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue)
            {
                String ancestorBlobSHA1 = m_MergeNodeMapsResultFromMerge.getAncestorNodeMaps().getSHA1ByPath().get(newValue);
                String oursBlobSHA1 = m_MergeNodeMapsResultFromMerge.getOursNodeMaps().getSHA1ByPath().get(newValue);
                String theirBlobSHA1 = m_MergeNodeMapsResultFromMerge.getTheirNodeMaps().getSHA1ByPath().get(newValue);

                String ancestorBlobContent = getBlobContent(m_MergeNodeMapsResultFromMerge.getAncestorNodeMaps().getNodeBySHA1(), ancestorBlobSHA1);
                String oursBlobContent = getBlobContent(m_MergeNodeMapsResultFromMerge.getOursNodeMaps().getNodeBySHA1(), oursBlobSHA1);
                String theirBlobContent = getBlobContent(m_MergeNodeMapsResultFromMerge.getTheirNodeMaps().getNodeBySHA1(), theirBlobSHA1);;

                resultTextArea.clear();
                ancestorTextArea.setText(ancestorBlobContent);
                ourTextArea.setText(oursBlobContent);
                theirTextArea.setText(theirBlobContent);
            }
        });
    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event) throws IOException
    {
        // remove the file selected in list view from fs if it exists
        Path conflictedFilePath = conflictsListView.getSelectionModel().getSelectedItem();
        if (m_TopController.isPathExists(conflictedFilePath))
        {
            m_TopController.deleteFile(conflictedFilePath);
        }

        // create new file on fs
        String conflictedFileContent = resultTextArea.getText();
        m_TopController.createPathToFile(conflictedFilePath);
        m_TopController.createAndWriteTxtFile(conflictedFilePath, conflictedFileContent);


        // deleting the file from list view after resolving the conflict
        m_MergeNodeMapsResultFromMerge.getConflicts().remove(conflictedFilePath);
        updateConflictList();

        // clean ancestor, ours, their and result text areas
        clearAllTextAreas();

        // close conflict solver if there is no more conflicts
        if (m_ConflictListObservableList.size() == 0)
        {
            m_TopController.removeEmptyDirectories();
            StageUtilities.closeOpenSceneByActionEvent(event);
        }
    }

    private void clearAllTextAreas()
    {
        ancestorTextArea.clear();
        ourTextArea.clear();
        theirTextArea.clear();
        resultTextArea.clear();
    }

    public void updateConflictList()
    {
        m_ConflictListObservableList = FXCollections.observableList(m_MergeNodeMapsResultFromMerge.getConflicts());
        conflictsListView.setItems(m_ConflictListObservableList);
    }


    public void setMergeNodeMaps(MergeNodeMaps i_MergeNodeMapsResult)
    {
        m_MergeNodeMapsResultFromMerge = i_MergeNodeMapsResult;
    }

    private String getBlobContent(Map<String, Node> i_NodeBySHA1Map , String i_BlobSHA1)
    {
        String result;

        if (i_NodeBySHA1Map.containsKey(i_BlobSHA1))
        {
            result = i_NodeBySHA1Map.get(i_BlobSHA1).getContent();
        }
        else
        {
            result = "################### The File Was Deleted ###################";
        }

        return result;
    }

    public void deleteFileButtonAction(ActionEvent actionEvent) throws IOException
    {
        Path conflictedFilePath = conflictsListView.getSelectionModel().getSelectedItem();
        if (m_TopController.isPathExists(conflictedFilePath))
        {
            m_TopController.deleteFile(conflictedFilePath);
        }

        // deleting the file from list view after resolving the conflict
        m_MergeNodeMapsResultFromMerge.getConflicts().remove(conflictedFilePath);
        updateConflictList();

        // clean ancestor, ours, their and result text areas
        clearAllTextAreas();

        // close conflict solver if there is no more conflicts
        if (m_ConflictListObservableList.size() == 0)
        {
            m_TopController.removeEmptyDirectories();
            StageUtilities.closeOpenSceneByActionEvent(actionEvent);
        }
    }
}
