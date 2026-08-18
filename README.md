# 💳 AtividadeDSCartão

Aplicativo Android desenvolvido em Kotlin como atividade avaliativa da disciplina de **Programação Para Dispositivos Móveis**.

## 📱 Sobre o Projeto

Simulação de uma interface de cartão de crédito com visualização em tempo real dos dados inseridos pelo usuário. O cartão é exibido no topo da tela e reflete instantaneamente as informações digitadas no formulário abaixo.

## ✨ Funcionalidades

- Exibição dinâmica do número do cartão com máscara automática (blocos de 4 dígitos)
- Atualização em tempo real do nome do titular
- Máscara automática de validade no formato MM/AA
- Animação de flip do cartão ao focar nos campos de Validade e CVV
- Identificação automática da bandeira (Visa / Mastercard) com troca de cor e logo
- Validação dos campos antes de finalizar o pagamento

## 🛠️ Tecnologias Utilizadas

- Kotlin
- ConstraintLayout
- CardView
- ViewFlipper
- Material Components (TextInputLayout)
- TextWatcher para máscaras em tempo real

## 🚀 Como Rodar

1. Clone o repositório
2. Abra no Android Studio
3. Sincronize o Gradle
4. Rode em um dispositivo físico ou emulador com API 24+

## 📋 Validações

- Número do cartão deve ter 16 dígitos
- Nome do titular deve ter ao menos 3 caracteres
- Validade deve seguir o padrão MM/AA
- CVV deve ter 3 dígitos

<img width="407" height="678" alt="image" src="https://github.com/user-attachments/assets/43c26da6-71af-488c-a1ac-e14837440047" />


## 👨‍💻 Autor

Desenvolvido por **Arthur Xavier** 
