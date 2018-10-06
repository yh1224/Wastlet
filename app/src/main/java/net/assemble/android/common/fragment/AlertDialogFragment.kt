package net.assemble.android.common.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AlertDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!)
                .setTitle(arguments?.getString(ARG_DIALOG_TITLE))
                .setMessage(arguments?.getString(ARG_DIALOG_MESSAGE))
                .setPositiveButton(arguments?.getString(ARG_DIALOG_OK_TEXT), { _, _ ->
                    dismiss()
                })
                .create()
    }

    companion object {
        private const val ARG_DIALOG_TITLE = "title"
        private const val ARG_DIALOG_MESSAGE = "message"
        private const val ARG_DIALOG_OK_TEXT = "okText"

        /**
         * メッセージダイアログを表示
         *
         * @param title ダイアログのタイトル
         * @param message メッセージ
         * @return CommonDialogFragment
         */
        fun newInstance(title: String, message: String, okText: String): AlertDialogFragment =
                AlertDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_DIALOG_TITLE, title)
                        putString(ARG_DIALOG_MESSAGE, message)
                        putString(ARG_DIALOG_OK_TEXT, okText)
                    }
                }
    }
}
