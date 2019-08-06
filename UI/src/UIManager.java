import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
            }
            catch (NumberFormatException e)
            {
                System.out.println("The number you entered is not valid, please enter an Integer");
            }
        }

        switch (userChoiceInt)
        {
            case 1:
                updateUserName();
                break;
            case 2: // GIT INIT
                initializeRepository();
                break;
            case 4:
                changeRepository();
                break;
            case 5:
                showDetailsOfCurrentCommit();
                break;
            case 6:
                showStatus();
                break;
            case 7: // COMMIT
                commit();
                break;
            case 8:
                showAllBranches();
                break;
            case 9:
                createNewBranch();
                break;
            case 10:
                deleteBranch();
                break;
            case 11:
                checkout();
                break;
            case 12:
                showActiveBranchHistory();
                break;
            default:
                exit(0);
        }
    }

    private void showStatus() throws IOException
    {
        OpenChanges openChanges = m_Engine.getOpenChanges();
        printOpenChanges(openChanges);
    }

    private void printOpenChanges(OpenChanges openChanges)
    {
        if(openChanges.isFileSystemClean())
        {
            System.out.println("WC is clean");
        }
        else
        {
            printList("Deleted", openChanges.getDeletedNodes());
            printList("Modified", openChanges.getModifiedNodes());
            printList("New", openChanges.getNewNodes());
        }
    }

    private void printList(String i_Status, List<Path> i_OpenChangesList)
    {
        //printing an openchanges list with its status
        for (Path path : i_OpenChangesList)
        {
            System.out.println(i_Status + ": " + path);
        }
    }

    private void initializeRepository()
    {
        Path repPath = requestPath();
        try
        {
            if (m_Engine.isPathExists(repPath))
            { //  the path is exists
                if (m_Engine.isRepository(repPath)) // check if there is repo in path\\repositoryName
                {
                    // the dir is repository
                    boolean toStash = doesUserWantToStashExistingRepository();
                    if(toStash)
                    {
                        m_Engine.stashRepository(repPath);
                        createNewRepository(repPath);
                    }
                }
                else
                {
                    createNewRepository(repPath);
                }
            }
            else
            {
                System.out.println("Path does not exists");
            }
        }
        catch (IOException e) //TODO handle create dir fail
        {

        }
    }

    private void createNewRepository(Path i_RepPath) throws IOException
    {
        String repositoryName = requestRepositoryName();
        m_Engine.createRepository(i_RepPath,repositoryName);
        System.out.println("A new repository has been created successfully");
    }

    private boolean doesUserWantToStashExistingRepository()
    {
        System.out.println("The directory is already a repository");
        boolean isValid = true;
        int choiceInt;

        do
        {
            System.out.println("Would you like to stash the existing repository?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();
            choiceInt = Integer.parseInt(choice);
            isValid = isUserChoiceInRange(1,2, choiceInt);
            if(!isValid)
            {
                userChoiceNotInRange();
            }
        }while(!isValid);

        return choiceInt == 1;
    }

    private Path requestPath()
    {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Please enter a legal path");
        String pathString = myObj.nextLine();  // Read path input

        return Paths.get(pathString);
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
    {//TODO menu.requestusrname??
        Menu.requestUserName();
        Scanner scanner = new Scanner(System.in);
        String userName = scanner.nextLine();
        EngineManager.setUserName(userName);
    }

    private void commit() throws IOException //TODO handle exception
    {
        // func #7
        boolean isWCDirty;

        String commitMessage = requestCommitMessage();
        isWCDirty = m_Engine.commit(commitMessage);
        if (isWCDirty)
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
            if (m_Engine.isBranchNameEqualsHead(branchName))
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

    private void checkout() throws IOException
    {
        // func #11
        String branchName = requestBranchName();
        if (m_Engine.isBranchExists(branchName))
        {
            OpenChanges openChanges = m_Engine.getOpenChanges();
            if (m_Engine.isFileSystemDirty(openChanges))
            {
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
            System.out.println("Branch " + branchName + " does not exists");
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

    private void printBranch(Branch i_Branch, Head i_Head)
    {
        // printing all branches - helper of func #8
        boolean isGitHaveCommits = m_Engine.getRepository().getMagit().getCommits().containsKey(i_Branch.getCommitSHA1());
        String commitMessage = isGitHaveCommits ?
                m_Engine.getRepository().getMagit().getCommits().get(i_Branch.getCommitSHA1()).getMessage() : "";
        if(i_Branch == i_Head.getActiveBranch())
        {
            System.out.println("1. Branch name: " + i_Branch.getName() + " (HEAD)");
        }
        else
        {
            System.out.println("1. Branch name: " + i_Branch.getName());
        }
        if (isGitHaveCommits)
        {
            System.out.println("2. SHA1 of the pointed commit: " + i_Branch.getCommitSHA1());
            System.out.println("3. Message of the pointed commit: " + commitMessage);
        }
        System.out.println(System.lineSeparator());
    }

    private void userChoiceNotInRange()
    {
        System.out.println("The number you entered is not valid, please enter a valid number from the list below:");
    }

    private void changeRepository() throws IOException
    {
        // func #4
        Path repoPath = requestPath();
        if(m_Engine.isPathExists(repoPath))
        {
            if(m_Engine.isRepository(repoPath))
            {
                boolean isMagitRepo = m_Engine.isRepository(repoPath);
                m_Engine.changeRepository(repoPath);
                System.out.println(System.lineSeparator());
                System.out.println("Repository " + m_Engine.getRepository().getName() + " has been loaded");
            }
            else
            {
                System.out.println("The given path does not represents a M.A Git repository");
            }
        }
        else
        {
            System.out.println("The path does not exists");
        }
    }

    private void showAllBranches()
    {
        // func #8
        Map<String, Branch> branches = m_Engine.getRepository().getMagit().getBranches();
        Head head = m_Engine.getRepository().getMagit().getHead();
        for (Map.Entry<String, Branch> entry : branches.entrySet())
        {
            printBranch(entry.getValue(), head);
        }
    }

    private void showDetailsOfCurrentCommit()
    {
        // func #5
        NodeMaps nodeMaps = m_Engine.getRepository().getNodeMaps();
        if(nodeMaps.isEmpty())
        {
            System.out.println("Commit has not been done yet");
        }
        else
        {
            for (Map.Entry<Path, String> entry : nodeMaps.getSHA1ByPath().entrySet())
            {
                if (m_Engine.isDirectory(entry.getKey()))
                {
                    Folder folder = (Folder) nodeMaps.getNodeBySHA1().get(entry.getValue());
                    List<Item> items = folder.getItems();
                    Path folderPath = entry.getKey();
                    for (Item item : items)
                    {
                        printCurrentItemDetails(folderPath, item);
                    }
                }
            }
        }
    }

    private void printCurrentItemDetails(Path i_FolderPath, Item i_Item)
    {
        System.out.println("Full name: " + i_FolderPath.resolve(i_Item.getName()));
        System.out.println("Type: " + i_Item.getType());
        System.out.println("SHA-1: " + i_Item.getSHA1());
        System.out.println("Last modifier name: " + i_Item.getAuthor());
        System.out.println("Date modified: " + DateUtils.FormatToString(i_Item.getModificationDate()));
        System.out.println(System.lineSeparator());
    }

    private void showActiveBranchHistory()
    {
        // func #12
        Branch activeBranch = m_Engine.getRepository().getMagit().getHead().getActiveBranch();
        String commitSHA1 = activeBranch.getCommitSHA1();
        while (!commitSHA1.equals(""))
        {
            printCommit(m_Engine.getRepository().getMagit().getCommits().get(commitSHA1), commitSHA1);
            commitSHA1 = m_Engine.getRepository().getMagit().getCommits().get(commitSHA1).getParentSHA1();
        }
    }

    private void printCommit(Commit i_Commit, String i_CommitSHA1)
    {
        System.out.println("1. SHA1: " + i_CommitSHA1);
        System.out.println("2. Commit message: " + i_Commit.getMessage());
        System.out.println("3. Commit date: " + DateUtils.FormatToString(i_Commit.getCommitDate()));
        System.out.println("4. Commit author: " + i_Commit.getCommitAuthor());
        System.out.println(System.lineSeparator());
    }

    private void deleteBranch()
    {
        // func #10
        String branchName = requestBranchName();
        boolean isHeadBranch = m_Engine.isBranchNameRepresentsHead(branchName);
        boolean isBranchExists = m_Engine.isBranchExists(branchName);
        if (isHeadBranch)
        {
            System.out.println("Cannot delete active branch");
        } else if (!isBranchExists)
        {
            System.out.println("Branch " + branchName + " does not exists");
        } else
        {
            m_Engine.deleteBranch(branchName);
            System.out.println("Branch " + branchName + " deleted successfully");
        }
    }


}


