# Lambadega Cometa - Sistema de Gestão de Fretes

Sistema Java Web para controle do ciclo de vida de fretes: autenticação, cadastros base, emissão de fretes, confirmação de saída, registro de ocorrências e acompanhamento de status.

O projeto foi desenvolvido para o projeto avaliativo **Sistema de Gestão de Fretes**, usando Java 8, JSP, Servlets, JDBC, PostgreSQL, Gradle/Gretty e JasperReports.

## Tecnologias Utilizadas

- Java 8
- JSP e JSTL
- Servlets
- JDBC
- PostgreSQL
- Gradle
- Gretty com Tomcat 9
- JasperReports
- Git/GitHub

## Pré-requisitos

Antes de rodar o projeto, instale e configure:

- JDK 8
- PostgreSQL
- Git
- Permissão de execução no Gradle Wrapper do projeto

Verifique as versões:

```bash
java -version
psql --version
git --version
```

Se o `gradlew` não tiver permissão de execução:

```bash
chmod +x gradlew
```

## Estrutura do Projeto

```text
.
├── build.gradle
├── gradlew
├── sql
│   ├── 00_drop_database.sql
│   └── 01_create_database.sql
├── src/main/java/br/com/gw
│   ├── cliente
│   ├── exception
│   ├── filter
│   ├── frete
│   ├── motorista
│   ├── usuario
│   ├── util
│   └── veiculo
├── src/main/resources
│   └── db.properties
└── src/main/webapp
    ├── WEB-INF
    │   ├── web.xml
    │   └── views
    ├── css
    ├── erro.jsp
    └── index.jsp
```

## Arquitetura

O fluxo principal segue o padrão:

```text
JSP -> Controller/Servlet -> BO -> DAO -> PostgreSQL
```

Responsabilidades:

- **JSP:** renderiza telas, formulários, mensagens e dados recebidos do controller.
- **Controller/Servlet:** recebe requisições HTTP, monta objetos a partir dos parâmetros, chama BOs e encaminha para JSPs.
- **BO:** concentra regras de negócio, validações e transações JDBC.
- **DAO:** executa SQL e mapeia dados do banco para objetos Java.
- **PostgreSQL:** armazena os dados do sistema.

## Módulos Implementados

- Login e autenticação por sessão
- Filtro de autenticação para páginas protegidas
- Cadastro de clientes
- Cadastro de motoristas
- Cadastro de veículos
- Emissão e acompanhamento de fretes
- Registro de ocorrências de frete
- Relatório PDF de fretes em aberto com JasperReports
- Romaneio PDF individual por frete com JasperReports
- Exportação da listagem de fretes para CSV
- Tratamento de exceções de negócio
- Script SQL com estrutura e dados iniciais

## Relatórios JasperReports

O sistema possui dois relatórios em PDF:

- **Fretes em aberto**: disponível na listagem de fretes pelo botão **Imprimir fretes em aberto**. Considera fretes com status `EMITIDO`, `SAIDA_CONFIRMADA` e `EM_TRANSITO`.
- **Romaneio de frete**: disponível no detalhe do frete pelo botão **Gerar romaneio PDF**.

Os templates ficam em:

```text
src/main/resources/relatorios/fretes_abertos.jrxml
src/main/resources/relatorios/romaneio_frete.jrxml
```

## Diferencial Escolhido

O diferencial escolhido foi a **exportação da listagem de fretes para CSV**.

A funcionalidade está integrada à tela de listagem de fretes:

```text
src/main/webapp/WEB-INF/views/frete/listarFrete.jsp
src/main/java/br/com/gw/frete/FreteControlador.java
src/main/java/br/com/gw/frete/FreteBO.java
src/main/java/br/com/gw/frete/FreteDAO.java
```

Na listagem de fretes existe o botão **Exportar CSV**. Ele respeita o filtro digitado na tela e gera um arquivo `.csv` com os fretes encontrados, incluindo número, status, datas, remetente, destinatário, motorista, veículo, origem, destino, carga e valores.

Esse diferencial facilita a conferência operacional dos fretes fora do sistema, por exemplo em planilhas.

## Configuração do Banco de Dados

O projeto usa PostgreSQL. O banco padrão configurado no script é:

```text
LambadegaCometa
```

O script principal está em:

```text
sql/01_create_database.sql
```

Esse script:

- remove o banco `LambadegaCometa`, se existir;
- cria o banco novamente;
- cria as tabelas principais;
- cria constraints de integridade;
- insere dados iniciais para teste.

Tabelas principais:

- `usuario`
- `cliente`
- `motorista`
- `veiculo`
- `frete`
- `ocorrencia_frete`

Dados iniciais incluídos:

- 1 usuário administrador;
- 3 clientes;
- 3 motoristas;
- 3 veículos;
- 5 fretes em status diferentes;
- ocorrências de exemplo.

## Criando o Banco do Zero

Entre na raiz do projeto:

```bash
cd /home/estagiario1/Documentos/Lambadega_Cometa
```

Execute o script como um usuário PostgreSQL com permissão para criar banco de dados:

```bash
psql -U postgres -f sql/01_create_database.sql
```

Se o PostgreSQL estiver usando autenticação local por usuário do sistema, use:

```bash
sudo -u postgres psql -f sql/01_create_database.sql
```

Atenção: o script contém `DROP DATABASE IF EXISTS LambadegaCometa`. Ele apaga e recria o banco. Não rode esse script em um banco com dados importantes.

Para conferir se o banco foi criado:

```bash
psql -U postgres -d LambadegaCometa -c "\dt"
```

Para conferir os dados iniciais:

```bash
psql -U postgres -d LambadegaCometa -c "SELECT COUNT(*) FROM cliente;"
psql -U postgres -d LambadegaCometa -c "SELECT COUNT(*) FROM motorista;"
psql -U postgres -d LambadegaCometa -c "SELECT COUNT(*) FROM veiculo;"
psql -U postgres -d LambadegaCometa -c "SELECT status, COUNT(*) FROM frete GROUP BY status;"
```

## Configurando o `db.properties`

A conexão com o banco é carregada pela classe:

```text
src/main/java/br/com/gw/util/ConnectionFactory.java
```

Ela procura o arquivo:

```text
src/main/resources/db.properties
```

Crie esse arquivo com o seguinte conteúdo, ajustando usuário e senha conforme sua instalação local:

```properties
db.url=jdbc:postgresql://localhost:5432/LambadegaCometa
db.usuario=postgres
db.senha=sua_senha_aqui
```

Exemplo para ambiente local:

```properties
db.url=jdbc:postgresql://localhost:5432/LambadegaCometa
db.usuario=postgres
db.senha=1234
```

Importante:

- `db.properties` contém credenciais reais e não deve ser versionado.
- O `.gitignore` do projeto deve conter `db.properties`.
- Em uma entrega final, o ideal é manter apenas um `db.properties.example` com dados fictícios.

Modelo recomendado para `db.properties.example`:

```properties
db.url=jdbc:postgresql://localhost:5432/LambadegaCometa
db.usuario=seu_usuario
db.senha=sua_senha
```

## Compilando o Projeto

Na raiz do projeto, execute:

```bash
./gradlew clean build
```

Se o build finalizar corretamente, o WAR será gerado em:

```text
build/libs/
```

## Rodando Localmente

O projeto usa Gretty com Tomcat 9. Para subir a aplicação:

```bash
./gradlew appRun
```

Depois acesse:

```text
http://localhost:8080/SISTEMA-FRETES/LoginControlador
```

Context path configurado:

```text
/SISTEMA-FRETES
```

Configuração no Gradle:

```groovy
gretty {
   servletContainer = 'tomcat9'
   contextPath = '/SISTEMA-FRETES'
}
```

## Usuário Inicial

O script SQL cria um usuário administrador para teste:

```text
Usuário: admin
Senha: 123456
```

Essa senha é apenas para ambiente local e dados de demonstração.

## Fluxo Básico de Uso

1. Acesse a tela de login.
2. Entre com o usuário `admin`.
3. Cadastre ou consulte clientes.
4. Cadastre ou consulte motoristas.
5. Cadastre ou consulte veículos.
6. Emita um novo frete.
7. Acompanhe o detalhe do frete.
8. Exporte a listagem de fretes para CSV, se precisar conferir os dados em planilha.
9. Confirme saída.
10. Registre ocorrências.
11. Finalize como entregue, não entregue ou cancelado, conforme o fluxo permitido.

## Status de Frete

O sistema utiliza enum para status de frete:

```text
EMITIDO
SAIDA_CONFIRMADA
EM_TRANSITO
ENTREGUE
NAO_ENTREGUE
CANCELADO
```

Fluxo esperado:

```text
EMITIDO -> SAIDA_CONFIRMADA -> EM_TRANSITO -> ENTREGUE
                                      └──── -> NAO_ENTREGUE

EMITIDO -> CANCELADO
```

## Tipos de Ocorrência

O sistema utiliza enum para tipos de ocorrência:

```text
SAIDA_DO_PATIO
EM_ROTA
TENTATIVA_ENTREGA
ENTREGA_REALIZADA
AVARIA
EXTRAVIO
OUTROS
```

## Transações JDBC

Operações que alteram mais de uma informação importante são feitas com transação JDBC manual no BO, usando:

```java
conn.setAutoCommit(false);
conn.commit();
conn.rollback();
```

Exemplos:

- confirmar saída do frete;
- atualizar status do veículo para `EM_VIAGEM`;
- registrar entrega;
- atualizar status do frete;
- devolver veículo para `DISPONIVEL`;
- registrar ocorrências.

## Tratamento de Erros

O projeto possui exceções próprias em:

```text
src/main/java/br/com/gw/exception
```

Principais exceções:

- `NegocioException`
- `CadastroException`
- `FreteException`
- `DAOException`
- `AuthenticationException`
- `ValidationException`

As exceções de negócio são capturadas pelos controllers e enviadas para as JSPs como mensagens amigáveis.

## Arquivos SQL

```text
sql/00_drop_database.sql
```

Script auxiliar para remover tabelas/banco em ambiente de desenvolvimento.

```text
sql/01_create_database.sql
```

Script principal para criar o banco, tabelas, constraints e dados iniciais.

## Comandos Úteis

Compilar:

```bash
./gradlew clean build
```

Rodar:

```bash
./gradlew appRun
```

Parar o servidor Gretty:

```bash
Ctrl + C
```

Recriar banco:

```bash
psql -U postgres -f sql/01_create_database.sql
```

Entrar no banco:

```bash
psql -U postgres -d LambadegaCometa
```

Listar tabelas:

```sql
\dt
```

Consultar fretes:

```sql
SELECT numero, status, data_emissao, data_previsao_entrega
FROM frete
ORDER BY id;
```

## Observações de Segurança

- Não commitar `db.properties` com senha real.
- Não usar a senha `123456` em ambiente real.
- Em produção, senhas de usuários devem ser armazenadas com hash.
- O usuário do banco usado pela aplicação deve ter apenas as permissões necessárias.

## Solução de Problemas

### Erro de conexão com o banco

Verifique se o PostgreSQL está rodando:

```bash
sudo systemctl status postgresql
```

Se necessário:

```bash
sudo systemctl start postgresql
```

Confira se o banco existe:

```bash
psql -U postgres -l
```

Confira o arquivo:

```text
src/main/resources/db.properties
```

### Porta 8080 ocupada

Verifique o processo usando a porta:

```bash
sudo lsof -i :8080
```

Finalize o processo, se for seguro:

```bash
sudo kill -9 PID
```

Depois rode novamente:

```bash
./gradlew appRun
```

### Tela de login não abre

Use a URL do controller:

```text
http://localhost:8080/SISTEMA-FRETES/LoginControlador
```

As JSPs internas ficam dentro de `WEB-INF/views` e não devem ser acessadas diretamente pelo navegador.

### Alterei o SQL e nada mudou

Rode novamente o script de criação:

```bash
psql -U postgres -f sql/01_create_database.sql
```

Lembre-se: esse comando recria o banco e apaga os dados anteriores.

## Checklist Para Rodar do Zero

1. Instalar JDK 8.
2. Instalar PostgreSQL.
3. Clonar o repositório.
4. Entrar na pasta do projeto.
5. Criar `src/main/resources/db.properties`.
6. Ajustar usuário e senha do banco no `db.properties`.
7. Rodar `psql -U postgres -f sql/01_create_database.sql`.
8. Rodar `./gradlew clean build`.
9. Rodar `./gradlew appRun`.
10. Acessar `http://localhost:8080/SISTEMA-FRETES/LoginControlador`.
11. Entrar com `admin` / `123456`.

## Autor

Projeto desenvolvido para avaliação prática de Java Web com PostgreSQL, JSP, Servlets, JDBC, JasperReports e arquitetura em camadas.
