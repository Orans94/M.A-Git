const USER_URL = buildUrlWithContextPath("user");

$(function() { // onload function
    console.log("check2");

    $.ajax({
        url: "/magitHub/pages/main/user",
        data:{"isLoggedInUser" : "TRUE"},
        timeout: 2000,
        error: function() {
            console.log("no");
        },
        success: function(data) {
            console.log(data);
            $('<p>' + 'Welcome ' + data.m_Name + '</p>').appendTo($("#username"));

            var repositoriesMap = data.m_Engine.m_Repositories;
            $.each(repositoriesMap || [], function(index, repositories) {
                //create a new <option> tag with a value in it and
                //appeand it to the #userslist (div with id=userslist) element
                $('<li>' + repositories + '</li>').appendTo($("#repositories"));
            });
        }
    });

});