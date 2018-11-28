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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/")
    public String adminRedirect() {
        return "redirect:/admin/operation-publish";
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

    @GetMapping("vote-result")
    public String getVoteResultView(@RequestParam String type, ModelMap resMap) {
        if (adminService.getUnVotedCount() == 0) {
            List<Project> projects = adminService.getVoteResult();

            int special = 0, level1 = 0, level2 = 0, level3 = 0;
            for (Project p : projects) {
                special += p.getSpecialNum();
                level1 += p.getFirstNum();
                level2 += p.getSecondNum();
                level3 += p.getThirdNum();
            }
            resMap.put("totalOfSpecial", special);
            if (special > 0) resMap.put("showSpecial", true);
            resMap.put("totalOfFirst", level1);
            if (level1 > 0) resMap.put("showFirst", true);
            resMap.put("totalOfSecond", level2);
            if (level2 > 0) resMap.put("showSecond", true);
            resMap.put("totalOfThird", level3);
            if (level3 > 0) resMap.put("showThird", true);
            resMap.put("numberOfWaiting", 0);
            if (type.contains("rank")) {
                projects.sort((p1, p2) -> {
                    if (p1.getSpecialNum() != p2.getSpecialNum()) {
                        return p2.getSpecialNum() - p1.getSpecialNum();
                    } else if (p1.getFirstNum() != p2.getFirstNum()) {
                        return p2.getFirstNum() - p1.getFirstNum();
                    } else if (p1.getSecondNum() != p2.getSecondNum()) {
                        return p2.getSecondNum() - p1.getSecondNum();
                    } else if (p1.getThirdNum() != p2.getThirdNum()) {
                        return p2.getThirdNum() - p1.getThirdNum();
                    } else {
                        return p1.getNumber().compareTo(p2.getNumber());
                    }
                });
            }
            resMap.put("projects", projects);
        }
        switch (type) {
            case "origin":
                return "vote-result";
            case "origin-print":
                return "vote-result-print";
            case "rank":
                return "rank-vote-result";
            default:
                return "rank-vote-result-print";
        }
    }

//    @GetMapping("result/print")
//    public String getVoteResultPrint(ModelMap resMap) {
//        getVoteResultData(resMap);
//        return "vote-result-print";
//    }

    @GetMapping("vote/finish")
    @ResponseBody
    public CommonResult isVoteFinished() {
        long unVotedCount = adminService.getUnVotedCount();
        Map<String, Object> res = new HashMap<>();
        if (unVotedCount == 0) {
            res.put("status", "finish");
            return CommonResult.success("投票结束", res);
        } else {
            res.put("status", "unFinish");
            res.put("count", unVotedCount);
            return CommonResult.success("投票未结束", res);
        }
    }

}
