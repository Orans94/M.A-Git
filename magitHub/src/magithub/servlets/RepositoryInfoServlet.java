package magithub.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.branches.Branch;
import engine.core.Repository;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.utils.FileUtilities;
import magithub.utils.ServletUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

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
            case "createBranch":
                createBranchRequest(request,response);
                break;
            case "commit":
                commitRequest(request,response);
                break;
            case "WCStatus":
                WCStatusRequest(request,response);
                break;
        }
    }

    private void WCStatusRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine = user.getEngine();
        Repository rep = engine.getRepositories().get(repositoryPath);
        PrintWriter out = response.getWriter();
        if(rep.getFileSystemStatus().isFileSystemClean())
        {
            out.print("WC Status: Clean");
        }
        else
        {
            out.print("WC Status: Dirty");
        }
    }

    private void commitRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        String commitMessage = request.getParameter("commitMessage");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine = user.getEngine();
        Repository rep = engine.getRepositories().get(repositoryPath);
        rep.removeEmptyDirectories();
        rep.commit(commitMessage, true, null);
    }

    private void createBranchRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        String branchName = request.getParameter("branchName");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine = user.getEngine();
        Repository rep = engine.getRepositories().get(repositoryPath);
        String activeBranchCommitSHA1 = engine.getHead().getActiveBranch().getCommitSHA1();
        rep.createNewBranch(branchName, activeBranchCommitSHA1);
    }

    private void deleteFileRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        String fileName = request.getParameter("fileName");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        Path filePath = repositoryPath.resolve(fileName);
        if(FileUtilities.isExists(filePath))
        {
            FileUtilities.deleteFile(filePath);
        }
        else
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "File not found");
        }
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
