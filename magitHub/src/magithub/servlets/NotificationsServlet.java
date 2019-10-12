package magithub.servlets;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.notifications.ForkNotification;
import engine.notifications.Notification;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@WebServlet(name = "NotificationsServlet")
public class NotificationsServlet extends HttpServlet
{
    private final String REPOSITORY_OWNER_NAME = "repositoryOwnerName";
    private final String FORKED_REPOSITORY_NAME = "forkedRepositoryName";
    private final String FORKING_USERNAME = "forkingUsername";
    private final String NOTIFICATIONS_VERSION = "notificationsVersion";
    private final String UPDATED_NOTIFICATIONS_VERSION = "updatedNotificationsVersion";
    private final String NEW_NOTIFICATIONS = "newNotifications";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String requestType = request.getParameter("notificationType");
        switch (requestType)
        {
            case "NOTIFICATIONS_VERSION":
                notificationVersionRequest(request,response);
                break;
            case "FORK":
                forkNotificationRequest(request,response);
                break;
        }
    }

    private void notificationVersionRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        User user;
        String username, notificationsVersionParameter;
        int updatedUserNotificationsVersion;

        response.setContentType("application/json");
        username = SessionUtils.getUsername(request);
        notificationsVersionParameter = request.getParameter(NOTIFICATIONS_VERSION);
        if (notificationsVersionParameter == null)
            notificationsVersionParameter = "0";

        try (PrintWriter out = response.getWriter())
        {
            UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());
            user = usersManager.getUsers().get(username);
            updatedUserNotificationsVersion = user.getNotificationVersion();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(UPDATED_NOTIFICATIONS_VERSION, updatedUserNotificationsVersion);
            int oldNotificationVersion = Integer.parseInt(notificationsVersionParameter);
            if (oldNotificationVersion != updatedUserNotificationsVersion)
            {
                // adding new notifications to returned object
                jsonObject.add(NEW_NOTIFICATIONS, extractNewNotifications(user.getNotifications(), oldNotificationVersion, updatedUserNotificationsVersion));
            }
            out.println(jsonObject);
            out.flush();
        }
    }

    private JsonArray extractNewNotifications(ArrayList<Notification> allNotificationsList, int oldNotificationsVersion, int newNotificationsVersion)
    {
        List<Notification> newNotifications = new LinkedList<>();
        Gson gson = new Gson();
        JsonArray notificationsJsonArray = new JsonArray();


        for (int i = oldNotificationsVersion; i < newNotificationsVersion; i++)
        {
            newNotifications.add(allNotificationsList.get(i));
            notificationsJsonArray.add(gson.toJsonTree(allNotificationsList.get(i)));
        }

        return notificationsJsonArray;
    }

    private void forkNotificationRequest(HttpServletRequest request, HttpServletResponse response)
    {
        String repositoryOwnerNameParameter, forkedRepositoryNameParameter, forkingUsernameParameter;

        repositoryOwnerNameParameter = request.getParameter(REPOSITORY_OWNER_NAME);
        forkedRepositoryNameParameter = request.getParameter(FORKED_REPOSITORY_NAME);
        forkingUsernameParameter = request.getParameter(FORKING_USERNAME);

        UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());
        User ownerRepositoryUser = usersManager.getUsers().get(repositoryOwnerNameParameter);
        ownerRepositoryUser.addNotification(new ForkNotification(forkedRepositoryNameParameter,forkingUsernameParameter));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req,resp);
    }
}
