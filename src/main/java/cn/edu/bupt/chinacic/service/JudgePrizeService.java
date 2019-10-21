package cn.edu.bupt.chinacic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JudgePrizeService {

    @Value("${special-threshold}")
    private int specialThreshold;

    @Value("${first-threshold}")
    private int firstThreshold;

    @Value("${second-threshold}")
    private int secondThreshold;

    @Value("${third-threshold}")
    private int thirdThreshold;

    public boolean isSpecial(int special) {
        return special >= specialThreshold;
    }

    public boolean isFirst(int special, int first) {
        return !isSpecial(special) &&  first >= firstThreshold;
    }

    public boolean isSecond(int special, int first, int second) {
        return !isSpecial(special) && !isFirst(special, first) && second >= secondThreshold;
    }

    public boolean isThird(int special, int first, int second, int third) {
        return !isSpecial(special) && !isFirst(special, first) && !isSecond(special, first, second) &&
                third >= thirdThreshold;
    }

}
