
package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    //ReportとEmployeeテーブルの連結に利用
    @ManyToOne(fetch = FetchType.EAGER) // 「意味：「このエンティティ（Report）は 1人のEmployeeに属している」　強制的に一緒に取得する。　Fetch~の意味」日報データを取得したときに、自動的に紐づくEmployeeも一緒に取得するという指定。
    @JoinColumn(name = "employee_code", referencedColumnName = "code") // employeeCodeは勝手につくられる。どの列がどのテーブルと紐づいているかを明示的に指定
    private Employee employee;

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    // 日付
    @Column(nullable = false)
    @NotNull
    private LocalDate reportDate;

    // タイトル
    @Column(columnDefinition="VARCHAR(100)", nullable = false)
    @NotEmpty
    @Length(max = 100)
    private String title;

    // 内容
    @Column(length = 600, nullable = false)
    @NotEmpty
    @Length(max = 600)
    private String content;

    // @JoinColumn(name = "employee_code", referencedColumnName = "code")の追記により削除（employeeCode フィールドがあると、JPAが競合・混乱する）
    //private String employeeCode;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}