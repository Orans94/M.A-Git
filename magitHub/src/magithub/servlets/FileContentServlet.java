package magithub.servlets;

import engine.dataobjects.NodeMaps;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.objects.Node;
import engine.utils.FileUtilities;
import magithub.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(name = "FileContentServlet")
public class FileContentServlet extends HttpServlet
{
    private void commitRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String file = request.getParameter("filePath");
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        String commitSHA1 = request.getParameter("commitSHA1");
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine= user.getEngine();
        NodeMaps nodeMaps = engine.getLazyLoadedNodeMapsByCommitSHA1(commitSHA1);

        Path filePath = Paths.get("C:\\magit-ex3").resolve(username).resolve(repositoryName).resolve(file);
        String fileSHA1 = nodeMaps.getSHA1ByPath().get(filePath);
        Node node = nodeMaps.getNodeBySHA1().get(fileSHA1);
        PrintWriter out = response.getWriter();
        out.print(node.getContent());
    }

    private void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String filePath = request.getParameter("filePath");
        String fileContent = request.getParameter("fileContent");
        String username = Paths.get(filePath).subpath(1,2).toString();
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine= user.getEngine();
        changeFileContent(Paths.get(filePath), fileContent, engine);
    }

    private void changeFileContent(Path i_FilePath, String i_FileContent, EngineManager i_Engine) throws IOException
    {
        if(FileUtilities.isExists(i_FilePath))
        {
            i_Engine.modifyTxtFile(i_FilePath, i_FileContent);
        }
        else
        {
            Files.createDirectories(i_FilePath.getParent());
            i_Engine.createAndWriteTxtFile(i_FilePath, i_FileContent);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        processPost(request,response);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException
    {
        switch (request.getParameter("requestType"))
        {
            case "Commit":
                commitRequest(request,response);
                break;
            case "WC":
                WCRequest(request,response);
                break;
        }
    }

    private void WCRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String fileContent;
        String file = request.getParameter("filePath");
        String username = request.getParameter("username");
        String repositoryName = request.getParameter("repositoryName");
        Path filePath = Paths.get("C:\\magit-ex3").resolve(username).resolve(repositoryName).resolve(file);
        PrintWriter out = response.getWriter();
        if(filePath.endsWith(".zip"))
        {
            // if zip file - get txt from zip
            fileContent = FileUtilities.getTxtFromZip(filePath.toString(), Paths.get(file).getFileName().toString());
        }
        else
        {
            fileContent = new String(Files.readAllBytes(filePath));
        }

        out.print(fileContent);
    }
}
