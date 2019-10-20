package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import cn.edu.bupt.chinacic.pojo.po.key.ExpertProjectPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ExpertProjectRepository extends JpaRepository<ExpertProject, ExpertProjectPrimaryKey> {
    @Transactional
    @Modifying
    @Query("delete from ExpertProject")
    void clear();
}
