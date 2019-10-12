package engine.notifications;

public class ForkNotification implements Notification
{
    private String m_NotificationDetails;

    public ForkNotification(String i_ForkedRepositoryName, String i_ForkingUsername)
    {
        m_NotificationDetails = i_ForkingUsername + " has forked " + i_ForkedRepositoryName + " repository";
    }

    @Override
    public String getNotificationDetails() { return m_NotificationDetails; }
}
