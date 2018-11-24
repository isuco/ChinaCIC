package cn.edu.bupt.chinacic.pojo.vo;

import cn.edu.bupt.chinacic.pojo.po.Project;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PublishProjectVo {

    private long id;
    private String name;
    private boolean isPublish;

    public PublishProjectVo(Project project) {
        BeanUtils.copyProperties(project, this);
    }

}
