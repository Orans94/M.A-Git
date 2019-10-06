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
            updateRepositoriesCardsInHTML(repositoriesArray, data.m_Name, "Choose repository");
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

$(function() { // onload function- update users list in side bar

    $.ajax({
        method:'get',
        url: "/magitHub/pages/main/usersList",
        data: {"onlyActiveUsers" : "TRUE"},
        //timeout: 4000, TODO delete comment
        error: function(e) {
            alert("Unable to load users list in side bar")
        },
        success: function(data) {
            // data represent an array of users that have at least 1 repository
            var url;
            var userItemElem;
            var friendClickFunc = function(event) {
                url = "../friend/friend.html?username=" + event.data.friendName;
                window.location.href = url;
            };

            for (var i = 0; i < data.length ; i++){
                $(".side-bar").append($('<li class="list-group-item pl-3 py-2 user-item">'));
                userItemElem = $(".user-item:last");
                userItemElem.append($('<a href="#"><i class="fa fa-user-o" aria-hidden="true"><span class="ml-2 align-middle">' + data[i] + '</span></i></a>'));
                userItemElem.click({friendName : data[i]}, friendClickFunc);

                }
            }
        });
    });