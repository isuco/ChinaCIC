package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.pojo.jo.ExpertVoteJo;
import cn.edu.bupt.chinacic.pojo.vo.HomeTreeVo;
import cn.edu.bupt.chinacic.pojo.vo.VoteItemVo;
import cn.edu.bupt.chinacic.service.ConfigService;
import cn.edu.bupt.chinacic.service.IndexService;
import cn.edu.bupt.chinacic.service.UserService;
import cn.edu.bupt.chinacic.util.CommonResult;
import cn.edu.bupt.chinacic.util.NetworkUtils;
import cn.edu.bupt.chinacic.util.Prize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("user")
@Slf4j
public class UserController {

    private UserService userService;
    private IndexService indexService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("registry")
    public String userRegistryView() {
        return "registry";
    }

    @PostMapping("registry")
    public String userRegistry(HttpServletRequest request, @RequestParam String name, HttpSession session, ModelMap resMap) {
        String ip = NetworkUtils.getIpAddr(request);
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(name)) {
            resMap.put("error", "注册信息不完整");
            return "registry";
        }
        log.info("注册用户 name = {}, ip = {}", name, ip);
        boolean isSuccess = userService.registryUser(ip, name);
        if (!isSuccess) {
            log.error("您的Ip = {}已注册", ip, name);
            resMap.put("error", "用户已存在，不能重复注册");
            return "registry";
        } else {
            log.info("用户 ip = {}, name = {}注册成功", ip, name);
            return "redirect:/";
        }
    }

    @GetMapping("projects")
    @ResponseBody
    public CommonResult getAllProjects() {
        List<HomeTreeVo> projects = userService.getAllPublishedProjects();
        if (projects == null || projects.size() == 0) {
            return CommonResult.failure("没有发布的参评项目");
        } else {
            return CommonResult.success("success", projects);
        }
    }

    @GetMapping("vote")
    public String getVoteView(HttpServletRequest request, ModelMap resMap) {
        String ip = NetworkUtils.getIpAddr(request);
        // 投票奖项
        Prize prize = ConfigService.prize;
        if (prize == null){
            resMap.addAttribute("error", "没有开启投票");
            return "vote-error";
        }
        List<String> voteItems = ConfigService.voteItems;
        resMap.addAttribute("item", prize.type);
        resMap.addAttribute("prizes", voteItems);

        // 已经投票过
        if(userService.isVoted(ip)){
            return "vote-success";
        }

        // 没有投票资格
        if (StringUtils.isEmpty(ip) || indexService.needRegistry(ip)){
            resMap.addAttribute("error", "没有投票资格");
            return "vote-error";
        }

        // 获取投票项目
        List<VoteItemVo> vos = userService.getVoteData(ip);
        if (vos.size() == 0){
            resMap.addAttribute("error", "没有参与投票的项目");
            return "vote-error";
        }
        resMap.addAttribute("voteData", vos);

        return "expert-vote";
    }

    @PostMapping("vote")
    @ResponseBody
    public CommonResult expertVote(HttpServletRequest request, @RequestBody List<ExpertVoteJo> expertVotes) {
        String ip = NetworkUtils.getIpAddr(request);
        if (userService.isVoted(ip)) {
            return CommonResult.failure("不能重复投票");
        }
        userService.expertVote(ip, expertVotes);
        return CommonResult.success("投票成功");
    }

}
