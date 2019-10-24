var notificationsVersion;
var usersListVersion;
var refreshRate = 2000; //milli seconds

// import navigation, users sidebar and notification sidebar
$(function(){
    $("#navigation-import").load("../../common/navigation.html", function () {
        $("#notificationsSidebar-import").load("../../common/notificationsSidebar.html", function () {
            configureNavigationLinks();
            loadNotificationsAndInitNotifictionsVersion();
            $(".toggle").click(function () {
                console.log("toggling sidebar");
                $(".notification-sidebar").toggleClass('active');
                var notificationButtonElem = $(".notification-sidebar.active");
                if (notificationButtonElem.length > 0) {
                    // notification side-bar open now
                    $(this).css({'animation-iteration-count' : '1'});
                    updateNotificationsLastVersionSeen();
                }
                else{
                    $(".new-notification").css({'background-color' : '#0d0d0d'}).removeClass("new-notification");
                }

            });
            $(".cancel").click(function () {
                console.log("toggling visibility");
                $(this).parent().toggleClass('gone');

            });
        });
    });

    $("#usersSidebar-import").load("../../common/usersSidebar.html", function () {
        //The users list is refreshed automatically every refreshRate defined above
        // update users list and active timer for updating users list
        ajaxUsersListContent();

        $(".users-list-link").click(function () {
            var sideBarElement = $(".side-bar");
            sideBarElement.toggleClass('active');
            var usersListElem = $(".side-bar.active");
            if (usersListElem.length > 0) {
                // hide users list
                sideBarElement.css({'visibility': 'hidden'});
                $(this).text("Show Users List")
            } else {
                // show users list
                sideBarElement.css({'visibility': ''});
                $(this).text("Hide Users List")
            }
        })
    });


});


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


function extractLatestCommit(commits) {
    var commitsArray = [];
    $.each( commits, function (key, value){
        commitsArray.push({
            "key" : key,
            "value" : value
        })
    });

    commitsArray.sort(function(a,b){
        var c = new Date(a.value.m_CommitDate);
        var d = new Date(b.value.m_CommitDate);
        return d-c;
    });

    return commitsArray[0];

}

function extractNumberOfRepositoryBranches(branches) {
    var branchesCounter = 0;

    $.each( branches, function (key, value){
        branchesCounter++;
    });

    return branchesCounter;
}


function updateRepositoriesCardsInHTML(repositoriesArray, username, chooseOrFork, chooseOrForkClickMethod){
    var numberOfRepositories = 0;
    $.each( repositoriesArray, function (key, value){
        if (numberOfRepositories % 3 === 0){
            var rowElem = $('<div class="row">');
            $( ".row.mb-4").before(rowElem);
        }

        var repositoryName = value.m_Name;
        var magitObj = value.m_Magit;
        var repositoryActiveBranchName = "Active branch : " + magitObj.m_Head.m_ActiveBranch.m_Name + '<br>';
        var numberOfRepositoryBranches = "Branches amount : " + extractNumberOfRepositoryBranches(magitObj.m_Branches) + '<br>';
        var latestCommit = extractLatestCommit(magitObj.m_Commits);
        var latestCommitDate = "Latest commit date : " + latestCommit.value.m_CommitDate + '<br>';
        var latestCommitMessage = "Latest commit message : " +  latestCommit.value.m_Message + '<br>';

        var commitDetails = repositoryActiveBranchName + numberOfRepositoryBranches + latestCommitDate + latestCommitMessage;
        var isRepositoryCloned = value.m_RemoteRepositoryPath !== undefined;

        $(".row").eq(-2).append( $('<div class="col-lg-4 mb-4">'));
        $(".col-lg-4:last").append( $('<div class="card h-100">'));
        $(".card.h-100:last").append($('<h4 class="card-header" id="repositoryName" value=""' + repositoryName + '>' + repositoryName + '</h4>'));
        $(".card.h-100:last").append($('<div class="card-body">'));
        $(".card-body:last").append( $('<p class="card-text">' + commitDetails + '</p>'));
        $(".card.h-100:last").append($('<div class="card-footer">'));
        $(".card-footer:last").append( $('<a href="#" class="btn btn-primary chooseRepo">' + chooseOrFork + '</a>'));
        $(".chooseRepo:last").click({repositoryName: repositoryName, username: username, isRepositoryCloned: isRepositoryCloned}, chooseOrForkClickMethod);
        numberOfRepositories++;
    });
}

function configureNavigationLinks() {
    $(".logo-link").attr("href", "/magitHub/pages/main/main.html");
    $(".home-link").attr("href", "/magitHub/pages/main/main.html");
    $(".chat-link").attr("href", "/magitHub/pages/chat/chat.html");
    $(".pr-link").attr("href", "/magitHub/pages/pullRequest/pullRequest.html");
    $(".logout-link").attr("href", "/magitHub/pages/main/logout");

}


function loadNotificationsAndInitNotifictionsVersion() {
    $.ajax({
        method: 'GET',
        data: {
            "notificationType": "NOTIFICATIONS_VERSION"
            , "notificationsVersion": "INITIALIZE"
        },
        url: "/magitHub/pages/friend/notifications",
        error: function (data) {
            console.log("error was occurred while getting notifications version");
        },
        success: function (data) {
            notificationsVersion = data.lastVersionSeen;
            appendToNotificationArea(data.seenNotifications, false);
        }
    })
}


function appendToNotificationArea(newNotifications, toMarkAsNewNotifications) {
    $.each(newNotifications || [], function(index, entry){
        var entryElement = createNotificationElement(entry, toMarkAsNewNotifications);
        $(".notification-title").after(entryElement)
    });

    if (toMarkAsNewNotifications) {
        $(".new-notification").css({'background-color': '#191970'});
        blinkNotificationsButton()
    }
}


function getCurrentDateTimeString() {
    var today = new Date();
    var date = today.getDate()+ '.' + (today.getMonth()+1) + '.' + today.getFullYear();
    var time = today.getHours() + ":" + today.getMinutes();
    return date + ' at ' + time;
}

function createNotificationElement (entry, toMarkAsNewNotifications){
    var elementResult;
    var currentDateTime = getCurrentDateTimeString();

    if (toMarkAsNewNotifications) {
        elementResult = '<a href="#" role="button" style="text-decoration:none"> <div class="notibox new-notification">' + entry.m_NotificationDetails + '<div class="time" style="color: white; font-size: 8px; margin-left: 7.5em;">' + currentDateTime + '</div> <div class="cancel">✕</div> </div> </a>';
    } else{
        elementResult = '<a href="#" role="button" style="text-decoration:none"> <div class="notibox">' + entry.m_NotificationDetails + '<div class="time" style="color: white; font-size: 8px; margin-left: 7.5em;">' + currentDateTime + '</div> <div class="cancel">✕</div> </div> </a>';
    }

    return elementResult;
}

function blinkNotificationsButton() {
    var notificationButtonElem = $(".notification-sidebar.active");
    if (notificationButtonElem.length === 0){
        // notification arrived and notification side-bar is closed
        $(".toggle").css({'animation' : 'changeColor ease'
            ,'animation' : 'changeColor ease'
            ,'animation-iteration-count' : 'infinite'
            ,'animation-duration' : '1s'
            ,'animation-fill-mode' : 'both'});
    }

}

function markNewNotifications() {
    var notificationButtonElem = $(".notification-sidebar.active");
    if (notificationButtonElem.length === 0){
        // notification arrived and notification side-bar is closed
        // marking new notification background
        $(".new-notification").css({'background-color' : '#191970'});
    }
}

function ajaxNotificationsContent() {
    $.ajax({
        method: 'GET',
        data: {"notificationType" : "NOTIFICATIONS_VERSION"
            ,"notificationsVersion" : notificationsVersion
        },
        url: "/magitHub/pages/friend/notifications",
        error: function (data) {
            console.log("error was occurred in ajaxNotificationsContent");
            triggerAjaxNotificationsContent();
        },
        success: function (data) {
            if (notificationsVersion !== data.updatedNotificationsVersion){
                // new notifications arrived to user
                notificationsVersion = data.updatedNotificationsVersion;
                appendToNotificationArea(data.newNotifications, true);
                blinkNotificationsButton();
                markNewNotifications();
            }
            triggerAjaxNotificationsContent();
        }
    });
}

function ajaxUsersListContent(){
    $.ajax({
        method:'get',
        url: "/magitHub/pages/main/usersList",
        data: {"requestType" : "usersList"
            ,"onlyActiveUsers" : "TRUE"},
        error: function(e) {
            console.log("Unable to load users list in side bar");
            triggerUsersListContent();
        },
        success: function(data) {
            // data represent an array of users that have at least 1 repository
            if (data.length !== usersListVersion) {
                var url;
                var userItemElem;
                var sideBarElement = $(".side-bar");
                var friendClickFunc = function (event) {
                    url = "../friend/friend.html?username=" + event.data.friendName;
                    window.location.href = url;
                };

                sideBarElement.empty();
                for (var i = 0; i < data.length; i++) {
                    sideBarElement.append($('<li class="list-group-item pl-3 py-2 user-item">'));
                    userItemElem = $(".user-item:last");
                    userItemElem.append($('<a href="#"><i class="fa fa-user-o" aria-hidden="true"><span class="ml-2 align-middle">' + data[i] + '</span></i></a>'));
                    userItemElem.click({friendName: data[i]}, friendClickFunc);
                }

                usersListVersion = data.length;
            }
            triggerUsersListContent();
        }
    });

}

function triggerAjaxNotificationsContent() {
    setTimeout(ajaxNotificationsContent, refreshRate);
}

function triggerUsersListContent() {
    setTimeout(ajaxUsersListContent, refreshRate);
}

//activate the timer calls after the page is loaded
$(function() {
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxNotificationsContent();
});

function updateNotificationsLastVersionSeen() {
    $.ajax({
        method: 'POST',
        data: {"notificationType" : "LAST_VERSION_SEEN",
            "lastVersionSeen" : $(".notibox").length
        },
        url: "/magitHub/pages/friend/notifications",
        error: function (data) {
            console.log("error");

        },
        success: function (data) {
            console.log("last version seen set to " + $(".notibox").length);
        }
    });

}
