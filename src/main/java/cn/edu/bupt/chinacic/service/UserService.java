package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.vo.HomeTreeVo;
import cn.edu.bupt.chinacic.pojo.vo.PublishProjectVo;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import cn.edu.bupt.chinacic.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private ExpertRepository expertRepository;

    private ProjectRepository projectRepository;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setExpertRepository(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
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
    public List<HomeTreeVo> getAllPublishedProjects(){
        return projectRepository.queryByPublish().stream()
                .map(p-> new HomeTreeVo(p.getName(), true, p.getProjectPath()))
                .collect(Collectors.toList());
    }

}
