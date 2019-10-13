package engine.notifications;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;

public class NotificationManager
{
    private ArrayList<Notification> m_Notifications; //TODO - on logout set last seen index or(?) clear notifications of logged in user(what happens if someone added notification while im logged in?)
    private int m_LastVersionSeen;

    public NotificationManager() { m_Notifications = new ArrayList<>(); m_LastVersionSeen = 0;}

    public void addNotification(ForkNotification i_ForkNotification)
    {
        m_Notifications.add(i_ForkNotification);
        //m_NotificationVersion++;
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
}
