package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        List<Report> reports = reportService.findAll();
        model.addAttribute("reportList", reports);
        model.addAttribute("listSize", reports.size());
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable("code") String code, Model model) {

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
    @GetMapping("/update")
    public String update(@RequestParam("code") String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        Report report = reportService.findByCode(code);
        model.addAttribute("report", report);
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/update")
    public String reportUpdate(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        //@ModelAttribute（または引数の Report report）は フォームで送信されたフィールドだけを埋める＝フォーム側に値を埋める or POST時に補完する必要がある。(nullのまま渡すと画面がクラッシュする)
        report.setEmployee(userDetail.getEmployee());

        //１画面からうけとった日付＆テーブルの方にはいっている日報の日付（更新元）1件分で比較＝日付がかわったかどうかの確認、２変更したほうの日付と同じのがないかすでにあるすべての日報一覧と比較
        if(res.hasErrors()) {
            return "reports/update";
        }
        reportService.update(report);
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable("code") String code, @AuthenticationPrincipal UserDetail userdetail, Model model){
        ErrorKinds result = reportService.delete(code, userdetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/reports";
    }
}