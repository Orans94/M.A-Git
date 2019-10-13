package engine.managers;


import engine.dataobjects.PullRequest;
import engine.notifications.ForkNotification;
import engine.notifications.Notification;
import engine.notifications.NotificationManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class User
{
    private String m_Name;
    private EngineManager m_Engine;

    private NotificationManager m_NotificationManager;
    private List<PullRequest> m_PullRequests;

    public User(String i_Username)
    {
        m_Name = i_Username;
        m_Engine = new EngineManager();
        m_Engine.setUserName(m_Name);
        m_PullRequests = new LinkedList<>();
        m_NotificationManager = new NotificationManager();
    }

    public List<PullRequest> getPullRequests() { return m_PullRequests; }

    public String getName() { return m_Name; }

    public void setName(String i_Name) { this.m_Name = i_Name; }

    public EngineManager getEngine() { return m_Engine; }

    public NotificationManager getNotificationsManager() { return m_NotificationManager; }

    // active user is a user with at least 1 repository
    public boolean isActiveUser() { return !m_Engine.getRepositories().isEmpty();}


}
