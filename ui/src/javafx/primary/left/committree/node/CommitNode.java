package javafx.primary.left.committree.node;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static javafx.CommonResourcesPaths.COMMIT_NODE_GRAPH_FXML_RESOURCE;

public class CommitNode extends AbstractCell
{
    private String m_SHA1;
    private Date m_DateOfCreation;
    private CommitNodeController m_CommitNodeController;

    public CommitNode(Date i_DateOfCreation, String i_SHA1)
    {
        this.m_DateOfCreation = i_DateOfCreation;
        this.m_SHA1 = i_SHA1;
    }

    @Override
    public Region getGraphic(Graph graph)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource(COMMIT_NODE_GRAPH_FXML_RESOURCE);
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());

            m_CommitNodeController = fxmlLoader.getController();

            return root;
        }
        catch (IOException e)
        {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge)
    {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(m_CommitNodeController.getCircleRadius());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return m_DateOfCreation != null ? m_DateOfCreation.equals(that.m_DateOfCreation) : that.m_DateOfCreation == null;
    }

    @Override
    public int hashCode()
    {
        return m_DateOfCreation != null ? m_DateOfCreation.hashCode() : 0;
    }

    public String getSHA1() { return m_SHA1; }
    public Date getDateOfCreation() { return m_DateOfCreation; }
}
