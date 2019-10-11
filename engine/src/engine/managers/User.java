package engine.managers;


import engine.dataobjects.PullRequest;
import engine.notifications.Notification;

import java.util.LinkedList;
import java.util.List;

public class User
{
    private String m_Name;
    private EngineManager m_Engine;
    private List<Notification> m_UnseenNotifications; //TODO - on logout set last seen index or(?) clear notifications of logged in user(what happens if someone added notification while im logged in?)
    private List<PullRequest> m_PullRequests;

    public User(String i_Username)
    {
        m_PullRequests = new LinkedList<>();
        m_Name = i_Username;
        m_Engine = new EngineManager();
        m_Engine.setUserName(m_Name);
        m_UnseenNotifications = new LinkedList<>();
    }

    public List<PullRequest> getPullRequests() { return m_PullRequests; }

    public String getName() { return m_Name; }

    public void setName(String i_Name) { this.m_Name = i_Name; }

    public EngineManager getEngine() { return m_Engine; }

    public List<Notification> getUnseenNotifications() { return m_UnseenNotifications; }

    public void setUnseenNotifications(List<Notification> i_UnseenNotifications) { this.m_UnseenNotifications = i_UnseenNotifications; }

    // active user is a user with at least 1 repository
    public boolean isActiveUser() { return !m_Engine.getRepositories().isEmpty();}
}
