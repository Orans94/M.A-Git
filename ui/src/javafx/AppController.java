package javafx;

import engine.EngineManager;
import javafx.primary.bottom.BottomController;
import javafx.primary.center.CenterController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.left.LeftController;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.primary.top.TopController;

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


    public void createNewBranch(CheckBox i_CheckoutAfterCreateCheckbox, String i_BranchName)
    {
        m_Engine.createNewBranch(i_BranchName);
        if(i_CheckoutAfterCreateCheckbox.isSelected())
        {
            m_Engine.setActiveBranchName(i_BranchName);
        }
    }

    public void commit(String i_Message) { m_Engine.commit(i_Message); }

    public void checkout(String i_BranchName) { m_Engine.checkout(i_BranchName); }

    public void deleteBranch(String i_BranchName) { m_Engine.deleteBranch(i_BranchName); }
}
