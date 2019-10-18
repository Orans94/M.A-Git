package engine.notifications;

public class PullRequestUpdateNotification implements Notification
{
    private String m_Status;
    private String m_Username;
    private String m_NotificationDetails;

    public PullRequestUpdateNotification(String i_Status, String i_Username)
    {
        m_Status = i_Status;
        m_Username = i_Username;
        m_NotificationDetails = this.toString();
    }

    @Override
    public String toString()
    {
        return "The Pull Request that you sent to user " + m_Username + " has been " + m_Status;
    }

    @Override
    public String getNotificationDetails()
    {
        return m_NotificationDetails;
    }
}
