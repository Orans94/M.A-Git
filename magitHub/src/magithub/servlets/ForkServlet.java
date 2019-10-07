package magithub.servlets;

import com.google.gson.JsonObject;
import engine.managers.EngineManager;
import engine.managers.UsersManager;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import static magithub.constants.Constants.MAGITEX3_DIRECTORY_PATH;


public class ForkServlet extends HttpServlet
{
    private final String REPOSITORY_OWNER_NAME = "repositoryOwnerName";
    private final String REPOSITORY_NAME = "repositoryNameInOwner";
    private final String REPOSITORY_NAME_TO_FORK = "repositoryNameToFork";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String userNameFromSession, repositoryOwnerName, repositoryName, repositoryNameToFork;
        UsersManager usersManager;
        EngineManager userEngine;
        boolean isCloneSuccess = false;

        response.setContentType("application/json");
        userNameFromSession = SessionUtils.getUsername(request);
        usersManager = ServletUtils.getUsersManager(getServletContext());
        repositoryOwnerName = request.getParameter(REPOSITORY_OWNER_NAME);
        repositoryName = request.getParameter(REPOSITORY_NAME);
        repositoryNameToFork = request.getParameter((REPOSITORY_NAME_TO_FORK));
        userEngine = usersManager.getUsers().get(userNameFromSession).getEngine();
        try
        {
            userEngine.cloneRepository(
                    MAGITEX3_DIRECTORY_PATH.resolve(repositoryOwnerName).resolve(repositoryName)
                    , MAGITEX3_DIRECTORY_PATH.resolve(userNameFromSession).resolve(repositoryNameToFork)
                    , repositoryNameToFork
            );
            isCloneSuccess = true;
        }
        catch (ParseException e)
        {
            isCloneSuccess = false;
        }
        finally
        {
            setResponse(response, isCloneSuccess, userNameFromSession, repositoryOwnerName, repositoryName, repositoryNameToFork);
        }

    }

    private void setResponse(HttpServletResponse response, boolean isCloneSuccess, String userNameFromSession, String repositoryOwnerName, String repositoryName, String repositoryNameToFork) throws IOException
    {
        response.setStatus(isCloneSuccess ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
        try (PrintWriter out = response.getWriter())
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("loggedInUsername", userNameFromSession);
            jsonObject.addProperty("repositoryName", repositoryName);
            jsonObject.addProperty("repositoryOwnerName", repositoryOwnerName);
            jsonObject.addProperty("repositoryNameToFork", repositoryNameToFork);
            out.println(jsonObject);
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        processRequest(request,response);
    }
}
