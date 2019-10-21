$(function () {

    let publishBtn = $("#submit-publish-btn");
    let projectsUl = $('#projects-ul');
    let selectSpecialBtn = $('#select-special-btn');
    let selectFirstBtn = $('#select-first-btn');
    let selectSecondBtn = $("#select-second-btn");
    let selectThirdBtn = $('#select-third-btn');
    let content;

    toastr.options = {
        "closeButton": false,
        "debug": false,
        "newestOnTop": false,
        "progressBar": false,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "2000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };

    // 获取所有项目
    fetchAllProjects();

    function fetchAllProjects() {
        $.ajax({
            url: '/admin/projects',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: (result) => {
                console.log(result);
                if (result['code'] === 'FAILURE') {
                    toastr.options.timeout = 2000;
                    toastr.error(result['reason']);
                } else {
                    content = result['content'];
                    projectsUl.empty();
                    for (let i = 0; i < content.length; ++i) {
                        if (content[i].isPublish) {
                            projectsUl.append(`<input type="checkbox" checked id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
                        } else {
                            projectsUl.append(`<input type="checkbox" id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
                        }
                    }
                }
            }
        });
    }

    // 发布项目
    publishBtn.click(function () {
        let publishProjects = [];
        projectsUl.find('input').each(function () {
            let projectId = $(this).attr('id');
            let isPublish = $(this).is(':checked');
            let project = {};
            project['projectId'] = projectId;
            project['publish'] = isPublish;
            publishProjects.push(project);
        });
        console.log(publishProjects);

        $.ajax({
            url: '/admin/projects',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(publishProjects),
            success: (result) => {
                if (result['code'] !== 'FAILURE') {
                    toastr.options.timeout = 2000;
                    toastr.info(result['reason']);
                    fetchAllProjects();
                }
                else {
                    toastr.options.timeout = 2000;
                    toastr.error(result['reason']);
                }
            }
        });
    });

    // 选择提名一等奖
    selectFirstBtn.click(function () {
        projectsUl.empty();
        for (let i = 0; i < content.length; ++i) {
            if (content[i].prize === '一等奖') {
                projectsUl.append(`<input type="checkbox" checked id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
            } else {
                projectsUl.append(`<input type="checkbox" id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
            }
        }
    });

    // 选择提名二等 + 一等落选
    selectSecondBtn.click(function () {
        projectsUl.empty();
        for (let i = 0; i < content.length; ++i) {
            if (content[i].prize === '二等奖') {
                projectsUl.append(`<input type="checkbox" checked id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
            } else {
                projectsUl.append(`<input type="checkbox" id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
            }
        }
    });

    // 选择提名三等奖
    selectThirdBtn.click(function () {
        projectsUl.empty();
        for (let i = 0; i < content.length; ++i) {
            if (content[i].prize === '三等奖') {
                projectsUl.append(`<input type="checkbox" checked id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
            } else {
                projectsUl.append(`<input type="checkbox" id="${content[i].id}" style="margin-top: 8px"/>
                                    &nbsp;&nbsp;&nbsp;${content[i].prize}&nbsp;&nbsp;&nbsp;&nbsp;${content[i].name}<br>`)
            }
        }
    });

    $('#start-special').click(function () {
        startVote('特等奖');
    });

    $('#start-first').click(function () {
        startVote('一等奖');
    });

    $('#start-second').click(function () {
        startVote('二等奖');
    });

    $('#start-third').click(function () {
        startVote('三等奖');
    });

    $('#start-all').click(function () {
        startVote('初评');
    });

    $('#final-watch').click(function () {
        console.log("查看最终结果");
        $.ajax({
            url: '/admin/final-watch',
            type: 'POST',
            dataType: 'json',
            success: (result) => {
                toastr.options.timeout = 2000;
                if (result['code'] !== 'FAILURE') {
                    toastr.info(result['reason']);
                }
                else {
                    toastr.error(result['reason']);
                }
            }
        });
    });

    $('#init-btn').click(function () {
        console.log("ini");
        var dirPath = $('#dirPathInput').val();
        if (dirPath === undefined || dirPath.length === 0) {
            toastr.options.timeout = 1000;
            toastr.error("请填写文件夹路径");
        } else {
            $.ajax({
                url: '/admin/project/ini',
                type: 'POST',
                dataType: 'json',
                data: {'csvPath': dirPath},
                success: (result) => {
                    toastr.options.timeout = 2000;
                    if (result['code'] !== 'FAILURE') {
                        toastr.info(result['reason']);
                    }
                    else {
                        toastr.error(result['reason']);
                    }
                }
            });
        }
    });

    function startVote(prizeType) {
        $.ajax({
            url: '/admin/vote',
            type: 'POST',
            dataType: 'json',
            data: {'type': prizeType},
            success: (result) => {
                toastr.options.timeout = 2000;
                if (result['code'] !== 'FAILURE') {
                    toastr.info(result['reason']);
                }
                else {
                    toastr.error(result['reason']);
                }
            }
        });
    }
});