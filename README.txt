TreeTasker is a simple task organizer based on a tree component.
Copyright (C) 2012 - VAUSHELL - contact@vaushell.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

--------------------------------------------------------------------------------------

The project has been developed in Java using Eclipse IDE.
Android SDK and Google App Engine SDK are needed in order to build the project.

tree-view-list is a small widget that provides tree view list on Android.

TreeTaskerAD is an android application which allows to create, edit and synchronize tasks with GAE server.
For now, in order to use another server, the location must be set manually for the constant
com.vaushell.treetasker.application.model.TreeTaskerControllerDAO.WEB_RESOURCE, before building the project.

TreeTaskerModel contains the common model between TreeTaskerAD and TreeTaskerServer

TreeTaskerServer contains :
* a RESTful web service used for task synchronization and user identification.
* a web application which allows to create, edit and synchronize tasks with GAE server.
TreeTaskerServer must be deployed on an Google App Engine.