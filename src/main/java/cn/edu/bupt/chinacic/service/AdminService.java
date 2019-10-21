package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.jo.PublishProjectJo;
import cn.edu.bupt.chinacic.pojo.po.NumToName;
import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.vo.PublishProjectVo;
import cn.edu.bupt.chinacic.repository.ExpertProjectRepository;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import cn.edu.bupt.chinacic.repository.NumNameRepository;
import cn.edu.bupt.chinacic.repository.ProjectRepository;
import cn.edu.bupt.chinacic.util.FileUtils;
import cn.edu.bupt.chinacic.util.Prize;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.security.krb5.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminService {

    private ProjectRepository projectRepository;
    private ExpertRepository expertRepository;
    private NumNameRepository numNameRepository;
    private ExpertProjectRepository expertprojectRepository;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setExpertProjectRepository(ExpertProjectRepository projectRepository) {
        this.expertprojectRepository = projectRepository;
    }

    @Autowired
    public void setExpertRepository(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    @Autowired
    public void setNumNameRepository(NumNameRepository numNameRepository) {
        this.numNameRepository = numNameRepository;
    }

    @Transactional
    public synchronized boolean startVote(String type) {
        ConfigService.voteItems.clear();
//        List<Project> projects =null;
        switch (type) {
            case "特等奖":
                ConfigService.prize = Prize.SPECIAL;
                ConfigService.voteItems.add("特等奖");
//                projects=projectRepository.querySpecial();
                break;
            case "一等奖":
                ConfigService.prize = Prize.FIRST;
                ConfigService.voteItems.add("一等奖");
//                projects=projectRepository.queryFirst();
                break;
            case "二等奖":
                ConfigService.prize = Prize.SECOND;
                ConfigService.voteItems.add("二等奖");
//                projects=projectRepository.querySecond();
                break;
            case "三等奖":
                ConfigService.prize = Prize.THIRD;
                ConfigService.voteItems.add("三等奖");
//                projects=projectRepository.queryThird();
                break;
            default:
                ConfigService.prize = Prize.ALL;
                ConfigService.voteItems.add("一等奖");
                ConfigService.voteItems.add("二等奖");
                ConfigService.voteItems.add("三等奖");
                ConfigService.voteItems.add("无");
//                projects=projectRepository.queryByPublish();
        }
        expertRepository.updateUnVoted();
        List<Project> projects = projectRepository.queryByPublish();
        // 获取投过票的所有专家, 把 voted 设为 false ? 不会有问题吗
        projects.forEach(p -> p.getExperts().forEach(pp -> pp.setVoted(false)));
        // 发布所有项目
//        projects.forEach(p-> p.setPublish(true));
        // 提交操作
        projectRepository.saveAll(projects);

        return true;
    }

    public boolean clearDatabase(){
        expertprojectRepository.clear();
        expertRepository.clear();
        projectRepository.clear();
        return true;
    }

    private Project generateProject(String number, String name, String mainRecUnit, String mainComUnit, String type) {
        Project project = new Project();
        project.setType(type);
        project.setNumber(number);
        project.setName(name);
        project.setMainCompUnit(mainComUnit);
        project.setRecoUnit(mainRecUnit);
        project.setPublish(false);
       // Optional<NumToName> numToName = this.numNameRepository.queryByNum(String.valueOf(number.substring(0, 2)));
        /*if (numToName.isPresent()) {
            type = numToName.get().getName();
        }*/
        project.setPrize("无");
        //log.info("项目 {} 解析完成", filePath);
        return project;
//        project.setType(ConfigService.types.get(number.charAt(0)));
//        return projectRepository.save(project);
    }

    @Transactional
    public void publishProject(List<PublishProjectJo> publishProjects) {
        for (PublishProjectJo publishProject : publishProjects) {
            Optional<Project> project = projectRepository.findById(publishProject.getProjectId());
            project.ifPresent(p -> {
                p.setPublish(publishProject.isPublish());
//                projectRepository.save(p);
            });
        }
    }

    @Transactional
    public List<PublishProjectVo> getPublishVos() {
        return projectRepository.findAll().stream()
                .map(p -> {
                    PublishProjectVo projectVo = new PublishProjectVo();
                    projectVo.setId(p.getId());
                    projectVo.setNumber(p.getNumber());
                    projectVo.setName(p.getNumber() + " " + p.getName());
                    projectVo.setPublish(p.isPublish());
                    projectVo.setPrize(p.getPrize());
                    projectVo.setType(p.getType());
                    return projectVo;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    public long getUnVotedCount() {
        return expertRepository.count() - expertRepository.votedCount();
    }

    @Transactional
    public List<Project> getVoteResult() {
        return projectRepository.queryByPublish().stream()
                .map(p -> {
                    Project target = new Project(p);
                    if (ConfigService.prize != Prize.ALL) {
                        target.setSpecialNum(0);
                        target.setFirstNum(0);
                        target.setSecondNum(0);
                        target.setThirdNum(0);
                        if (ConfigService.prize == Prize.SPECIAL) {
                            target.setSpecialNum(p.getSpecialNum());
                        } else if (ConfigService.prize == Prize.FIRST) {
                            target.setFirstNum(p.getFirstNum());
                        } else if (ConfigService.prize == Prize.SECOND) {
                            target.setSecondNum(p.getSecondNum());
                        } else if (ConfigService.prize == Prize.THIRD) {
                            target.setThirdNum(p.getThirdNum());
                        }
                    }
                    return target;
                }).collect(Collectors.toList());
    }

    public boolean readCSV(String path){
        ArrayList<Project> projects=new ArrayList<Project>();
        File inFile = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                String[] columns=line.split(",");
                projects.add(generateProject(columns[1], columns[2],columns[3],columns[4],columns[0]));
            }
            reader.close();
            projectRepository.saveAll(projects);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
    }
    return true;
    }

    public List<Project> getRankResult() {
        return projectRepository.queryByPublish().stream()
                .map(p -> {
                    Project target = new Project(p);
                    if (ConfigService.prize != Prize.ALL) {
                        target.setSpecialNum(0);
                        target.setFirstNum(0);
                        target.setSecondNum(0);
                        target.setThirdNum(0);
                        if (p.getPrize().equals(Prize.SPECIAL.type)) {
                            target.setSpecialNum(p.getSpecialNum());
                        } else if ((p.getPrize().equals(Prize.FIRST.type))) {
                            target.setFirstNum(p.getFirstNum());
                        } else if (p.getPrize().equals(Prize.SECOND.type)) {
                            target.setSecondNum(p.getSecondNum());
                        } else if (p.getPrize().equals(Prize.THIRD.type)) {
                            target.setThirdNum(p.getThirdNum());
                        }
                    }
                    return target;
                }).collect(Collectors.toList());
    }
}
