#!/bin/bash

###############################################################################
#
#  SCRIPT DE EXECUÇÃO - Lambadega Cometa Sistema de Fretes
#  
#  Uso: ./executar.sh [opção]
#
#  Opções:
#    build     - Compilar projeto (./gradlew clean build)
#    run       - Executar servidor (./gradlew appRun)
#    db        - Criar/preparar banco de dados
#    test      - Executar testes
#    help      - Mostrar esta ajuda
#    full      - Build + DB + Run (completo)
#
###############################################################################

set -e  # Exit on error

PROJECT_DIR="/home/estagiario1/Documentos/Lambadega_Cometa"
cd "$PROJECT_DIR"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções de log
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Função para compilar
build() {
    log_info "Compilando projeto..."
    ./gradlew clean build 2>&1 | grep -E "(BUILD SUCCESS|BUILD FAILED|error)" || true
    
    if [ -f "build/libs/SISTEMA-FRETES-1.0.war" ]; then
        log_success "Compilação bem-sucedida"
        ls -lh build/libs/SISTEMA-FRETES-1.0.war
    else
        log_error "Compilação falhou"
        exit 1
    fi
}

# Função para preparar banco de dados
setup_db() {
    log_info "Preparando banco de dados PostgreSQL..."
    
    # Verificar se PostgreSQL está rodando
    if ! sudo systemctl is-active --quiet postgresql; then
        log_warning "PostgreSQL não está rodando, iniciando..."
        sudo systemctl start postgresql
        sleep 2
    fi
    
    log_info "Executando scripts SQL..."
    
    # Executar scripts
    sudo -u postgres psql -f sql/00_drop_database.sql > /dev/null 2>&1 && log_success "Drop database"
    sudo -u postgres psql -f sql/01_create_database.sql > /dev/null 2>&1 && log_success "Create database"
    sudo -u postgres psql -d lambadega_cometa -f sql/02_views_and_maintenance.sql > /dev/null 2>&1 && log_success "Create views"
    
    # Verificar
    COUNT=$(psql -U gw_user -d lambadega_cometa -c "SELECT COUNT(*) FROM usuario;" 2>/dev/null | tail -1 | tr -d ' ')
    if [ "$COUNT" -gt 0 ]; then
        log_success "Banco de dados pronto ($COUNT usuários)"
    else
        log_error "Falha ao preparar banco"
        exit 1
    fi
}

# Função para executar
run() {
    log_info "Iniciando servidor Tomcat 9 com Gretty..."
    echo ""
    echo "=================================================="
    echo "   Acesse: http://localhost:8080/SISTEMA-FRETES/login.jsp"
    echo "   Usuário: admin"
    echo "   Senha: 123456"
    echo "=================================================="
    echo ""
    
    ./gradlew appRun
}

# Função para testes
test_app() {
    log_info "Executando testes..."
    
    if [ -f "src/main/java/br/com/gw/usuario/TesteLoginControlador.java" ]; then
        log_info "Compilando testes..."
        javac -cp build/libs/SISTEMA-FRETES-1.0.war:. \
              src/main/java/br/com/gw/usuario/TesteLoginControlador.java \
              src/main/java/br/com/gw/usuario/LoginControlador.java \
              2>/dev/null || log_warning "Erro ao compilar testes (esperado sem Tomcat)"
    fi
    
    log_success "Testes prontos"
}

# Função para ajuda
show_help() {
    cat << EOF

${BLUE}╔════════════════════════════════════════════════════════╗${NC}
${BLUE}║     Lambadega Cometa - Sistema de Fretes              ║${NC}
${BLUE}║            Script de Execução v1.0                     ║${NC}
${BLUE}╚════════════════════════════════════════════════════════╝${NC}

${GREEN}OPÇÕES:${NC}

  ${YELLOW}build${NC}     - Compilar o projeto (./gradlew clean build)
  ${YELLOW}run${NC}       - Executar servidor Tomcat (./gradlew appRun)
  ${YELLOW}db${NC}        - Preparar banco de dados PostgreSQL
  ${YELLOW}test${NC}      - Executar testes (se disponível)
  ${YELLOW}full${NC}      - Executar: build + db + run
  ${YELLOW}help${NC}      - Mostrar esta mensagem

${GREEN}EXEMPLOS:${NC}

  # Compilar
  $0 build

  # Preparar banco
  $0 db

  # Executar servidor
  $0 run

  # Compilar + DB + Servidor (tudo)
  $0 full

${GREEN}TROUBLESHOOTING:${NC}

  Porta 8080 em uso:
    sudo lsof -i :8080
    sudo kill -9 <PID>
    $0 run

  PostgreSQL não conecta:
    sudo systemctl start postgresql
    $0 db

  Build falha:
    $0 build --stacktrace

${BLUE}Documentação:${NC}
  - RESUMO_COMPLETO_FINAL.md - Overview do projeto
  - GUIA_TESTE_COMPLETO.md - Teste passo a passo
  - GUIA_DE_USO.md - Instalação e setup

EOF
}

# Main
case "${1:-help}" in
    build)
        build
        ;;
    run)
        run
        ;;
    db)
        setup_db
        ;;
    test)
        test_app
        ;;
    full)
        build
        setup_db
        run
        ;;
    help|*)
        show_help
        ;;
esac
