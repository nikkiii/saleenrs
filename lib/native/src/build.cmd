@echo off
gcc -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I"C:\Program Files\Java\jdk1.6.0_23\include" -I"C:\Program Files\Java\jdk1.6.0_23\include\win32"  -shared "Console.c" -o "../console.dll"
pause