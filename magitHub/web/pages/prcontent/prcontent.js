var PRID = getUrlParameter('PRID');

$(function() { //onload function
    $('#backButton').on('click', function(e){
        e.preventDefault();
        var url = "../pullRequest/pullRequest.html";
        window.location.href = url;
    });
    $.ajax({
        url: "/magitHub/pullRequest",
        data: {"requestType": "openChanges", "PRID": PRID},
        error: function () {
            console.log("no");
        },
        success: function (data) {
            var files = data.split("\n"), i; // TODO - maybe not \n
            var fileButtonClick = function(event){
                $.ajax({
                    url: "/magitHub/pullRequest",
                    data: {"requestType": "fileContent", "PRID": PRID, "filePath": event.data.filePath},
                    error: function () {
                        console.log("no");
                    },
                    success: function (data) {
                        // break the textblock into an array of lines
                        var lines = data.split('\n');
                        var status = lines[0];
                         // remove one line, starting at the first position
                        lines.splice(0,1);
                        // join the array back into a single string
                        var newtext = lines.join('\n');
                        $("#fileStatus").empty();
                        $("#fileStatus").append(status);
                        $("#fileContent").empty();
                        $("#fileContent").append(newtext);
                        $("#fileName").empty();
                        $("#fileName").append(event.data.filePath);
                    }
                });
            };
            for(i=0;i<files.length-1;i++) // notice - its length-1 because files also includes another cell of newline only
            {

                var fileName = files[i].replace(/(\r\n|\n|\r)/gm, "").trim();
                var element = document.createElement("li");
                element.innerHTML = '<button class="commitFile" type="button">'+ fileName + '</button>';
                document.getElementById("fileTreeDemo_1").appendChild(element);
                //document.getElementById("fileTreeDemo_1").appendChild(document.createElement("BR"));
                $(".commitFile:last").click({filePath : fileName}, fileButtonClick);
            }

            $(".commitFile:last").trigger("click");
        }
    });
});