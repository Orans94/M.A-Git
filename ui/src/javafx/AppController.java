package javafx;

import engine.EngineManager;
import javafx.bottom.BottomController;
import javafx.center.CenterController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.left.LeftController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.top.TopController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static javafx.CommonResourcesPaths.APP_FXML_INCLUDE_RESOURCE;
import static javafx.CommonResourcesPaths.CREATE_NEW_REPOSITORY_FXML_RESOURCE;

public class AppController
{
    @FXML private VBox m_TopComponent;
    @FXML private TopController m_TopComponentController;
    @FXML private VBox m_LeftComponent;
    @FXML private LeftController m_LeftComponentController;
    @FXML private BorderPane m_CenterComponent;
    @FXML private CenterController m_CenterComponentController;
    @FXML private BorderPane m_BottomComponent;
    @FXML private BottomController m_BottomComponentController;

    private EngineManager m_Engine = new EngineManager();

    @FXML
    public void initialize()
    {
        if(m_BottomComponentController != null && m_CenterComponentController != null
        && m_LeftComponentController != null && m_TopComponentController != null)
        {
            m_BottomComponentController.setMainController(this);
            m_CenterComponentController.setMainController(this);
            m_LeftComponentController.setMainController(this);
            m_TopComponentController.setMainController(this);
        }
    }

    public void createNewRepository(ActionEvent actionEvent)
    {
    }
}
