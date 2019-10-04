package magithub.servlets;

import engine.dataobjects.NodeMaps;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

@WebServlet(name = "FileContentServlet")
public class FileContentServlet extends HttpServlet
{
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String fileContent;
        String file = request.getParameter("filePath");
        String userName = request.getParameter("userName");
        String repositoryName = request.getParameter("repositoryName");
        Path filePath = Paths.get("C:").resolve(userName).resolve(repositoryName).resolve(file);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException
    {
        processRequest(request,response);
    }
}
