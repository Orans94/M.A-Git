package javafx.primary.top.popup.showinformation;

import engine.*;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class ShowInformationController implements PopupController
{
    @FXML private TextArea informationTextArea;
    @FXML private TopController m_TopController;

    public void setInformationTextArea (Showable i_Showable)
    {
        informationTextArea.setText(i_Showable.getInformation());
    }

    @Override
    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

}
