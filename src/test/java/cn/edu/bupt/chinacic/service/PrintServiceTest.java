package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.po.key.ExpertProjectPrimaryKey;
import cn.edu.bupt.chinacic.repository.ExpertProjectRepository;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import cn.edu.bupt.chinacic.repository.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintServiceTest {

    @Autowired
    private PrintService printService;

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ExpertProjectRepository expertProjectRepository;

    @Test
    @Transactional
    public void testPrintVotePerExpert() {
        List<Project> projects = projectRepository.queryByPublish();
        Expert expert = expertRepository.getOne(3L);
        List<ExpertProject> votesPerExpert = new ArrayList<>();
        for (Project project : projects) {
            ExpertProjectPrimaryKey key = new ExpertProjectPrimaryKey();
            key.setExpertId(expert.getId());
            key.setProjectId(project.getId());
            votesPerExpert.add(expertProjectRepository.getOne(key));
        }
        printService.printVotePerExpert(votesPerExpert, expert, "E:/cic/result/李旺.pdf");
    }
}
