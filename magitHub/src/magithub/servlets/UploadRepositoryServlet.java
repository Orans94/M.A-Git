package magithub.servlets;


import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;
import mypackage.MagitRepository;
import mypackage.MagitSingleFolder;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static engine.utils.StringFinals.EMPTY_STRING;
import static magithub.constants.Constants.MAGITEX3_DIRECTORY_PATH;


@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadRepositoryServlet extends HttpServlet
{
    private EngineManager m_UserEngine;

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, JAXBException, ParseException
    {
        response.setContentType("text/html;charset=UTF-8");

        String loggedInUsername;
        User loggedInUser;
        Path XMLRepositoryLocation;
        boolean isRepositoryAlreadyExistsInPath;
        StringBuilder validationErrorMessage = new StringBuilder(EMPTY_STRING);
        UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());

        PrintWriter out = response.getWriter();
        loggedInUsername = SessionUtils.getUsername(request);
        loggedInUser = ServletUtils.getUsersManager(getServletContext()).getUsers().get(loggedInUsername);
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
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("a repository in the same name is already exists");
            }
            else
            {
                // ----------- the repository in XML is empty && there is no repository on location path ---------
                m_UserEngine.createRepository(XMLRepositoryLocation, repositoryName);
            }
        }
        else if (validateXMLRepository(XMLRepo, m_UserEngine.getMagitSingleFolderByID(), validationErrorMessage))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                // response error- a repository name already exists
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("a repository in the same name is already exists");
            }
            else
            {
                if (m_UserEngine.isDirectoryEmpty(XMLRepositoryLocation))
                {
                    m_UserEngine.readRepositoryFromXMLFile(XMLRepo, m_UserEngine.getXMLMagitMaps());
                }
                else
                {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("the repository folder has content");
                }
            }
        }
        else
        {
            // send validation error message
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(validationErrorMessage.toString());
        }

        out.close();
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

        if (!m_UserEngine.areIDsValid(i_XmlRepo))
        {
            // 3.2
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: There are 2 identical IDs").append(System.lineSeparator());
        }

        if (!m_UserEngine.areFoldersReferencesValid(i_XmlRepo.getMagitFolders(), i_XmlRepo.getMagitBlobs()))
        {
            // 3.3, 3.4, 3.5
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: Folders references are not valid").append(System.lineSeparator());
        }

        if (!m_UserEngine.areCommitsReferencesAreValid(i_XmlRepo.getMagitCommits(), i_MagitFolderByID))
        {
            // 3.6, 3.7
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: Commits references are not valid").append(System.lineSeparator());
        }

        if (!m_UserEngine.areBranchesReferencesAreValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitCommits()))
        {
            // 3.8
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: Branches references are not valid").append(System.lineSeparator());
        }

        if (!m_UserEngine.isHeadReferenceValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitBranches().getHead()))
        {
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: Head reference is not valid").append(System.lineSeparator());
        }

        if(!m_UserEngine.isMagitRemoteReferenceValid(i_XmlRepo))
        {
            // 4.1
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: There is no M.A Git repository on the path represents the M.A Git remote repository").append(System.lineSeparator());
        }

        if(!m_UserEngine.areBranchesTrackingAfterAreValid(i_XmlRepo.getMagitBranches()))
        {
            // 4.2
            isXMLTotallyValid = false;
            i_ValidatioErrorMessage.append("Load repository by XML error: A tracking branch is tracking after a non remote branch").append(System.lineSeparator());
        }

        return isXMLTotallyValid;
    }
}
