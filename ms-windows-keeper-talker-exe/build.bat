@REM
@REM Copyright (C) 2013, Moss Computing Inc.
@REM
@REM This file is part of appsnap.
@REM
@REM appsnap is free software; you can redistribute it and/or modify
@REM it under the terms of the GNU General Public License as published by
@REM the Free Software Foundation; either version 2, or (at your option)
@REM any later version.
@REM
@REM appsnap is distributed in the hope that it will be useful, but
@REM WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
@REM General Public License for more details.
@REM
@REM You should have received a copy of the GNU General Public License
@REM along with appsnap; see the file COPYING.  If not, write to the
@REM Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
@REM 02110-1301 USA.
@REM
@REM Linking this library statically or dynamically with other modules is
@REM making a combined work based on this library.  Thus, the terms and
@REM conditions of the GNU General Public License cover the whole
@REM combination.
@REM
@REM As a special exception, the copyright holders of this library give you
@REM permission to link this library with independent modules to produce an
@REM executable, regardless of the license terms of these independent
@REM modules, and to copy and distribute the resulting executable under
@REM terms of your choice, provided that you also meet, for each linked
@REM independent module, the terms and conditions of the license of that
@REM module.  An independent module is a module which is not derived from
@REM or based on this library.  If you modify this library, you may extend
@REM this exception to your version of the library, but you are not
@REM obligated to do so.  If you do not wish to do so, delete this
@REM exception statement from your version.
@REM

set MINGW=C:\MinGW
mkdir Release
mkdir Release\src
cd Release
%MINGW%\bin\g++ -O3 -Wall -c -fmessage-length=0 -osrc\ms-windows-keeper-talker.o ..\src\ms-windows-keeper-talker.cpp
%MINGW%\bin\g++ -O3 -Wall -c -fmessage-length=0 -osrc\basic-client.o ..\src\basic-client.cpp
%MINGW%\bin\g++ -O3 -Wall -c -fmessage-length=0 -osrc\ws-util.o ..\src\ws-util.cpp
%MINGW%\bin\g++ -mwindows  -oms-windows-keeper-talker.exe src\ms-windows-keeper-talker.o src\ws-util.o src\basic-client.o	 -lwsock32	
copy /Y ms-windows-keeper-talker.exe ..\..\keeper\src\main\resources\com\moss\appsnap\keeper\windows\keeper-talker.exe