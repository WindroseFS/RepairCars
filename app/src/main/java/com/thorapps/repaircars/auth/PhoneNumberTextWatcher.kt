package com.thorapps.repaircars.auth

import android.text.Editable
import android.text.TextWatcher

class PhoneNumberTextWatcher(private val editText: android.widget.EditText) : TextWatcher {
    private var isFormatting = false
    private var deletedDash = false
    private var dashPosition = 0

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (isFormatting) return

        // Verificar se um traço foi deletado
        if (count == 1 && after == 0) {
            val charDeleted = s?.getOrNull(start)
            if (charDeleted == '-' || charDeleted == ' ' || charDeleted == '(' || charDeleted == ')') {
                deletedDash = true
                dashPosition = start
            } else {
                deletedDash = false
            }
        } else {
            deletedDash = false
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return
        isFormatting = true

        val phone = s.toString().replace(Regex("[^\\d]"), "")

        if (deletedDash && dashPosition > 0) {
            // Se um traço foi deletado, mover cursor para posição correta
            editText.setSelection(dashPosition - 1)
        } else {
            // Formatar o telefone
            val formatted = formatPhoneNumber(phone)
            s?.replace(0, s.length, formatted)

            // Posicionar cursor no final
            editText.setSelection(formatted.length)
        }

        isFormatting = false
    }

    private fun formatPhoneNumber(phone: String): String {
        return when {
            phone.length <= 2 -> phone
            phone.length <= 6 -> "(${phone.substring(0, 2)}) ${phone.substring(2)}"
            phone.length <= 10 -> "(${phone.substring(0, 2)}) ${phone.substring(2, 6)}-${phone.substring(6)}"
            else -> "(${phone.substring(0, 2)}) ${phone.substring(2, 7)}-${phone.substring(7, 11)}"
        }
    }
}