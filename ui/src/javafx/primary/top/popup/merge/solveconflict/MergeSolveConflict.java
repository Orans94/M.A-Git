package javafx.primary.top.popup.merge.solveconflict;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class MergeSolveConflict implements PopupController
{
    private TopController m_TopController;
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
}
