package engine.managers;


import engine.notifications.Notification;

import java.util.LinkedList;
import java.util.List;

public class User
{
    private String m_Name;
    private EngineManager m_Engine;
    private List<Notification> m_UnseenNotifications; //TODO - on logout clear notifications of logged in user(what happens if someone added notification while im logged in?)

    public User(String i_Username)
    {
        m_Name = i_Username;
        m_Engine = new EngineManager();
        m_UnseenNotifications = new LinkedList<>();
    }

    public String getName() { return m_Name; }

    public void setName(String i_Name) { this.m_Name = i_Name; }

    public List<Notification> getUnseenNotifications() { return m_UnseenNotifications; }

    public void setUnseenNotifications(List<Notification> i_UnseenNotifications) { this.m_UnseenNotifications = i_UnseenNotifications; }

}
