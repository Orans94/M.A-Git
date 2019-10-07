
var username = getUrlParameter('username');
var repositoryName = getUrlParameter('repositoryName');
var commitSHA1 = getUrlParameter('commitSHA1');
var repositoryPath = "C:\\magit-ex3\\" + username + "\\" + repositoryName;
$(document).ready(function(){
    $('#fileName').focus();
});

$(function() { //onload function
    $.ajax({
        url: "/magitHub/repositoryInfo",
        data: {"requestType": "activeBranch", "username": username, "repositoryName": repositoryName},
        error: function () {
            console.log("no");
        },
        success: function (data) {
            var activeBranchCommitSHA1 = JSON.parse(data).m_CommitSHA1;
            if(commitSHA1 === activeBranchCommitSHA1)
            {
                var edit = function () {
                    $("#fileContent").removeAttr('readonly');
                };
                $("#editButton").click(edit);

                var createNewFileButton = function () {
                    do {
                        var input = prompt("Enter the new file name");
                    }while(input == null || input === "");

                    $("#deleteFileButton").hide();
                    $("#createNewFileButton").hide();

                    $("#fileName").empty();
                    $("#fileName").append(input);

                    $("#fileContent").empty();
                    $("#fileContent").attr("placeholder", "Please enter the new file content");
                };
                $("#createNewFileButton").click(createNewFileButton);

                var deleteFileButton = function () {
                    var fileName = $("#fileName")[0].innerHTML
                    $.ajax({
                        url: "/magitHub/repositoryInfo",
                        data: {"requestType": "deleteFile", "username": username, "repositoryName": repositoryName, "fileName": fileName},
                        error: function () {
                            console.log("no");
                        },
                        success: function () {
                            console.log("deleted");
                            $.each($(".commitFile"), function () {
                                if(this.innerHTML === $("#fileName")[0].innerHTML)
                                {
                                    this.parentNode.removeChild(this);
                                }

                                $.each($("#fileTreeDemo_1"), function () {
                                    if(this.innerHTML === $("#fileName")[0].innerHTML)
                                    {
                                        this.parentNode.removeChild(this);
                                    }
                                })

                                $(".commitFile:last").trigger("click");
                            })
                        }
                });
                    return false;
                };
                $("#deleteFileButton").click(deleteFileButton);

                var save = function () {
                    $("#deleteFileButton").show();
                    $("#createNewFileButton").show();
                    var pathToFile = repositoryPath + "\\" + $("#fileName")[0].innerHTML;
                    var fileContent = $("#fileContent")[0].value;
                    $.ajax({
                        method: 'POST',
                        url: "/magitHub/pages/filemanager/fileContent",
                        data: {"filePath": pathToFile, "fileContent": fileContent, "commitSHA1": commitSHA1},
                        error: function () {
                            console.log("no");
                        },
                        success: function () {
                            console.log("changed file");
                        }
                    });
                    $("#saveButton").click(save);

                    var url = "fileManager.html?username=" + username + "&repositoryName=" + repositoryName + "&commitSHA1=" + commitSHA1;
                    window.location.href = url;
                };

                $("#deleteFileButton").show();
                $("#createNewFileButton").show();
                $("#editButton").show();
                $("#saveButton").show();
            }
            else
            {
                $("#deleteFileButton").hide();
                $("#createNewFileButton").hide();
                $("#editButton").hide();
                $("#saveButton").hide();
            }
        }
    });


    $.ajax({
        url: "/magitHub/pages/filemanager/commit",
        data: {"username": username, "repositoryName" : repositoryName, "commitSHA1" : commitSHA1},
        //timeout: 2000, TODO delete comment
        error: function () {
            console.log("no");
        },
        success: function (data) {
            var files = data.split("\n"), i; // TODO - maybe not \n
            var fileButtonClick = function(event){
                $.ajax({
                    url: "/magitHub/pages/filemanager/fileContent",
                    data: {"filePath": event.data.filePath, "username" : username, "repositoryName" : repositoryName},
                    error: function () {
                        console.log("no");
                    },
                    success: function (data) {
                        $("#fileContent").empty();
                        $("#fileContent").append(data);
                        $("#fileName").empty();
                        $("#fileName").append(event.data.filePath);
                    }
                });
            };
            for(i=0;i<files.length-1;i++) // notice - its length-1 because files also includes another cell of newline only
            {

                var element = document.createElement("BUTTON");
                element.className = "commitFile";
                element.type = "button";
                element.innerHTML = files[i].replace(/(\r\n|\n|\r)/gm, "").trim();
                document.getElementById("fileTreeDemo_1").appendChild(element);
                document.getElementById("fileTreeDemo_1").appendChild(document.createElement("BR"));
                $(".commitFile:last").click({filePath : element.innerHTML}, fileButtonClick);
            }

            $(".commitFile:last").trigger("click");
        }

    });
});