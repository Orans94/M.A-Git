package engine.managers;

import com.sun.xml.internal.ws.api.pipe.Engine;

public class User
{
    private String m_Name;
    private EngineManager m_Engine;

    public User(String i_Username)
    {
        m_Name = i_Username;
        m_Engine = new EngineManager();
    }
}
