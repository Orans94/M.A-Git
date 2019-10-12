package engine.managers;


import engine.dataobjects.PullRequest;
import engine.notifications.ForkNotification;
import engine.notifications.Notification;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class User
{
    private String m_Name;
    private EngineManager m_Engine;
    private ArrayList<Notification> m_Notifications; //TODO - on logout set last seen index or(?) clear notifications of logged in user(what happens if someone added notification while im logged in?)
    private int m_NotificationVersion;
    private List<PullRequest> m_PullRequests;

    public User(String i_Username)
    {
        m_Name = i_Username;
        m_Engine = new EngineManager();
        m_Engine.setUserName(m_Name);
        m_NotificationVersion = 0;
        m_Notifications = new ArrayList<>();
        m_PullRequests = new LinkedList<>();
    }

    public int getNotificationVersion() { return m_Notifications.size(); }

    public List<PullRequest> getPullRequests() { return m_PullRequests; }

    public String getName() { return m_Name; }

    public void setName(String i_Name) { this.m_Name = i_Name; }

    public EngineManager getEngine() { return m_Engine; }

    public ArrayList<Notification> getNotifications() { return m_Notifications; }

    // active user is a user with at least 1 repository
    public boolean isActiveUser() { return !m_Engine.getRepositories().isEmpty();}

    public void addNotification(ForkNotification i_ForkNotification)
    {
        m_Notifications.add(i_ForkNotification);
        //m_NotificationVersion++;
    }
}
