var chatVersion = 0;
var refreshRate = 1500; //milli seconds

//users = a list of usernames, essentially an array of javascript strings:
// ["moshe","nachum","nachche"...]

//entries = the added chat strings represented as a single string
function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");

    // add the relevant entries
    $.each(entries || [], appendChatEntry);

    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $(".panel-body");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");

    // scroll down page
    $("html, body").animate({ scrollTop: $(document).height() }, 1000);
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatform").before(entryElement);
}

function createChatEntry (entry){
    return $('<li class="media"><a href="#" class="pull-left"><img src="https://bootdey.com/img/Content/user_3.jpg" alt="" class="img-circle"></a><div class="media-body"><span class="text-muted pull-right"><small class="text-muted"></small></span><strong class="text-success">' + entry.username + '</strong><p>' + entry.chatString + '</p></div></li>');
}


//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well
function ajaxChatContent() {
    $.ajax({
        url: "/magitHub/pages/chat/chatUtils",
        data: {"chatversion" : chatVersion,
              "requestType" : "chatContent"
        },
        dataType: 'json',
        success: function(data) {
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do
    //add a function to the submit event
    $("#chatform").submit(function() {
        var formInput = {};
        $.each($('#chatform').serializeArray(), function(i, field) {
            formInput[field.name] = field.value;
        });

        $.ajax({
            data: {
                "requestType": "sendMessage",
                "userstring": formInput.userstring
            },
            url: "/magitHub/pages/chat/chatUtils",
            timeout: 2000,
            error: function() {
                console.error("Failed to submit");
            },
            success: function(r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
            }
        });

        $("#userstring").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});

function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}

//activate the timer calls after the page is loaded
$(function() {
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxChatContent();
});