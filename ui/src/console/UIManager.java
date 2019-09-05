package console;
import engine.*;
import mypackage.MagitRepository;
import mypackage.MagitSingleFolder;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

public class UIManager
{
    private EngineManager m_Engine = new EngineManager();

    public void Run() throws IOException, JAXBException
    {
        boolean didUserChoseToExit = false;
        Menu menu = new Menu();
        updateUserName();
        while (!didUserChoseToExit)
        {
            menu.Show();
            didUserChoseToExit = handleUserChoice();
            System.out.println(System.lineSeparator());
        }
        System.out.println("Exiting M.A-Git");
    }

    private boolean handleUserChoice() throws IOException, JAXBException
    {
        // this method return true if user chose to exit
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

        try
        {
            switch (userChoiceInt)
            {
                case 1:
                    updateUserName();
                    break;
                case 2:
                    initializeRepository();
                    break;
                case 3:
                    readRepositoryFromXMLFile();
                    break;
                case 4:
                    loadRepositoryByPath();
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
                case 13:
                    changeActiveBranchPointedCommit();
                    break;
                case 14:// exit
                    return true;
            }
        }
        catch (Exception ex)
        {
            System.out.println("error: " + ex.getMessage());
        }
        return false;
    }

    private void changeActiveBranchPointedCommit() throws IOException
    {
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {

            String commitSHA1 = getCommitSHA1FromUser();
            if (m_Engine.isCommitSHA1Exists(commitSHA1))
            {
                OpenChanges openChanges = m_Engine.getFileSystemStatus();
                if (m_Engine.isFileSystemDirty(openChanges))
                {
                    int userChoiceInt;
                    do
                    {
                        System.out.println("Please notice, the WC is dirty. if you will continue all changes will be lost");
                        System.out.println("Would you like to continue?");
                        System.out.println("1. Yes");
                        System.out.println("2. No");
                        Scanner scanner = new Scanner(System.in);
                        String userChoice = scanner.nextLine();
                        userChoiceInt = Integer.parseInt(userChoice);
                    } while (!isUserChoiceInRange(1, 2, userChoiceInt));

                    if (userChoiceInt == 1)
                    {
                        m_Engine.changeActiveBranchPointedCommit(commitSHA1);
                        System.out.println("The active branch is now pointing on commit " + commitSHA1);
                        m_Engine.checkout(m_Engine.getActiveBranchName());
                        showDetailsOfCurrentCommit();
                    }
                }
                else
                {
                    m_Engine.changeActiveBranchPointedCommit(commitSHA1);
                    System.out.println("The active branch is now pointing on commit " + commitSHA1);
                    m_Engine.checkout(m_Engine.getActiveBranchName());
                    showDetailsOfCurrentCommit();
                }
            }
            else
            {
                System.out.println("The SHA1 you entered does not represents an existing commit");
            }
        }
    }

    private String getCommitSHA1FromUser()
    {
        System.out.println("Please enter SHA1 represents the new pointed commit");
        ;
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }

    private void readRepositoryFromXMLFile() throws JAXBException, IOException, ParseException
    {
        Path XMLFilePath = requestPath();
        Path XMLRepositoryLocation;
        boolean isRepositoryAlreadyExistsInPath, toStash;

        if (m_Engine.isPathExists(XMLFilePath))
        {
            // validate process
            if (m_Engine.getXMLManager().getXMLValidator().isXMLFile(XMLFilePath))
            {
                MagitRepository XMLRepo = m_Engine.createXMLRepository(XMLFilePath);
                m_Engine.getXMLManager().loadXMLRepoToMagitMaps(XMLRepo);
                XMLRepositoryLocation = Paths.get(XMLRepo.getLocation());
                if(!m_Engine.isPathExists(XMLRepositoryLocation))
                {
                    m_Engine.createRepositoryPathDirectories(XMLRepositoryLocation);
                }

                isRepositoryAlreadyExistsInPath = m_Engine.isRepository(XMLRepositoryLocation);
                if (m_Engine.isXMLRepositoryEmpty(XMLRepo))
                {
                    if (isRepositoryAlreadyExistsInPath)
                    {
                        toStash = doesUserWantToStashExistingRepository();
                        if (toStash)
                        {
                            m_Engine.stashRepository(XMLRepositoryLocation);
                            m_Engine.createEmptyRepository(XMLRepositoryLocation, XMLRepo.getName());
                            notifyRepositoryHasBeenLoaded();
                        }
                    }
                    else
                    {
                        m_Engine.createEmptyRepository(XMLRepositoryLocation, XMLRepo.getName());
                        notifyRepositoryHasBeenLoaded();
                    }
                }
                else if (validateXMLRepository(XMLRepo, XMLFilePath, m_Engine.getXMLManager().getXMLMagitMaps().getMagitSingleFolderByID()))
                {
                    if (isRepositoryAlreadyExistsInPath)
                    {
                        readRepositoryIfUserChosedToStash(XMLRepositoryLocation, XMLRepo);
                    }
                    else
                    {
                        if (m_Engine.isDirectoryEmpty(XMLRepositoryLocation))
                        {
                            m_Engine.readRepositoryFromXMLFile(XMLRepo, m_Engine.getXMLManager().getXMLMagitMaps());
                            System.out.println("Repository " + XMLRepo.getName() + " loaded successfully from xml file");
                        }
                        else
                        {
                            System.out.println("The directory already has content in it");
                        }
                    }
                }
            }
            else
            {
                System.out.println("The given path does not represents an XML file");
            }
        }
        else
        {
            System.out.println("Path does not exists");
        }
    }

    private void readRepositoryIfUserChosedToStash(Path i_XMLRepositoryLocation, MagitRepository XMLRepo) throws IOException, ParseException
    {
        boolean toStash;
        toStash = doesUserWantToStashExistingRepository();
        if (toStash)
        {
            m_Engine.stashRepository(i_XMLRepositoryLocation);
            m_Engine.readRepositoryFromXMLFile(XMLRepo, m_Engine.getXMLManager().getXMLMagitMaps());
            notifyRepositoryHasBeenLoaded();
        }
    }

    private boolean validateXMLRepository(MagitRepository i_XmlRepo, Path i_XMLFilePath, Map<String, MagitSingleFolder> i_MagitFolderByID)
    {
        boolean isXMLTotallyValid = true;


        if (!m_Engine.getXMLManager().getXMLValidator().areIDsValid(i_XmlRepo))
        {
            // 3.2
            isXMLTotallyValid = false;
            System.out.println("There are 2 identical IDs");
        }

        if (!m_Engine.getXMLManager().getXMLValidator().areFoldersReferencesValid(i_XmlRepo.getMagitFolders(), i_XmlRepo.getMagitBlobs()))
        {
            // 3.3, 3.4, 3.5
            isXMLTotallyValid = false;
            System.out.println("Folders references are not valid");
        }

        if (!m_Engine.getXMLManager().getXMLValidator().areCommitsReferencesAreValid(i_XmlRepo.getMagitCommits(), i_MagitFolderByID))
        {
            // 3.6, 3.7
            isXMLTotallyValid = false;
            System.out.println("Commits references are not valid");
        }

        if (!m_Engine.getXMLManager().getXMLValidator().areBranchesReferencesAreValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitCommits()))
        {
            // 3.8
            isXMLTotallyValid = false;
            System.out.println("Branches references are not valid");
        }

        if (!m_Engine.getXMLManager().getXMLValidator().isHeadReferenceValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitBranches().getHead()))
        {
            isXMLTotallyValid = false;
            System.out.println("Head reference is not valid");
        }

        return isXMLTotallyValid;
    }

    private void showStatus() throws IOException
    {
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            //if(!m_Engine.isRootFolderEmpty())
            //  {
            printOpenChanges(m_Engine.getFileSystemStatus());
            // }
            //  else
            //  {
            // System.out.println("Root folder is empty");
            // }
        }
    }

    private void printOpenChanges(OpenChanges openChanges)
    {
        if (openChanges.isFileSystemClean())
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

    private void initializeRepository() throws IOException
    {
        Path repPath = requestPath();
        if (!m_Engine.isPathExists(repPath))
        {
            m_Engine.createRepositoryPathDirectories(repPath);
            createNewRepository(repPath);
        }
        else
        {
            System.out.println("The given path already exists");
        }
    }

    private void createNewRepository(Path i_RepPath) throws IOException
    {
        String repositoryName = requestRepositoryName();
        m_Engine.createRepository(i_RepPath, repositoryName);
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
            isValid = isUserChoiceInRange(1, 2, choiceInt);
            if (!isValid)
            {
                userChoiceNotInRange();
            }
        } while (!isValid);

        return choiceInt == 1;
    }

    private Path requestPath()
    {
        Path path;
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Please enter a legal path");
        String pathString = myObj.nextLine();  // Read path input
        try
        {
            path = Paths.get(pathString);
        }
        catch (InvalidPathException e)
        {
            throw new InvalidPathException(pathString, "Invalid path entered");
        }

        return path;
    }

    private String requestRepositoryName()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a legal repository name");

        return scanner.nextLine();
    }

    private void updateUserName()
    {
        Menu.requestUserName();
        Scanner scanner = new Scanner(System.in);
        String userName = scanner.nextLine();
        EngineManager.setUserName(userName);
    }

    private void commit() throws IOException
    {
        // func #7
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            boolean isWCDirty;
            if (!m_Engine.isRootFolderEmpty())
            {
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
            else
            {
                System.out.println("Root folder is empty, there is nothing to commit");
            }
        }
    }

    private String requestCommitMessage()
    {
        System.out.println("Please enter commit message");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void createNewBranch() throws IOException
    {
        // func #9
        boolean toCheckout;

        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
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
                    toCheckout = doesUserWantToCheckoutAfterCreatingNewBranch();
                    if (toCheckout)
                    {
                        OpenChanges openChanges = m_Engine.getFileSystemStatus();
                        if (m_Engine.isFileSystemDirty(openChanges))
                        {
                            System.out.println("The WC status is dirty, the system did not checked out");
                        }
                        else
                        {
                            m_Engine.setActiveBranchName(branchName);
                            System.out.println("Checkout to branch " + branchName + " has been made successfully");
                        }
                    }
                }
            }
        }
    }

    private boolean doesUserWantToCheckoutAfterCreatingNewBranch()
    {
        showUserCheckoutChoice();
        Scanner scanner = new Scanner(System.in);
        String userChoice = scanner.nextLine();
        int userChoiceInt = Integer.parseInt(userChoice);
        boolean isInRange = isUserChoiceInRange(1, 2, userChoiceInt);
        while (!isInRange)
        {
            userChoiceNotInRange();
            showUserCheckoutChoice();
            userChoice = scanner.nextLine();
            userChoiceInt = Integer.parseInt(userChoice);
            isInRange = isUserChoiceInRange(1, 2, userChoiceInt);
        }

        return userChoice.equals("1");
    }

    private void showUserCheckoutChoice()
    {
        System.out.println("Would you like to checkout the newly created branch?");
        System.out.println("1. Yes");
        System.out.println("2. No");
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
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            String branchName = requestBranchName();
            if (m_Engine.isBranchExists(branchName))
            {
                if (!m_Engine.isBranchPointedCommitSHA1Empty(branchName))
                {
                    OpenChanges openChanges = m_Engine.getFileSystemStatus();
                    if (m_Engine.isFileSystemDirty(openChanges))
                    {
                        notifyUserDirtyStatusBeforeCheckout();
                        boolean toCommit = doesUserWantToCommitBeforeCheckout();
                        if (toCommit)
                        {
                            commit(); // (UI commit)
                        }
                    }

                    m_Engine.checkout(branchName);
                    System.out.println("Checkout made successfully");
                }
                else
                {
                    System.out.println("Branch " + branchName + " is not pointing on a commit");
                    System.out.println("The system did not checked out");
                }
            }
            else
            {
                System.out.println("Branch " + branchName + " does not exists");
            }
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
        if (i_Branch == i_Head.getActiveBranch())
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

    private void loadRepositoryByPath() throws IOException, ParseException
    {
        // func #4
        Path repoPath = requestPath();
        if (m_Engine.isPathExists(repoPath))
        {
            if (m_Engine.isRepository(repoPath))
            {
                if (m_Engine.isRepositoryEmpty(repoPath))
                {
                    // the given path is an empty repository
                    m_Engine.loadEmptyRepository(repoPath);
                    notifyRepositoryHasBeenLoaded();
                }
                else
                {
                    m_Engine.loadRepositoryByPath(repoPath);
                    notifyRepositoryHasBeenLoaded();
                }
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

    private void notifyRepositoryHasBeenLoaded()
    {
        System.out.println(System.lineSeparator());
        System.out.println("Repository " + m_Engine.getRepository().getName() + " has been loaded");
    }

    private void showAllBranches()
    {
        // func #8
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            Map<String, Branch> branches = m_Engine.getRepository().getMagit().getBranches();
            Head head = m_Engine.getRepository().getMagit().getHead();
            for (Map.Entry<String, Branch> entry : branches.entrySet())
            {
                printBranch(entry.getValue(), head);
            }
        }
    }

    private void showDetailsOfCurrentCommit()
    {
        // func #5
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            NodeMaps nodeMaps = m_Engine.getRepository().getNodeMaps();
            if (nodeMaps.isEmpty())
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
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            String activeBranchName = m_Engine.getActiveBranchName();
            if (!m_Engine.isBranchPointedCommitSHA1Empty(activeBranchName))
            {
                Map<String, Commit> commitBySHA1;
                SortedSet<String> orderedCommitHistorySHA1;
                orderedCommitHistorySHA1 = m_Engine.getRepository().getActiveBranchHistory();
                commitBySHA1 = m_Engine.getRepository().getMagit().getCommits();
                for (String SHA1 : orderedCommitHistorySHA1)
                {
                    printCommit(commitBySHA1.get(SHA1), SHA1);
                }
            }
            else
            {
                System.out.println("Active branch " + activeBranchName + " is not pointing on a commit");
            }
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

    private void deleteBranch() throws IOException
    {
        // func #10
        if (m_Engine.isRepositoryNull())
        {
            System.out.println("Repository have to be loaded or initialized before making this operation");
        }
        else
        {
            String branchName = requestBranchName();
            boolean isHeadBranch = m_Engine.isBranchNameRepresentsHead(branchName);
            boolean isBranchExists = m_Engine.isBranchExists(branchName);
            if (isHeadBranch)
            {
                System.out.println("Cannot delete active branch");
            }
            else if (!isBranchExists)
            {
                System.out.println("Branch " + branchName + " does not exists");
            }
            else
            {
                m_Engine.deleteBranch(branchName);
                System.out.println("Branch " + branchName + " deleted successfully");
            }
        }
    }
}


