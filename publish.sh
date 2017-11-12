#!/bin/bash

set -e # exit with nonzero exit code if anything fails

openssl aes-256-cbc -K $encrypted_6c42794c35b3_key -iv $encrypted_6c42794c35b3_iv -in secrets.tar.enc -out secrets.tar -d

tar xvf secrets.tar

sbt releaseEarly
