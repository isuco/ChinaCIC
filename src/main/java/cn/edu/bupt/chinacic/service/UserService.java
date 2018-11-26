package cn.edu.bupt.chinacic.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private ExpertRepository expertRepository;

    private ProjectRepository projectRepository;

    private ExpertProjectRepository expertProjectRepository;

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
            ExpertProjectPrimaryKey key = new ExpertProjectPrimaryKey(expertId, project.getId());
            VoteItemVo vo = new VoteItemVo();
            vo.setId(project.getId());
            vo.setNum(project.getNumber());
            vo.setProjectName(project.getNumber() + " " + project.getName());
            vo.setCompUnit(project.getMainCompUnit());
            if (!expert.isVoted()) continue;
            Optional<ExpertProject> expertProjectOptional = expertProjectRepository.findById(new ExpertProjectPrimaryKey(expertId, project.getId()));
            if (expertProjectOptional.isPresent()) {
                ExpertProject expertProject = expertProjectOptional.get();
                if (expertProject.isVoted() && (ConfigService.prize == Prize.SPECIAL && expertProject.getSpecialNum() != 0
                        || ConfigService.prize == Prize.FIRST && expertProject.getFirstNum() != 0
                        || ConfigService.prize == Prize.THIRD && expertProject.getSecondNum() != 0
                        || ConfigService.prize == Prize.THIRD && expertProject.getThirdNum() != 0)) {
                    vo.setVoted(true);
                } else {
                    vo.setVoted(false);
                }
            } else {
                vo.setVoted(false);
            }
            vos.add(vo);
        }
        return vos;
    }
}
