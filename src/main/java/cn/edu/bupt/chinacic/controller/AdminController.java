package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.pojo.jo.PublishProjectJo;
import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.vo.PublishProjectVo;
import cn.edu.bupt.chinacic.service.AdminService;
import cn.edu.bupt.chinacic.util.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("admin")
@Slf4j
public class AdminController {

    private AdminService adminService;

    @Value("${adminPassword}")
    private String adminPassword;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("operation-publish")
    public String operationPublish() {
        return "operation-publish";
    }

    @GetMapping("operation-start")
    public String operationStart() {
        return "operation-start";
    }

    @GetMapping("login")
    public String loginView() {
        return "login";
    }

    @PostMapping("login")
    public String login(HttpSession session, @RequestParam String password, ModelMap resMap) {
        if (StringUtils.isEmpty(password)) {
            resMap.put("error", "请填写密码");
            return "login";
        } else if (!adminPassword.equals(password)) {
            resMap.put("error", "密码不正确");
            return "login";
        } else {
            session.setAttribute("adminLogin", true);
            return "redirect:operation-publish";
        }
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
                return CommonResult.success("开启" + type + "投票成功");
            } else {
                log.error("开启投票{}失败", type);
                return CommonResult.failure("开启" + type + "投票失败");
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

    @GetMapping("projects")
    @ResponseBody
    public CommonResult getPublishOriginData() {
        List<PublishProjectVo> projects = this.adminService.getPublishVos();
        if (projects == null || projects.size() == 0) {
            return CommonResult.failure("请先初始化项目");
        } else {
            return CommonResult.success("success", projects);
        }
    }

    @PostMapping("projects")
    @ResponseBody
    public CommonResult publishProject(@RequestBody List<PublishProjectJo> publishProjects) {
        if (publishProjects == null || publishProjects.size() == 0) {
            return CommonResult.failure("参数不能为空");
        } else {
            adminService.publishProject(publishProjects);
            return CommonResult.success("发布项目成功");
        }
    }

}
