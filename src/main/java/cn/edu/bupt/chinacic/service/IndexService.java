package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import cn.edu.bupt.chinacic.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndexService {

    private ProjectRepository projectRepository;
    private ExpertRepository expertRepository;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setExpertRepository(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    @Transactional
    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            project.setName(project.getProjectPath());
        }
        return projects;
    }

    @Transactional
    public boolean needRegistry(String ip) {
        return !expertRepository.existsByIp(ip);
    }
}
