@rem
@rem Run application with arguments
@rem

@echo off

@rem version should match with application build.gradle
set VERSION="1.0.0"

call .\application-%VERSION%\bin\application.bat %*
