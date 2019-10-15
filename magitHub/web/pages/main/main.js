const USER_URL = buildUrlWithContextPath("user");


$(function() { // onload function- load repositories cards
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
            updateRepositoriesCardsInHTML(repositoriesArray, data.m_Name, "Choose repository", function(event) {
                    var url = "../repository/repository.html?repositoryName=" + event.data.repositoryName + "&username=" + event.data.username + "&isRepositoryCloned=" + event.data.isRepositoryCloned;
                    window.location.href = url;
                });
        }
    });
});

$(function() { // onload function- attach functionality to upload repository button
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
                location.reload();
                alert("a new repository loaded successfully")
            }
        });

        // return value of the submit operation
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    })
});