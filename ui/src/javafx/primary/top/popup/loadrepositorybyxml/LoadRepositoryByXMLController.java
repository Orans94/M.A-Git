package javafx.primary.top.popup.loadrepositorybyxml;

import javafx.AlertFactory;
import javafx.BrowseManager;
import javafx.StageUtilities;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.primary.top.popup.PopupController;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import mypackage.MagitRepository;
import mypackage.MagitSingleFolder;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;

public class LoadRepositoryByXMLController implements PopupController
{

    private TopController m_TopController;

    @FXML private VBox createNewRepositoryComponent;
    @FXML private TextField xmlPathTextField;
    @FXML private Button browseButton;
    @FXML private Button loadButton;

    public void setTopController(TopController i_TopController) { m_TopController = i_TopController; }


    @FXML
    public void initialize()
    {
        // binding Create button to directory text field
        BooleanBinding isTextFieldEmpty = Bindings.isEmpty(xmlPathTextField.textProperty());
        loadButton.disableProperty().bind(isTextFieldEmpty);
    }

    @FXML
    void browseButtonAction(ActionEvent event)
    {

        BrowseManager browseManager = new BrowseManager();
        File selectedDirectory = browseManager.openFileChooser(event, "*.xml");

        // if user chose a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            xmlPathTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void loadButtonAction(ActionEvent event) throws IOException, JAXBException, ParseException
    {
        Path XMLFilePath;
        Path XMLRepositoryLocation;
        boolean isRepositoryAlreadyExistsInPath, toStash;
        Task task = null;

        XMLFilePath = Paths.get(xmlPathTextField.getText());
        MagitRepository XMLRepo = m_TopController.createXMLRepository(XMLFilePath);
        m_TopController.loadXMLRepoToMagitMaps(XMLRepo);
        XMLRepositoryLocation = Paths.get(XMLRepo.getLocation());
        if(!m_TopController.isPathExists(XMLRepositoryLocation))
        {
            m_TopController.createRepositoryPathDirectories(XMLRepositoryLocation);
        }

        isRepositoryAlreadyExistsInPath = m_TopController.isRepository(XMLRepositoryLocation);
        String repositoryName = XMLRepo.getName();

        if (m_TopController.isXMLRepositoryEmpty(XMLRepo))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                // ----------- the repository in XML is empty && a repository is already exists in location path ---------
                toStash = doesUserWantToStashExistingRepository();
                if (toStash)
                {
                    task = createStashAndCreateEmptyRepositoryTask(XMLRepositoryLocation, repositoryName);
                    bindTaskToProgressBar(task);
                    new Thread(task).start();
                }
            }
            else
            {
                // ----------- the repository in XML is empty && there is no repository on location path ---------
                task = createCreateEmptyRepositoryTask(XMLRepositoryLocation, repositoryName);
                bindTaskToProgressBar(task);
                new Thread(task).start();
            }
        }
        else if (validateXMLRepository(XMLRepo, XMLFilePath, m_TopController.getMagitSingleFolderByID()))
        {
            if (isRepositoryAlreadyExistsInPath)
            {
                toStash = doesUserWantToStashExistingRepository();
                if (toStash)
                {
                    task = createCreateNonEmptyRepositoryTask(true, XMLRepositoryLocation, XMLRepo);
                    bindTaskToProgressBar(task);
                    new Thread(task).start();
                }
            }
            else
            {
                if (m_TopController.isDirectoryEmpty(XMLRepositoryLocation))
                {
                    task = createCreateNonEmptyRepositoryTask(false, XMLRepositoryLocation, XMLRepo);
                    bindTaskToProgressBar(task);
                    new Thread(task).start();
                }
                else
                {
                    notifyTheLoadingWasCanceledFolderHasContent();
                }
            }
        }

        StageUtilities.closeOpenSceneByActionEvent(event);
    }




    private void bindTaskToProgressBar(Task i_Task)
    {
        ProgressBar pb = m_TopController.getProgressBar();
        pb.progressProperty().bind(i_Task.progressProperty());
    }

    private void updateUIComponents() throws IOException
    {
        m_TopController.updateUIComponents();
    }

    private void createNewRepository(Path i_RepositoryPath, String i_RepositoryName) throws IOException
    {
        m_TopController.createRepository(i_RepositoryPath, i_RepositoryName);
    }

    private boolean validateXMLRepository(MagitRepository i_XmlRepo, Path i_XMLFilePath, Map<String, MagitSingleFolder> i_MagitFolderByID)
    {
        boolean isXMLTotallyValid = true;

        if (!m_TopController.areIDsValid(i_XmlRepo))
        {
            // 3.2
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "There are 2 identical IDs")
                    .showAndWait();
        }

        if (!m_TopController.areFoldersReferencesValid(i_XmlRepo.getMagitFolders(), i_XmlRepo.getMagitBlobs()))
        {
            // 3.3, 3.4, 3.5
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "Folders references are not valid")
                    .showAndWait();
        }

        if (!m_TopController.areCommitsReferencesAreValid(i_XmlRepo.getMagitCommits(), i_MagitFolderByID))
        {
            // 3.6, 3.7
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "Commits references are not valid")
                    .showAndWait();
        }

        if (!m_TopController.areBranchesReferencesAreValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitCommits()))
        {
            // 3.8
            isXMLTotallyValid = false;

            AlertFactory.createErrorAlert("Load repository by XML", "Branches references are not valid")
                    .showAndWait();
        }

        if (!m_TopController.isHeadReferenceValid(i_XmlRepo.getMagitBranches(), i_XmlRepo.getMagitBranches().getHead()))
        {
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "Head reference is not valid")
                    .showAndWait();
        }

        if(!m_TopController.isMagitRemoteReferenceValid(i_XmlRepo))
        {
            // 4.1
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "There is no M.A Git repository on the path represents the M.A Git remote repository")
                    .showAndWait();
        }

        if(!m_TopController.areBranchesTrackingAfterAreValid(i_XmlRepo.getMagitBranches()))
        {
            // 4.2
            isXMLTotallyValid = false;
            AlertFactory.createErrorAlert("Load repository by XML", "A tracking branch is tracking after a non remote branch")
                    .showAndWait();
        }

        return isXMLTotallyValid;
    }

    private void notifyRepositoryHasBeenCreated()
    {
        String repositoryName = m_TopController.getRepositoryName();
        String message = "A new repository " + repositoryName + " has been created successfully";

        AlertFactory.createInformationAlert("Load repository", message)
                .showAndWait();
    }


    private void notifyRepositoryHasBeenLoaded()
    {
        String repositoryName = m_TopController.getRepositoryName();
        String message = "Repository " + repositoryName + " has been loaded";

        AlertFactory.createInformationAlert("Load repository", message)
                .showAndWait();
    }


    private boolean doesUserWantToStashExistingRepository()
    {
        // ask user if he want to stash the existing repository
        Alert alert = AlertFactory.createYesNoAlert("Create new repository", "Would you like to stash the existing repository?");

        return alert.showAndWait().get().getText().equals("Yes");
    }

    private void notifyTheLoadingWasCanceledFolderHasContent()
    {
        AlertFactory.createErrorAlert("Load repository by XML", "The directory already has content in it")
        .showAndWait();
    }

    private void notifyRepositoryLoadedSuccessfullyFromXML(String i_RepoName)
    {
        AlertFactory.createInformationAlert("Load repository by XML", "Repository " + i_RepoName + " loaded successfully from xml file")
        .showAndWait();
    }

    private Task createStashAndCreateEmptyRepositoryTask(Path i_XMLRepositoryLocation, String i_RepositoryName)
    {
        return new Task() {
            @Override
            protected Object call() throws IOException
            {
                    Platform.runLater(() -> m_TopController.getProgressBar().setVisible(true));
                    updateProgress(1, 5);
                    m_TopController.stashRepository(i_XMLRepositoryLocation);
                    updateProgress(2, 5);
                    m_TopController.createEmptyRepository(i_XMLRepositoryLocation, i_RepositoryName);
                    updateProgress(3, 5);
                    Platform.runLater(() ->
                    {
                        try
                        {
                            updateUIComponents(i_XMLRepositoryLocation);
                        }
                        catch (IOException e)
                        {
                            //TODO
                            e.printStackTrace();
                        }
                    });
                    updateProgress(4, 5);
                    Platform.runLater(() -> notifyRepositoryHasBeenLoaded());
                    updateProgress(5, 5);
                    Platform.runLater(() -> m_TopController.getProgressBar().setVisible(false));

                    return null;
            }
        };
    }

    private Task createCreateEmptyRepositoryTask(Path i_XMLRepositoryLocation, String i_RepositoryName)
    {
        return new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                Platform.runLater(() -> m_TopController.getProgressBar().setVisible(true));
                updateProgress(1, 3);
                createNewRepository(i_XMLRepositoryLocation, i_RepositoryName);
                updateProgress(2, 3);
                Platform.runLater(() ->
                {
                    try
                    {
                        updateUIComponents(i_XMLRepositoryLocation);
                    }
                    catch (IOException e)
                    {
                        //TODO
                        e.printStackTrace();
                    }
                });
                updateProgress(3, 3);
                Platform.runLater(() -> notifyRepositoryHasBeenCreated());
                Platform.runLater(() -> m_TopController.getProgressBar().setVisible(false));

                return null;
            }
        };
    }

    private Task createCreateNonEmptyRepositoryTask(boolean i_ToStash, Path i_XMLRepositoryLocation, MagitRepository i_XMLRepo)
    {
        return new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                try
                {
                    Platform.runLater(() -> m_TopController.getProgressBar().setVisible(true));
                    updateProgress(1, 4);
                    if (i_ToStash)
                    {
                        m_TopController.stashRepository(i_XMLRepositoryLocation);
                    }
                    updateProgress(2, 4);
                    m_TopController.readRepositoryFromXMLFile(i_XMLRepo, m_TopController.getXMLMagitMaps());
                    updateProgress(3, 4);
                    Platform.runLater(() ->
                    {
                        try
                        {
                            updateUIComponents(i_XMLRepositoryLocation);
                        }
                        catch (IOException e)
                        {
                            //TODO
                            e.printStackTrace();
                        }
                    });
                    updateProgress(4, 4);
                    Platform.runLater(() -> notifyRepositoryLoadedSuccessfullyFromXML(i_XMLRepo.getName()));
                    Platform.runLater(() -> m_TopController.getProgressBar().setVisible(false));
                }
                catch (Exception e) {e.printStackTrace();}
                return null;

            }
        };
    }
    private void updateUIComponents(Path i_XMLRepositoryLocation) throws IOException
    {
        updateUIComponents();
        updateRepositoryFullPathSplitMenuButtonUI(i_XMLRepositoryLocation);

    }

    private void updateRepositoryFullPathSplitMenuButtonUI(Path i_XMLRepositoryLocation)
    {
        m_TopController.setRepositoryFullPathSplitMenuButton(i_XMLRepositoryLocation);
    }
}

