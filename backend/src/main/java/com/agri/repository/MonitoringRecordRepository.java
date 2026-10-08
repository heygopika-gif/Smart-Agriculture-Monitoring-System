package com.agri.repository;

import com.agri.entity.MonitoringRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonitoringRecordRepository extends JpaRepository<MonitoringRecord, Long> {

    List<MonitoringRecord> findAllByOrderByRecordedAtDesc(Pageable pageable);

    List<MonitoringRecord> findByLocationOrderByRecordedAtDesc(String location, Pageable pageable);

    Optional<MonitoringRecord> findTopByLocationOrderByRecordedAtDesc(String location);
}
