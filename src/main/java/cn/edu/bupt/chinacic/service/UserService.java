package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.jo.ExpertVoteJo;
import cn.edu.bupt.chinacic.pojo.po.Expert;
import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.po.key.ExpertProjectPrimaryKey;
import cn.edu.bupt.chinacic.pojo.vo.HomeTreeVo;
import cn.edu.bupt.chinacic.pojo.vo.PublishProjectVo;
import cn.edu.bupt.chinacic.pojo.vo.VoteItemVo;
import cn.edu.bupt.chinacic.repository.ExpertProjectRepository;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import cn.edu.bupt.chinacic.repository.ProjectRepository;
import cn.edu.bupt.chinacic.util.Prize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private ExpertRepository expertRepository;

    private ProjectRepository projectRepository;

    private ExpertProjectRepository expertProjectRepository;

    private JudgePrizeService judgePrizeService;



    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setExpertRepository(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    @Autowired
    public void setExpertProjectRepository(ExpertProjectRepository expertProjectRepository) {
        this.expertProjectRepository = expertProjectRepository;
    }

    @Autowired
    public void setJudgePrizeService(JudgePrizeService judgePrizeService) {
        this.judgePrizeService = judgePrizeService;
    }


    @Transactional
    public boolean registryUser(String ip, String name) {
        Expert expert = this.expertRepository.queryByIp(ip);
        if (expert == null) {
            expert = new Expert();
            expert.setIp(ip);
            expert.setName(name);
            this.expertRepository.save(expert);
            return true;
        }
        return false;
    }

    @Transactional
    public List<PublishProjectVo> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(PublishProjectVo::new).collect(Collectors.toList());
    }

    @Transactional
    public List<HomeTreeVo> getAllPublishedProjects() {
        return projectRepository.queryByPublish().stream()
                .map(p -> new HomeTreeVo(p.getNumber() + " " + p.getName(), true, p.getProjectPath()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<VoteItemVo> getVoteData(String ip) {
        Expert expert = expertRepository.queryByIp(ip);
        List<VoteItemVo> vos = new ArrayList<>();
        List<Project> projects = projectRepository.queryByPublish();
        long expertId = expert.getId();
        for (Project project : projects) {
            VoteItemVo vo = new VoteItemVo();
            vo.setId(project.getId());
            vo.setNum(project.getNumber());
            vo.setProjectName(project.getNumber() + " " + project.getName());
            vo.setCompUnit(project.getMainCompUnit());
            vo.setType(project.getType());
            vo.setRecoUnit(project.getRecoUnit());
//            if (!expert.isVoted()) {
//                vo.setVoted(false);
//                vo.setPrize("无");
//                vos.add(vo);
//                continue;
//            }
            Optional<ExpertProject> expertProjectOptional = expertProjectRepository.findById(new ExpertProjectPrimaryKey(expertId, project.getId()));
            if (expertProjectOptional.isPresent()) {
                ExpertProject expertProject = expertProjectOptional.get();

                vo.setVoted(true);
                if (ConfigService.prize == Prize.SPECIAL && expertProject.getSpecialNum() != 0
                        || ConfigService.prize == Prize.FIRST && expertProject.getFirstNum() != 0
                        || ConfigService.prize == Prize.SECOND && expertProject.getSecondNum() != 0
                        || ConfigService.prize == Prize.THIRD && expertProject.getThirdNum() != 0)
                    vo.setPrize(ConfigService.prize.type);
                else if (ConfigService.prize == Prize.ALL) {
                    vo.setVoted(true);
                    if (expertProject.getFirstNum() != 0) vo.setPrize("一等奖");
                    else if (expertProject.getSecondNum() != 0) vo.setPrize("二等奖");
                    else if (expertProject.getThirdNum() != 0) vo.setPrize("三等奖");
                    else if (expertProject.getSpecialNum() != 0) vo.setPrize("特等奖");
                    else vo.setPrize("无");
                } else {
                    vo.setVoted(false);
                    vo.setPrize("无");
                }
            } else {
                vo.setVoted(false);
                vo.setPrize("无");
            }
            vos.add(vo);
        }
        return vos;
    }

    @Transactional
    public void expertVote(String ip, List<ExpertVoteJo> expertVotes) {
        // 得到专家
        Expert expert = expertRepository.queryByIp(ip);
        for (ExpertVoteJo expertVote : expertVotes) {
            // 专家,项目 联合主键
            ExpertProjectPrimaryKey key = new ExpertProjectPrimaryKey(expert.getId(), expertVote.getProjectId());
            // 获得关系数据
            Optional<ExpertProject> expertProjectOptional = expertProjectRepository.findById(key);

            ExpertProject expertProject = expertProjectOptional.orElseGet(() -> {
                ExpertProject e = new ExpertProject();
                e.setId(key);
                return e;
            });
            updateVote(expertProject, expertVote.getPrize());
            expertProject.setExpert(expert);
            expertProject.setProject(projectRepository.getOne(expertVote.getProjectId()));
            expertProjectRepository.save(expertProject);
        }
        expert.setVoted(true);
        expertRepository.save(expert);

        // 更新最终投票结果
        if (expertRepository.votedCount() == expertRepository.count()) {
            finishVote();
        }

    }


    @Transactional
    public void finishVote() {
        List<Project> projects = projectRepository.queryByPublish();
        for (Project project : projects) {
            int level1 = 0, level2 = 0, level3 = 0, special = 0;
            for (ExpertProject expertProject : project.getExperts()) {
                special += expertProject.getSpecialNum();
                level1 += expertProject.getFirstNum();
                level2 += expertProject.getSecondNum();
                level3 += expertProject.getThirdNum();
            }

            if (ConfigService.prize == Prize.SPECIAL) {
                project.setSpecialNum(special);
                if (judgePrizeService.isSpecial(special))
                    project.setPrize("特等奖");
                else project.setPrize("一等奖");
            } else if (ConfigService.prize == Prize.FIRST) {
                if (judgePrizeService.isFirst(0, level1))
                    project.setPrize("一等奖");
                else project.setPrize("无");
                project.setFirstNum(level1);
            } else if (ConfigService.prize == Prize.SECOND) {
                if (judgePrizeService.isSecond(0, 0, level2))
                    project.setPrize("二等奖");
                else project.setPrize("三等奖");
                project.setSecondNum(level2);
            } else if (ConfigService.prize == Prize.THIRD) {
                if (judgePrizeService.isThird(0, 0, 0, level3))
                    project.setPrize("三等奖");
                else project.setPrize("无");
                project.setThirdNum(level3);
            } else {
                project.setSpecialNum(special);
                project.setFirstNum(level1);
                project.setSecondNum(level2);
                project.setThirdNum(level3);
                if (judgePrizeService.isSpecial(special)) project.setPrize("特等奖");
                else if (judgePrizeService.isFirst(special, level1)) project.setPrize("一等奖");
                else if (judgePrizeService.isSecond(special, level1, level2)) project.setPrize("二等奖");
                else if (judgePrizeService.isThird(special, level1, level2, level3)) project.setPrize("三等奖");
                else project.setPrize("无");
            }
        }
//        projects.forEach(p-> p.setPublish(false));
        projectRepository.saveAll(projects);
    }

    @Transactional
    public boolean isVoted(String ip) {
        Expert expert = expertRepository.queryByIp(ip);
        return expert.isVoted();
    }

    private void updateVote(ExpertProject expertProject, String prize) {
        expertProject.setVoted(true);
        if (ConfigService.prize == Prize.ALL) {
            expertProject.setSpecialNum(0);
            expertProject.setFirstNum(0);
            expertProject.setSecondNum(0);
            expertProject.setThirdNum(0);
        }

        // 如果是"无"
        if (prize.equals("无")){
            switch (ConfigService.prize.type) {
                case "特等奖":
                    expertProject.setSpecialNum(0);
                    break;
                case "一等奖":
                    expertProject.setFirstNum(0);
                    break;
                case "二等奖":
                    expertProject.setSecondNum(0);
                    break;
                case "三等奖":
                    expertProject.setThirdNum(0);
                    break;
            }
            return;
        }

        switch (prize) {
            case "特等奖":
                expertProject.setSpecialNum(1);
                break;
            case "一等奖":
                expertProject.setFirstNum(1);
                break;
            case "二等奖":
                expertProject.setSecondNum(1);
                break;
            case "三等奖":
                expertProject.setThirdNum(1);
                break;
        }
    }
}
