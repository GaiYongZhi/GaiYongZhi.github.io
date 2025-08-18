@echo on

set REPOSITORY_PATH2=E:\zhiwang301\Repository


rem seraching 2...
for /f "delims=" %%i in ('dir /b /s "%REPOSITORY_PATH2%\*lastUpdated*"') do (
    del /s /q %%i
)
for /f "delims=" %%i in ('dir /b /s "%REPOSITORY_PATH2%\*_remote.repositories*"') do (
    del /s /q %%i
)
rem searchfinish
pause