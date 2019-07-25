import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class UIManager
{
    private EngineManager m_Engine = new EngineManager();

    public void Run()
    {
        initializeRepository();
        Menu menu = new Menu();
        //menu.show();
        //while(!exit)
        //

    }

    private void initializeRepository() throws IOException
    { // TODO handle this expection
        Path repPath = requestPath();
        String repositoryName = requestRepositoryName();


        // check if the path is exists
        if (m_Engine.isPathExists(repPath.resolve(repositoryName)))
        {
            if (m_Engine.IsRepository(repPath.resolve(repositoryName))) // check if there is repo in path\\repositoryName
            {
                // the dir is repository
                System.out.println("The directory is already a repository");
            }
            else
            {
                // create a new repository
                m_Engine.CreateRepository(repPath.resolve(repositoryName));
            }
        }
        else
        {
            m_Engine.CreateDirectory(repPath, repositoryName);
            m_Engine.CreateRepository(repPath.resolve(repositoryName));
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

    private String requestUserName()
    {
        System.out.println("Please enter your user name");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void updateUserName()
    {
        // func #1
        String userName = requestUserName();
        m_Engine.UpdateUserName(userName);
    }

    private void ReadRepositoryFromXMLFile()
    {
        // func #3
        Path XMLFilePath = requestPath();
        // maybe returning a value if something is wrong and if so what is wrong(Exception or string?)
        m_Engine.ReadRepositoryFromXMLFile(XMLFilePath);
    }

    private void changeRepository()
    {
        // func #4
        Path repoPath = requestPath();
        boolean isMagitRepo = m_Engine.IsRepository(repoPath);
        if (!isMagitRepo)
        {
            System.out.println("The given path does not represents a M.A Git repository");
        } else
        {
            m_Engine.ChangeRepository(repoPath);
            System.out.println("Current ");
        }
    }


    private void showDetailsOfCurrentCommit()
    {
        // func #5
        //Commit or branch or HEAD?
        Commit currentCommit = m_Engine.GetCurrentCommit();
        printCurrentCommitDetails(currentCommit);
    }

    private void printCurrentCommitDetails(Commit i_CurrentCommit)
    {
        //printing current commit details - helper of func #5
    }

    private void showStatus()
    {
        // func #6
        // maybe a lot of gets for user name, deleted files...
        // or like this one get of working copy represents all the data
        m_Engine.GetWorkingCopy();
        printWorkingCopyStatus();
    }

    private void printWorkingCopyStatus()
    {
        // printing the working copy status - helper of func #6
    }

    private String requestCommitMessage()
    {
        System.out.println("Please enter commit message");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void Commit()
    {
        // func #7
        String commitMessage = requestCommitMessage();
        m_Engine.Commit(commitMessage);
        System.out.println("Committed successfully");
    }

    private void showAllBranches()
    {
        // func #8
        m_Engine.GetAllBranches();
        printAllBranches();
    }

    private void printAllBranches()
    {
        // printing all branches - helper of func #8
    }

    private String requestBranchName()
    {
        // requesting branch name from the user and returning a string represents it.
        System.out.println("Please enter the branch name");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void createNewBranch()
    {
        // func #9
        String branchName = requestBranchName();
        boolean isBranchExists = m_Engine.IsBranchExists(branchName);
        if (isBranchExists)
        {
            System.out.println("Branch " + branchName + " already exists");
        } else
        {
            m_Engine.CreateNewBranch(branchName);
            System.out.println("Branch " + branchName + " created successfully");
        }
    }

    private void deleteBranch()
    {
        // func #10
        String branchName = requestBranchName();
        boolean isHeadBranch = m_Engine.IsBranchNameRepresentsHead(branchName);
        boolean isBranchExists = m_Engine.IsBranchExists(branchName);
        if (isHeadBranch)
        {
            System.out.println("Cannot delete HEAD branch");
        } else if (!isBranchExists)
        {
            System.out.println("Branch " + branchName + " does not exists");
        } else
        {
            m_Engine.DeleteBranch(branchName);
            System.out.println("Branch " + branchName + " deleted successfully");
        }
    }

    private void checkout()
    {
        // func #11
        String branchName = requestBranchName();
        boolean isStatusClean = m_Engine.IsStatusClean();
        if (isStatusClean)
        {
            m_Engine.Checkout(branchName);
        } else
        {
            notifyUserDirtyStatusBeforeCheckout();
            showStatus();
            boolean toCommit = doesUserWantToCommitBeforeCheckout();
            if (toCommit)
            {
                Commit();
            }
            m_Engine.Checkout(branchName);
            System.out.println("Checkout made successfully");
        }
    }

    private void userChoiceNotInRange()
    {
        System.out.println("The number you entered is not valid, please enter a valid number from the list below:");
    }

    private void showUserCommitChoice()
    {
        System.out.println("Do you want to commit before checkout?");
        System.out.println("1. Yes");
        System.out.println("2. No");
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

    private boolean isUserChoiceInRange(int i_Start, int i_End, int i_UserChoice)
    {
        return i_UserChoice >= i_Start && i_UserChoice <= i_End;
    }

    private void notifyUserDirtyStatusBeforeCheckout()
    {
        System.out.println("Please notice:");
        System.out.println("You chose to checkout but the WC status is not clean.");
        System.out.println("If you will not commit before checkout all open changes will lost");
    }

    private void showCommitsHistoryOfCurrentBranch()
    {
        // func #12

    }

    private void exit()
    {
        //TODO maybe call everything we need to do logically before exiting.
    }

}


