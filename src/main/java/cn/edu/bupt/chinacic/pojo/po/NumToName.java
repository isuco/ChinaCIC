package cn.edu.bupt.chinacic.pojo.po;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "num_name")
@Data
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NumToName {

    @Id
    @GeneratedValue
    private long id;

    private String num;

    private String name;

}
