package cn.edu.bupt.chinacic.pojo.po;

import cn.edu.bupt.chinacic.service.ConfigService;
import cn.edu.bupt.chinacic.util.Prize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
public class Project {

    public Project(Project project) {
        BeanUtils.copyProperties(project, this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 10)
    private String number;

    private String name;

    @Column(name = "main_com_unit")
    private String mainCompUnit;

    @Column(name = "reco_unit")
    private String recoUnit;

    @Column(length = 32)
    private String type;

    @Column(name = "is_publish")
    private boolean isPublish;


    @Column(name = "special_num")
    private int specialNum;

    @Column(name = "first_num")
    private int firstNum;

    @Column(name = "second_num")
    private int secondNum;

    @Column(name = "third_num")
    private int thirdNum;

    @Column(name = "project_path")
    private String projectPath;

    private String prize;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ExpertProject> experts = new ArrayList<>();

    public void resetBeforeVote(){
        if(ConfigService.prize==Prize.SPECIAL){
            specialNum=0;
        }else if(ConfigService.prize==Prize.FIRST){
            firstNum=0;
        }else if(ConfigService.prize== Prize.SECOND){
            secondNum=0;
        }else if(ConfigService.prize== Prize.THIRD){
            thirdNum=0;
        }
    }
}
