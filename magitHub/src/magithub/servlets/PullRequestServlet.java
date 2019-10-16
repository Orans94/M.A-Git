package magithub.servlets;

import engine.dataobjects.PullRequest;
import engine.dataobjects.ePullRequestState;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.notifications.NewPullRequestNotification;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import static magithub.constants.Constants.*;

public class PullRequestServlet extends HttpServlet
{
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException
    {
        // ASSUMPTION: 1.the user will have the option to press on the pull request button only on relevant RR repositories.
        //             2.all the relevant parameters are presetted on the request.
        String targetBranchName, baseBranchName, LRUserName, RRUserName, LRName, RRName, PRMessage, repositoryName;
        Path LRPath, RRPath;
        User LRUser, RRUser;
        EngineManager LRUserEngine;
        PullRequest pullRequest;

        LRName = request.getParameter("repositoryName");
        baseBranchName = request.getParameter("baseBranch");
        targetBranchName = request.getParameter("targetBranch");
        PRMessage = request.getParameter("Message");
        LRUserName = SessionUtils.getUsername(request);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        LRUser = userManager.getUsers().get(LRName);
        LRUserEngine = LRUser.getEngine();


        LRPath = MAGITEX3_DIRECTORY_PATH.resolve(LRUserName).resolve(LRName);

        RRName = LRUserEngine.getRepositories().get(LRPath).getRemoteRepositoryPath().getFileName().toString();
        RRUserName = LRUserEngine.getRepositories().get(LRPath).getRemoteRepositoryPath().getParent().getFileName().toString();
        RRPath = MAGITEX3_DIRECTORY_PATH.resolve(RRUserName).resolve(RRName);

        RRUser = ServletUtils.getUsersManager(getServletContext()).getUsers().get(RRUserName);
        if(LRUserEngine.getRepositories().get(LRPath).getRemoteRepositoryPath() == null)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Remote repository is undefined");
        }
        else
        {
         //   pullRequest = new PullRequest(LRPath, RRPath, RRUserName, LRUserName, targetBranchName, baseBranchName, PRMessage);
            LRUserEngine.getRepositories().get(LRPath).pullRequest();

           // RRUser.getPullRequests().add(pullRequest);
            //RRUser.getNotificationsManager().addNotification(new NewPullRequestNotification(pullRequest));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        demoProcessRequest(req,resp);
    /*    try
        {
            processRequest(req, resp);
        }
        catch (ParseException e) // TODO
        {
            e.printStackTrace();
        }*/
    }

    private void demoProcessRequest(HttpServletRequest request, HttpServletResponse response)
    {

        PullRequest pullRequest = new PullRequest("fok1", "rep 1", "Oran", "Tomer", "targetBranch", "baseBranch","little message");
        if (request.getParameter("Message").equals("a"))
        {
            pullRequest.setStatus(ePullRequestState.Approved);
        }
        String loggedInUsername = SessionUtils.getUsername(request);
        UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());
        User user = usersManager.getUsers().get("oran");
        user.getPullRequests().add(pullRequest);
    }
}
