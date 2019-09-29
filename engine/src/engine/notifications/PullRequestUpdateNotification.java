package engine.notifications;

public class PullRequestUpdateNotification implements Notification
{
    private String m_Status;
    private String m_RepositoryName;
    private String m_TargetBranchName;
    private String m_BaseBranchName;
    private String m_Username;
    private String m_PullRequestMessage;

    @Override
    public String toString()
    {
        return "The Pull Request for user " + m_Username + " regarding repository" + m_RepositoryName + " has been " + m_Status +System.lineSeparator()
                +"Message: \"" + m_PullRequestMessage + "\""
                + System.lineSeparator() + "Base branch: " + m_BaseBranchName
                + System.lineSeparator() + "Target branch: " + m_TargetBranchName;
    }
}
