package cn.edu.bupt.chinacic.pojo.po;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Data
public class Project {

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

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ExpertProject> experts = new ArrayList<>();

}
