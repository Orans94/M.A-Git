import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.exit;

public class UIManager
{
    private EngineManager m_Engine = new EngineManager();

    public void Run()
    {
        Menu menu = new Menu();
        while (true)// actually while user didnt choose to exit
        {
            menu.Show();
            handleUserChoice();
        }
    }

    private void handleUserChoice()
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
                try{m_Engine.Commit("stu?");}
                catch (Exception e){}
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
                if (m_Engine.IsRepository(repPath.resolve(repositoryName))) // check if there is repo in path\\repositoryName
                {
                    // the dir is repository
                    System.out.println("The directory is already a repository");
                } else
                {
                    // create a new repository
                    m_Engine.CreateRepository(repPath.resolve(repositoryName));
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

}


