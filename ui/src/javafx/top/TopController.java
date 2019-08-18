package javafx.top;

import javafx.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class TopController
{
    private AppController m_MainController;

    @FXML private MenuItem createNewRepository;
    @FXML private MenuItem loadRepositoryByPath;
    @FXML private MenuItem loadRepositoryFromXML;
    @FXML private MenuItem cloneRepository;
    @FXML private RadioMenuItem changeThemeToLight;
    @FXML private ToggleGroup themes;
    @FXML private RadioMenuItem changeThemeToDark;
    @FXML private RadioMenuItem changeThemeToStadium;
    @FXML private RadioMenuItem setBackgroundImage;
    @FXML private MenuItem commit;
    @FXML private MenuItem pull;
    @FXML private MenuItem push;
    @FXML private MenuItem createNewBranch;
    @FXML private MenuItem deleteBranch;
    @FXML private MenuItem checkout;
    @FXML private MenuItem merge;
    @FXML private MenuItem resetBranch;
    @FXML private MenuItem showAllBranches;
    @FXML private MenuItem showCurrentBranchHistory;
    @FXML private MenuItem showStatus;
    @FXML private MenuItem showCurrentCommitDetails;
    @FXML private MenuItem contactUs;
    @FXML private MenuItem about;
    @FXML private Button refresh;


    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    public void createNewRepositoryClicked(ActionEvent action) throws IOException
    {
        m_MainController.createNewRepositoryClicked(action);
    }
}