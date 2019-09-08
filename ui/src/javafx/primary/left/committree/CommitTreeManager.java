package javafx.primary.left.committree;

import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import com.fxgraph.graph.PannableCanvas;
import engine.Commit;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.primary.left.LeftController;
import javafx.primary.left.committree.node.CommitNode;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.primary.left.committree.layout.CommitTreeLayout;
import sun.awt.image.ImageWatched;

import java.net.URL;
import java.util.*;

public class CommitTreeManager
{
    private Graph m_TreeGraph;
    private LeftController m_LeftController;

    public CommitTreeManager(LeftController i_LeftController)
    {
        m_LeftController = i_LeftController;
        m_TreeGraph = new Graph();
    }

    public void start(Stage primaryStage) throws Exception {

        createCommits(m_TreeGraph);

/*
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("main.fxml");
        fxmlLoader.setLocation(url);
        GridPane root = fxmlLoader.load(url.openStream());

        final Scene scene = new Scene(root, 700, 400);

        ScrollPane scrollPane = (ScrollPane) scene.lookup("#scrollpaneContainer");
        PannableCanvas canvas = tree.getCanvas();
        //canvas.setPrefWidth(100);
        //canvas.setPrefHeight(100);
        scrollPane.setContent(canvas);

        primaryStage.setScene(scene);
        primaryStage.show();
*/

        Platform.runLater(() -> {
            m_TreeGraph.getUseViewportGestures().set(false);
            m_TreeGraph.getUseNodeGestures().set(false);
        });

    }

    private void createCommits(Graph graph)
    {
        final Model model = graph.getModel();
        graph.beginUpdate();

        addCommitsToGraphModel();

        // addBranchesToGraphModel();

        graph.endUpdate();

        graph.layout(new CommitTreeLayout());

    }

    private void addCommitsToGraphModel()
    {

        List<Commit> orderedCommitsByDate = m_LeftController.getOrderedCommitsByDate();
        List<CommitNode> orderedCommitsNodeByDate;
        List<CommitNode> toRemove = new LinkedList<>();
        Set<CommitNode> openCommits = new HashSet<>();
        boolean isFather, isFatherFound;
        int startX = 10;
        int startY = 50;
        int childXPosition;
        final int COMMIT_TABLE_VIEW_ROW_SIZE = 20;
        final int X_DIFF_BETWEEN_COMMITSNODES = 10;

        orderedCommitsNodeByDate = createCommitNodeList(orderedCommitsByDate);
        Model graphModel = m_TreeGraph.getModel();

        for(CommitNode currentCommit : orderedCommitsNodeByDate)
        {
            isFatherFound = false;
            graphModel.addCell(currentCommit);

            // check if commit is father of commit from the openCommits- if it is delete from openCommit
            //add edges if needed
            for(CommitNode openCommitNode : openCommits)
            {
                // check if currentCommit is father of openCommitNode
                isFather = m_LeftController.isCommitFather(currentCommit.getSHA1(), openCommitNode.getSHA1());
                if(isFather)
                {
                    // remove open commit from openCommits
                    toRemove.add(openCommitNode);

                    //relocate
                    if(!isFatherFound)
                    {
                        childXPosition = (int) m_TreeGraph.getGraphic(openCommitNode).getScaleX();
                        m_TreeGraph.getGraphic(currentCommit).relocate(childXPosition, startY);
                        startY += COMMIT_TABLE_VIEW_ROW_SIZE;
                    }

                    //add edge
                    graphModel.addEdge(new Edge(currentCommit, openCommitNode));
                    isFatherFound = true;
                }
            }
            for(CommitNode nodeToRemove : toRemove)
            {
                openCommits.remove(nodeToRemove);
            }
            toRemove.clear();
            if(!isFatherFound)
            {
                // paint in new row new column
                m_TreeGraph.getGraphic(currentCommit).relocate(startX,startY);

                startX += X_DIFF_BETWEEN_COMMITSNODES;
                startY += COMMIT_TABLE_VIEW_ROW_SIZE;
            }
            openCommits.add(currentCommit);
        }
    }
}
