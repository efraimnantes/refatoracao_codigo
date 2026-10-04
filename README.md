# 🏆 Refatoração de Código Limpo - Apuração JIUFMS

Este repositório contém o trabalho prático da disciplina de **Manutenção de Software**, focado na aplicação de conceitos de *Clean Code* descritos no Capítulo 2 do livro *Fundamentos de Manutenção de Software*.

## 🎯 Objetivo
Demonstrar a evolução de um código confuso e legado para um código limpo, legível e manutenível. O domínio escolhido foi o sistema de apuração esportiva do Jogos Interatléticas da UFMS (JIUFMS), lidando com soma de medalhas e penalidades disciplinares.

## 🛠️ Tecnologias Utilizadas
- **Java**: Backend estruturado em Orientação a Objetos.
- **HTML5 & CSS3 & JavaScript**: Frontend para a calculadora interativa.

## 📐 Premissas de Clean Code Aplicadas
1. **Verificadores de Estilo e Formatadores**: Tipagem estrita e convenções de nomenclatura Java.
2. **Nomes Legíveis**: Identificadores explícitos (`calcularPontosModalidades`, `TipoInfracao`).
3. **Evitar Números Mágicos**: Constantes globais (`PONTOS_OURO`, `PENALIDADE_BRIGA`).
4. **Linguagem Ubíqua**: Domínio representado por entidades reais (`Atletica`, `JuizGeral`).
5. **Funções Coesas e Desacopladas**: Separação da matemática de apuração da lógica de arbitragem.
6. **Separação dos Fluxos de Execução**: *Guard Clauses* para desclassificações imediatas por exceção.

## 🌐 Teste ao Vivo
Acesse a calculadora online via GitHub Pages no link disponível nas configurações do repositório.
