
var username = getUrlParameter('username');
var repositoryName = getUrlParameter('repositoryName');
var commitSHA1 = getUrlParameter('commitSHA1');
var repositoryPath = "C:\\magit-ex3\\" + username + "\\" + repositoryName;

$(document).ready(function(){
    $('#fileName').focus();
    //$('#fileContent').autosize();

});

$(function() { //onload function
    var edit = function(){$("#fileContent").removeAttr('readonly');}
    $("#editButton").click(edit);
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

                //TODO sending the parameter to showFileContent didnt work - need to fix it or find another way
                //$("#fileTreeDemo_1").append($('<button class="commitFile" onclick="showFileContent(\''+files[i]+'\')">'+files[i]+'</button>'));
                var element = document.createElement("BUTTON");
                element.className = "commitFile";
                element.type = "button";
                element.innerHTML = files[i].replace(/(\r\n|\n|\r)/gm, "").trim();
                document.getElementById("fileTreeDemo_1").appendChild(element);
                document.getElementById("fileTreeDemo_1").appendChild(document.createElement("BR"));
                $(".commitFile:last").click({filePath : element.innerHTML}, fileButtonClick);
            }
        }

    });
});