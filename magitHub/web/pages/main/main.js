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

            var counter = 0;
            var repositoriesMap = data.m_Engine.m_Repositories;
            $.each(repositoriesMap || [], function(index, repositories) {

                //create a new <option> tag with a value in it and
                //appeand it to the #userslist (div with id=userslist) element
                $('<li>' + repositories + '</li>').appendTo($("#repositories"));
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
            timeout: 4000,
            error: function(e) {
                console.log("error");
            },
            success: function(r) {
                console.log("success");
                //$("#result").text(r);
            }
        });

        // return value of the submit operation
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    })
})
