====
    Copyright (C) 2013, Moss Computing Inc.

    This file is part of appsnap.

    appsnap is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2, or (at your option)
    any later version.

    appsnap is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with appsnap; see the file COPYING.  If not, write to the
    Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
    02110-1301 USA.

    Linking this library statically or dynamically with other modules is
    making a combined work based on this library.  Thus, the terms and
    conditions of the GNU General Public License cover the whole
    combination.

    As a special exception, the copyright holders of this library give you
    permission to link this library with independent modules to produce an
    executable, regardless of the license terms of these independent
    modules, and to copy and distribute the resulting executable under
    terms of your choice, provided that you also meet, for each linked
    independent module, the terms and conditions of the license of that
    module.  An independent module is a module which is not derived from
    or based on this library.  If you modify this library, you may extend
    this exception to your version of the library, but you are not
    obligated to do so.  If you do not wish to do so, delete this
    exception statement from your version.
====

APPSNAP HIGH-LEVEL OVERVIEW

AppSnap is a system enabling the distribution of software, including the installation and maintenance of managed software installations.

CORE CONCEPTS
	
	Installable: An installable is a bundle of all the binary components needed to install a program on a client machine.
	    - note: this is a launch-spec.
	    
	Application: An application is a sequence (or collecton???) of one or more installables, each of which constitutes a different version of what is 
				 considered to be the same program.
	
	
	Stream: A stream is a distribution channel for one or more Applications. Each Application may be distributed by one or more streams.  
			A stream consists of a sequence of one or more stream updates, where each stream update in turn describes how/whether to update 
			an installation of each of the applications distributed by the stream.  The purpose of a stream is to provide for a means of controlling how 
			and when application update commands are sent to a set of installations. A stream can be stand-alone, or be a fork of a parent stream.  In the
			case of a fork, the forked stream is merely a mechanism for controlling the rate of flow of updates from the parent stream.

	Group: A group is a named association of a set of streams and users.  The purpose of controlling which streams are available to which users.  
	       Each group has one or more group administrators.
	
MANAGED INSTALLATIONS

	Installation: An installation is an instance where an installable has been installed on a client machine.  Each installation is managed by a keeper on that machine.
	
	Keeper: The keeper is a software component that is installed on each client machine.  The keeper is a background process, with a 'system tray' component.
			The keeper is responsible for
				1) maintaining software installations on the local machine.  This includes desktop integration stuff like launch scripts, shortcuts, etc.
				2) responding to commands from the network service (such as cache-download, install, upgrade, recall, rename, etc).

	Installer: The installer is a java applet that comlements the keeper.  It allows users to select and install managed applications.  The set of applications from which they may choose is controlled by their group membership.

NETWORK SERVICE
	
	The network service: The hub of AppSnap is the network service.  This service serves as 
		1) a database distribution streams
		2) a database of AppSnap groups
		3) a control hub for managed installations
			3.1) a database of managed software installations
			3.2) a command queue for managed software installations
			3.3) an intermediary between keepers and the AppSnap administration software.




