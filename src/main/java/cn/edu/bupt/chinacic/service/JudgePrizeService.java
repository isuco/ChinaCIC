package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JudgePrizeService {

    @Value("${special-threshold}")
    private int specialThreshold;

    @Value("${first-threshold}")
    private int firstThreshold;

    @Value("${second-threshold}")
    private int secondThreshold;

    @Value("${third-threshold}")
    private int thirdThreshold;

    public boolean isSpecial(Project project) {
        return project.getSpecialNum() >= specialThreshold;
    }

    public boolean isFirst(Project project) {
        return !isSpecial(project) && project.getFirstNum() + project.getSpecialNum() >= firstThreshold;
    }

    public boolean isSecond(Project project) {
        return !isSpecial(project) && !isFirst(project) && project.getSecondNum() + project.getFirstNum()
                + project.getSecondNum() >= secondThreshold;
    }

    public boolean isThird(Project project) {
        return !isSpecial(project) && !isFirst(project) && !isSecond(project) && project.getThirdNum()
                + project.getSecondNum() + project.getSecondNum() + project.getSecondNum() >= thirdThreshold;
    }

}
