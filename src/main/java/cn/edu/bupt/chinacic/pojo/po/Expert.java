package cn.edu.bupt.chinacic.pojo.po;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.jws.soap.SOAPBinding;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expert")
@Data
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Expert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 16)
    private String ip;
    @Column(length = 16)
    private String name;
    @Column(name = "is_voted")
    private boolean isVoted;

    @OneToMany(mappedBy = "expert", cascade = CascadeType.ALL)
    private List<ExpertProject> projects = new ArrayList<>();
}
