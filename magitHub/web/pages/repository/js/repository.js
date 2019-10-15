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

$(function () { // onload function
    $('#backButton').on('click', function (e) {
        e.preventDefault();
        window.location.href = "../main/main.html";
    });

    if(!(isRepositoryCloned === "true"))
    {
        $("#pullButton").hide();
        $("#pullRequestButton").hide();
        $("#pushButton").hide();
    }


    var wcStatus = $("#WCStatus");
    $.ajax({
        url: "/magitHub/repositoryInfo",
        data: {"requestType": "WCStatus", "username": username, "repositoryName": repositoryName},
        error: function () {
            console.log("no");
        },
        success: function (data) {
            console.log(data);
            wcStatus.empty();
            wcStatus.append(data);
            if (data.includes("Clean")) {
                wcStatus.css('background-color', 'green');
            } else {
                wcStatus.css('background-color', 'red');
            }
        }
    });

    var pullRequestClick = function () {
        var message = prompt("Please enter pull request message");

        do {
            var baseBranch = prompt("Please enter base branch name");
        } while (baseBranch == null || baseBranch === "");

        do {
            var targetBranch = prompt("Please enter target branch name");
        } while (targetBranch == null || targetBranch === "");

        $.ajax({
            url: "/magitHub/pullRequest",
            data: {"repositoryName" : repositoryName, "baseBranch": baseBranch, "targetBranch": targetBranch, "Message": message},
            error: function () {
                console.log("no");
            },
            success: function (data) {
                alert("Pull request sent successfully");
            }
        });
    };
    $("#pullRequestButton").click(pullRequestClick);

    var pullClick = function () {
        if(wcStatus[0].innerHTML.includes("Dirty"))
        {
            alert("WC status is dirty, can not pull");
        }
        else {
            $.ajax({
                url: "../../repositoryInfo",
                data: {"requestType": "activeBranch", "username": username, "repositoryName": repositoryName},
                //timeout: 2000,
                error: function () {
                    console.log("error while getting active branch");
                },
                success: function (data) {
                    var br = JSON.parse(data);
                    if(br.m_IsTracking) {
                        $.ajax({
                            url: "../../repositoryInfo",
                            data: {
                                "requestType": "isPushRequired",
                                "username": username,
                                "repositoryName": repositoryName
                            },
                            //timeout: 2000,
                            error: function () {
                                console.log("error while checking if push required");
                            },
                            success: function (data) {
                                if (data === "true") {
                                    alert("Push has to be made before pulling");
                                } else {
                                    $.ajax({
                                        method: "POST",
                                        url: "../../repositoryInfo",
                                        data: {
                                            "requestType": "Pull",
                                            "username": username,
                                            "repositoryName": repositoryName
                                        },
                                        //timeout: 2000,
                                        error: function () {
                                            console.log("error while checking if push required");
                                        },
                                        success: function (data) {
                                            alert("Pulled successfully");
                                            window.location.reload();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else
                    {
                        alert("The head branch is not tracking after an RB");
                    }
                }
            });
        }
    };
    $("#pullButton").click(pullClick);

    var pushClick = function () {
        $.ajax({
            url: "../../repositoryInfo",
            data: {
                "requestType": "Push",
                "username": username,
                "repositoryName": repositoryName
            },
            //timeout: 2000,
            error: function () {
                console.log("error on push");
            },
            success: function (data) {
                alert("Pushed successfully");
            }
            });
    };
    $("#pushButton").click(pushClick);

    var updateWC = function () {
        var url = "../filemanager/fileManager.html?username=" + username + "&repositoryName=" + repositoryName + "&commitSHA1=" + "nothing" + "&requestType=" + "WC" + "&isRepositoryCloned=" + isRepositoryCloned;
        window.location.href = url;
    };
    $("#updateWC").click(updateWC);

    var commitClick = function () {
        if (wcStatus[0].innerText.includes("Dirty")) {
            do {
                var commitMessage = prompt("Please enter commit message");
            } while (commitMessage == null || commitMessage === "");
            $.ajax({
                url: "/magitHub/repositoryInfo",
                data: {
                    "requestType": "commit",
                    "username": username,
                    "repositoryName": repositoryName,
                    "commitMessage": commitMessage
                },
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
        } else {
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
            $.each(repositories, function (key, value) {
                if (value.m_Name === repositoryName) {
                    repository = value;
                }
            });

            var repository;


            var branches = repository.m_Magit.m_Branches;
            var commits = repository.m_Magit.m_Commits;

            $.each(branches, function (key, value) {
                $(".branchTableBody").append('<tr>\n' +
                    '                        <td class="branchNameColumn">' + value.m_Name + '</td>\n' +
                    '                        <td class="isRemoteColumn">' + value.m_IsRemote + '</td>\n' +
                    '                        <td class="isTrackingColumn">' + value.m_IsTracking + '</td>\n' +
                    '                        <td class="pointedCommitColumn">' + value.m_CommitSHA1 + '</td>\n' +
                    '                        <td class="commitMessageColumn">' + commits[value.m_CommitSHA1].m_Message + '</td>\n' +
                    '                    </tr>');
            });

            $.ajax({
                url: "../../repositoryInfo",
                data: {"requestType": "activeBranch", "username": username, "repositoryName": repositoryName},
                //timeout: 2000,
                error: function () {
                    console.log("error while getting active branch");
                },
                success: function (data) {
                    var br = JSON.parse(data);
                    $.each($(".branchNameColumn"), function ()
                    {
                        if(this.innerHTML === br.m_Name)
                        {
                            this.append("(HEAD)");
                        }
                    })
                 }
                });

            $.each(commits, function (key, value) {
                $(".commitTableBody").append('<tr id="tableRow">\n' +
                    '                        <td class="commitMessageColumn">' + value.m_Message + '</td>\n' +
                    '                        <td class="commitAuthorColumn">' + value.m_CommitAuthor + '</td>\n' +
                    '                        <td class="commitDateColumn">' + value.m_CommitDate + '</td>\n' +
                    '                        <td class="commitSHA1Column">' + key + '</td>\n' +
                    '                    </tr>');
            });

            onRowClick("commitTable", function (row) {
                var commitSHA1 = row.getElementsByClassName("commitSHA1Column")[0].textContent;
                // redirect to filemanager page with parameters - username, repository, and commit sha1
                var url = "../filemanager/fileManager.html?username=" + username + "&repositoryName=" + repositoryName + "&commitSHA1=" + commitSHA1 + "&requestType=" + "Commit" + "&isRepositoryCloned=" + isRepositoryCloned;
                window.location.href = url;
            });

            var createBranchClick = function () {
                do {
                    var branchName = prompt("Please enter branch name");
                } while (branchName == null || branchName === "");
                $.ajax({
                    url: "../../repositoryInfo",
                    data: {
                        "requestType": "createBranch",
                        "branchName": branchName,
                        "username": username,
                        "repositoryName": repositoryName
                    },
                    error: function () {
                        console.log("no");
                    },
                    success: function (data) {
                        console.log("branch created");
                        var url = "repository.html?repositoryName=" + repositoryName + "&username=" + username + "&isRepositoryCloned=" + isRepositoryCloned;
                        window.location.href = url;
                    }
                })
            };
            $("#createNewBranchButton").click(createBranchClick);

            var checkoutButton = $("#checkoutButton");
            var checkoutClick = function ()
            {
            if (wcStatus[0].innerText.includes("Clean"))
            {
                do{
                    var branchName = prompt("Please enter branch name");
                }while(branchName == null || branchName === "");
                    $.ajax({
                        url: "../../repositoryInfo",
                        data: {
                            "requestType": "isBranchExist",
                            "branchName": branchName,
                            "username": username,
                            "repositoryName": repositoryName
                        },
                        error: function () {
                            console.log("no");
                        },
                        success: function (data) {
                                if (data === "true") {
                                    //checkout
                                    $.ajax({
                                        method: "POST",
                                        url: "../../repositoryInfo",
                                        data: {
                                            "requestType": "checkout",
                                            "branchName": branchName,
                                            "username": username,
                                            "repositoryName": repositoryName
                                        },
                                        error: function () {
                                            console.log("no");
                                        },
                                        success: function (data) {
                                            alert("Checked out to branch " + branchName + " successfully");
                                            window.location.reload(true);
                                        }
                                    });
                                } else if (data === "active") {
                                    alert("Head is already on branch " + branchName);
                                } else if (data === "RB") {
                                    if (confirm("The chosen branch is a remote branch.\nWould you like to create a Remote Tracking Branch and checkout to him?")) {
                                        //create rtb
                                        $.ajax({
                                            method: "POST",
                                            url: "../../repositoryInfo",
                                            data: {
                                                "requestType": "checkoutRTB",
                                                "branchName": branchName,
                                                "username": username,
                                                "repositoryName": repositoryName
                                            },
                                            error: function () {
                                                console.log("no");
                                            },
                                            success: function (data) {
                                                alert("Checked out to branch " + branchName + " successfully");
                                                window.location.reload(true);
                                            }
                                        });
                                    } else {
                                        alert("The system did not checked out");
                                    }
                                } else {
                                    alert("Branch " + branchName + " does not exist");
                                }
                        }
                    });
            }
            else
            {
                //WC is dirty
                alert("The system can not checkout when the WC status is dirty");
            }
                };
            checkoutButton.click(checkoutClick);
        }
    });
});