var notificationsVersion;
var refreshRate = 2000; //milli seconds

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

function appendToNotificationArea(newNotifications) {
    $.each(newNotifications || [], appendNotification);
}

function appendNotification(index, entry){
    var entryElement = createNotificationElement(entry);
    $(".notification-sidebar").append(entryElement);
}

function createNotificationElement (entry){
    return '<a href="#" role="button" style="text-decoration:none"> <div class="notibox">' + entry.m_NotificationDetails +' <div class="cancel">✕</div> </div> </a>';
}

function ajaxNotificationsContent() {
    $.ajax({
        method: 'GET',
        data: {"notificationType" : "NOTIFICATIONS_VERSION"
            ,"notificationsVersion" : notificationsVersion
        },
        url: "/magitHub/pages/friend/notifications",
        //timeout: 4000, TODO delete comment
        error: function (data) {
            alert("error was occurred while getting notifications version");
            triggerAjaxNotificationsContent();
        },
        success: function (data) {
            if (notificationsVersion === undefined){
                notificationsVersion = 0;
            }
            if (notificationsVersion !== data.updatedNotificationsVersion){
                // new notifications arrived to user
                notificationsVersion = data.updatedNotificationsVersion;
                appendToNotificationArea(data.newNotifications);
            }
            triggerAjaxNotificationsContent();
        }
    });
}

function triggerAjaxNotificationsContent() {
    setTimeout(ajaxNotificationsContent, refreshRate);
}

//activate the timer calls after the page is loaded
$(function() {

    //The users list is refreshed automatically every second
    //TODO need to refresh users list

    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxNotificationsContent();
});

