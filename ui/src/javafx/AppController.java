package javafx;

import engine.EngineManager;
import engine.OpenChanges;
import javafx.primary.bottom.BottomController;
import javafx.primary.center.CenterController;
import javafx.fxml.FXML;
import javafx.primary.left.LeftController;
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


    public void createNewBranch(String i_BranchName) throws IOException { m_Engine.createNewBranch(i_BranchName); }

    public boolean commit(String i_Message) throws IOException { return m_Engine.commit(i_Message); }

    public void checkout(String i_BranchName) throws IOException { m_Engine.checkout(i_BranchName); }

    public void deleteBranch(String i_BranchName) throws IOException { m_Engine.deleteBranch(i_BranchName); }

    public boolean isRepository(Path i_UserInputPath)
    {
        return m_Engine.isRepository(i_UserInputPath);
    }

    public void stashRepository(Path i_UserInputPath) throws IOException { m_Engine.stashRepository(i_UserInputPath); }

    public void createNewRepository(Path i_UserInputPath, String i_UserInputRepoName) throws IOException { m_Engine.createRepository(i_UserInputPath, i_UserInputRepoName); }

    public boolean isRootFolderEmpty() throws IOException { return m_Engine.isRootFolderEmpty(); }

    public boolean isRepositoryNull() { return m_Engine.isRepositoryNull(); }

    public boolean isBranchExists(String i_BranchName) { return m_Engine.isBranchExists(i_BranchName); }

    public boolean isBranchNameEqualsHead(String i_BranchName) { return m_Engine.isBranchNameEqualsHead(i_BranchName); }

    public OpenChanges getFileSystemStatus() throws IOException { return m_Engine.getFileSystemStatus(); }

    public boolean isFileSystemDirty(OpenChanges i_OpenChanges) { return m_Engine.isFileSystemDirty(i_OpenChanges); }

    public void setActiveBranchName(String i_BranchName) throws IOException
    {
        m_Engine.setActiveBranchName(i_BranchName);
    }
}
