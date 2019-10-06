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


function updateRepositoriesCardsInHTML(repositoriesArray, username, chooseOrFork){
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

        $(".row").eq(-2).append( $('<div class="col-lg-4 mb-4">'));
        $(".col-lg-4:last").append( $('<div class="card h-100">'));
        $(".card.h-100:last").append($('<h4 class="card-header" id="repositoryName" value=""' + repositoryName + '>' + repositoryName + '</h4>'));
        $(".card.h-100:last").append($('<div class="card-body">'));
        $(".card-body:last").append( $('<p class="card-text">' + commitDetails + '</p>'));
        $(".card.h-100:last").append($('<div class="card-footer">'));
        $(".card-footer:last").append( $('<a href="#" class="btn btn-primary chooseRepo">' + chooseOrFork + '</a>'));
        $(".chooseRepo:last").click(function () {
            var url = "../repository/repository.html?repositoryName=" + repositoryName + "&username=" + username;
            window.location.href = url;
        });

        numberOfRepositories++;
    });
}