package cn.edu.bupt.chinacic.pojo.po.key;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
public class ExpertProjectPrimaryKey implements Serializable{

    @Column(name = "expert_id")
    private long expertId;

    @Column(name = "project_id")
    private long projectId;

}
