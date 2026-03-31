package com.vn.vodka_server.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.User;
import com.vn.vodka_server.util.EGender;
import com.vn.vodka_server.util.EProvider;
import com.vn.vodka_server.util.ERole;
import com.vn.vodka_server.util.EStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        boolean existsByEmail(String email);

        boolean existsByPhone(String phone);

        Optional<User> findByEmail(String email);

        @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :from AND u.createdAt <= :to")
        long countByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        // Lấy danh sách user bao gồm lấy danh sách, lọc, phân trang, tìm kiếm, sắp xếp
        @Query("""
                        SELECT u FROM User u
                        WHERE (:search IS NULL
                               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                               OR u.phone LIKE CONCAT('%', :search, '%'))
                        AND (:status IS NULL OR u.status = :status)
                        AND (:provider IS NULL OR u.provider = :provider)
                        AND (:gender IS NULL OR u.gender = :gender)
                        AND (:role IS NULL OR u.role = :role)
                        """)
        Page<User> findAllWithFilters(
                        @Param("search") String search,
                        @Param("status") EStatus status,
                        @Param("provider") EProvider provider,
                        @Param("gender") EGender gender,
                        @Param("role") ERole role,
                        Pageable pageable);

        // Đếm user (dùng cho thống kê)(không đếm admin)
        long countByRole(ERole role);

        long countByStatusAndRole(EStatus status, ERole role);

        @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.createdAt >= :from AND u.createdAt <= :to")
        long countByRoleAndCreatedAtBetween(@Param("role") ERole role, @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);
}
