package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("from Project p")
    List<Project> queryAll();

    @Query("from Project p where p.isPublish = 1")
    List<Project> queryByPublish();

//    @Query("from Project p where p.type='提名特等'")
    @Query("from Project p where p.prize='一等奖'")
    List<Project> querySpecial();

    @Query("from Project p where p.type='提名一等'")
    List<Project> queryFirst();

//    @Query("from Project p where p.type='提名二等'")
    @Query("from Project p where (p.type='提名二等' or (p.type='提名一等' and p.prize = '无'))")
    List<Project> querySecond();

    @Query("from Project p where p.type='提名莫得'")
    List<Project> queryThird();

    @Transactional
    @Modifying
    @Query("update Project set first_num = 0,isvoted=false,second_num=0,special_num=0,third_num=0")
    void init();

    @Transactional
    @Modifying
    @Query("delete from Project")
    void clear();
}
