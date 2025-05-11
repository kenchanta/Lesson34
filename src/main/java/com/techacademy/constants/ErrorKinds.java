package com.techacademy.constants;

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
    // 名前空白チェックエラー
    NAME_BLANK_ERROR,
    // 名前字数チェックエラー
    NAME_RANGCHECK_ERROR,
    // 該当社員がいない
    NOT_FOUND;

}
