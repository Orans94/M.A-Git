package engine.notifications;

public class BranchDeletedNotification implements Notification
{
    private String m_NotificationDetails;
    private String m_BranchName;
    private String m_Username;

    public BranchDeletedNotification(String i_BranchName, String i_Username)
    {
        m_BranchName = i_BranchName;
        m_Username = i_Username;
        m_NotificationDetails = this.toString();
    }

    @Override
    public String getNotificationDetails()
    {
        return m_NotificationDetails;
    }

    @Override
    public String toString()
    {
        return "User " + m_Username + " has deleted branch " + m_BranchName;
    }
}
