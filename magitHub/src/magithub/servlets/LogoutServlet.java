package magithub.servlets;

import engine.managers.User;
import engine.managers.UsersManager;
import engine.notifications.NotificationManager;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LogoutServlet extends HttpServlet
{
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession != null) {
            System.out.println("Clearing session for " + usernameFromSession);
            clearSeenUserNotification(request);
            SessionUtils.clearSession(request);
            response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }

    private void clearSeenUserNotification(HttpServletRequest request)
    {
        UsersManager usersManager;
        User user;

        usersManager = ServletUtils.getUsersManager(getServletContext());
        user = usersManager.getUsers().get(SessionUtils.getUsername(request));
        user.getNotificationsManager().removeSeenNotifications();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
