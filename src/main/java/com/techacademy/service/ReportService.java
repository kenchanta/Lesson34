package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    //全件を検索
    public List<Report> findAll(){
        return reportRepository.findAll();
    }

    //一件を検索
    public Report findByCode(Integer code) {
        Optional<Report> option = reportRepository.findById(code);
        Report report = option.orElse(null);
        return report;
    }

    public List<Report> findByEmployeeCode(String code) {
        return reportRepository.findByEmployee_Code(code);
    }

    //更新
    @Transactional //失敗すればロールバック.@Service などSpringのコンポーネントとして管理されていないと意味をなさない
    public ErrorKinds update(Report report) {
        Report original = findByCode(report.getId());

        original.setReportDate(report.getReportDate());
        original.setTitle(report.getTitle());
        original.setContent(report.getContent());
        original.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(original);
        return ErrorKinds.SUCCESS;
    }


    //削除 論理削除（フラグを立てて非表示にする）
    @Transactional
    public ErrorKinds delete(Integer code, UserDetail userDetail) {
        Report report = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);
        return ErrorKinds.SUCCESS;
        }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    public boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate) {
        return reportRepository.existsByEmployeeAndReportDate(employee, reportDate);
    }

}
