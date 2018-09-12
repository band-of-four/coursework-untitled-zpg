#!/bin/bash
set -u
set -e

specfile=spec.tex

while ! inotifywait -e close_write ${specfile}
do
  echo "h"
  xelatex ${specfile}
done
