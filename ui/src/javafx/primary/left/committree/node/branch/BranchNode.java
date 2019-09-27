package javafx.primary.left.committree.node.branch;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import javafx.ComponentControllerConnector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.primary.left.committree.CommitTreeManager;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.io.IOException;

import static engine.utils.StringFinals.EMPTY_STRING;
import static javafx.CommonResourcesPaths.BRANCH_NODE_GRAPH_FMXL_RESOURCE;

public class BranchNode extends AbstractCell
{
    private String m_BranchName;

    private BranchNodeController m_BranchNodeController;

    private CommitTreeManager m_CommitTreeManager;

    @FXML private ImageView arrowImageView;
    public BranchNode(String i_BranchName, boolean i_IsHead, boolean i_IsRemote)
    {
        m_BranchName = i_BranchName;
        m_BranchName += i_IsHead ? " (HEAD)" : EMPTY_STRING;
        m_BranchName += i_IsRemote ? " (RB)" : EMPTY_STRING;
    }

    public CommitTreeManager getCommitTreeManager() { return m_CommitTreeManager; }

    public void setBranchName(String m_BranchName) { this.m_BranchName = m_BranchName; }

    public BranchNodeController getBranchNodeController()
    {
        return m_BranchNodeController;
    }

    public String getBranchName() { return m_BranchName; }

    @Override
    public Region getGraphic(Graph graph)
    {
        try
        {
            ComponentControllerConnector connector = new ComponentControllerConnector();
            FXMLLoader fxmlLoader = connector.getFXMLLoader(BRANCH_NODE_GRAPH_FMXL_RESOURCE);

            m_BranchNodeController = fxmlLoader.getController();

            // set ref to CommitNode from CommitNodeController
            m_BranchNodeController.setBranchNode(this);

            return fxmlLoader.getRoot();
        }
        catch (IOException e)
        {
            return new Label("Error when tried to create graphic node !");
        }
    }

    public void setCommitTreeManager(CommitTreeManager i_CommitTreeManager) { m_CommitTreeManager = i_CommitTreeManager; }

    public void animateBranchConnections()
    {
        m_CommitTreeManager.animateBranchConnections(this);
    }
}
