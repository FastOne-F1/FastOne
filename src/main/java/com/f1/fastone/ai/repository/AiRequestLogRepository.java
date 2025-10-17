package com.f1.fastone.ai.repository;

import com.f1.fastone.ai.entity.AiRequestLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRequestLogRepository extends JpaRepository<AiRequestLog, UUID> {
}