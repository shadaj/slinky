#!/bin/bash

set -e # exit with nonzero exit code if anything fails

openssl aes-256-cbc -K $encrypted_key -iv $encrypted_iv -in secrets.tar.enc -out secrets.tar -d

tar xvf secrets.tar

sbt publishSignedAll coreIntellijSupport/updateIntellij coreIntellijSupport/publishSigned sonatypeBundleRelease
