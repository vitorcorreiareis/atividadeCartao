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
        // findViewById busca cada view pelo id definido no layout.
        // Feito uma vez só no onCreate para não repetir a busca depois.
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
        // Ideia: campos "sensíveis" (validade/CVV) mostram o verso do
        // cartão quando o usuário está preenchendo eles; campos "públicos"
        // (número/nome) mostram a frente.

        // Ao focar em Validade → mostra o VERSO (índice 1 do ViewFlipper)
        // Ao perder o foco → volta pra FRENTE (índice 0)
        etExpiry.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && flipper.displayedChild != 1) flipper.displayedChild = 1
            else if (!hasFocus && flipper.displayedChild != 0) flipper.displayedChild = 0
        }
        // Mesmo comportamento para o campo CVV
        etCVV.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && flipper.displayedChild != 1) flipper.displayedChild = 1
            else if (!hasFocus && flipper.displayedChild != 0) flipper.displayedChild = 0
        }
        // Número e Nome → sempre voltam pra FRENTE quando ganham foco
        listOf(etNumber, etName).forEach { field ->
            field.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && flipper.displayedChild != 0) flipper.displayedChild = 0
            }
        }

        // ── NÚMERO DO CARTÃO + DESAFIO 2: Detecção de Bandeira ─────
        etNumber.addTextChangedListener(object : TextWatcher {
            // Flag de controle: evita loop infinito, já que setText() dentro
            // do próprio listener dispararia onTextChanged de novo
            private var isUpdating = false

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Se essa chamada foi causada pelo nosso próprio setText,
                // ignora e reseta a flag (não reprocessa)
                if (isUpdating) { isUpdating = false; return }

                // Remove espaços existentes pra pegar só os dígitos "crus"
                val digits = s.toString().replace(" ", "")
                // Recoloca um espaço a cada 4 dígitos (formato "0000 0000...")
                val masked = digits.chunked(4).joinToString(" ")

                // Aplica o texto formatado de volta no campo
                isUpdating = true
                etNumber.setText(masked)
                // Move o cursor pro final do texto formatado
                // (ATENÇÃO: isso quebra edição no meio do texto, ver nota abaixo)
                etNumber.setSelection(masked.length)

                // Espelha o número formatado no cartão visual (frente)
                tvCardNumber.text = masked.ifEmpty { "**** **** **** ****" }

                // Detecção simplificada de bandeira pelos primeiros dígitos (BIN)
                when {
                    // Visa: começa sempre com "4"
                    digits.startsWith("4") -> {
                        imgBrand.setImageResource(R.drawable.logo_visa)
                        val color = Color.parseColor("#1A1F71") // azul Visa
                        cardFront.setCardBackgroundColor(color)
                        cardBack.setCardBackgroundColor(color)
                    }
                    // Mastercard: BIN 51 a 55 (faixa simplificada;
                    // no mundo real também existe a faixa 2221-2720)
                    digits.length >= 2 && digits.substring(0, 2).toIntOrNull() in 51..55 -> {
                        imgBrand.setImageResource(R.drawable.logo_mastercard)
                        val color = Color.parseColor("#252525") // preto Mastercard
                        cardFront.setCardBackgroundColor(color)
                        cardBack.setCardBackgroundColor(color)
                    }
                    // Bandeira não identificada (ou campo vazio) → volta ao
                    // amarelo padrão e remove o logo
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
        // Simplesmente espelha o texto digitado em maiúsculas no cartão visual
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

                // Remove a "/" pra trabalhar só com os dígitos
                val digits = s.toString().replace("/", "")

                // Assim que tiver 2+ dígitos, insere a "/" automaticamente
                // entre mês e ano (ex: "1234" -> "12/34")
                if (digits.length >= 2) {
                    val formatted = digits.substring(0, 2) + "/" + digits.substring(2)
                    isUpdating = true
                    etExpiry.setText(formatted)
                    etExpiry.setSelection(formatted.length)
                    tvCardExpiry.text = formatted
                } else {
                    // Com 0 ou 1 dígito ainda não insere a barra
                    tvCardExpiry.text = digits.ifEmpty { "MM/AA" }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // ── CVV ──────────────────────────────────────────────────
        // No cartão visual, nunca mostra os dígitos reais do CVV,
        // só asteriscos na mesma quantidade de caracteres digitados
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
        // Executada só quando o botão "FINALIZAR PAGAMENTO" é clicado
        btnProcess.setOnClickListener {
            val numero = etNumber.text.toString().replace(" ", "")
            val nome   = etName.text.toString().trim()
            val expiry = etExpiry.text.toString()
            val cvv    = etCVV.text.toString()

            // Validação em cascata: para na primeira regra que falhar,
            // mostra o erro no campo (TextInputEditText.error) e foca nele
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
                // Só chega aqui se TODAS as condições acima passarem
                else -> {
                    Toast.makeText(this, "✅ Pagamento Aprovado!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}