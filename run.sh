#!/usr/bin/env bash
# Compila e abre o Sudoku com interface gráfica.
# Uso: ./run.sh                 -> jogo padrão
#      ./run.sh 0,0;4,false ... -> jogo informado nos argumentos (81 posições)
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
javac --release 21 -d out $(find src -name '*.java')
java -cp out br.com.dio.UIMain "$@"
