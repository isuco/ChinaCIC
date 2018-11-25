$(function () {

    let entryContent;
    $.ajax({
        url: '/user/projects',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: (result) => {
            console.log(result);
            content = result['content'];
            entryContent = content;
            $("#tree").treeview({
                data: content,
                emptyIcon: "glyphicon glyphicon-file",
                onNodeSelected: function (event, data) {
                    if (data.clickable) {
                        console.log(data.filePath);
                        $('#pdf_viewer').attr("data", data.filePath);
                    }
                }
            })
        }
    });
});