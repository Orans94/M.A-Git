package magithub.servlets;

import engine.managers.UsersManager;
import magithub.constants.Constants;
import magithub.utils.ServletUtils;
import magithub.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static magithub.constants.Constants.USERNAME;

public class LoginServlet extends HttpServlet
{
    private final String SIGN_UP_URL = "../signup/signup.html";
    private final String MAIN_URL = "../main/main.html";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        String userNameFromSession = SessionUtils.getUsername(request);
        UsersManager usersManager = ServletUtils.getUsersManager(getServletContext());
        if (userNameFromSession == null)
        {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty())
            {
                //no username in session and no username in parameter -
                //redirect back to the index page
                //this return an HTTP code back to the browser telling it to load
                response.sendRedirect(SIGN_UP_URL);
            }
            else
            {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                //lower case all user name letters
                usernameFromParameter = usernameFromParameter.toLowerCase();
                /*
                One can ask why not enclose all the synchronizations inside the usersManager object ?
                Well, the atomic action we need to perform here includes both the question (isUserExists) and (potentially) the insertion
                of a new user (addUser). These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough.
                (of course there are other more sophisticated and performable means for that (atomic objects etc) but these are not in our scope)

                The synchronized is on this instance (the servlet).
                As the servlet is singleton - it is promised that all threads will be synchronized on the very same instance (crucial here)

                A better code would be to perform only as little and as necessary things we need here inside the synchronized block and avoid
                do here other not related actions (such as request dispatcher\redirection etc. this is shown here in that manner just to stress this issue
                 */
                synchronized (this)
                {
                    if (!usersManager.isUserExists(usernameFromParameter))
                    {
                        usersManager.addUser(usernameFromParameter);
                    }
                    //add the new user to the users list
                    //set the username in a session so it will be available on each request
                    //the true parameter means that if a session object does not exists yet
                    //create a new one
                    request.getSession(true).setAttribute(USERNAME, usernameFromParameter);

                    //redirect the request to the chat room - in order to actually change the URL
                    System.out.println("On login, request URI is: " + request.getRequestURI());
                    response.sendRedirect(MAIN_URL);

                }
            }
        }
        else
        {
            //user is already logged in
            response.sendRedirect(MAIN_URL);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        processRequest(req, resp);
    }
}
