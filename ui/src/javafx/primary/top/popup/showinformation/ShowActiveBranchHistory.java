package javafx.primary.top.popup.showinformation;

import engine.branches.Branch;
import engine.objects.Commit;
import engine.utils.DateUtils;

import java.util.Map;
import java.util.SortedSet;

public class ShowActiveBranchHistory implements Showable
{
    private Branch m_ActiveBranch;
    private ShowInformationController m_ShowInformationController;

    public ShowActiveBranchHistory(Branch i_ActiveBranch, ShowInformationController i_ShowInformationController)
    {
        m_ActiveBranch = i_ActiveBranch;
        m_ShowInformationController = i_ShowInformationController;
    }

    public String getActiveBranchInformation()
    {
        String result = "";

        String activeBranchName = m_ActiveBranch.getName();
        Map<String, Commit> commitBySHA1;
        SortedSet<String> orderedCommitHistorySHA1;
        orderedCommitHistorySHA1 = m_ShowInformationController.getActiveBranchHistory();
        commitBySHA1 = m_ShowInformationController.getCommits();
        for (String SHA1 : orderedCommitHistorySHA1)
        {
            result = result.concat(getCommit(commitBySHA1.get(SHA1), SHA1) + System.lineSeparator());
        }

        return result;
    }

    private String getCommit(Commit i_Commit, String i_CommitSHA1)
    {
        String result = "";

        result = result.concat("1. SHA1: " + i_CommitSHA1 + System.lineSeparator());
        result = result.concat("2. Commit message: " + i_Commit.getMessage() + System.lineSeparator());
        result = result.concat("3. Commit date: " + DateUtils.FormatToString(i_Commit.getCommitDate()) + System.lineSeparator());
        result = result.concat("4. Commit author: " + i_Commit.getCommitAuthor() + System.lineSeparator());
        result = result.concat(System.lineSeparator());

        return result;
    }

    @Override
    public String getInformation()
    {
        return getActiveBranchInformation();
    }
}
