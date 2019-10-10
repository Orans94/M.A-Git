package magithub.servlets;

import engine.core.Repository;
import engine.dataobjects.NodeMaps;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import magithub.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Set;

import static magithub.constants.Constants.MAGITEX3_DIRECTORY_PATH;

@WebServlet(name = "CommitFilesServlet")
public class CommitFilesServlet extends HttpServlet
{
    private void commitRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        String commitSHA1 = request.getParameter("commitSHA1");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine= user.getEngine();
        PrintWriter out = response.getWriter();
        NodeMaps nodeMaps = engine.getNodeMapsByCommitSHA1(repositoryPath, commitSHA1);
        for (Path path : nodeMaps.getSHA1ByPath().keySet())
        {
            if (!engine.isDirectory(path))
            {
                out.println(path.subpath(3,path.getNameCount()));
            }
        }

        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        switch(request.getParameter("requestType"))
        {
            case "workingCopy":
                workingCopyRequest(request,response);
                break;
            case "Commit":
                commitRequest(request,response);
                break;
        }
    }

    private void workingCopyRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        Path repositoryPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(repositoryName);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine= user.getEngine();
        PrintWriter out = response.getWriter();
        Repository repo = engine.getRepositories().get(repositoryPath);
        Set<Path> wcPaths = repo.getWCFilePaths();

        for (Path path : wcPaths)
        {
            if (!engine.isDirectory(path))
            {
                out.println(path.subpath(3,path.getNameCount()));
            }
        }

        out.close();
    }
}
