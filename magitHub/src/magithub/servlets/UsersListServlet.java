package magithub.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import engine.managers.User;
import engine.managers.UsersManager;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class UsersListServlet extends HttpServlet
{

    private void usersListRequest(HttpServletRequest request, HttpServletResponse response, boolean onlyActiveUsers)
            throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            Set<String> usersList = extractUserList(request, onlyActiveUsers);
            String json = gson.toJson(usersList);
            out.println(json);
            out.flush();
        }
    }

    private Set<String> extractUserList(HttpServletRequest request, boolean i_OnlyActiveUsers)
    {
        Set<String> usersList;

        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        if (i_OnlyActiveUsers)
        {
            usersList = new HashSet<>();
            for (User user : userManager.getUsers().values())
            {
                if (user.isActiveUser())
                {
                    usersList.add(user.getName());
                }
            }
        }
        else
        {
            usersList = userManager.getUsers().keySet();
        }

        usersList.remove(SessionUtils.getUsername(request));

        return usersList;
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
        // active user <=> user got at least 1 repository
        String requestType;

        requestType = request.getParameter("requestType");
        switch (requestType)
        {
            case "usersList":
                usersListRequest(request, response, Boolean.parseBoolean(request.getParameter("onlyActiveUsers")));
                break;
            case "numberOfUsersList":
                numberOfUsersListRequest(request, response);
                break;
        }
    }

    private void numberOfUsersListRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter())
        {
            JsonObject returnedJsonObj = new JsonObject();
            UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());
            returnedJsonObj.addProperty("usersListVersion", usersManager.getUsers().size());
            out.println(returnedJsonObj);
            out.flush();
        }
    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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
