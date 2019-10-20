package cn.edu.bupt.chinacic.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeTreeVo {

    private String text;
    private boolean clickable;
    private String filePath;

}
