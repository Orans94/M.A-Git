package magithub.servlets;

import engine.dataobjects.PullRequest;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.notifications.NewPullRequestNotification;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import static magithub.constants.Constants.*;

public class PullRequestServlet extends HttpServlet
{
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ParseException
    {
        // ASSUMPTION: 1.the user will have the option to press on the pull request button only on relevant RR repositories.
        //             2.all the relevant parameters are presetted on the request.
        String targetBranchName, baseBranchName, LRUserName, RRUserName, LRName, RRName, PRMessage;
        Path LRPath, RRPath;
        User LRUser, RRUser;
        EngineManager LRUserEngine;
        PullRequest pullRequest;

        baseBranchName = request.getParameter("baseBranch");
        targetBranchName = request.getParameter("targetBranch");
        PRMessage = request.getParameter("Message");
        LRName = request.getParameter("LRName");
        RRName = request.getParameter("RRName");
        RRUserName = request.getParameter("RRUserName");
        LRUserName = SessionUtils.getUsername(request);

        LRPath = MAGITEX3_DIRECTORY_PATH.resolve(LRUserName).resolve(LRName);
        RRPath = MAGITEX3_DIRECTORY_PATH.resolve(RRUserName).resolve(RRName);

        LRUser = ServletUtils.getUserManager(getServletContext()).getUsers().get(LRUserName);
        RRUser = ServletUtils.getUserManager(getServletContext()).getUsers().get(RRUserName);
        LRUserEngine = LRUser.getEngine();
        if(LRUserEngine.getLoadedRepository().getRemoteRepositoryPath() == null)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Remote repository is undefined");
        }
        else
        {
            pullRequest = new PullRequest(LRPath, RRPath, RRUserName, LRUserName, targetBranchName, baseBranchName, PRMessage);
            LRUserEngine.getLoadedRepository().pullRequest();

            LRUser.getPullRequests().add(pullRequest);
            RRUser.getPullRequests().add(pullRequest);
            //RRUser.getUnseenNotifications().add(new NewPullRequestNotification(pullRequest));
            //TODO- above line not compiling
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            processRequest(req, resp);
        }
        catch (ParseException e) // TODO
        {
            e.printStackTrace();
        }
    }
}
