package magithub.servlets;

import engine.dataobjects.NodeMaps;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.objects.Folder;
import engine.objects.Item;
import magithub.utils.ServletUtils;
import mypackage.PrecedingCommits;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static magithub.constants.Constants.MAGITEX3_DIRECTORY_PATH;

@WebServlet(name = "CommitFilesServlet")
public class CommitFilesServlet extends HttpServlet
{
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ParseException
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try // TODO
        {
            processRequest(request,response);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
}
