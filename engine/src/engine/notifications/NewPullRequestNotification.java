package engine.notifications;

public class NewPullRequestNotification implements Notification
{
    private String m_RepositoryName;
    private String m_PullRequestMessage;
    private String m_TargetBranchName;
    private String m_BaseBranchName;
    private String m_Username;
    @Override
    public String toString()
    {
        return "A new Pull Request for repository " + m_RepositoryName + " has been recieved from user " + m_Username + System.lineSeparator()
                +"Message: \"" + m_PullRequestMessage + "\""
                + System.lineSeparator() + "Base branch: " + m_BaseBranchName
                + System.lineSeparator() + "Target branch: " + m_TargetBranchName;
    }

    @Override
    public String getNotificationDetails()
    {
        return null;
    }
}
