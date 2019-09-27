package javafx.primary.left.committree;

import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.Model;
import engine.branches.Branch;
import engine.objects.Commit;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.primary.left.LeftController;
import javafx.primary.left.committree.node.branch.BranchNode;
import javafx.primary.left.committree.node.branchpulseanimation.PulseTransition;
import javafx.primary.left.committree.node.commit.CommitNode;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.List;

import static engine.utils.StringFinals.EMPTY_STRING;

public class CommitTreeManager
{
    private Graph m_TreeGraph;

    public LeftController getLeftController() { return m_LeftController; }

    private LeftController m_LeftController;
    private Map<Commit, CommitNode> m_CommitNodeByCommit;
    private Map<CommitNode, Commit> m_CommitByCommitNode;
    private Map<Branch, BranchNode> m_BranchNodeByBranch;
    private Map<BranchNode, Branch> m_BranchByBranchNode;
    private List<Edge> m_GraphEdges;

    public CommitTreeManager(LeftController i_LeftController)
    {
        // m_TreeGraph not assigning in here
        m_LeftController = i_LeftController;
        m_GraphEdges = new LinkedList<>();
        m_CommitNodeByCommit = new HashMap<>();
        m_CommitByCommitNode = new HashMap<>();
        m_BranchNodeByBranch = new HashMap<>();
        m_BranchByBranchNode = new HashMap<>();
    }

    public void update(ScrollPane i_PaneToDraw)
    {
        clearMaps();
        m_TreeGraph = new Graph();
        i_PaneToDraw.setContent(m_TreeGraph.getCanvas());
        buildCommitTree(m_TreeGraph);

        Platform.runLater(() -> {
            m_TreeGraph.getUseViewportGestures().set(false);
            m_TreeGraph.getUseNodeGestures().set(false);
        });
    }

    private void clearMaps()
    {
        m_CommitByCommitNode.clear();
        m_CommitNodeByCommit.clear();
        m_BranchByBranchNode.clear();
        m_BranchNodeByBranch.clear();
        m_GraphEdges.clear();
    }

    public Graph getTreeGraph() { return m_TreeGraph; }

    private void buildCommitTree(Graph graph)
    {
        final Model model = graph.getModel();
        Platform.runLater(() -> {
            m_TreeGraph.getUseViewportGestures().set(false);
            m_TreeGraph.getUseNodeGestures().set(false);
        });
        graph.beginUpdate();

        addCommitsToGraphModel();

        addBranchesToGraphModel();

        graph.endUpdate();
    }

    private void addBranchesToGraphModel()
    {
        final int DIFF_BETWEEN_BRANCH_COMMIT = 50;
        final int ADUST_BRANCH_COMMIT_HEIGHT = 20;
        List<Branch> pointingBranches;
        BranchNode currentBranchNode = null;
        CommitNode pointedCommitNode;
        Model graphModel;
        double xPosition, yPosition;
        String branchesNames;

        graphModel = m_TreeGraph.getModel();
        for(CommitNode commitNode : m_CommitByCommitNode.keySet())
        {
            // getting pointed branches from commit
            pointingBranches = m_LeftController.getContainedBranches(commitNode.getSHA1());
            branchesNames = EMPTY_STRING;

            // create BranchNode from every Branch, add it to graph and reallocate it
            for (Branch branch : pointingBranches)
            {
                currentBranchNode = new BranchNode(branch.getName(), isBranchRepresentHead(branch), branch.getIsRemote());
                branchesNames = branchesNames.concat(currentBranchNode.getBranchName() + ", ");

                // add branch to graph
                graphModel.addCell(currentBranchNode);
                currentBranchNode.setCommitTreeManager(this);
                xPosition = m_TreeGraph.getGraphic(commitNode).getLayoutX();
                xPosition -= DIFF_BETWEEN_BRANCH_COMMIT;
                yPosition = m_TreeGraph.getGraphic(commitNode).getLayoutY();
                yPosition -= ADUST_BRANCH_COMMIT_HEIGHT;

                // reallocate current branch
                m_TreeGraph.getGraphic(currentBranchNode).relocate(xPosition, yPosition);

                // add branch to maps
                m_BranchByBranchNode.put(currentBranchNode, branch);
                m_BranchNodeByBranch.put(branch, currentBranchNode);
            }
            if(!branchesNames.equals(""))
            {
                branchesNames = branchesNames.substring(0, branchesNames.length() - 2);
                currentBranchNode.getBranchNodeController().getBranchesNamesLabel().setText(branchesNames);
            }
        }
    }

    private boolean isBranchRepresentHead(Branch i_Branch)
    {
        // this method checking if branch represent head by name equality and not by same objects
        return i_Branch.getName().equals(m_LeftController.getActiveBranch().getName());
    }


    private void addCommitsToGraphModel()
    {
        Map<Commit, List<Commit>> edgesFromCommit = new HashMap<>();
        List<Commit> orderedCommitsByDate = m_LeftController.getOrderedCommitsByDate();
        List<CommitNode> orderedCommitsNodeByDate;
        List<CommitNode> toRemove = new LinkedList<>();
        Set<CommitNode> openCommits = new HashSet<>();
        boolean isFather, isFatherFound;
        Edge edge;
        int startX = 70;
        int startY = 45;
        int childXPosition;
        final int COMMIT_TABLE_VIEW_ROW_SIZE = 40;
        final int X_DIFF_BETWEEN_COMMITSNODES = 50;

        orderedCommitsNodeByDate = createCommitNodeList(orderedCommitsByDate);
        Model graphModel = m_TreeGraph.getModel();

        for(CommitNode currentCommit : orderedCommitsNodeByDate)
        {
            isFatherFound = false;
            graphModel.addCell(currentCommit);
            currentCommit.setCommitTreeManager(this);

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
                        childXPosition = (int) m_TreeGraph.getGraphic(openCommitNode).getLayoutX();
                        m_TreeGraph.getGraphic(currentCommit).relocate(childXPosition, startY);
                        startY += COMMIT_TABLE_VIEW_ROW_SIZE;
                    }

                    //add edge
                    edge = new Edge(openCommitNode, currentCommit);
                    graphModel.addEdge(edge);
                    addToEdgesFromCommitMap(edgesFromCommit, openCommitNode, currentCommit);
                    m_GraphEdges.add(edge);
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
        
        addSecondParentEdgesToGraph(edgesFromCommit);

    }



    private void addToEdgesFromCommitMap(Map<Commit, List<Commit>> i_EdgesFromCommit, CommitNode i_ChildCommitNode, CommitNode i_ParentCommitNode)
    {
        if(i_EdgesFromCommit.containsKey(m_CommitByCommitNode.get(i_ChildCommitNode)))
        {
            List<Commit> edgesFromChild = i_EdgesFromCommit.get(m_CommitByCommitNode.get(i_ChildCommitNode));
            if (!edgesFromChild.contains(m_CommitByCommitNode.get(i_ParentCommitNode)))
            {
                edgesFromChild.add(m_CommitByCommitNode.get(i_ParentCommitNode));
                i_EdgesFromCommit.put(m_CommitByCommitNode.get(i_ChildCommitNode), edgesFromChild);
            }
        }
        else
        {
            List<Commit> commits = new LinkedList<>();
            commits.add(m_CommitByCommitNode.get(i_ParentCommitNode));
            i_EdgesFromCommit.put(m_CommitByCommitNode.get(i_ChildCommitNode), commits);
        }
    }

    private List<CommitNode> createCommitNodeList(List<Commit> i_OrderedCommitsByDate)
    {
        List<CommitNode> commitNodes = new LinkedList<>();

        for(Commit currentCommit : i_OrderedCommitsByDate)
        {
            CommitNode commitNode = new CommitNode(currentCommit.getCommitDate(), currentCommit.getSHA1());
            commitNodes.add(commitNode);
            m_CommitNodeByCommit.put(currentCommit, commitNode);
            m_CommitByCommitNode.put(commitNode, currentCommit);
        }

        return commitNodes;
    }

    private void addSecondParentEdgesToGraph(Map<Commit, List<Commit>> i_EdgesFromCommit)
    {
        // this method adds missing edges between commits with two parents to his parent
        List<Commit> commitsWithTwoParents = m_LeftController.getAllCommitsWithTwoParents();
        Edge edge;

        for(Commit currentCommit : commitsWithTwoParents)
        {
            for(String parentSHA1 : currentCommit.getParentsSHA1())
            {
                Commit parentCommit = m_LeftController.getCommit(parentSHA1);
                List<Commit> edgesFromCurrentCommit = i_EdgesFromCommit.get(currentCommit);
                if(!edgesFromCurrentCommit.contains(parentCommit))
                {
                    //adding edge to map and to graph
                    edge = new Edge(m_CommitNodeByCommit.get(currentCommit), m_CommitNodeByCommit.get(parentCommit));
                    m_TreeGraph.getModel().addEdge(edge);
                    addToEdgesFromCommitMap(i_EdgesFromCommit,m_CommitNodeByCommit.get(currentCommit),m_CommitNodeByCommit.get(parentCommit));
                }
            }
        }
    }

    public Map<CommitNode, Commit> getCommitByCommitNode()
    {
        return m_CommitByCommitNode;
    }

    public void commitNodeTreeSelected(String i_CommitSHA1)
    {
        m_LeftController.commitNodeTreeSelected(i_CommitSHA1);
    }

    public void resetBranchAnimate(String i_CommitSHA1)
    {

        Branch activeBranch = m_LeftController.getActiveBranch();

        clearMaps();
        m_TreeGraph = new Graph();
        m_LeftController.getTreeSurfaceScrollPane().setContent(m_TreeGraph.getCanvas());

        final Model model = m_TreeGraph.getModel();
        Platform.runLater(() -> {
            m_TreeGraph.getUseViewportGestures().set(false);
            m_TreeGraph.getUseNodeGestures().set(false);
        });
        m_TreeGraph.beginUpdate();

        addCommitsToGraphModel();

        addBranchesToGraphModel();

        BranchNode animateBranchNode = m_BranchNodeByBranch.get(activeBranch);
        animateBranchNode.getBranchNodeController().setBranchesNamesLabelText(activeBranch.getName());

        CommitNode commitNode = m_CommitNodeByCommit.get(m_LeftController.getCommit(i_CommitSHA1));
        Region branchNodeGraphic = m_TreeGraph.getGraphic(animateBranchNode);
        Region commitNodeGraphic = m_TreeGraph.getGraphic(commitNode);

        Line line = new Line(branchNodeGraphic.getLayoutX(), branchNodeGraphic.getLayoutY(), commitNodeGraphic.getLayoutX(), commitNodeGraphic.getLayoutY());

        PathTransition pt = new PathTransition(Duration.millis(4000), line, branchNodeGraphic);

        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pt.setOnFinished(e->
        {
            try
            {
                m_LeftController.updateUIComponents();
            }
            catch (IOException ex)
            {
                //TODO ??
                ex.printStackTrace();
            }
        });
        pt.play();

        m_TreeGraph.endUpdate();
    }

    public List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        return m_LeftController.getContainedBranches(i_CommitSHA1);
    }

    public String getActiveBranchPointedCommit()
    {
        return m_LeftController.getActiveBranch().getCommitSHA1();
    }

    public void resetHeadBranch(String i_CommitSHA1) throws IOException
    {
        m_LeftController.resetBranch(i_CommitSHA1);
    }

    public void merge(String chosedBranchName) throws IOException
    {
        m_LeftController.merge(chosedBranchName);
    }

    public void deleteBranch(String i_ChosedBranchName) throws IOException
    {
        m_LeftController.deleteBranch(i_ChosedBranchName);
    }

    public void animateBranchConnections(BranchNode i_BranchNode)
    {
        List<Commit> connectedCommits = m_LeftController.getConnectedCommitsByCommitSHA1(m_BranchByBranchNode.get(i_BranchNode).getCommitSHA1());
        PulseTransition pulseTransition;

        for (Commit commit : connectedCommits)
        {
            pulseTransition = new PulseTransition(m_TreeGraph.getGraphic(m_CommitNodeByCommit.get(commit)));
            pulseTransition.setAutoReverse(false);
            pulseTransition.setRate(60);
            pulseTransition.setCycleCount(130);
            pulseTransition.play();
        }
    }

    public void boldCommitHierarchy(String i_CommitSHA1)
    {
        List<Commit> connectedCommits = m_LeftController.getConnectedCommitsByCommitSHA1(i_CommitSHA1);

        for (Commit commit : connectedCommits)
        {
            CommitNode commitNode = m_CommitNodeByCommit.get(commit);
            int circleRadius = commitNode.getCommitNodeController().getCircleRadius();
            m_CommitNodeByCommit.get(commit).getCommitNodeController().setCircleRadius( circleRadius * 1.4);
        }
    }

    public void updateCommitTree()
    {
        m_LeftController.updateCommitTree();
    }
}
