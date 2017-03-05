#!/bin/bash
git init

# create basic repository files
touch .gitignore
touch cm.txt

# write standard info to files
printf "bin/\r\n" >> .gitignore
printf "cm.txt\r\n" >> .gitignore
printf "*.sh\r\n" >> .gitignore

printf "Initialize empty repository\r\n" >> cm.txt
printf "\r\nAdd gitignore" >> cm.txt

#set up first commit
git add .
git commit -F cm.txt