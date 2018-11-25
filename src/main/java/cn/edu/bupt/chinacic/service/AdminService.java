package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.jo.PublishProjectJo;
import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.vo.PublishProjectVo;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import cn.edu.bupt.chinacic.repository.NumNameRepository;
import cn.edu.bupt.chinacic.repository.ProjectRepository;
import cn.edu.bupt.chinacic.util.Prize;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminService {

    private ProjectRepository projectRepository;
    private ExpertRepository expertRepository;
    private NumNameRepository numNameRepository;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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
        switch (type) {
            case "特等奖":
                ConfigService.prize = Prize.SPECIAL;
                ConfigService.voteItems.add("特等奖");
                break;
            case "一等奖":
                ConfigService.prize = Prize.FIRST;
                ConfigService.voteItems.add("一等奖");
                break;
            case "二等奖":
                ConfigService.prize = Prize.SECOND;
                ConfigService.voteItems.add("二等奖");
                break;
            case "三等奖":
                ConfigService.prize = Prize.THIRD;
                ConfigService.voteItems.add("三等奖");
                break;
            default:
                ConfigService.voteItems.add("一等奖");
                ConfigService.voteItems.add("二等奖");
                ConfigService.voteItems.add("三等奖");
        }
        ConfigService.voteItems.add("无");
        expertRepository.updateUnVoted();
        List<Project> projects = projectRepository.queryByPublish();
        projects.forEach(p->p.getExperts().forEach(pp->pp.setVoted(false)));
        projectRepository.saveAll(projects);
//        expertRepository.updateUnVoted();
        return true;
    }

    public boolean parseProject(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            log.error("文件 {} 不存在或不是一个目录", dirPath);
            return false;
        }
        File[] childFiles = dirFile.listFiles(childFile -> childFile.isFile() && childFile.getName().endsWith(".pdf"));

        PDFTextStripper stripper;
        try {
            stripper = new PDFTextStripper();
        } catch (IOException e) {
            log.error("创建PDF解析器失败");
            return false;
        }
        Splitter splitter = new Splitter();
        if (childFiles != null && childFiles.length > 0) {
            Arrays.sort(childFiles, Comparator.comparing(File::getName));
            for (File childFile : childFiles) {
                String content = null;
                PDDocument document=null;
                try {
                     document = PDDocument.load(childFile);
                    content = parseOneProject(stripper, splitter, document);
                } catch (IOException e) {
                    log.error("文件{}不能被PDF解析器解析", childFile.getPath());
                    e.printStackTrace();
                }finally {
                    if(document!=null){
                        try {
                            document.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String mainRecUnit=null, mainComUnit=null;
                if (content == null || StringUtils.isEmpty(content.trim())) {
                    log.error("文件{}为图片类型PDF", childFile.getPath());
                } else {
                    int recStart = content.indexOf("提名者") + "专家推荐".length();
                    int recEnd = content.indexOf("项目名称");
                    if (recStart == -1 || recEnd == -1) {
                        log.error("文件{}解析推荐单位或推荐人失败", childFile.getPath());
                    } else {
                        mainRecUnit = content.substring(recStart, recEnd).replace("\n", "");
                    }

                    int comStart = content.indexOf("主要完成单位") + "主要完成单位".length();
                    int comEnd = content.indexOf("项目密级");
                    if (comStart == -1 || comEnd == -1) {
                        log.error("文件{}解析主要完成单位失败", childFile.getPath());
                    } else {
                        mainComUnit = content.substring(comStart, comEnd).replace("\n", "");
                    }
                }
                String[] split = childFile.getName().split(" ");
                Project project = insertProject(split[0], split[1], mainRecUnit, mainComUnit, childFile.getPath());
                if (project == null) {
                    log.error("项目{}持久化失败", childFile.getName());
                } else {
                    log.info("项目{}持久化成功", childFile.getName());
                }
            }
        }
        return true;
    }

    @Transactional
    public Project insertProject(String number, String name, String mainRecUnit, String mainComUnit, String filePath) {
        Project project = new Project();
        project.setNumber(number);
        project.setName(name);
        project.setMainCompUnit(mainComUnit);
        project.setRecoUnit(mainRecUnit);
        project.setPublish(false);
        project.setProjectPath(filePath);
        project.setType(this.numNameRepository.queryByNum(String.valueOf(number.charAt(0))).getName());
//        project.setType(ConfigService.types.get(number.charAt(0)));
        return projectRepository.save(project);
    }

    private String parseOneProject(PDFTextStripper stripper, Splitter splitter, PDDocument document) throws IOException {
        String content = null;
        int index = 0;
        List<PDDocument> childDocuments = null;
        try {
            childDocuments = splitter.split(document);
            do {
                content = stripper.getText(childDocuments.get(index));
                if (++index > 5) break;
            } while (!content.contains("主要完成单位") && index < childDocuments.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (childDocuments != null && childDocuments.size() > 0) {
                for (PDDocument childDocument : childDocuments) {
                    childDocument.close();
                }
            }
        }
        return content;
    }

    @Transactional
    public void publishProject(List<PublishProjectJo> publishProjects) {
        for (PublishProjectJo publishProject : publishProjects) {
            System.out.println(publishProject.getProjectId() + " " + publishProject.isPublish());
            Optional<Project> project = projectRepository.findById(publishProject.getProjectId());
            project.ifPresent(p -> {
                p.setPublish(publishProject.isPublish());
                for (ExpertProject expertProject : p.getExperts()) {
                    expertProject.setVoted(false);
                }
                projectRepository.save(p);
            });
        }
    }

    @Transactional
    public List<PublishProjectVo> getPublishVos(){
        return projectRepository.findAll().stream()
                .map(p->{
                    PublishProjectVo projectVo = new PublishProjectVo();
                    projectVo.setId(p.getId());
                    projectVo.setName(p.getNumber()+" "+p.getName());
                    projectVo.setPublish(p.isPublish());
                    return projectVo;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
