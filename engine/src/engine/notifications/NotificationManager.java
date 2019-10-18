package engine.notifications;

import java.util.ArrayList;

public class NotificationManager
{
    private ArrayList<Notification> m_Notifications; //TODO - on logout set last seen index or(?) clear notifications of logged in user(what happens if someone added notification while im logged in?)
    private int m_LastVersionSeen;

    public NotificationManager() { m_Notifications = new ArrayList<>(); m_LastVersionSeen = 0;}

    public void addNotification(Notification i_Notification)
    {
        m_Notifications.add(i_Notification);
    }

    public int getNotificationVersion()
    {
        return m_Notifications.size();
    }

    public void setLastVersionSeen(int i_LastVersionSeen) { m_LastVersionSeen = i_LastVersionSeen; }

    public int getLastVersionSeen() { return m_LastVersionSeen;}

    public ArrayList<Notification> getNotifications()
    {
        return m_Notifications;
    }

    public void removeSeenNotifications()
    {
        m_Notifications = new ArrayList<>(m_Notifications.subList(m_LastVersionSeen, getNotificationVersion()));
        m_LastVersionSeen = 0;
    }
}
