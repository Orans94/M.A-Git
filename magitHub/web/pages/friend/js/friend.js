var username = getUrlParameter("username");


$(function() { // onload function- load friend repository

    $.ajax({
        method:'get',
        url: "/magitHub/pages/main/user",
        data: {"isLoggedInUser" : "FALSE",
                "username" : username},
        //timeout: 4000, TODO delete comment
        error: function(e) {
            alert("Unable to load friend repositories")
        },
        success: function(data) {
            // data represent the user object include all it repositories

            var repositoriesArray = data.m_Engine.m_Repositories;
            var username = data.m_Name;
            updateRepositoriesCardsInHTML(repositoriesArray, username, "Fork repository");
            $(".header-title").text(username + "'s Repositories");
        }
    });
})