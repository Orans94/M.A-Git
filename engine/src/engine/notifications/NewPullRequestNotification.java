package engine.notifications;

import engine.dataobjects.PullRequest;

public class NewPullRequestNotification implements Notification
{
    private PullRequest m_PullRequest;
    private String m_NotificationDetails;

    public NewPullRequestNotification(PullRequest i_PullRequest)
    {
        m_PullRequest = i_PullRequest;
        m_NotificationDetails = this.toString();
    }

    @Override
    public String toString()
    {
        return "A new Pull Request for repository " + m_PullRequest.getRRName() + " has been recieved from user " + m_PullRequest.getLRUsername() + System.lineSeparator()
                +"Message: \"" + m_PullRequest.getMessage() + "\"" + System.lineSeparator()
                + System.lineSeparator() + "Base branch: " + m_PullRequest.getBaseBranchName() + System.lineSeparator()
                + System.lineSeparator() + "Target branch: " + m_PullRequest.getTargetBranchName();
    }

    @Override
    public String getNotificationDetails()
    {
        return this.toString();
    }
}
