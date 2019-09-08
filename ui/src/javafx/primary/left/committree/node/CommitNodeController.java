package javafx.primary.left.committree.node;

import javafx.fxml.FXML;
import javafx.scene.shape.Circle;

public class CommitNodeController
{
    @FXML private Circle CommitCircle;

    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

}
