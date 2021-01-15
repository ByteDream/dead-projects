#!/bin/bash

which python3 &> /dev/null
[ $? -eq 0 ] || apt-get -y install python3

which pip3 &> /dev/null
[ $? -eq 0 ] || apt-get -y install python3-pip

python3 -c "import aiogram" &> /dev/null
[ $? -eq 0 ] || yes | pip3 install aiogram 1> /dev/null

python3 -c "import discord" &> /dev/null
[ $? -eq 0 ] || yes | pip3 install discord 1> /dev/null

python3 -c "import webuntis" &> /dev/null
[ $? -eq 0 ] || yes | pip3 install webuntis 1> /dev/null

python3 main.py "discord bot token" "telegram bot token" "untis username" "untis password"
