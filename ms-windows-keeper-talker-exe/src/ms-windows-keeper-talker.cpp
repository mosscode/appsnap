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
/***********************************************************************
 main.cpp - The main() routine for all the "Basic Winsock" suite of
    programs from the Winsock Programmer's FAQ.  This function parses
    the command line, starts up Winsock, and calls an external function
    called DoWinsock to do the actual work.

 This program is hereby released into the public domain.  There is
 ABSOLUTELY NO WARRANTY WHATSOEVER for this product.  Caveat hacker.
***********************************************************************/

#include <winsock.h>

#include <windows.h>

#include <stdlib.h>
#include <iostream>
#include <fstream.h>

#include <string.h>

#include "basic-client.h"

using namespace std;

//// Constants /////////////////////////////////////////////////////////

// Default port to connect to on the server
//const int kDefaultServerPort = 4242;

int readTextFromFile(char* filePath, char* text, int textMax);

//// main //////////////////////////////////////////////////////////////
/*
 * Use cases:
 *   1) pass all info on command line: host, port, message
 *   2) omit host & port (message is on command line)
 *   3) no args (message read from message.txt)
 */
int main(int argc, char* argv[])
{

	char* string;
	const char* pcHost;
	int nPort;

	if(argc == 4){
		// Get host and port from the command line
		pcHost = argv[1];
		nPort = atoi(argv[2]);

		string = argv[3];
	} else{
		// READ PORT FROM FILE
		char* outputLocation = "port.txt";

		char port[6];

		char* paths[] = {"port.txt", "..\\..\\port.txt"};
		int nPaths = 2;

		int readSucceeded = false;
		for(int x=0;x<nPaths;x++){
			if(readTextFromFile(paths[x], port, 6)){
				readSucceeded = true;
				break;
			}
		}

		if(!readSucceeded){
			return -1;
		}


		pcHost = "127.0.0.1";
		nPort = atoi(port);

		if(argc==2){
			// read message from command line
			string = argv[1];
		}else{
			// read messgae from file
			char filePath[] = "message.txt";

			const int stringSize = 255;

			string = new char[stringSize];
			if(!readTextFromFile(filePath, string, stringSize)){
				return -1;
			}

		}
	}
//	} else {
////		char mem[256];
////
////		gets(mem);
////		string = mem;
////	}else{
//		cerr << "usage: " << argv[0] << " you need to pass me some arguments " << endl;
//		return 1;
//	}

//	cout << "you 1 entered \"" << string << "\"\n" << flush;



    // Start Winsock up
    WSAData wsaData;
	int nCode;
    if ((nCode = WSAStartup(MAKEWORD(1, 1), &wsaData)) != 0) {
		cerr << "WSAStartup() returned error code " << nCode << "." << 	endl;
        return 255;
    }

//    cerr << "Talking to server " << flush;

    // Call the main example routine.

    int responseLen = 256*4;
    char response[responseLen];
    int retval = talkToServer(pcHost, nPort, string, response, responseLen);

//    cerr << "Showing dialog " << flush;

    char serverSaid[] = "Launch Error: ";

    char m[strlen(serverSaid) + strlen(response)];
	strcpy(m, serverSaid);
	strcat(m, response);

	cerr << "Response: " << response << "\n" << flush;

    if(retval!=0){
		MessageBox(NULL, m, NULL, NULL);
    	return retval;
    }


//    cerr << "Response: " << response << flush;
//    cerr << m << flush;

	if(strcmp(response, "OK")!=0){
		MessageBox(NULL, m, NULL, NULL);
		return -1;
	}



    // Shut Winsock back down and take off.
    WSACleanup();
    return retval;
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
