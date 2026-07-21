package az.ilkin.eis.repository;

import az.ilkin.eis.entity.Classroom;
import az.ilkin.eis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClassRepository extends JpaRepository<Classroom,Long>{

    boolean existsByName(String name);

    @Query("SELECT c FROM Classroom c JOIN c.teachers t WHERE t = :user")
    List<Classroom>findByTeachersContaining(@Param("user") User user);

    @Query("SELECT c FROM Classroom c JOIN c.students s WHERE s = :user")
    List<Classroom> findByStudentsContaining(@Param("user") User user);

}


