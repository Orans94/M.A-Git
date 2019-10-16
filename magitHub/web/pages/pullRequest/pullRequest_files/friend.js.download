var username = getUrlParameter("username");


function notifyFriendAboutForkingItRepository(repositoryOwnerName, forkedRepositoryName, forkingUsername) {
    $.ajax({
        method: 'POST',
        data: {
            "notificationType" : "FORK",
            "repositoryOwnerName": repositoryOwnerName,
            "forkedRepositoryName": forkedRepositoryName,
            "forkingUsername": forkingUsername
        },
        url: "/magitHub/pages/friend/notifications",
        //timeout: 4000, TODO delete comment
        error: function (data) {
            alert("error was occurred while notifying repository has forked");
        },
        success: function () {
            console.log("notification about forking forwarded to server successfully");
        }
    });

}

$(function() { // onload function- load friend repository

    $.ajax({
        method:'get',
        url: "/magitHub/pages/main/user",
        data: {"isLoggedInUser" : "FALSE",
                "username" : username},
        //timeout: 4000, TODO delete comment
        error: function(e) {
            alert("Unable to load friend repositories")
        },
        success: function (data) {
            // data represent the user object include all it repositories

            var username = data.m_Name;
            var repositoriesArray = data.m_Engine.m_Repositories;
            $(".header-title").text(username + "'s Repositories");
            updateRepositoriesCardsInHTML(repositoriesArray, username, "Fork repository", function (event) {
                // when user forking friend's repository below code execute
                var userRepositoryNameInput = prompt("Please enter repository name for the forked repository");
                if (!(userRepositoryNameInput == null || userRepositoryNameInput === "")){
                    $.ajax({
                        method: 'POST',
                        data: {
                            "repositoryOwnerName": event.data.username,
                            "repositoryNameInOwner": event.data.repositoryName,
                            "repositoryNameToFork": userRepositoryNameInput
                        },
                        url: "/magitHub/pages/friend/fork",
                        //timeout: 4000, TODO delete comment
                        error: function (data) {
                            alert("error was occurred while forking " + data.repositoryName + "from " + data.repositoryOwnerName);
                        },
                        success: function (data) {
                            // data represent loggedInUsername and repositoryName
                            notifyFriendAboutForkingItRepository(data.repositoryOwnerName, data.repositoryName, data.loggedInUsername);
                            alert("repository " + data.repositoryName + " forked successfully to you. The forked repository name is " +  data.repositoryNameToFork);
                            var url = "../main/main.html";
                            window.location.href = url;
                        }
                    });
                }
            });
        }
    });
});