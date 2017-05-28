/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * HTML code manipulation.
 */
stendhal.ui.html = {
	esc: function(msg){
		return msg.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace("\n", "<br>");
	}
};
