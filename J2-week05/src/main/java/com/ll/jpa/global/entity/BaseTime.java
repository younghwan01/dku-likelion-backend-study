package com.ll.jpa.global.entity;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTime extends BaseEntity {
    @CreatedDate @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;
    @LastModifiedDate @Column(nullable = false)
    private LocalDateTime modifyDate;
}
