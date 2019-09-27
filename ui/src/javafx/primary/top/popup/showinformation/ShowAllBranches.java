package javafx.primary.top.popup.showinformation;

import engine.branches.Branch;
import engine.branches.Head;

import java.util.Map;

public class ShowAllBranches implements Showable
{
    private ShowInformationController m_ShowInformationController;
    private Map<String, Branch> m_Branches;
    private Map<String, String> m_CommitsMessageBy;
    private Head m_Head;


    public ShowAllBranches(Map<String, Branch> i_Branches, Head i_Head, ShowInformationController i_ShowInformationController)
    {
        m_Branches = i_Branches;
        m_Head = i_Head;
        m_ShowInformationController = i_ShowInformationController;
    }

    @Override
    public String getInformation()
    {
        return getBranchesInformation(m_Branches, m_Head);
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
       boolean isGitHaveCommits = m_ShowInformationController.isCommitExists(i_Branch.getCommitSHA1());
       String commitMessage = isGitHaveCommits ?
                m_ShowInformationController.getCommitMessage(i_Branch.getCommitSHA1()) : "";
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
}
