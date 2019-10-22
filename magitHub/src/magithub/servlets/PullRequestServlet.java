package magithub.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.branches.Branch;
import engine.core.Repository;
import engine.dataobjects.NodeMaps;
import engine.dataobjects.OpenChanges;
import engine.dataobjects.PullRequest;
import engine.dataobjects.ePullRequestState;
import engine.managers.EngineManager;
import engine.managers.User;
import engine.managers.UsersManager;
import engine.notifications.ForkNotification;
import engine.notifications.NewPullRequestNotification;
import engine.notifications.PullRequestUpdateNotification;
import engine.objects.Commit;
import engine.objects.Node;
import magithub.WindowsPathConverter;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import static magithub.constants.Constants.*;

public class PullRequestServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String requestType = request.getParameter("requestType");

        switch(requestType)
        {
            case "handlePR":
                handlePRRequest(request, response);
                /*
                in this flow the user choose to approve or decline a pr
                this is the parameters sent from the ajax call:
                data: {
                        "requestType": "handlePR"
                        ,"PRID": event.data.PRID
                        ,"userDecision" : event.data.userDecision
                        },
                * if the user approved the request than => userDecision = approve
                * if the user decline the request than => userDecision = decline

                this ajax call is from pullRequest.js in handlePR function
                */
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String requestType = req.getParameter("requestType");

        switch(requestType)
        {
            case "openChanges":
                openChangesRequest(req, resp);
                break;
            case "fileContent":
                fileContentRequest(req, resp);
                break;
            case "newPR":
                newPullRequest(req,resp);
                break;
        }
    }

    private void handlePRRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String PRID = req.getParameter("PRID");
        String userDecision = req.getParameter("userDecision");
        String message= req.getParameter("message");
        String username = SessionUtils.getUsername(req);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        User RRUser = userManager.getUsers().get(username);
        PullRequest pullRequest = RRUser.getPullRequests().get(Integer.parseInt(PRID));
        User LRUser= userManager.getUsers().get(pullRequest.getLRUsername());
        Repository repo = RRUser.getEngine().getRepositories().get(MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(pullRequest.getRRName()));
        PrintWriter out = resp.getWriter();
        if(repo.getFileSystemStatus().isFileSystemClean())
        {
            switch (userDecision)
            {
                case "approve":
                    pullRequest.setStatus(ePullRequestState.Approved);
                    LRUser.getNotificationsManager().addNotification(new PullRequestUpdateNotification("approved", username, ""));
                    RRUser.getEngine().checkout(pullRequest.getBaseBranchName());
                    repo.setActiveBranchPointedCommitByBranchName(pullRequest.getTargetBranchName());
                    repo.checkout(pullRequest.getBaseBranchName());
                    break;
                case "decline":
                    pullRequest.setStatus(ePullRequestState.Denied);
                    LRUser.getNotificationsManager().addNotification(new PullRequestUpdateNotification("declined", username, message));
                    break;
            }
        }
        else
        {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("Dirty");
            out.flush();
            out.close();
        }
    }

    private void newPullRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String targetBranchName, baseBranchName, LRUserName, RRUserName, LRName, RRName, PRMessage;
        Path LRPath;
        User LRUser, RRUser;
        EngineManager LRUserEngine;
        PullRequest pullRequest;

        LRName = req.getParameter("repositoryName");
        baseBranchName = req.getParameter("baseBranch");
        targetBranchName = req.getParameter("targetBranch");
        PRMessage = req.getParameter("Message");
        LRUserName = SessionUtils.getUsername(req);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        LRUser = userManager.getUsers().get(LRUserName);
        LRUserEngine = LRUser.getEngine();
        LRPath = MAGITEX3_DIRECTORY_PATH.resolve(LRUserName).resolve(LRName);

        if(!LRUserEngine.getRepositories().get(LRPath).getMagit().getBranches().containsKey(baseBranchName))
        {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().print("Branch " + baseBranchName + " does not exist");
        }
        else if(!LRUserEngine.getRepositories().get(LRPath).getMagit().getBranches().containsKey(targetBranchName))
        {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().print("Branch " + targetBranchName + " does not exist");
        }
        else
        {
            Branch target = LRUserEngine.getRepositories().get(LRPath).getMagit().getBranches().get(targetBranchName);
            Branch base = LRUserEngine.getRepositories().get(LRPath).getMagit().getBranches().get(baseBranchName);
            if (!target.getIsTracking())
            {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print("Branch " + targetBranchName + " is not a RTB");
            }
            else if (!(base.getIsTracking() || base.getIsRemote()))
            {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print("Branch " + baseBranchName + " is not a RTB or RB");
            }
            else
            {
                RRName = LRUserEngine.getRepositories().get(LRPath).getRemoteRepositoryPath().getFileName().toString();
                RRUserName = LRUserEngine.getRepositories().get(LRPath).getRemoteRepositoryPath().getParent().getFileName().toString();
                pullRequest = new PullRequest(LRName, RRName, RRUserName, LRUserName, targetBranchName, baseBranchName, PRMessage);
                RRUser = ServletUtils.getUsersManager(getServletContext()).getUsers().get(RRUserName);
                RRUser.addPullRequest(pullRequest);
                RRUser.getNotificationsManager().addNotification(new NewPullRequestNotification(pullRequest));
            }
        }
    }

    private void fileContentRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        User user;
        String targetBranchName, baseBranchName, targetCommitSHA1, baseCommitSHA1;
        Commit targetCommit, baseCommit;

        String file = req.getParameter("filePath");
        String PRID = req.getParameter("PRID");
        String username = SessionUtils.getUsername(req);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        user = userManager.getUsers().get(username);
        EngineManager engine = user.getEngine();
        PullRequest pullRequest = user.getPullRequests().get(Integer.parseInt(PRID));
        Path repoPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(pullRequest.getRRName());
        Repository repo = engine.getRepositories().get(repoPath);
        Path filePath = Paths.get("C:\\magit-ex3").resolve(username).resolve(pullRequest.getRRName()).resolve(file);

        targetBranchName = pullRequest.getTargetBranchName();
        baseBranchName = pullRequest.getBaseBranchName();
        targetCommitSHA1 = repo.getMagit().getBranches().get(targetBranchName).getCommitSHA1();
        baseCommitSHA1 = repo.getMagit().getBranches().get(baseBranchName).getCommitSHA1();
        targetCommit = repo.getMagit().getCommits().get(targetCommitSHA1);
        baseCommit = repo.getMagit().getCommits().get(baseCommitSHA1);
        PrintWriter out = resp.getWriter();
        NodeMaps nodeMaps = engine.getNodeMapsByCommitSHA1(repoPath, targetCommitSHA1);
        String fileSHA1 = nodeMaps.getSHA1ByPath().get(filePath);
        Node node = nodeMaps.getNodeBySHA1().get(fileSHA1);
        OpenChanges openChanges = repo.delta(baseCommit, targetCommit);

        if(openChanges.getDeletedNodes().contains(filePath))
        {
            out.println("File Deleted");
        }
        else if(openChanges.getNewNodes().contains(filePath))
        {
            out.println("New File");
            out.print(node.getContent());
        }
        else
        {
            // modified file
            out.println("Modified File");
            out.print(node.getContent());
        }

        out.flush();
        out.close();
    }

    private void openChangesRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        User user;
        String targetBranchName, baseBranchName, targetCommitSHA1, baseCommitSHA1;
        Commit targetCommit, baseCommit;

        String PRID = req.getParameter("PRID");
        String username = SessionUtils.getUsername(req);
        UsersManager userManager = ServletUtils.getUsersManager(getServletContext());
        user = userManager.getUsers().get(username);
        EngineManager engine = user.getEngine();
        PullRequest pullRequest = user.getPullRequests().get(Integer.parseInt(PRID));
        Path repoPath = MAGITEX3_DIRECTORY_PATH.resolve(username).resolve(pullRequest.getRRName());
        Repository repo = engine.getRepositories().get(repoPath);

        targetBranchName = pullRequest.getTargetBranchName();
        baseBranchName = pullRequest.getBaseBranchName();
        targetCommitSHA1 = repo.getMagit().getBranches().get(targetBranchName).getCommitSHA1();
        baseCommitSHA1 = repo.getMagit().getBranches().get(baseBranchName).getCommitSHA1();
        targetCommit = repo.getMagit().getCommits().get(targetCommitSHA1);
        baseCommit = repo.getMagit().getCommits().get(baseCommitSHA1);
        PrintWriter out = resp.getWriter();

        OpenChanges openChanges = repo.delta(baseCommit, targetCommit);

        for (Path path : openChanges.getDeletedNodes())
        {
            if (!engine.isDirectory(path))
            {
                out.println(path.subpath(3,path.getNameCount()));
            }
        }
        for (Path path : openChanges.getModifiedNodes())
        {
            if (!engine.isDirectory(path))
            {
                out.println(path.subpath(3,path.getNameCount()));
            }
        }
        for (Path path : openChanges.getNewNodes())
        {
            if (!engine.isDirectory(path))
            {
                out.println(path.subpath(3,path.getNameCount()));
            }
        }

        out.flush();
        out.close();
    }
}
