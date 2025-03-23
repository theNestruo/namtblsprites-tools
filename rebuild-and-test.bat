@echo off
call mvn clean verify
java ^
-XX:SharedArchiveFile=./target/tmx2namtblsprites.jsa ^
-jar ./target/tmx2namtblsprites.jar ^
./src/test/resources/example.tmx ^
./src/test/resources/example.tmx.asm ^
-verbose -width 3 -height 3 -name .EXAMPLE -align ^
%*
