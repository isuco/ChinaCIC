package cn.edu.bupt.chinacic.util;

import lombok.Data;

public enum Prize {

    FIRST("一等奖"),
    SECOND("二等奖"),
    THIRD("三等奖"),
    SPECIAL("特等奖"),
    ALL("初评");

    public String type;

    Prize(String type) {
        this.type = type;
    }

}
