@echo off
SET pp=%1
SET hdd=%pp:~1,3%
SET pathInst=%pp:~3%
cd %hdd%

cd %pathInst%
IF EXIST %3 DEL %3 
rename %2 %3