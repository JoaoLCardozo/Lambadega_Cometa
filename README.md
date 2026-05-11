# Lambadega Cometa - Sistema de Gestão de Fretes

Sistema Java Web para gestão operacional de fretes. O projeto cobre autenticação, recuperação de senha, cadastros base, emissão de fretes, acompanhamento de status, ocorrências, relatórios e um monitor operacional para apoiar a tomada de decisão.

Foi desenvolvido para avaliação prática usando Java 8, JSP, Servlets, JDBC, PostgreSQL, Gradle/Gretty, Tomcat 9 e JasperReports.

## Tecnologias Utilizadas

- Java 8
- JSP, JSTL e Servlets
- JDBC
- PostgreSQL
- Gradle e Gretty
- Tomcat 9
- JasperReports
- JavaMail
- HTML, CSS e JavaScript

## Funcionalidades

- Login com sessão e filtro de autenticação
- Recuperação de senha por e-mail com código de verificação
- Senhas armazenadas com hash PBKDF2
- Cadastro, edição, listagem e exclusão controlada de clientes
- Cadastro, edição, listagem e inativação controlada de motoristas
- Cadastro, edição, listagem e exclusão controlada de veículos
- Emissão, confirmação de saída, entrega, não entrega e cancelamento de fretes
- Registro de ocorrências de frete
- Monitor de Fretes com indicadores, alertas, fretes críticos e ranking dos 3 melhores motoristas
- Relatórios PDF com JasperReports
- Exportação CSV da listagem de fretes
- Validações de negócio no backend
- Máscaras e melhorias de UX nos formulários
- Tratamento amigável para erros e páginas não encontradas

## Estrutura

```text
.
├── build.gradle
├── gradlew
├── sql
│   ├── 00_drop_database.sql
│   ├── 01_create_database.sql
│   └── 02_popular_monitor_fretes.sql
├── src/main/java/br/com/gw
│   ├── cliente
│   ├── exception
│   ├── filter
│   ├── frete
│   ├── monitorfretes
│   ├── motorista
│   ├── usuario
│   ├── util
│   └── veiculo
├── src/main/resources
│   ├── db.properties
│   ├── email.properties
│   └── relatorios
└── src/main/webapp
    ├── WEB-INF/views
    ├── css
    ├── js
    ├── erro.jsp
    └── index.jsp
```

## Arquitetura

O fluxo principal segue o padrão:

```text
JSP -> Controller/Servlet -> BO -> DAO -> PostgreSQL
```

- **JSP:** telas, formulários, mensagens e listagens.
- **Controller/Servlet:** recebe requisições, monta objetos e direciona o fluxo.
- **BO:** concentra regras de negócio, validações e transações.
- **DAO:** executa SQL parametrizado e mapeia resultados.
- **PostgreSQL:** armazena usuários, clientes, motoristas, veículos, fretes e ocorrências.

## Pré-requisitos

- JDK 8
- PostgreSQL
- Git
- Gradle Wrapper com permissão de execução

Verifique:

```bash
java -version
psql --version
git --version
```

Se necessário:

```bash
chmod +x gradlew
```

## Banco de Dados

O banco padrão do projeto é:

```text
LambadegaCometa
```

Crie o banco do zero com:

```bash
psql -U postgres -f sql/01_create_database.sql
```

Se seu PostgreSQL usa autenticação local por usuário do sistema:

```bash
sudo -u postgres psql -f sql/01_create_database.sql
```

O script `sql/01_create_database.sql` recria o banco, então ele apaga dados existentes. Use apenas em ambiente local ou de demonstração.

Dados iniciais incluídos:

- 1 usuário de teste
- Clientes, motoristas, veículos, fretes e ocorrências de exemplo
- Tabela de recuperação de senha
- Triggers de atualização de status de veículo vinculadas ao ciclo do frete

Para popular uma base com mais dados para o Monitor de Fretes:

```bash
psql -U postgres -d LambadegaCometa -f sql/02_popular_monitor_fretes.sql
```

## Configuração do Banco

Crie ou ajuste o arquivo:

```text
src/main/resources/db.properties
```

Exemplo:

```properties
db.url=jdbc:postgresql://localhost:5432/LambadegaCometa
db.usuario=postgres
db.senha=sua_senha
```

Também é possível configurar por variáveis de ambiente. Elas têm prioridade sobre o arquivo:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/LambadegaCometa
export DB_USUARIO=lambadega_app
export DB_SENHA=sua_senha
```

O sistema não usa mais usuário e senha padrão quando a configuração está ausente. Se `db.properties` ou as variáveis de ambiente não forem informadas, a aplicação falha com uma mensagem clara.

Esse arquivo contém credenciais locais e não deve conter dados reais em commits públicos. Para um ambiente mais seguro, crie um usuário específico da aplicação no PostgreSQL em vez de usar o superusuário `postgres`.

## Configuração de E-mail

A recuperação de senha usa SMTP e lê as configurações em:

```text
src/main/resources/email.properties
```

Exemplo para Gmail com senha de app:

```properties
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.smtp.auth=true
email.smtp.starttls.enable=true

email.usuario=seuemail@gmail.com
email.senha=sua_senha_de_app
email.remetente=seuemail@gmail.com
email.nomeRemetente=Lambadega Cometa
```

No Gmail, use uma senha de app, não a senha normal da conta. O `email.usuario` e o `email.remetente` devem ser o mesmo e-mail na configuração mais simples.

## Executando

Compile:

```bash
./gradlew clean build
```

Suba a aplicação:

```bash
./gradlew appRun
```

Acesse:

```text
http://localhost:8080/SISTEMA-FRETES/LoginControlador
```

Usuário inicial:

```text
Usuário: usuario
Senha: 123456
```

## Módulos

### Usuários e Login

- Login por usuário e senha
- Sessão protegida por filtro de autenticação
- Cadastro e listagem de usuários
- Recuperação de senha por e-mail com código temporário
- Hash de senha com PBKDF2

### Clientes

- Cadastro de pessoa física e jurídica
- Máscaras para CPF/CNPJ, CEP e telefone
- Integração ViaCEP no formulário
- Validação de e-mail e UF
- Status ativo/inativo
- Filtros por nome/razão social, documento, município, tipo e status
- Clientes inativos não aparecem para novos fretes
- Cliente vinculado a frete não pode ser excluído

### Motoristas

- Cadastro com CPF, telefone, CNH, categoria, validade e tipo de vínculo
- Validação de CPF, CNH, idade mínima e CNH vencida
- Bloqueio de CPF e CNH duplicados
- Filtros por nome, CPF, status, tipo de vínculo e categoria CNH
- Rótulos amigáveis na listagem
- Alerta visual para CNH vencida
- Motorista inativo não aparece para novos fretes
- Motorista com frete ativo não pode ser inativado

### Veículos

- Cadastro com placa, RNTRC, ano, tipo, tara, capacidade, volume e status
- Aceita placa antiga e Mercosul
- Placa convertida para maiúscula automaticamente
- Validações de ano, tara, capacidade, volume e RNTRC
- Bloqueio de placa duplicada
- Filtros por placa, tipo, status e ano
- Status exibido com rótulo amigável
- Veículo em viagem ou manutenção não aparece para novos fretes
- Veículo vinculado a frete não pode ser excluído

### Fretes

- Emissão de fretes com remetente, destinatário, motorista e veículo
- Cálculo de ICMS e valor total
- Controle de status
- Confirmação de saída
- Registro de ocorrências
- Finalização como entregue, não entregue ou cancelado
- Uso de transações JDBC para operações que alteram frete e veículo
- Exportação CSV da listagem

### Monitor de Fretes

O Monitor de Fretes reúne uma visão operacional do sistema:

- Indicadores gerais
- Distribuição por status
- Alertas operacionais
- Fretes atrasados ou críticos
- Ranking dos 3 melhores motoristas do mês
- Atalhos para clientes, frota e performance por motorista

## Relatórios

O sistema possui três relatórios PDF com JasperReports:

- **Fretes em aberto:** considera fretes `EMITIDO`, `SAIDA_CONFIRMADA` e `EM_TRANSITO`.
- **Romaneio de frete:** documento individual do frete.
- **Performance de motoristas:** consulta por motorista e período.

Templates:

```text
src/main/resources/relatorios/fretes_abertos.jrxml
src/main/resources/relatorios/romaneio_frete.jrxml
src/main/resources/relatorios/performance_motorista.jrxml
```

## Status de Frete

```text
EMITIDO
SAIDA_CONFIRMADA
EM_TRANSITO
ENTREGUE
NAO_ENTREGUE
CANCELADO
```

Fluxo principal:

```text
EMITIDO -> SAIDA_CONFIRMADA -> EM_TRANSITO -> ENTREGUE
                                      └──── -> NAO_ENTREGUE

EMITIDO -> CANCELADO
```

## Segurança e Validações

- SQL com `PreparedStatement`
- Normalização de campos de texto
- Bloqueio de entradas com HTML/script em campos sensíveis
- Tratamento amigável para IDs inexistentes
- Hash de senha com PBKDF2
- Código de recuperação de senha salvo com hash e expiração
- Cookies de sessão com `http-only`
- Mensagens de erro e sucesso padronizadas

## Comandos Úteis

Compilar:

```bash
./gradlew clean build
```

Rodar:

```bash
./gradlew appRun
```

Recriar banco:

```bash
psql -U postgres -f sql/01_create_database.sql
```

Popular Monitor de Fretes:

```bash
psql -U postgres -d LambadegaCometa -f sql/02_popular_monitor_fretes.sql
```

Consultar tabelas:

```bash
psql -U postgres -d LambadegaCometa -c "\dt"
```

Consultar fretes:

```sql
SELECT numero, status, data_emissao, data_previsao_entrega
FROM frete
ORDER BY id;
```

## Solução de Problemas

### Erro de conexão com o banco

Verifique se o PostgreSQL está rodando:

```bash
sudo systemctl status postgresql
```

Confira o arquivo:

```text
src/main/resources/db.properties
```

### Porta 8080 ocupada

Verifique o processo:

```bash
sudo lsof -i :8080
```

Finalize apenas se tiver certeza:

```bash
sudo kill -9 PID
```

### E-mail de recuperação não é enviado

Confira:

- `email.properties` está preenchido
- SMTP, porta e TLS estão corretos
- Gmail usa senha de app
- `email.usuario` e `email.remetente` são compatíveis
- O usuário informado possui e-mail cadastrado e está ativo

### Tela de login não abre

Use a URL do controller:

```text
http://localhost:8080/SISTEMA-FRETES/LoginControlador
```

As JSPs ficam dentro de `WEB-INF/views` e não devem ser acessadas diretamente pelo navegador.

## Checklist Para Rodar do Zero

1. Instalar JDK 8.
2. Instalar PostgreSQL.
3. Clonar o repositório.
4. Entrar na pasta do projeto.
5. Criar `src/main/resources/db.properties`.
6. Ajustar conexão no `db.properties`.
7. Configurar `src/main/resources/email.properties`, se for testar recuperação de senha.
8. Rodar `psql -U postgres -f sql/01_create_database.sql`.
9. Opcionalmente rodar `psql -U postgres -d LambadegaCometa -f sql/02_popular_monitor_fretes.sql`.
10. Rodar `./gradlew clean build`.
11. Rodar `./gradlew appRun`.
12. Acessar `http://localhost:8080/SISTEMA-FRETES/LoginControlador`.
13. Entrar com `usuario` / `123456`.

## Autor

Projeto desenvolvido para avaliação prática de Java Web com PostgreSQL, JSP, Servlets, JDBC, JasperReports e arquitetura em camadas.
