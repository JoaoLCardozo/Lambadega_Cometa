(function(window) {
    var IBGE_LOCALIDADES_URL = 'https://servicodados.ibge.gov.br/api/v1/localidades';
    var estadosPromise;

    function criarOpcao(valor, texto) {
        var opcao = document.createElement('option');
        opcao.value = valor;
        opcao.textContent = texto;
        return opcao;
    }

    function prepararSelect(select, textoInicial) {
        select.innerHTML = '';
        select.appendChild(criarOpcao('', textoInicial));
    }

    function selecionarValor(select, valor) {
        if (!valor) return;
        select.value = valor;
    }

    function buscarEstados() {
        if (!estadosPromise) {
            estadosPromise = fetch(IBGE_LOCALIDADES_URL + '/estados?orderBy=nome')
                .then(function(response) {
                    if (!response.ok) throw new Error('Erro ao carregar UFs');
                    return response.json();
                });
        }

        return estadosPromise;
    }

    function buscarMunicipios(uf) {
        return fetch(IBGE_LOCALIDADES_URL + '/estados/' + uf + '/municipios?orderBy=nome')
            .then(function(response) {
                if (!response.ok) throw new Error('Erro ao carregar municípios');
                return response.json();
            });
    }

    function preencherUfs(select, estados, textoInicial) {
        prepararSelect(select, textoInicial || 'Selecione a UF');

        estados.forEach(function(estado) {
            select.appendChild(criarOpcao(estado.sigla, estado.sigla + ' - ' + estado.nome));
        });

        selecionarValor(select, (select.dataset.selected || '').toUpperCase());
    }

    function preencherMunicipios(select, municipios, textoInicial) {
        prepararSelect(select, textoInicial || 'Selecione o município');

        municipios.forEach(function(municipio) {
            select.appendChild(criarOpcao(municipio.nome, municipio.nome));
        });

        selecionarValor(select, select.dataset.selected || '');
        select.disabled = false;
    }

    function carregarMunicipios(ufSelect, municipioSelect, opcoes) {
        var uf = ufSelect.value;
        municipioSelect.disabled = true;

        if (!uf) {
            prepararSelect(
                municipioSelect,
                opcoes.municipioSemUf || 'Selecione a UF primeiro'
            );
            return;
        }

        prepararSelect(municipioSelect, 'Carregando municípios...');

        buscarMunicipios(uf)
            .then(function(municipios) {
                preencherMunicipios(
                    municipioSelect,
                    municipios,
                    opcoes.municipioPlaceholder
                );
            })
            .catch(function() {
                prepararSelect(municipioSelect, 'Não foi possível carregar municípios');
            });
    }

    function configurarPar(par) {
        var ufSelect = document.getElementById(par.ufId);
        var municipioSelect = document.getElementById(par.municipioId);
        var opcoes = {
            municipioPlaceholder: par.municipioPlaceholder,
            municipioSemUf: par.municipioSemUf
        };

        if (!ufSelect || !municipioSelect) return;

        ufSelect.addEventListener('change', function() {
            municipioSelect.dataset.selected = '';
            carregarMunicipios(ufSelect, municipioSelect, opcoes);
        });

        if (ufSelect.value) {
            carregarMunicipios(ufSelect, municipioSelect, opcoes);
        }
    }

    function configurar(pares) {
        buscarEstados()
            .then(function(estados) {
                pares.forEach(function(par) {
                    var ufSelect = document.getElementById(par.ufId);
                    if (!ufSelect) return;
                    preencherUfs(ufSelect, estados, par.ufPlaceholder);
                });

                pares.forEach(configurarPar);
            })
            .catch(function() {
                pares.forEach(function(par) {
                    var ufSelect = document.getElementById(par.ufId);
                    if (ufSelect) {
                        prepararSelect(ufSelect, 'Não foi possível carregar UFs');
                    }
                });
            });
    }

    window.LocalidadesIBGE = {
        configurar: configurar
    };
})(window);
