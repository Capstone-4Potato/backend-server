package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Refresh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);
    @Transactional
    void deleteByRefresh(String refresh);
    @Query("SELECT r.refresh FROM refresh r WHERE r.socialId = :socialId")
    String findRefreshBySocialId(@Param("socialId") String socialId);
}
