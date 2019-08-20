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

import java.io.IOException;
import java.nio.file.Path;

public class AppController
{
    // ------ CONTROLLERS AND COMPONENTS ------

    @FXML private VBox m_TopComponent;
    @FXML private TopController m_TopComponentController;
    @FXML private VBox m_LeftComponent;
    @FXML private LeftController m_LeftComponentController;
    @FXML private BorderPane m_CenterComponent;
    @FXML private CenterController m_CenterComponentController;
    @FXML private BorderPane m_BottomComponent;
    @FXML private BottomController m_BottomComponentController;

    // ------ CONTROLLERS AND COMPONENTS ------

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


    public void createNewBranch(CheckBox i_CheckoutAfterCreateCheckbox, String i_BranchName) throws IOException
    {
        m_Engine.createNewBranch(i_BranchName);
        if(i_CheckoutAfterCreateCheckbox.isSelected())
        {
            m_Engine.setActiveBranchName(i_BranchName);
        }
    }

    public void commit(String i_Message) throws IOException { m_Engine.commit(i_Message); }

    public void checkout(String i_BranchName) throws IOException { m_Engine.checkout(i_BranchName); }

    public void deleteBranch(String i_BranchName) throws IOException { m_Engine.deleteBranch(i_BranchName); }

    public boolean isRepository(Path i_UserInputPath)
    {
        return m_Engine.isRepository(i_UserInputPath);
    }

    public void stashRepository(Path i_UserInputPath) throws IOException
    {
        m_Engine.stashRepository(i_UserInputPath);
    }

    public void createNewRepository(Path i_UserInputPath, String i_UserInputRepoName) throws IOException
    {
        m_Engine.createRepository(i_UserInputPath, i_UserInputRepoName);
    }
}
