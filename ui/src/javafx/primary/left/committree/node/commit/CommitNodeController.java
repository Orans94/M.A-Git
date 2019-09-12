package javafx.primary.left.committree.node.commit;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class CommitNodeController
{
    @FXML private Circle CommitCircle;
    private CommitNode m_CommitNode;

    @FXML
    void commitNodeMouseClicked(MouseEvent event)
    {
        String commitSHA1 = m_CommitNode.getSHA1();
        m_CommitNode.commitNodeTreeSelected(commitSHA1);
    }
    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

    public CommitNode getCommitNode()
    {
        return m_CommitNode;
    }

    public void setCommitNode(CommitNode i_CommitNode)
    {
        this.m_CommitNode = i_CommitNode;
    }
}
