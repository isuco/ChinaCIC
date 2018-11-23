package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import cn.edu.bupt.chinacic.repository.ExpertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private ExpertRepository expertRepository;

    @Autowired
    public void setExpertRepository(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    @Transactional
    public Expert registryUser(String ip, String name) {
        Expert expert = new Expert();
        expert.setIp(ip);
        expert.setName(name);
        return this.expertRepository.save(expert);
    }

}
