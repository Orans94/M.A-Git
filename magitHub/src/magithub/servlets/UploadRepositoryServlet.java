package magithub.servlets;


import com.sun.xml.internal.ws.streaming.XMLReaderException;
import engine.managers.EngineManager;
import engine.managers.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;
import mypackage.MagitRepository;
import mypackage.MagitSingleFolder;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;


@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadRepositoryServlet extends HttpServlet
{
    private final Path MAGITEX3_DIRECTORY_PATH = Paths.get("C:\\magit-ex3");
    private EngineManager m_UserEngine;

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, JAXBException, ParseException
    {
        response.setContentType("text/html;charset=UTF-8");
        String loggedInUsername;
        User loggedInUser;
        Path XMLRepositoryLocation;
        boolean isRepositoryAlreadyExistsInPath;
        StringBuilder validationErrorMessage = new StringBuilder();

        PrintWriter out = response.getWriter();
        loggedInUsername = SessionUtils.getUsername(request);
        loggedInUser = ServletUtils.getUserManager(getServletContext()).getUsers().get(loggedInUsername);
        m_UserEngine = loggedInUser.getEngine();

        Collection<Part> parts = request.getParts();
        StringBuilder XMLFileContent = new StringBuilder();

        for (Part part : parts) {
            //to write the content of the file to a string
            XMLFileContent.append(readFromInputStream(part.getInputStream()));
        }

        MagitRepository XMLRepo = m_UserEngine.createXMLRepository(XMLFileContent.toString());
        XMLRepo.setLocation(MAGITEX3_DIRECTORY_PATH.resolve(loggedInUser.getName()).resolve(XMLRepo.getName()).toString());
        m_UserEngine.loadXMLRepoToMagitMaps(XMLRepo);
        XMLRepositoryLocation = Paths.get(XMLRepo.getLocation());
        if(!m_UserEngine.isPathExists(XMLRepositoryLocation))
        {
            m_UserEngine.createRepositoryPathDirectories(XMLRepositoryLocation);
        }

        isRepositoryAlreadyExistsInPath = m_UserEngine.isRepository(XMLRepositoryLocation);
        String repositoryName = XMLRepo.getName();

        if (m_UserEngine.isXMLRepositoryEmpty(XMLRepo))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                // ----------- the repository in XML is empty && a repository is already exists in location path ---------

                // response error- a repository name already exists
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"a repository in the name is already exists");
            }
            else
            {
                // ----------- the repository in XML is empty && there is no repository on location path ---------
                m_UserEngine.createRepository(XMLRepositoryLocation, repositoryName);
                printRepositoryDetailsToResponse(XMLRepositoryLocation, out, XMLRepo, repositoryName);
                //updateUIComponents();
                //notifyRepositoryHasBeenCreated
            }
        }
        else if (validateXMLRepository(XMLRepo, m_UserEngine.getMagitSingleFolderByID(), validationErrorMessage))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                // response error- a repository name already exists
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"a repository in the name is already exists");
            }
            else
            {
                if (m_UserEngine.isDirectoryEmpty(XMLRepositoryLocation))
                {
                    m_UserEngine.readRepositoryFromXMLFile(XMLRepo, m_UserEngine.getXMLMagitMaps());
                    printRepositoryDetailsToResponse(XMLRepositoryLocation, out, XMLRepo, repositoryName);
                    //updateUIComponents();
                    //notifyRepositoryLoadedSuccessfullyFromXML(i_XMLRepo.getName());
                }
                else
                {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,"the repository folder has content");
                    //notifyTheLoadingWasCanceledFolderHasContent();
                }
            }
        }
        else
        {
            // send validation error message
            response.sendError(HttpServletResponse.SC_FORBIDDEN,validationErrorMessage.toString());
        }
    }

    private void printRepositoryDetailsToResponse(Path i_XMLRepositoryLocation, PrintWriter i_Out, MagitRepository i_XMLRepo, String i_RepositoryName)
    {
        i_Out.println("Repository name: " + i_RepositoryName);
        i_Out.println("Repository location: " + i_XMLRepositoryLocation);
        //i_Out.println("Active branch pointed commit: " + i_XMLRepo.getMagitBranches().getHead().substring(0, 7));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            processRequest(request, response);
        }
        catch (JAXBException e) // TODO
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private boolean validateXMLRepository(MagitRepository i_XmlRepo, Map<String, MagitSingleFolder> i_MagitFolderByID, StringBuilder i_ValidatioErrorMessage)
    {
        boolean isXMLTotallyValid = true;
        String errorMessage = null;

        if (!m_UserEngine.areIDsValid(i_XmlRepo))
        {
            // 3.2
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: There are 2 identical IDs";
        }

        if (!m_UserEngine.areFoldersReferencesValid(i_XmlRepo.getMagitFolders(), i_XmlRepo.getMagitBlobs()))
        {
            // 3.3, 3.4, 3.5
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: Folders references are not valid";
        }

        if (!m_UserEngine.areCommitsReferencesAreValid(i_XmlRepo.getMagitCommits(), i_MagitFolderByID))
        {
            // 3.6, 3.7
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: Commits references are not valid";
        }

        if (!m_UserEngine.areBranchesReferencesAreValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitCommits()))
        {
            // 3.8
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: Branches references are not valid";
        }

        if (!m_UserEngine.isHeadReferenceValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitBranches().getHead()))
        {
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: Head reference is not valid";
        }

        if(!m_UserEngine.isMagitRemoteReferenceValid(i_XmlRepo))
        {
            // 4.1
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: There is no M.A Git repository on the path represents the M.A Git remote repository";
        }

        if(!m_UserEngine.areBranchesTrackingAfterAreValid(i_XmlRepo.getMagitBranches()))
        {
            // 4.2
            isXMLTotallyValid = false;
            errorMessage = "Load repository by XML error: A tracking branch is tracking after a non remote branch";
        }
        if (errorMessage != null) i_ValidatioErrorMessage.append(errorMessage);
        return isXMLTotallyValid;
    }
}
