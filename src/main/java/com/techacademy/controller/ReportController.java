package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportController(ReportService reportService, ReportRepository reportRepository) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        List<Report> reports;

        //追加：一般権限者の制限
        if(userDetail.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            // 管理者：すべての日報を取得
            reports = reportService.findAllActive();
        }else {
            // 一般ユーザー：自分が登録した日報のみ取得
            String code = userDetail.getEmployee().getCode();
            reports = reportService.findByEmployeeCode(code);
            }

        model.addAttribute("reportList", reports);
        model.addAttribute("listSize", reports.size());
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable("code") Integer code, Model model) {

        model.addAttribute("report", reportService.findByCode(code));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping("/add")
    public String createReport(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        report.setEmployee(userDetail.getEmployee());
        model.addAttribute("report", report);
        return "reports/add";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        if(res.hasErrors()){
            return createReport(report, userDetail, model);
        }
        // 入力チェック(アノテーションバリデーション)。エンティティの@Validated（または @Valid）アノテーション付きのオブジェクトのバリデーション結果をチェック
        if(report.getReportDate() == null) {
            ErrorKinds result = ErrorKinds.REPORT_BLANK_ERROR;
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return createReport(report, userDetail, model);
        }
        //入力チェック（独自（手動チェック））。バリデーションアノテーションでは表現できない「ロジックチェック」などは、自分でBindingResult にエラーを追加
        Employee loginUser = userDetail.getEmployee();
            if(reportService.existsByEmployeeAndReportDate(loginUser, report.getReportDate())) {
                ErrorKinds result = ErrorKinds.DUPLICATE_REPORT_ERROR;
                res.rejectValue(
                        "reportDate",
                        ErrorMessage.getErrorName(result),    // エラーコード ("reportError")
                        ErrorMessage.getErrorValue(result)    // エラーメッセージ ("既に登録されている日付です")
                    );
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return createReport(report, userDetail, model);
            }

         report.setEmployee(loginUser);
         reportService.save(report);

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping("/{code}/update")
    public String update(@PathVariable("code") Integer code, Model model) {
        Report report = reportService.findByCode(code);
        model.addAttribute("report", report);
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{code}/update")
    public String reportUpdate(@PathVariable("code") Integer code, @Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        //@ModelAttribute（または引数の Report report）は フォームで送信されたフィールドだけを埋める＝フォーム側に値を埋める or POST時に補完する必要がある。(nullのまま渡すと画面がクラッシュする)
        report.setEmployee(userDetail.getEmployee());
        Report original = reportService.findByCode(code);

        if(res.hasErrors()) {
            model.addAttribute("report",report);
            return "reports/update";
        }

        //１)画面からうけとった日付＆テーブルの方にはいっている日報の日付（更新元）1件分で比較＝日付がかわったかどうかの確認
        if(!report.getReportDate().isEqual(original.getReportDate())){
            List<Report> reportList = reportService.findAllActive(); //findAllは論理削除されたレポートも含めてすべて取得しているため、「削除したはずのレポートが表示される」状態になる。
            //２)変更したほうの日付と同じのがないか、日報一覧と比較
            for(Report ReportForCheck : reportList) {
                if(ReportForCheck.getReportDate().isEqual(report.getReportDate())) {
                    model.addAttribute("reportError", "既に登録されている日付です");
                    return "reports/update";
                }
            }
        }

        reportService.update(report);
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable("code") Integer code, @AuthenticationPrincipal UserDetail userdetail, Model model){
        ErrorKinds result = reportService.delete(code, userdetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/reports";
    }
}