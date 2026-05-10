package az.ilkin.eis.repository;

import az.ilkin.eis.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Classroom,Long>{

    boolean existsByName(String name);
}


