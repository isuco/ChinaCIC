package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("from Project p where p.isPublish = 1")
    List<Project> queryByPublish();
}
