package engine.notifications;

public class ForkNotification implements Notification
{
    private String m_ForkedRepositoryName;
    private String m_ForkingUsername;

    @Override
    public String toString()
    {
        return m_ForkingUsername + " has forked repository " + m_ForkedRepositoryName;
    }
}
