package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.pojo.jo.PublishProjectJo;
import cn.edu.bupt.chinacic.service.AdminService;
import cn.edu.bupt.chinacic.util.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("admin")
@Slf4j
public class AdminController {

    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("vote")
    @ResponseBody
    public CommonResult startVote(@RequestParam String type) {
        if (StringUtils.isEmpty(type)) {
            return CommonResult.failure("开启的投票奖项不能为空");
        } else {
            boolean isSuccess = adminService.startVote(type);
            if (isSuccess) {
                log.info("开启投票{}成功", type);
                return CommonResult.success("开启投票成功");
            } else {
                log.error("开启投票{}失败", type);
                return CommonResult.failure("开启投票失败");
            }
        }
    }

    @PostMapping("project/ini")
    @ResponseBody
    public CommonResult parseProject(@RequestParam String dirPath) {
        if (StringUtils.isEmpty(dirPath)) {
            return CommonResult.failure("请填写文件路径");
        }
        if (adminService.parseProject(dirPath)) {
            return CommonResult.failure("参评项目初始化成功");
        } else {
            return CommonResult.success("参评项目初始化成功");
        }
    }

    @PostMapping("project/publish")
    public CommonResult publishProject(@RequestParam List<PublishProjectJo> publishProjects) {
        if(publishProjects==null||publishProjects.size()==0){
            return CommonResult.failure("参数不能为空");
        }else{
            adminService.publishProject(publishProjects);
            return CommonResult.success("发布项目成功");
        }
    }

}
