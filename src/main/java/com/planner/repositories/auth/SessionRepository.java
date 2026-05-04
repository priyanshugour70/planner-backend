package com.planner.repositories.auth;

import com.planner.entities.auth.Session;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.accessToken = :accessToken AND s.isRevoked = false AND s.active = true")
    @Cacheable(value = "sessions", key = "#accessToken", unless = "#result == null")
    Optional<Session> findByAccessTokenAndIsRevokedFalse(@Param("accessToken") String accessToken);

    @Query("SELECT s FROM Session s WHERE s.refreshToken = :refreshToken AND s.isRevoked = false AND s.active = true")
    Optional<Session> findByRefreshTokenAndIsRevokedFalse(@Param("refreshToken") String refreshToken);

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.isRevoked = false AND s.active = true ORDER BY s.createdAt DESC")
    List<Session> findActiveSessionsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Session s SET s.isRevoked = true, s.revokedAt = CURRENT_TIMESTAMP, s.revokeReason = :reason WHERE s.user.id = :userId AND s.isRevoked = false")
    @CacheEvict(value = "sessions", allEntries = true)
    int revokeAllSessionsByUserId(@Param("userId") Long userId, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE Session s SET s.isRevoked = true, s.revokedAt = CURRENT_TIMESTAMP, s.revokeReason = :reason WHERE s.id = :sessionId")
    @CacheEvict(value = "sessions", allEntries = true)
    int revokeSessionById(@Param("sessionId") Long sessionId, @Param("reason") String reason);
}
