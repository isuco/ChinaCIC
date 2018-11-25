package cn.edu.bupt.chinacic.repository;

import cn.edu.bupt.chinacic.pojo.po.NumToName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumNameRepository extends JpaRepository<NumToName, Long>{

    NumToName queryByNum(String num);

}
