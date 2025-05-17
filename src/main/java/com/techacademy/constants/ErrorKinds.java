package com.techacademy.constants;

import java.util.ArrayList;
import java.util.Arrays;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数チェックエラー
    RANGECHECK_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,
    // 日付チェックエラー
    DATECHECK_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS,

    //-------下記から追加-------

    // 名前空白チェックエラー
    NAME_BLANK_ERROR,
    // 名前字数チェックエラー
    NAME_RANGCHECK_ERROR,

    // レポート空白チェックエラー（氏名・タイトル・コンテンツ）
    REPORT_BLANK_ERROR,
    // レポートタイトル字数チェックエラー
    TITILE_RANGCHECK_ERROR,
    //　レポート内容字数チェックエラー
    CONTENT_RANGCHECK_ERROR,
    //　レポート重複エラー（同日同人物）
    DUPLICATE_REPORT_ERROR,


    // 該当社員がいない
    NOT_FOUND;
}
