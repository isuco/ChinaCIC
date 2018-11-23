package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import cn.edu.bupt.chinacic.pojo.po.key.ExpertProjectPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertProjectRepository extends JpaRepository<ExpertProject, ExpertProjectPrimaryKey> {
}
