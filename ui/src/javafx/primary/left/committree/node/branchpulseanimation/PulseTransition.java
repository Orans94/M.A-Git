package javafx.primary.left.committree.node.branchpulseanimation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.scene.Node;
import javafx.util.Duration;

public class PulseTransition extends CachedTimelineTransition
{
    /**
     * Create new PulseTransition
     *
     * @param node The node to affect
     */
    public PulseTransition(final Node node)
    {
        super(
                node,
                TimelineBuilder.create()
                        .keyFrames(
                                new KeyFrame(Duration.millis(0),
                                        new KeyValue(node.scaleXProperty(), 1, WEB_EASE),
                                        new KeyValue(node.scaleYProperty(), 1, WEB_EASE)
                                ),
                                new KeyFrame(Duration.millis(500),
                                        new KeyValue(node.scaleXProperty(), 1.1, WEB_EASE),
                                        new KeyValue(node.scaleYProperty(), 1.1, WEB_EASE)
                                ),
                                new KeyFrame(Duration.millis(1000),
                                        new KeyValue(node.scaleXProperty(), 1, WEB_EASE),
                                        new KeyValue(node.scaleYProperty(), 1, WEB_EASE)
                                )
                        )
                        .build()
        );
        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0.2));
    }
}