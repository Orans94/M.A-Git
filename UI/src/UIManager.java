import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.exit;

public class UIManager
{
    private EngineManager m_Engine = new EngineManager();

    public void Run() throws IOException
    {
        Menu menu = new Menu();
        updateUserName();
        while (true)// actually while user didnt choose to exit
        {
            menu.Show();
            handleUserChoice();
        }
    }

    private void handleUserChoice() throws IOException
    {
        int userChoiceInt = 0;
        Scanner scanner = new Scanner(System.in);
        boolean isParseFailed = true;
        while (isParseFailed)
        {
            try
            {
                String userChoice = scanner.nextLine();
                userChoiceInt = Integer.parseInt(userChoice);
                isParseFailed = false;
            } catch (NumberFormatException e)
            {
                System.out.printf("The number you entered is not valid, please enter an Integer");
            }
        }

        switch (userChoiceInt)
        {
            case 2: // GIT INIT
                initializeRepository();
                break;
            case 7: // COMMIT
                try
                {
                    m_Engine.commit("stu?");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case 9:
                createNewBranch();
                break;
            case 11:
                checkout();
                break;
            default:
                exit(0);
        }
    }

    private void initializeRepository()
    {
        Path repPath = requestPath();
        String repositoryName = requestRepositoryName();
        try
        {
            // check if the path is exists
            if (m_Engine.isPathExists(repPath.resolve(repositoryName)))
            {
                if (m_Engine.isRepository(repPath.resolve(repositoryName))) // check if there is repo in path\\repositoryName
                {
                    // the dir is repository
                    System.out.println("The directory is already a repository");
                }
                else
                {
                    // create a new repository
                    m_Engine.createRepository(repPath.resolve(repositoryName));
                }
            }
            else //TODO ask: if the path doesn't exists create it? or leave a message?
            {
                System.out.println("Path does not exists");
                //m_Engine.CreateDirectory(repPath, repositoryName);
                //m_Engine.CreateRepository(repPath.resolve(repositoryName));
            }
        } catch (IOException e) //TODO handle create dir fail
        {

        }
    }

    private Path requestPath()
    {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Please enter a legal path");
        String pathString = myObj.nextLine();  // Read path input
        Path path = Paths.get(pathString);
        while (!m_Engine.isPathExists(path))
        {
            System.out.println("The path doesn't exists, Please enter a legal path");
            pathString = myObj.nextLine();
            path = Paths.get(pathString);
        }

        return path;
    }

    private String requestRepositoryName()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a legal repository name");
        String repositoryName = scanner.nextLine();

        while (!m_Engine.isDirectoryNameValid(repositoryName))
        { //TODO describe the prohibited symbols in folder name
            System.out.println("Invalid repository name, Please enter a legal repository name");
            repositoryName = scanner.nextLine();
        }

        return repositoryName;
    }

    private void updateUserName()
    {
        Menu.requestUserName();
        Scanner scanner = new Scanner(System.in);
        String userName = scanner.nextLine();
        m_Engine.setUserName(userName);
    }

    private void commit() throws IOException //TODO handle exception
    {
        // func #7
        boolean isWCDirty;

        String commitMessage = requestCommitMessage();
        isWCDirty = m_Engine.commit(commitMessage);
        if(isWCDirty)
        {
            System.out.println("Committed successfully");
        }
        else
        {
            System.out.println("There is nothing to commit, WC status is clean");
        }
    }

    private String requestCommitMessage()
    {
        System.out.println("Please enter commit message");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void createNewBranch()
    {
        // func #9
        String branchName = requestBranchName();
        boolean isBranchExists = m_Engine.isBranchExists(branchName);
        if (isBranchExists)
        {
            System.out.println("Branch " + branchName + " already exists");
        }
        else
        {
            if(m_Engine.isBranchNameEqualsHead(branchName))
            {
                System.out.println("You can not set branch name to HEAD");
            }
            else
            {
                m_Engine.createNewBranch(branchName);
                System.out.println("Branch " + branchName + " created successfully");
            }
        }
    }

    private String requestBranchName()
    {
        // requesting branch name from the user and returning a string represents it.
        System.out.println("Please enter the branch name");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void checkout() throws IOException {
        // func #11
        String branchName = requestBranchName();
        if(m_Engine.isBranchExists(branchName))
        {
            OpenChanges openChanges = m_Engine.getOpenChanges();
            if (m_Engine.isFileSystemDirty(openChanges)) {
                notifyUserDirtyStatusBeforeCheckout();
                boolean toCommit = doesUserWantToCommitBeforeCheckout();
                if (toCommit)
                {//TODO implement UI commit that gets a commit message, check if its clean already etc..
                    commit(); // (UI commit)
                }
            }

            m_Engine.checkout(branchName);
            System.out.println("Checkout made successfully");
        }
        else
        {
            System.out.println("Branch "+branchName+" does not exists");
        }
    }

    private void notifyUserDirtyStatusBeforeCheckout()
    {
        System.out.println("Please notice:");
        System.out.println("You chose to checkout but the WC status is not clean.");
        System.out.println("If you will not commit before checkout all open changes will lost");
    }

    private boolean doesUserWantToCommitBeforeCheckout()
    {
        showUserCommitChoice();
        Scanner scanner = new Scanner(System.in);
        String userChoice = scanner.nextLine();
        //TODO handle parsing and exceptions(if not an integer).
        int userChoiceInt = Integer.parseInt(userChoice);
        boolean isInRange = isUserChoiceInRange(1, 2, userChoiceInt);
        while (!isInRange)
        {
            userChoiceNotInRange();
            showUserCommitChoice();
            userChoice = scanner.nextLine();
            userChoiceInt = Integer.parseInt(userChoice);
            isInRange = isUserChoiceInRange(1, 2, userChoiceInt);
        }

        return userChoice.equals("1");
    }

    private void showUserCommitChoice()
    {
        System.out.println("Do you want to commit before checkout?");
        System.out.println("1. Yes");
        System.out.println("2. No");
    }

    private boolean isUserChoiceInRange(int i_Start, int i_End, int i_UserChoice)
    {
        return i_UserChoice >= i_Start && i_UserChoice <= i_End;
    }

    private void userChoiceNotInRange()
    {
        System.out.println("The number you entered is not valid, please enter a valid number from the list below:");
    }

}


