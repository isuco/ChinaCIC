package cn.edu.bupt.chinacic.util;

public enum Prize {

    FIRST("一等奖"),
    SECOND("二等奖"),
    THIRD("三等奖"),
    SPECIAL("特等奖");

    String type;

    Prize(String type) {
        this.type = type;
    }

}
