package com.ll.jpa.domain.surl.repository;
import com.ll.jpa.domain.surl.entity.Surl;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SurlRepository extends JpaRepository<Surl, Long> {
    @EntityGraph(attributePaths = "author") List<Surl> findAllByOrderByIdAsc();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Surl s where s.id = :id")
    Optional<Surl> findForUpdate(@Param("id") Long id);
}
