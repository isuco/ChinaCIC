package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.NumToName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NumNameRepository extends JpaRepository<NumToName, Long> {

    Optional<NumToName> queryByNum(String num);

}
