#!/bin/bash
git stash
git pull
git stash pop stash@{0}