#!/bin/bash

make clean
cd generation
./ajgen-noinvite.pl ../com/invite/core/Invite.aj asdf
cd ..
make runner

