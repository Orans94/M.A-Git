package javafx.primary.top.popup.checkout;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;

import java.io.IOException;

public class CheckoutController implements PopupController
{
    @FXML private SplitMenuButton branchNamesSplitMenuButton;
    @FXML private Button checkoutButton;
    private TopController m_TopController;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    @FXML void checkoutAction(ActionEvent event) throws IOException
    { m_TopController.checkout(branchNamesSplitMenuButton.getText()); }

}
