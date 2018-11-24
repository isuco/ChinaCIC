package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

    Expert queryByIp(String ip);

    @Modifying
    @Query("update Expert e set e.isVoted = 0")
    void updateUnVoted();

}
