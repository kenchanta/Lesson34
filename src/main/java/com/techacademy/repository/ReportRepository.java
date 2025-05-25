package com.techacademy.repository;


import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report,Integer> {
    //追加：指定された従業員（Employee）と報告日（reportDate）に一致するレコードがデータベースに存在するかどうかを確認する。
    // Spring Data JPA が「existsBy〇〇And〇〇」という命名ルールを読んでSQLを自動生成

    //findBy..のかわりに使える(finByのPowerUPver.)　存在するかどうか？の判定
    boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
}