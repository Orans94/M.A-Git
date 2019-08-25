package javafx.primary.top.popup.showinformation;

import engine.*;
import javafx.AlertFactory;
import javafx.StageUtilities;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class ShowInformationController implements PopupController
{
    public TextArea getInformationTextArea()
    {
        return informationTextArea;
    }

    @FXML private TextArea informationTextArea;
    @FXML private TopController m_TopController;

    @Override
    public void setTopController(TopController i_TopController){ m_TopController = i_TopController;}

    public void showAllBranches(Map<String, Branch> i_Branches, Head i_Head)
    {
        informationTextArea.setText(getBranchesInformation(i_Branches, i_Head));
    }

    private String getBranchesInformation(Map<String, Branch> i_Branches, Head i_Head)
    {
        String result = "";
        for (Branch branch : i_Branches.values())
        {
            result = result.concat(getSingleBranchInformation(branch, i_Head) + System.lineSeparator());
        }

        return result;
    }

    private String getSingleBranchInformation(Branch i_Branch, Head i_Head)
    {
        String result = "";
        boolean isGitHaveCommits = m_TopController.isCommitExists(i_Branch.getCommitSHA1());
        String commitMessage = isGitHaveCommits ?
                m_TopController.getCommitMessage(i_Branch.getCommitSHA1()) : "";
        if (i_Branch == i_Head.getActiveBranch())
        {
            result = result.concat("1. Branch name: " + i_Branch.getName() + " (HEAD)" + System.lineSeparator());
        }
        else
        {
            result = result.concat("1. Branch name: " + i_Branch.getName() + System.lineSeparator());
        }
        if (isGitHaveCommits)
        {
            result = result.concat("2. SHA1 of the pointed commit: " + i_Branch.getCommitSHA1() + System.lineSeparator());
            result = result.concat("3. Message of the pointed commit: " + commitMessage + System.lineSeparator());
        }

        return result;
    }

    public void showDetailsOfCurrentCommit(NodeMaps i_NodeMaps)
    {
        if (i_NodeMaps.isEmpty())
        {
            //show error
            AlertFactory.createErrorAlert("Show details of current commit", "Commit has not been done yet")
                    .showAndWait();
            //TODO close scene
            // result = null
        }
        else
        {
            informationTextArea.setText(getCurrentCommitInformation(i_NodeMaps));
        }
    }

    private String getCurrentCommitInformation(NodeMaps i_NodeMaps)
    {
        String result = "";

        for (Map.Entry<Path, String> entry : i_NodeMaps.getSHA1ByPath().entrySet())
        {
            if (m_TopController.isDirectory(entry.getKey()))
            {
                Folder folder = (Folder) i_NodeMaps.getNodeBySHA1().get(entry.getValue());
                List<Item> items = folder.getItems();
                Path folderPath = entry.getKey();
                for (Item item : items)
                {
                    result = result.concat(getCurrentItemDetails(folderPath, item) + System.lineSeparator());
                }
            }
        }

        return result;
    }

    private String getCurrentItemDetails(Path i_FolderPath, Item i_Item)
    {
        String result = "";
        result = result.concat("Full name: " + i_FolderPath.resolve(i_Item.getName()) + System.lineSeparator());
        result = result.concat("Type: " + i_Item.getType() + System.lineSeparator());
        result = result.concat("SHA-1: " + i_Item.getSHA1() + System.lineSeparator());
        result = result.concat("Last modifier name: " + i_Item.getAuthor() + System.lineSeparator());
        result = result.concat("Date modified: " + DateUtils.FormatToString(i_Item.getModificationDate()) + System.lineSeparator());

        return result;
    }

    public void showStatus() throws IOException
    {
        String statusInformation;

        //TODO
        //if(!m_Engine.isRootFolderEmpty())
        //  {
        informationTextArea.setText(getOpenChanges(m_TopController.getFileSystemStatus()));
        // }
        //  else
        //  {
        // System.out.println("Root folder is empty");
        // }
    }

    private String getOpenChanges(OpenChanges openChanges)
    {
        String result = "";

        if (openChanges.isFileSystemClean())
        {
            AlertFactory.createInformationAlert("Show status", "WC is clean")
                    .showAndWait();
            //TODO close scene
            // result = null
        }
        else
        {
            result = result.concat(getList("Deleted", openChanges.getDeletedNodes()) + System.lineSeparator());
            result = result.concat(getList("Modified", openChanges.getModifiedNodes()) + System.lineSeparator());
            result = result.concat(getList("New", openChanges.getNewNodes()) + System.lineSeparator());
        }

        return result;
    }

    private String getList(String i_Status, List<Path> i_OpenChangesList)
    {
        //get an openchanges list with its status
        String result = "";

        for (Path path : i_OpenChangesList)
        {
            result = result.concat(i_Status + ": " + path + System.lineSeparator());
        }

        return result;
    }

    public String showActiveBranchHistory(Branch i_Branch)
    {
        String result = "";

        String activeBranchName = i_Branch.getName();
        if (!m_TopController.isBranchPointedCommitSHA1Empty(activeBranchName))
        {
            Map<String, Commit> commitBySHA1;
            SortedSet<String> orderedCommitHistorySHA1;
            orderedCommitHistorySHA1 = m_TopController.getActiveBranchHistory();
            commitBySHA1 = m_TopController.getCommits();
            for (String SHA1 : orderedCommitHistorySHA1)
            {
                result = result.concat(getCommit(commitBySHA1.get(SHA1), SHA1) + System.lineSeparator());
            }
            informationTextArea.setText(result);
        }
        else
        {
            AlertFactory.createErrorAlert("Show active branch history", "Active branch " + activeBranchName + " is not pointing on a commit")
                    .showAndWait();
            //TODO close scene maybe return null
            // result = null
        }

        return result;
    }

    private String getCommit(Commit i_Commit, String i_CommitSHA1)
    {
        String result = "";

        result = result.concat("1. SHA1: " + i_CommitSHA1);
        result = result.concat("2. Commit message: " + i_Commit.getMessage());
        result = result.concat("3. Commit date: " + DateUtils.FormatToString(i_Commit.getCommitDate()));
        result = result.concat("4. Commit author: " + i_Commit.getCommitAuthor());
        result = result.concat(System.lineSeparator());

         return result;
    }

}
