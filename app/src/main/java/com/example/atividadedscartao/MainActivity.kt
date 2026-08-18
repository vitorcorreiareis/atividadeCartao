package com.example.atividadedscartao

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ── Referências ao XML ──────────────────────────────────
        val flipper      = findViewById<ViewFlipper>(R.id.cardFlipper)
        val cardFront    = findViewById<CardView>(R.id.cardFront)
        val cardBack     = findViewById<CardView>(R.id.cardBack)

        val etNumber     = findViewById<TextInputEditText>(R.id.etNumber)
        val etName       = findViewById<TextInputEditText>(R.id.etName)
        val etExpiry     = findViewById<TextInputEditText>(R.id.etExpiry)
        val etCVV        = findViewById<TextInputEditText>(R.id.etCVV)

        val tvCardNumber = findViewById<TextView>(R.id.tvCardNumber)
        val tvCardName   = findViewById<TextView>(R.id.tvCardName)
        val tvCardExpiry = findViewById<TextView>(R.id.tvCardExpiry)
        val tvCardCVV    = findViewById<TextView>(R.id.tvCardCVV)
        val imgBrand     = findViewById<ImageView>(R.id.imgBrandLogo)
        val btnProcess   = findViewById<Button>(R.id.btnProcess)

        // ── DESAFIO 1: Flip do cartão ───────────────────────────
        // Validade e CVV → mostram o VERSO
        etExpiry.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && flipper.displayedChild != 1) flipper.displayedChild = 1
            else if (!hasFocus && flipper.displayedChild != 0) flipper.displayedChild = 0
        }
        etCVV.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && flipper.displayedChild != 1) flipper.displayedChild = 1
            else if (!hasFocus && flipper.displayedChild != 0) flipper.displayedChild = 0
        }
        // Número e Nome → mostram a FRENTE
        listOf(etNumber, etName).forEach { field ->
            field.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && flipper.displayedChild != 0) flipper.displayedChild = 0
            }
        }

        // ── NÚMERO DO CARTÃO + DESAFIO 2: Bandeira ─────────────
        etNumber.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) { isUpdating = false; return }

                val digits = s.toString().replace(" ", "")
                val masked = digits.chunked(4).joinToString(" ")

                isUpdating = true
                etNumber.setText(masked)
                etNumber.setSelection(masked.length)

                tvCardNumber.text = masked.ifEmpty { "**** **** **** ****" }

                when {
                    digits.startsWith("4") -> {
                        imgBrand.setImageResource(R.drawable.logo_visa)
                        val color = Color.parseColor("#1A1F71")
                        cardFront.setCardBackgroundColor(color)
                        cardBack.setCardBackgroundColor(color)
                    }
                    digits.length >= 2 && digits.substring(0, 2).toIntOrNull() in 51..55 -> {
                        imgBrand.setImageResource(R.drawable.logo_mastercard)
                        val color = Color.parseColor("#252525")
                        cardFront.setCardBackgroundColor(color)
                        cardBack.setCardBackgroundColor(color)
                    }
                    else -> {
                        imgBrand.setImageDrawable(null)
                        val color = Color.parseColor("#FFF200")
                        cardFront.setCardBackgroundColor(color)
                        cardBack.setCardBackgroundColor(color)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── NOME DO TITULAR ─────────────────────────────────────
        etName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvCardName.text =
                    if (s.isNullOrEmpty()) "NOME DO TITULAR"
                    else s.toString().uppercase()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── VALIDADE (MM/AA) ────────────────────────────────────
        etExpiry.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) { isUpdating = false; return }

                val digits = s.toString().replace("/", "")

                if (digits.length >= 2) {
                    val formatted = digits.substring(0, 2) + "/" + digits.substring(2)
                    isUpdating = true
                    etExpiry.setText(formatted)
                    etExpiry.setSelection(formatted.length)
                    tvCardExpiry.text = formatted
                } else {
                    tvCardExpiry.text = digits.ifEmpty { "MM/AA" }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── CVV ──────────────────────────────────────────────────
        etCVV.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvCardCVV.text =
                    if (s.isNullOrEmpty()) "***"
                    else "*".repeat(s.length)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── VALIDAÇÃO ────────────────────────────────────────────
        btnProcess.setOnClickListener {
            val numero = etNumber.text.toString().replace(" ", "")
            val nome   = etName.text.toString().trim()
            val expiry = etExpiry.text.toString()
            val cvv    = etCVV.text.toString()

            when {
                numero.length != 16 -> {
                    etNumber.error = "O cartão deve ter 16 dígitos"
                    etNumber.requestFocus()
                }
                nome.length < 3 -> {
                    etName.error = "Nome deve ter ao menos 3 letras"
                    etName.requestFocus()
                }
                expiry.length != 5 -> {
                    etExpiry.error = "Validade inválida (MM/AA)"
                    etExpiry.requestFocus()
                }
                cvv.length != 3 -> {
                    etCVV.error = "CVV deve ter 3 dígitos"
                    etCVV.requestFocus()
                }
                else -> {
                    Toast.makeText(this, "✅ Pagamento Aprovado!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}