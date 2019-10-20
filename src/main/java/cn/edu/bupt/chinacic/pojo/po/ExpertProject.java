package cn.edu.bupt.chinacic.pojo.po;

import cn.edu.bupt.chinacic.pojo.po.key.ExpertProjectPrimaryKey;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "expert_project")
@Data
public class ExpertProject {

    @EmbeddedId
    private ExpertProjectPrimaryKey id;

    @Column(name = "is_voted")
    private boolean isVoted;

    @Column(name = "special_num")
    private int specialNum;

    @Column(name = "first_num")
    private int firstNum;

    @Column(name = "second_num")
    private int secondNum;

    @Column(name = "third_num")
    private int thirdNum;

    @ManyToOne
    @MapsId("expertId")
    private Expert expert;

    @ManyToOne
    @MapsId("projectId")
    private Project project;

}
