package engine.managers;

import javax.servlet.http.HttpSession;
import java.util.*;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UsersManager
{
    private final Map<String, User> m_UsersMap;

    public UsersManager() { m_UsersMap = new HashMap<>(); }

    public synchronized void addUser(String i_Username) { m_UsersMap.put(i_Username, new User(i_Username)); }

    public synchronized void removeUser(String i_Username) { m_UsersMap.remove(i_Username); }

    public synchronized Map<String, User> getUsers() { return Collections.unmodifiableMap(m_UsersMap); }

    public boolean isUserExists(String i_Username) { return m_UsersMap.containsKey(i_Username); }

}