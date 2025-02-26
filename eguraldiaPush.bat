@echo off
cd /d ../2Erronka_1Taldea_EGURALDIA_XML/

git checkout master

git add eguraldia.xml

git commit -m "Commit automatico eguraldia push"

git push origin master --force

echo Push realizado con éxito.
