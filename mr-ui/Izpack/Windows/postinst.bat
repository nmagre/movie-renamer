@echo off
SET pp=%1

SET hdd=%pp:~0,2%
SET pathInst=%pp:~2%


%hdd%
cd %pathInst%

IF EXIST %3 DEL %3
rename %2 %3
