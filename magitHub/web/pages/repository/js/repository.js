var username = getUrlParameter("username");
var repositoryName = getUrlParameter('repositoryName');

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
            var url = "../filemanager/fileManager.html?username=" + username + "&repositoryName=" + repositoryName + "&commitSHA1=" + commitSHA1;
            window.location.href = url;
        });
        }
    });
});