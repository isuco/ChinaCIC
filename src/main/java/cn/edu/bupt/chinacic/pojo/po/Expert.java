package cn.edu.bupt.chinacic.pojo.po;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expert")
@Data
public class Expert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 16)
    private String ip;
    @Column(length = 16)
    private String name;

    @OneToMany(mappedBy = "expert", cascade = CascadeType.ALL)
    private List<ExpertProject> projects = new ArrayList<>();
}
