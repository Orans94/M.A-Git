package magithub.servlets;

import engine.core.Repository;
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

import static magithub.constants.Constants.MAGITEX3_DIRECTORY_PATH;

@WebServlet(name = "FileContentServlet")
public class FileContentServlet extends HttpServlet
{
    private void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException
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

    private void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String filePath = request.getParameter("filePath");
        String fileContent = request.getParameter("fileContent");
        String commitSHA1 = request.getParameter("commitSHA1");
        String username = Paths.get(filePath).subpath(1,2).toString();
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User user = userManager.getUsers().get(username);
        EngineManager engine= user.getEngine();
        changeFileContent(commitSHA1, Paths.get(filePath), fileContent, engine);
    }

    private void changeFileContent(String i_CommitSHA1, Path i_FilePath, String i_FileContent, EngineManager i_Engine) throws IOException
    {
        if(FileUtilities.isExists(i_FilePath))
        {
            i_Engine.modifyTxtFile(i_FilePath, i_FileContent);
            String fileSHA1 = i_Engine.getLazyLoadedNodeMapsByCommitSHA1(i_CommitSHA1).getSHA1ByPath().get(i_FilePath);
            i_Engine.getLazyLoadedNodeMapsByCommitSHA1(i_CommitSHA1).getNodeBySHA1().get(fileSHA1).setContent(i_FileContent);
        }
        else
        {
            i_Engine.createAndWriteTxtFile(i_FilePath, i_FileContent);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        processPost(request,response);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException
    {
        processGet(request,response);
    }
}
