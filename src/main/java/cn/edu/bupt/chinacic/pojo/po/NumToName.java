package cn.edu.bupt.chinacic.pojo.po;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "num_name")
@Data
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NumToName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String num;

    private String name;

}
