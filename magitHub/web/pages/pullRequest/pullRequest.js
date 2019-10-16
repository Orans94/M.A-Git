function categorizePullRequests(pullRequests) {
    var result = {'openPR' : [],
                'closedPR' : []
                };

    $.each(pullRequests || [], function (index, entry) {
        if (pullRequests[index].m_Status === "Open")
            result.openPR.push(pullRequests[index]);
        else
            result.closedPR.push(pullRequests[index]);
    });

    return result;
}

function appendPRCardsByStatus(PRArray) {
    var numberOfPR = 0;
    var repositoryName, PRCreator, baseBranch, targetBranch, dateOfCreation, status;
    var lastRowElement, containerElement = $(".container");

    $.each(PRArray || [], function (index, entry) {
        if (numberOfPR % 4 === 0) {
            $(".row:last").after($('<div class="row mb-4"></div>'));
            if (entry.m_Status === "Open") {
                $(".closed-pr-header").before($('<div class="row">'));
            } else {
                $(".py-5 bg-dark").before($('<div class="row"></div>'));
            }
        }
        repositoryName = entry.m_RRName;
        PRCreator = entry.m_LRUsername;
        baseBranch = entry.m_BaseBranchName;
        targetBranch = entry.m_TargetBranchName;
        dateOfCreation = entry.m_DateOfCreation;
        status = entry.m_Status;

        $(".row:last").append('<div class="col-xl-3 col-md-6 col-sm-12"> <div class="card"> <div class="card-content"> <div class="card-body"> <h4 class="card-title">Requested repository: ' + repositoryName +'</h4> <h6 class="card-subtitle text-muted">Pull request creator: '+ PRCreator +'</h6> </div>' +
            '<div class="card-body"> <p class="card-text">Base branch: '+ baseBranch + '<br>Target branch: ' + targetBranch+ '<br>Date of creation :' + dateOfCreation + ' <br>Status: ' + status + '</p> <a href="http://localhost:8080/magitHub/pages/friend/friend.html?username=oran#" class="card-link pink">Approve</a> <a href="http://localhost:8080/magitHub/pages/friend/friend.html?username=oran#" class="card-link pink">Decline</a> </div> </div> </div> </div>');
        numberOfPR++;
    });
}

function appendPullRequestsCards(pullRequests) {
    var categorizedPR = categorizePullRequests(pullRequests);

    appendPRCardsByStatus(categorizedPR.openPR);
    appendPRCardsByStatus(categorizedPR.closedPR);
}

// initialize pull requests cards
$(function(){
    $.ajax({
        method: 'GET',
        data: {"isLoggedInUser" : "TRUE"
        },
        url: "/magitHub/pages/main/user",
        //timeout: 4000, TODO delete comment
        error: function (data) {
            console.log("error was occurred while retrieving pull requests information");
        },
        success: function (data) {
            appendPullRequestsCards(data.m_PullRequests);
        }
    });

});