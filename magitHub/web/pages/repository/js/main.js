
$(function() { // onload function
    var repository;

    var branches= repository.m_Magit.m_Branches.values;

    var commits = repository.m_Magit.m_Commits.values;

    $.each(branches, function(key, value){
        $(".branchTable").append('<tr>\n' +
            '                        <td class="branchNameColumn"> + value.m_Name + </td>\n' +
            '                        <td class="isRemoteColumn"> + value.m_IsRemote + </td>\n' +
            '                        <td class="isTrackingColumn"> + value.m_IsTracking + </td>\n' +
            '                        <td class="pointedCommitColumn"> + value.m_CommitSHA1 + </td>\n' +
            '                        <td class="commitMessageColumn">Message</td>\n' +
            '                    </tr>');
    });

    $.each(commits, function(key, value){
        $(".commitTable").append('<tr>\n' +
            '                        <td class="commitMessageColumn">' + value.m_Message + '</td>\n' +
            '                        <td class="commitAuthorColumn"> '+ value.m_CommitAuthor +' </td>\n' +
            '                        <td class="commitDateColumn"> '+ value.m_CommitDate +' </td>\n' +
            '                        <td class="commitSHA1Column"> '+ key +' </td>\n' +
            '                    </tr>');
    });
});