package magithub.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.branches.Branch;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.utils.FileUtilities;
import magithub.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Set;

import static magithub.constants.Constants.MAGITEX3_DIRECTORY_PATH;

public class RepositoryInfoServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException
    {
        String requestType = request.getParameter("requestType");
        switch(requestType)
        {
            case "activeBranch":
                activeBranchRequest(request,response);
                break;
            case "deleteFile":
                deleteFileRequest(request,response);
                break;
        }
    }

    private void deleteFileRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        String fileName = request.getParameter("fileName");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        Path filePath = repositoryPath.resolve(fileName);
        FileUtilities.deleteFile(filePath);
    }

    private void activeBranchRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        PrintWriter out = response.getWriter();
        EngineManager engine= user.getEngine();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        Branch activeBranch = engine.getRepositories().get(repositoryPath).getMagit().getHead().getActiveBranch();
        String json = gson.toJson(activeBranch);

        out.println(json);
        out.flush();
        out.close();
    }
}
