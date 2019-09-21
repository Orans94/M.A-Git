package javafx.primary.left.committree.node.branch;


import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


public class BranchNodeController
{
    @FXML private Label branchesNamesLabel;

    @FXML private ImageView arrowImageView;
    @FXML private VBox branchVBox;
    private BranchNode m_BranchNode;
    private StringProperty m_BranchesNameStringProperty;

    @FXML public void initialize()
    {
        Tooltip hoverToolTip = new Tooltip();
        hoverToolTip.textProperty().bind(branchesNamesLabel.textProperty());
        Tooltip.install(branchVBox, hoverToolTip);
    }

    public void setBranchesNamesLabelText(String branchesNamesLabelText) { branchesNamesLabel.setText(branchesNamesLabelText); }
    public BranchNode getBranchNode() { return m_BranchNode; }
    public void setBranchNode(BranchNode i_BranchNode) { this.m_BranchNode = i_BranchNode; }

    public Label getBranchesNamesLabel()
    {
        return branchesNamesLabel;
    }

    @FXML
    void arrowImageViewMouseClicked(MouseEvent event)
    {
        animateConnectionsIfSelected();
    }

    @FXML
    void branchesNamesLabelMouseClicked(MouseEvent event)
    {
        animateConnectionsIfSelected();
    }

    private void animateConnectionsIfSelected()
    {
        if (isBranchPulseAnimationCheckMenuItemSelected())
            m_BranchNode.animateBranchConnections();

    }

    private boolean isBranchPulseAnimationCheckMenuItemSelected()
    {
        return m_BranchNode.getCommitTreeManager()
                .getLeftController()
                .getMainController()
                .getTopComponentController()
                .getBranchPulseAnimationCheckMenuItem()
                .isSelected();
    }
}
