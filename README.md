# Controle de Produção — Projeto Final (AF) · Desenvolvimento Mobile

Aplicativo Android (Java + layouts XML) para organizar **registros, listas de controle (checklists) e
anotações operacionais** da área de produção. Funciona **100% offline**: os dados ficam em memória
(`ArrayList`), sem internet, sem serviços externos e sem banco de dados.

## Telas (Activities)

| Activity | Layout | Função |
|---|---|---|
| `MainActivity` | `activity_main.xml` | Painel inicial: totais (total / pendentes / concluídos), alerta de pendências de prioridade alta, botões para cadastro e listagem. Painel atualizado em `onResume()`. |
| `CadastroActivity` | `activity_cadastro.xml` | **CRUD completo**: inclui (Create), carrega para edição (Read), salva alterações (Update) e exclui com confirmação (Delete). Validação de campos obrigatórios. |
| `ListagemActivity` | `activity_listagem.xml` + `item_registro.xml` | `ListView` com adapter próprio, busca por texto, filtro por situação (chips) e FAB "Novo". Toque → detalhes; toque longo → edição. Lista recarregada em `onResume()`. |
| `DetalheActivity` | `activity_detalhe.xml` | Visualização completa do registro, marcar como concluído/pendente (Snackbar com **Desfazer**) e botão Editar. Fecha sozinha se o registro foi excluído. |

Classes de apoio (mesmo pacote):

- `Registro` — modelo de dados.
- `RegistroRepository` — Singleton com o `ArrayList` e todas as operações de CRUD/busca.
- `RegistroAdapter` — adapter da `ListView` (padrão ViewHolder).

## Requisitos atendidos

1. **Múltiplas Activities** declaradas no `AndroidManifest.xml` (Main, Cadastro, Listagem e Detalhe).
2. **Navegação** com Intents explícitas (métodos-fábrica `criarIntent...`), extras para passar o id,
   `parentActivityName` no manifest e uso dos métodos de ciclo de vida (`onCreate`, `onStart`,
   `onResume`, `onPause`, `onStop`, `onRestart`, `onDestroy` — registrados no Logcat).
3. **CRUD em memória** com `ArrayList` na `CadastroActivity`, via `RegistroRepository`.
4. **Interfaces XML** com componentes variados: `TextView`, `TextInputLayout/EditText`, menu suspenso
   (`MaterialAutoCompleteTextView`), `RadioGroup`, `ChipGroup`, `MaterialSwitch`, `MaterialCardView`,
   `ListView`, `ExtendedFloatingActionButton`, `MaterialButton`, `AlertDialog`.
5. **Feedback ao usuário** com `Toast` (registro criado/atualizado/excluído, cancelado, sair) e
   `Snackbar` (erros de validação, mudança de situação com "Desfazer", dica da lista).
6. **Código organizado e comentado**, textos em `strings.xml`, cores/dimensões em recursos.

Testes unitários do repositório: `app/src/test/.../RegistroRepositoryTest.java` (11 testes).

## Como abrir e executar

1. Android Studio → **File › Open** → selecione a pasta do projeto.
2. Aguarde o *Gradle Sync* (AGP 8.7.3, Gradle 8.9, compileSdk 35, minSdk 21).
3. Execute no emulador com **Run ▶**.

## Entrega

Renomeie a pasta do projeto para `<turma>_grupo<numero_do_grupo>_devmobile_projfinal` e compacte como
`<turma>_grupo<numero_do_grupo>_projfinal.zip` (sem as pastas `build/` e `.gradle/`). **Não** enviar `.apk`.
