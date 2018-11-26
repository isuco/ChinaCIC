package cn.edu.bupt.chinacic.pojo.vo;

import lombok.Data;

@Data
public class VoteItemVo {

    private long id;
    private String num;
    private boolean voted;
    private String projectName;
    private String compUnit;
    private String prize;

}
