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

var userName = getUrlParameter("userName");
var repositoryName = getUrlParameter('repositoryName');
$(function() { // onload function
    $.ajax({
        url: "/magitHub/pages/main/user",
        data: {"isLoggedInUser": "FALSE", "userName": userName},
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

            $.each(branches, function (key, value) {
                $(".branchTableBody").append('<tr>\n' +
                    '                        <td class="branchNameColumn"> ' + value.m_Name + ' </td>\n' +
                    '                        <td class="isRemoteColumn"> ' + value.m_IsRemote + ' </td>\n' +
                    '                        <td class="isTrackingColumn"> ' + value.m_IsTracking + ' </td>\n' +
                    '                        <td class="pointedCommitColumn"> ' + value.m_CommitSHA1 + ' </td>\n' +
                    '                        <td class="commitMessageColumn">' + commits[value.m_CommitSHA1].m_Message + '</td>\n' +
                    '                    </tr>');
            });

            var table = document.getElementById("branchTable"),
                rows = table.getElementsByTagName("tr"),
                i;
            for(i=0;i<)
            $.each(commits, function (key, value) {
                $(".commitTableBody").append('<tr>\n' +
                    '                        <td class="commitMessageColumn">' + value.m_Message + '</td>\n' +
                    '                        <td class="commitAuthorColumn"> ' + value.m_CommitAuthor + ' </td>\n' +
                    '                        <td class="commitDateColumn"> ' + value.m_CommitDate + ' </td>\n' +
                    '                        <td class="commitSHA1Column"> ' + key + ' </td>\n' +
                    '                    </tr>');
            });
        }
    });
});