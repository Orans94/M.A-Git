package javafx.primary.top.popup.showinformation;

import engine.objects.Commit;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.TextArea;

import java.nio.file.Path;
import java.util.Map;
import java.util.SortedSet;

public class ShowInformationController implements PopupController
{
    @FXML private TextArea informationTextArea;
    @FXML private TopController m_TopController;

    public void setInformationTextArea (Showable i_Showable)
    {
        informationTextArea.setText(i_Showable.getInformation());
    }

    @Override
    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    public boolean isCommitExists(String i_CommitSHA1)
    {
        return m_TopController.isCommitExists(i_CommitSHA1);
    }

    public String getCommitMessage(String i_CommitSHA1)
    {
        return m_TopController.getCommitMessage(i_CommitSHA1);
    }

    public boolean isDirectory(Path i_Path)
    {
        return m_TopController.isDirectory(i_Path);
    }

    public SortedSet<String> getActiveBranchHistory()
    {
        return m_TopController.getActiveBranchHistory();
    }

    public Map<String, Commit> getCommits()
    {
        return m_TopController.getCommits();
    }
}
