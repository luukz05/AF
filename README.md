# Controle de Produção — Projeto Final (AF)

Aplicativo Android em Java (layouts em XML) para cadastrar, listar, editar e excluir registros da produção. Os dados ficam em memória (`ArrayList`).

## Telas

- `MainActivity` (`activity_main.xml`): tela inicial com o total de registros e acesso às outras telas.
- `ListagemActivity` (`activity_listagem.xml`): `ListView` com os registros e FAB para novo cadastro.
- `CadastroActivity` (`activity_cadastro.xml`): CRUD completo (criar, carregar, atualizar, excluir) com mensagens via `Toast`.

Classes de apoio: `Registro` (modelo) e `RegistroRepository` (lista em memória e operações de CRUD).
