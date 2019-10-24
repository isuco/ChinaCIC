$(document).ready(function(){
    /**
     * 清空选择状态
     */
    const tableDOM = $("#content");
    tableDOM.clear = function(){
        tableDOM.find("input").prop("checked", false);
    };

    /**
     * 发布项目
     */
    const publishBtn = $("#submit-publish-btn");
    publishBtn.click(function () {
        let publishProjects = [];
        $('input').each(function () {
            // id
            const projectId = $(this).attr('id');
            // 状态
            const isPublish = $(this).is(':checked');
            let project = {};
            project['projectId'] = projectId;
            project['publish'] = isPublish;
            publishProjects.push(project);
        });

        $.ajax({
            url: '/admin/projects',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(publishProjects),
            success: function (result){
                if (result['code'] !== 'FAILURE') {
                    toastr.options.timeout = 2000;
                    toastr.info(result['reason']);
                    window.location.reload();
                } else {
                    toastr.options.timeout = 2000;
                    toastr.error(result['reason']);
                }
            }
        });
    });

    /**
     * 选择提名一等奖
     */
    const selectSpecialBtn = $("#select-special-btn");
    selectSpecialBtn.click(function () {
        tableDOM.clear();
        tableDOM.find("input[data-prize='一等奖']").prop("checked", true);
    });

    /**
     * 选择提名一等奖
     */
    const selectFirstBtn = $("#select-first-btn");
    selectFirstBtn.click(function () {
        tableDOM.clear();
        tableDOM.find("input[data-prizewant='提名一等']").prop("checked", true);
    });

    /**
     * 选择提名二等 + 一等落选
     */
    const selectSecondBtn = $("#select-second-btn");
    selectSecondBtn.click(function () {
        tableDOM.clear();
        tableDOM.find("input[data-prizewant='提名二等']").prop("checked", true);
        tableDOM.find("input[data-prizewant='提名一等'][data-prize='无']").prop("checked", true);
    });

    /**
     *  选择提名三等奖
     */
    const selectThirdBtn = $("#select-third-btn");
    selectThirdBtn.click(function () {
        tableDOM.clear();
        tableDOM.find("input[data-prizewant='提名三等']").prop("checked", true);
    });

    /**
     * 打印个人投票结果
     */
    const printPersonBtn = $("#print-person");
    printPersonBtn.click(function() {
        $.ajax({
            url: '/admin/print/expert',
            type: 'POST',
            success: function (result){
                if (result['code'] !== 'FAILURE') {
                    toastr.options.timeout = 2000;
                    toastr.info(result['reason']);
                } else {
                    toastr.options.timeout = 2000;
                    toastr.error(result['reason']);
                }
            }
        });
    })
});