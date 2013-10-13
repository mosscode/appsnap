/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of appsnap.
 *
 * appsnap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * appsnap is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with appsnap; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
//============================================================================
// Name        : ms-windows-daemon-launcher.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <fstream.h>
#include <windows.h>
#include <stdio.h>
#include <tchar.h>

using namespace std;


int readTextFromFile(char* inputPath, char* text, int textMax);

int main( int argc, TCHAR *argv[] )
{

	DWORD returnValue;

	do{
		STARTUPINFO si;
		PROCESS_INFORMATION pi;

		ZeroMemory( &si, sizeof(si) );
		si.cb = sizeof(si);
		ZeroMemory( &pi, sizeof(pi) );

		char* commandLine;

		if(argc==2){
			commandLine = argv[1];
		}else{
			int textMax = 1024*512;
			char text[textMax];
			readTextFromFile("launch.txt", text, textMax);
			commandLine = text;
		}

		cerr << "Launching " << commandLine << flush;

		// Start the child process.
		if( !CreateProcess( NULL,   // No module name (use command line)
				commandLine,        // Command line
				NULL,           // Process handle not inheritable
				NULL,           // Thread handle not inheritable
				FALSE,          // Set handle inheritance to FALSE
				0,              // No creation flags
				NULL,           // Use parent's environment block
				NULL,           // Use parent's starting directory
				&si,            // Pointer to STARTUPINFO structure
				&pi )           // Pointer to PROCESS_INFORMATION structure
		)
		{
			printf( "CreateProcess failed (%d).\n", GetLastError() );
			return -1;
		}

		// Wait until child process exits.
		returnValue = WaitForSingleObject( pi.hProcess, INFINITE );
		cerr << "Exited with value " << returnValue << "\n" << flush;

		// Close process and thread handles.
		CloseHandle( pi.hProcess );
		CloseHandle( pi.hThread );

	} while(returnValue==0);

	return 0;
}


int readTextFromFile(char* inputPath, char* text, int textMax){
	// READ PORT FROM FILE

	ifstream input(inputPath, ios::in);

	if(!input.good()){
		cerr << "Error opening file: " << inputPath << "\n" << flush;
		return false;
	}else{
		cerr << "Opened file\n" << flush;
	}



	input.get(text, textMax);

	//			input >> text;

	cerr << "Read \"" << text << "\" from file " << inputPath << "\n" << flush;

	input.close();

	return true;
}
