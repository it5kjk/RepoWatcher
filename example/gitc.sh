#!/bin/bash
 
# This script create a new repo on github.com, then pushes the local repo from the current directory to the new remote.

# It is a fork of https://gist.github.com/robwierzbowski/5430952/.  Some of Rob's lines just didn't work for me, and to fix them I needed to make it more verbose so that a mere electrical engineer could understand it.

# This script gets a username from .gitconfig.  If it indicates that your default username is an empty string, you can set it with
# git config --add github.user YOUR_GIT_USERNAME

# Gather constant vars
CURRENTDIR=${PWD##*/}
GITHUBUSER=$(git config github.user)
 
# Get user input
echo "Enter name for new repo, or just <return> to make it $CURRENTDIR"
read REPONAME
echo "Enter username for new, or just <return> to make it $GITHUBUSER"
read USERNAME
echo "Enter Github password:"
read PASS
echo "Enter description for your new repo, on one line, then <return>"
read DESCRIPTION

#only make 
PRIVACYWORD=public
PRIVATE_TF=false


REPONAME=${REPONAME:-${CURRENTDIR}}
#make it currentdir anyway
REPONAME=$CURRENTDIR
USERNAME=${USERNAME:-${GITHUBUSER}}
#remove any linebreaks
REPONAME=$(echo $REPONAME|tr -d '\r\n')
USERNAME=$(echo $USERNAME|tr -d '\r\n')
PASS=$(echo $PASS|tr -d '\r\n')
DESCRIPTION=$(echo $DESCRIPTION|tr -d '\r\n')

echo "($REPONAME)"
echo "($USERNAME)"

echo "Will create a new *$PRIVACYWORD* repo named $REPONAME"
echo "on github.com in user account $USERNAME, with this description:"
echo $DESCRIPTION

#echo "Type 'y' to proceed, any other character to cancel."
#read OK
#if [ "$OK" != "y" ]; then
#  echo "User cancelled"
#  exit
#fi

echo "Are you behind a proxy [y/n]?:"
read HASPROXY
HASPROXY=$(echo $HASPROXY|tr -d '\r\n')
if  [ "$HASPROXY" != "y" ]; then
  # Curl some json to the github API oh damn we so fancy
  /usr/bin/curl.exe -u $USERNAME https://api.github.com/user/repos -d '{\"name\": \"$REPONAME \"description\": \"$DESCRIPTION\", \"private\": false, \"has_issues\": true, \"has_downloads\": true, \"has_wiki\": false}'
else
  echo "Enter proxy username:"
  read PROXYUSER
  PROXYUSER=$(echo $PROXYUSER|tr -d '\r\n')
  echo "Enter proxy password:"
  read PROXYPASS
  PROXYPASS=$(echo $PROXYPASS|tr -d '\r\n')
  /usr/bin/curl.exe -x https://proxy.intern.hamburger-software.de:3128 -U "$PROXYUSER:$PROXYPASS" -u "$USERNAME:$PASS" https://api.github.com/user/repos -d '{\"name\": \"$REPONAME \"description\": \"$DESCRIPTION\", \"private\": false, \"has_issues\": true, \"has_downloads\": true, \"has_wiki\": false}'
fi

 
# Set the freshly created repo to the origin and push
# You'll need to have added your public key to your github account
git remote add origin https://github.com/$USERNAME/$REPONAME.git
git push -u origin master