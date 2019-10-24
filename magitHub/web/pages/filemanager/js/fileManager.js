function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
}

var username = getUrlParameter('username');
var repositoryName = getUrlParameter('repositoryName');
var commitSHA1 = getUrlParameter('commitSHA1');
var requestType = getUrlParameter('requestType');
var isRepositoryCloned = getUrlParameter("isRepositoryCloned");
var repositoryPath = "C:\\magit-ex3\\" + username + "\\" + repositoryName;
$(document).ready(function(){
    $('#fileName').focus();
});

$(function() { //onload function
    $('#backButton').on('click', function(e){
        e.preventDefault();
        var url = "../repository/repository.html?repositoryName=" + repositoryName + "&username=" + username + "&isRepositoryCloned=" + isRepositoryCloned;
        window.location.href = url;
    });
        if(requestType === "WC")
        {
            var edit = function () {
                $("#fileContent").removeAttr('readonly');
                $("#messageToUser").empty().append("The file is now editable");
                $("#messageToUser").css('color', 'red');
            };
            $("#editButton").click(edit);

            var createNewFileButton = function () {
                do {
                    var input = prompt("Enter the new file path");
                    //TODO - check path validation
                }while(input == null || input === "");
                $("#editButton").hide();
                $("#deleteFileButton").hide();
                $("#createNewFileButton").hide();
                $("#fileContent").removeAttr('readonly');

                $("#fileName").empty();
                $("#fileName").append(input);

                $("#fileContent").empty();
                $("#fileContent").attr("placeholder", "Please enter the new file content");
            };
            $("#createNewFileButton").click(createNewFileButton);

            var deleteFileButton = function () {
                var fileName = $("#fileName")[0].innerHTML;
                $.ajax({
                    url: "/magitHub/repositoryInfo",
                    data: {"requestType": "deleteFile", "username": username, "repositoryName": repositoryName, "fileName": fileName},
                    error: function (data) {
                        alert("File not found");
                        console.log("no");
                    },
                    success: function () {
                        alert("The file has been deleted successfully");
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
                            });

                            $(".commitFile:last").trigger("click");
                        })
                    }
            });
                //return false;
            };
            $("#deleteFileButton").click(deleteFileButton);

            var save = function () {
                $("#editButton").show();
                $("#deleteFileButton").show();
                $("#createNewFileButton").show();
                var pathToFile = repositoryPath + "\\" + $("#fileName")[0].innerHTML;
                var fileContent = $("#fileContent")[0].value;
                $.ajax({
                    method: 'POST',
                    url: "/magitHub/pages/filemanager/fileContent",
                    data: {"filePath": pathToFile, "fileContent": fileContent, "requestType": "Commit"},
                    error: function () {
                        alert("Editing\\creating the file has been failed");
                        console.log("no");
                    },
                    success: function () {
                        console.log("changed file");
                        alert("File has been edited\\created successfully");
                        window.location.reload(true);
                    }
                });
            };
            $("#saveButton").click(save);

            $("#deleteFileButton").show();
            $("#createNewFileButton").show();
            $("#editButton").show();
            $("#saveButton").show();


            $.ajax({
                url: "/magitHub/pages/filemanager/commit",
                data: {"username": username, "repositoryName" : repositoryName, "commitSHA1" : commitSHA1, "requestType": "workingCopy"},
                error: function () {
                    console.log("no");
                },
                success: function (data) {
                    var files = data.split("\n"), i; // TODO - maybe not \n
                    var fileButtonClick = function(event){
                        $.ajax({
                            url: "/magitHub/pages/filemanager/fileContent",
                            data: {"filePath": event.data.filePath, "username" : username, "repositoryName" : repositoryName, "commitSHA1": commitSHA1, "requestType": "WC"},
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

                        var fileName = files[i].replace(/(\r\n|\n|\r)/gm, "").trim();
                        var element = document.createElement("li");
                        element.innerHTML = '<button class="commitFile" type="button">'+ fileName + '</button>';
                        document.getElementById("fileTreeDemo_1").appendChild(element);
                        //document.getElementById("fileTreeDemo_1").appendChild(document.createElement("BR"));
                        $(".commitFile:last").click({filePath : fileName}, fileButtonClick);
                    }

                    $(".commitFile:last").trigger("click");
                }

            });

        }
        else
        { // COMMIT
            $("#deleteFileButton").hide();
            $("#createNewFileButton").hide();
            $("#editButton").hide();
            $("#saveButton").hide();


            $.ajax({
                url: "/magitHub/pages/filemanager/commit",
                data: {"username": username, "repositoryName" : repositoryName, "commitSHA1" : commitSHA1, "requestType": "Commit"},
                error: function () {
                    console.log("no");
                },
                success: function (data) {
                    var files = data.split("\n"), i; // TODO - maybe not \n
                    var fileButtonClick = function(event){
                        $.ajax({
                            url: "/magitHub/pages/filemanager/fileContent",
                            data: {"filePath": event.data.filePath, "username" : username, "repositoryName" : repositoryName, "commitSHA1": commitSHA1, "requestType": "Commit"},
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
                        var fileName = files[i].replace(/(\r\n|\n|\r)/gm, "").trim();
                        var element = document.createElement("li");
                        element.innerHTML = '<button class="commitFile" type="button">'+ fileName + '</button>';

                        /*var element = document.createElement("BUTTON");
                        element.className = "commitFile";
                        element.type = "button";
                        element.innerHTML = files[i].replace(/(\r\n|\n|\r)/gm, "").trim();*/
                        document.getElementById("fileTreeDemo_1").appendChild(element);
                        //document.getElementById("fileTreeDemo_1").appendChild(document.createElement("BR"));
                        $(".commitFile:last").click({filePath : fileName}, fileButtonClick);
                    }

                    $(".commitFile:last").trigger("click");
                }

            });
        }
    });

