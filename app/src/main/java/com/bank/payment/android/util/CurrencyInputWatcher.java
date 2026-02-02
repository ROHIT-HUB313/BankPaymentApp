package com.bank.payment.android.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * TextWatcher for formatting currency input.
 * Formats input as Indian Rupee with commas.
 */
public class CurrencyInputWatcher implements TextWatcher {

    private final EditText editText;
    private String current = "";
    private final DecimalFormat formatter;

    public CurrencyInputWatcher(EditText editText) {
        this.editText = editText;
        // Indian number format: 1,00,000.00
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("en", "IN"));
        formatter = new DecimalFormat("##,##,##0.00", symbols);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals(current))
            return;

        editText.removeTextChangedListener(this);

        String cleanString = s.toString()
                .replaceAll("[₹,\\s]", "")
                .replaceAll("[^\\d.]", "");

        if (cleanString.isEmpty()) {
            current = "";
            editText.setText(current);
        } else {
            try {
                double parsed = Double.parseDouble(cleanString);
                String formatted = "₹ " + formatter.format(parsed);
                current = formatted;
                editText.setText(formatted);
                editText.setSelection(formatted.length());
            } catch (NumberFormatException e) {
                // Invalid input, keep as is
            }
        }

        editText.addTextChangedListener(this);
    }

    /**
     * Get the raw numeric value from the formatted string.
     */
    public static double getNumericValue(String formatted) {
        try {
            String clean = formatted
                    .replaceAll("[₹,\\s]", "")
                    .replaceAll("[^\\d.]", "");
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
