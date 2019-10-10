var username = getUrlParameter("username");
var repositoryName = getUrlParameter('repositoryName');
var isRepositoryCloned = getUrlParameter('isRepositoryCloned');


function onRowClick(tableId, callback) {
    var table = document.getElementsByClassName(tableId),
        rows = $("#tableRow"),
        i;
    for (i = 1; i < table[0].rows.length; i++) {
        table[0].rows[i].onclick = function (row) {
            return function () {
                callback(row);
            };
        }(table[0].rows[i]);
    }

}
$(function() { // onload function
    $('#backButton').on('click', function(e){
        e.preventDefault();
        window.location.href = "../main/main.html";
    });

    var wcStatus = $("#WCStatus");
    $.ajax({
       url:"/magitHub/repositoryInfo",
       data:{"requestType": "WCStatus", "username":username, "repositoryName": repositoryName},
        error: function () {
            console.log("no");
        },
        success: function (data) {
            console.log(data);
            wcStatus.empty();
            wcStatus.append(data);
            if(data.includes("Clean"))
            {
                wcStatus.css('background-color', 'green');
            }
            else
            {
                wcStatus.css('background-color', 'red');
            }
        }
    });

    var updateWC = function(){
        var url = "../filemanager/fileManager.html?username=" + username + "&repositoryName=" + repositoryName + "&commitSHA1=" + "nothing" + "&requestType="+"WC";
        window.location.href = url;
    };
    $("#updateWC").click(updateWC);

    var commitClick = function () {
        if(wcStatus[0].innerText.includes("Dirty"))
        {
            do {
                var commitMessage = prompt("Please enter commit message");
            }while(commitMessage == null || commitMessage === "");
            $.ajax({
               url: "/magitHub/repositoryInfo",
               data:{"requestType": "commit", "username": username, "repositoryName": repositoryName, "commitMessage": commitMessage},
                error: function () {
                   alert("Commit failed");
                    console.log("no");
                },
                success: function (data) {
                   alert("Committed successfully");
                    console.log(data);
                    window.location.reload(true);
                }
            });
        }
        else
        {
            alert("WC status is clean, nothing to commit");
        }
    };
    $("#commit").click(commitClick);

    $.ajax({
        url: "/magitHub/pages/main/user",
        data: {"isLoggedInUser": "FALSE", "username": username},
        //timeout: 2000,
        error: function () {
            console.log("no");
        },
        success: function (data) {
        console.log(data);
        var repositories = data.m_Engine.m_Repositories;
        $.each(repositories, function(key,value){
               if(value.m_Name === repositoryName)
               {
                   repository = value;
               }
            });

            var repository;


            var branches = repository.m_Magit.m_Branches;

            var commits = repository.m_Magit.m_Commits;
            var createBranchClick = function(){
                do {
                    var branchName = prompt("Please enter branch name");
                }while(branchName == null || branchName === "");
                $.ajax({
                    url : "../../repositoryInfo",
                    data: {"requestType": "createBranch", "branchName": branchName, "username" : username, "repositoryName": repositoryName},
                    error: function () {
                        console.log("no");
                    },
                    success: function (data) {
                        console.log("branch created");
                        var url = "repository.html?repositoryName="+repositoryName+"&username="+username;
                        window.location.href = url;
                    }
                })
            };
            $("#createNewBranchButton").click(createBranchClick);

            $.each(branches, function (key, value) {
                $(".branchTableBody").append('<tr>\n' +
                    '                        <td class="branchNameColumn">' + value.m_Name + '</td>\n' +
                    '                        <td class="isRemoteColumn">' + value.m_IsRemote + '</td>\n' +
                    '                        <td class="isTrackingColumn">' + value.m_IsTracking + '</td>\n' +
                    '                        <td class="pointedCommitColumn">' + value.m_CommitSHA1 + '</td>\n' +
                    '                        <td class="commitMessageColumn">'+ commits[value.m_CommitSHA1].m_Message +'</td>\n' +
                    '                    </tr>');
            });

            $.each(commits, function (key, value) {
                $(".commitTableBody").append('<tr id="tableRow">\n' +
                    '                        <td class="commitMessageColumn">' + value.m_Message + '</td>\n' +
                    '                        <td class="commitAuthorColumn">' + value.m_CommitAuthor + '</td>\n' +
                    '                        <td class="commitDateColumn">' + value.m_CommitDate + '</td>\n' +
                    '                        <td class="commitSHA1Column">' + key + '</td>\n' +
                    '                    </tr>');
            });

            onRowClick("commitTable", function (row){
            var commitSHA1 = row.getElementsByClassName("commitSHA1Column")[0].textContent;
            // redirect to filemanager page with parameters - username, repository, and commit sha1
            var url = "../filemanager/fileManager.html?username=" + username + "&repositoryName=" + repositoryName + "&commitSHA1=" + commitSHA1 + "&requestType="+"Commit";
            window.location.href = url;
        });
        }
    });
});