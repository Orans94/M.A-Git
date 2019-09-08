package javafx.primary.left.committree.layout;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.Layout;
import javafx.primary.left.committree.node.CommitNode;

import java.util.List;

public class CommitTreeLayout implements Layout
{
    @Override
    public void execute(Graph graph) {
        final List<ICell> cells = graph.getModel().getAllCells();
        int startX = 10;
        int startY = 50;
        for (ICell cell : cells)
        {
            CommitNode c = (CommitNode) cell;
            if (every3rdNode % 3 == 0)
            {
                graph.getGraphic(c).relocate(startX + 50, startY);
            }
            else
                {
                graph.getGraphic(c).relocate(startX, startY);
            }
            startY += 50;
            every3rdNode++;
        }
    }
}
