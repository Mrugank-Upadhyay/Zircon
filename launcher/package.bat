@rem
@rem Create console launcher for Windows
@rem

@echo off

@rem version should match with application build.gradle
@rem if this version is changed, be sure to update .gitignore
set VERSION="1.0.0"

if exist .\application-%VERSION% rmdir /S /Q .\application-%VERSION%
cd ..
call gradlew distZip
call tar -xf .\application\build\distributions\application-%VERSION%.zip
move .\application-%VERSION% .\launcher
cd .\launcher

echo Package Completed
