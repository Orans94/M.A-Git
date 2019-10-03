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

var userName = getUrlParameter('userName');
var repositoryName = getUrlParameter('repositoryName');
var commitSHA1 = getUrlParameter('commitSHA1');
var repositoryPath = "C:\\magit-ex3\\" + userName + "\\" + repositoryName;

$(function() {
    $.ajax({
        url: "/magitHub/pages/filemanager/commit",
        data: {"userName": userName, "repositoryName" : repositoryName, "commitSHA1" : commitSHA1},
        //timeout: 2000, TODO delete comment
        error: function () {
            console.log("no");
        },
        success: function (data) {
            var files = data.split("\n"); // TODO - maybe not \n
            
        }
    });
});