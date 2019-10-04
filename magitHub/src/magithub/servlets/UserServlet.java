package magithub.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.managers.User;
import engine.managers.UsersManager;
import magithub.WindowsPathConverter;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class UserServlet extends HttpServlet
{
    private void processRequest(HttpServletRequest request, HttpServletResponse response, String i_UserName)
            throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new WindowsPathConverter());

            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
            User user = userManager.getUsers().get(i_UserName);
            String json = gson.toJson(user);
            out.println(json);
            out.flush();

        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if(request.getParameter("isLoggedInUser").equals("TRUE"))
        {
            // get current user
            processRequest(request, response, SessionUtils.getUsername(request));
        }
        else
        {
            processRequest(request, response, request.getParameter("userName"));
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

}
