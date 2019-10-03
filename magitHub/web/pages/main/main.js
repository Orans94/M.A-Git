const USER_URL = buildUrlWithContextPath("user");
var numberOfRepositories = 0;


$(function() { // onload function
    console.log("check2");

    $.ajax({
        url: "/magitHub/pages/main/user",
        data:{"isLoggedInUser" : "TRUE"},
        //timeout: 2000, TODO delete comment
        error: function() {
            console.log("no");
        },
        success: function(data) {
            console.log(data);
            var repositoriesArray = data.m_Engine.m_Repositories;

            $.each( repositoriesArray, function (key, value){
                if (numberOfRepositories % 3 === 0){
                    var rowElem = $('<div class="row">');
                    $( ".row.mb-4").before(rowElem);
                }

                var repositoryName = value.m_Name;
                var repositoryActiveBranchName = "Active branch name: " + value.m_Magit.m_Head.m_ActiveBranch.m_Name + '<br>';
                var repositoryActiveBranchCommit = "Active branch pointed commit: " + value.m_Magit.m_Head.m_ActiveBranch.m_CommitSHA1.substring(0,7) + '<br>';
                //var commitMessage = "Commit message: " + value.m_Magit.m_Head.m_ActiveBranch.m_Message + '\n';
                var commitDetails = repositoryActiveBranchName + repositoryActiveBranchCommit;

                $(".row").eq(-2).append( $('<div class="col-lg-4 mb-4">'));
                $(".col-lg-4:last").append( $('<div class="card h-100">'));
                $(".card.h-100:last").append($('<h4 class="card-header" id="repositoryName" value=""' + repositoryName + '>' + repositoryName + '</h4>'));
                //$(".card-header:last").text(repositoryName)
                $(".card.h-100:last").append($('<div class="card-body">'));
                $(".card-body:last").append( $('<p class="card-text">' + commitDetails + '</p>'));
                //$(".card-text:last").text(commitDetails);
                $(".card.h-100:last").append($('<div class="card-footer">'));
                $(".card-footer:last").append( $('<a href="#" class="btn btn-primary" id="chooseRepo">Choose repository</a>'));
                $("#chooseRepo").click(function () {
                    //var name = $("#repositoryName").val();
                    var url = "../repository/repository.html?repositoryName=" + repositoryName + "&userName=" + data.m_Name;
                    window.location.href = url;
                });


                numberOfRepositories++;
            });
        }
    });
});

$(function() { // onload...do
    $("#uploadRepositoryForm").submit(function() {

        var file1 = this[0].files[0];

        var formData = new FormData();
        formData.append("fake-key-1", file1);

        $.ajax({
            method:'POST',
            data: formData,
            url: this.action,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            //timeout: 4000, TODO delete comment
            error: function(xhr, status, error) {
                alert("Repository upload failed: " + xhr.responseText);
            },
            success: function(r) {
                // a new repository loaded successfully
                location.reload();
            }
        });

        // return value of the submit operation
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    })
})
