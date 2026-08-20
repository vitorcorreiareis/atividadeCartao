# 💳 Atividade Cartão

Aplicativo Android desenvolvido em Kotlin como atividade avaliativa da disciplina de **Programação Para Dispositivos Móveis**.

## 📱 Sobre o Projeto

Simulação de uma interface de cartão de crédito com visualização em tempo real dos dados inseridos pelo usuário. O cartão é exibido no topo da tela e reflete instantaneamente as informações digitadas no formulário abaixo.

## ✨ Funcionalidades

- Exibição dinâmica do número do cartão com máscara automática (blocos de 4 dígitos)
- Atualização em tempo real do nome do titular
- Máscara automática de validade no formato MM/AA
- Animação de flip do cartão ao focar no campo CVV (retorna à frente ao focar nos demais campos)
- Identificação automática da bandeira (Visa / Mastercard / Outra) com troca de cor e logotipo
- Validação dos campos antes de liberar o processamento

## 🛠️ Tecnologias Utilizadas

- Kotlin
- ConstraintLayout
- CardView
- ObjectAnimator (rotationY) para a animação de flip
- Material Components (TextInputLayout)
- TextWatcher para máscaras em tempo real

## 🚀 Como Rodar

1. Clone o repositório
2. Abra no Android Studio (**Empty Views Activity**, não Compose)
3. Sincronize o Gradle
4. Rode em um dispositivo físico ou emulador com API 23+

## 📋 Validações

- Número do cartão deve ter 16 dígitos
- Nome do titular deve ter ao menos 3 caracteres
- Validade segue o padrão MM/AA (aplicado via máscara)
- CVV limitado a 3 dígitos (aplicado via `maxLength` no campo)

## ⚠️ Observações técnicas

- O componente `CardFlipper` citado originalmente na atividade não existe no SDK do Android/AndroidX. O efeito de giro foi implementado manualmente com duas `CardView` sobrepostas animadas via `ObjectAnimator`.
- O "logotipo" da bandeira é um texto estilizado, não uma imagem — os logos oficiais de Visa/Mastercard são marca registrada.

<!-- Substitua pela URL do seu próprio screenshot antes de publicar -->
https://youtu.be/BD4o6HGQJNQ

## 👨‍💻 Autor

Desenvolvido por **Vitor Augusto Correia dos Reis**
