#!/bin/bash

git clone https://github.com/wg/wrk.git
pushd wrk
  make > make.log
popd
