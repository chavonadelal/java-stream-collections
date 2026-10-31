#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
mode="${1:-run}"
case "$mode" in
    build|run) ;;
    *) echo "Usage: bash scripts/project.sh [build|run]" >&2; exit 1 ;;
esac

jdk_tool() {
    local tool="$1"
    shift
    if [[ -n "${JAVA_HOME:-}" ]]; then
        "$JAVA_HOME/bin/$tool" "$@"
    else
        "$tool" "$@"
    fi
}

mkdir -p build/classes
mapfile -t main_sources < <(find src/main/java -name '*.java' -type f | sort)
jdk_tool javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d build/classes "${main_sources[@]}"
jdk_tool jar --create --file build/catalog.jar --main-class catalog.Main -C build/classes .

if [[ "$mode" == run ]]; then
    jdk_tool java -Dfile.encoding=UTF-8 -jar build/catalog.jar
else
    echo 'Built: build/catalog.jar'
fi
