# M.A-Git

M.A-Git is a distributed version control tool that demonstrates a fully Git experience.


### Key features
- Incredible look and friendly user interface.
- It does not require installing and maintaining any databases. It does not depend on any proprietary online services.
- It manages files in the known git way.

## Getting started
### JavaFX Version (Git GUI)
#### Demo
##### Step 1:
Download/clone this repository.

##### Step 2:
Go to `demo/desktop appliaction` directory and run magit.bat

##### Step 3:
You can work on repository in three ways, either initialize new repository or import an existing one, the ways to do it mentioned below. In our demo, we will chose to **create a repository from XML schema** and select `demo\desktop appliaction\xml-schema-example\origin-medium.xml`. After confirming it a new repository will appear in `C:\repo1`

###### Initialize a new repository:
* `Start` ->`Create new repository` 

###### Load an existing repository:
* `Start` ->`Load repository by path` 

###### Create a repository from XML schema:
* `Start` ->`Load repository by XML file` 

##### Step 4:
Your repository is now loaded, you are now ready to use it in the regular way you use to. (i.e change or create files on `C:\repo1` and perform commit)


#### How it should look like:
##### Start of application window (you can set your theme skin to dark as showed: View->Dark theme):
![javafx-start](https://i.ibb.co/Yp6j6hV/magit-main.png "javafx-start")

##### A repository named repo1 has been loaded to M.A-Git system:
![magit-loaded-repo1](https://i.ibb.co/xgHwNJN/magit-loaded-repo1.png "magit-loaded-repo1")

 ##### Start menu features:
![magit-start](https://i.ibb.co/nPVtVvL/magit-start.png "magit-start")

##### Command menu featues:
![magit-commands-delts](https://i.ibb.co/YyVPcyR/magit-commands-delts.png "magit-commands-delts")

##### Screen shot of repo1 directory on file system:
![fs-repo1](https://i.ibb.co/92p8xKq/repo1-fs.png "fs-repo1")

##### Merge conflict solver:
![magit-merge conflict solver](https://i.ibb.co/qdS4f5T/magit-merge-conflict-solver.png "magit-merge conflict solver")

### WebApp Version
The web application developed to illustrate GitHub experience, The application supports:
* Creating a pull request between users.
* Getting notifications about relevant events occurring.
* Clone user repository feature.
* Chat feature.

#### Demo
##### Step 1:
Download/clone this repository.

##### Step 2:
Deploy `demo/web application/magitHub.war` file to Tomcat.

##### Step 3:
Sign-up by providing unique username.

##### Step 4:
In the main page, you will be able to upload new repositories schemas (XML files).
upload `demo/web application/ex3-large.xml` to check it out.

#### How it should look like:

##### The main page of the application
![magitWeb-main](https://i.ibb.co/7X10NGk/magit-Web-main.png "magitWeb-main")

##### Friend's repositories
![magitWeb-oran's repositories](https://i.ibb.co/wNbRMfr/magit-Web-oran-Repositoies.png "magitWeb-oran's repositories")

##### Magit chat system
![magitWeb-chat](https://i.ibb.co/vDqcknT/magit-Web-chat.png "magitWeb-chat")
