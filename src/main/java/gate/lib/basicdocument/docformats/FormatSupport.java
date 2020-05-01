/*
 * Copyright (c) 2019 The University of Sheffield.
 *
 * This file is part of gateplugin-Format_Bdoc 
 * (see https://github.com/GateNLP/gateplugin-Format_Bdoc).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package gate.lib.basicdocument.docformats;

import gate.lib.basicdocument.BdocDocument;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface of all FormatSupport classes.
 * 
 * Format Support classes do the actual heavy lifting of converting to and
 * from a specific serialization format in a specific way. 
 * For each main format (JSON, MsgPack) there is a base class which can be 
 * used to automatically delegate to the proper subclasses: for saving the 
 * base class will delegate to the most recent or preferred format, for loading
 * the base class will parse the input to detect which format it is in and 
 * pass controll to the appropriate subclass for the format. 
 * 
 * @author Johann Petrak
 */
public interface FormatSupport {
  public void save(BdocDocument bdoc, OutputStream os);
  public BdocDocument load_bdoc(InputStream is);
}
