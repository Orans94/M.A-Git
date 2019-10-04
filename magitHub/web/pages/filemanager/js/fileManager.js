
var userName = getUrlParameter('userName');
var repositoryName = getUrlParameter('repositoryName');
var commitSHA1 = getUrlParameter('commitSHA1');
var repositoryPath = "C:\\magit-ex3\\" + userName + "\\" + repositoryName;

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
$(document).ready(function(){
    $('#fileName').focus();
    //$('#fileContent').autosize();

});
function showFileContent(file) {
    $.ajax({
        url: "/magitHub/pages/filemanager/fileContent",
        data: {"filePath": file, "userName" : userName, "repositoryName" : repositoryName},
        //timeout: 2000, TODO delete comment
        error: function () {
            console.log("no");
        },
        success: function (data) {
            $("#fileContent").append($(data));
            $("#fileName").append($(file));
        }
    });
}

$(function() {
    $.ajax({
        url: "/magitHub/pages/filemanager/commit",
        data: {"userName": userName, "repositoryName" : repositoryName, "commitSHA1" : commitSHA1},
        //timeout: 2000, TODO delete comment
        error: function () {
            console.log("no");
        },
        success: function (data) {
            var files = data.split("\n"), i; // TODO - maybe not \n
            for(i=0;i<files.length-1;i++) // notice - its length-1 because files also includes another cell of newline only
            {
                //TODO sending the parameter to showFileContent didnt work - need to fix it or find another way
                $("#fileTreeDemo_1").append($('<button class="commitFile" onclick="showFileContent(\''+files[i]+'\')">'+files[i]+'</button>'));
            }
        }
    });
});