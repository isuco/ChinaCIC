package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

    Expert queryByIp(String ip);

    boolean existsByIp(String ip);

    @Query("select count(e.id) from Expert e where e.isVoted = 1")
    int votedCount();

    @Modifying
    @Query("update Expert e set e.isVoted = 0")
    void updateUnVoted();

}
