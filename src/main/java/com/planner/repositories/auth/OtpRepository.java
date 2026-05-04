package com.planner.repositories.auth;

import com.planner.entities.auth.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findFirstByEmailAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, LocalDateTime now);

    @Modifying
    @Query("UPDATE OtpCode o SET o.isUsed = true WHERE o.email = :email AND o.isUsed = false")
    void invalidateAllByEmail(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
