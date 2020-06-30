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

package gate.lib.basicdocument;

import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Something that represents changes to a document.
 * 
 * @author  Johann Petrak johann.petrak@gmail.com
 */
public class ChangeLog {
  
  /**
   * The list of changes.
   */
  public List<Map<String, Object>> changes = new ArrayList<>();
  
  /**
   * The offset type.
   * This is "j" for Java and "p" for Python/proper.
   */
  public String offset_type = "j";
  
  /**
   * In-place updating of the offsets to the desired new type.
   * 
   * Changes the offsets of all annotation change entries to the given type 
   * using the given offset mapper. If the ChangeLog already has the desired
   * offset type, this does nothing. 
   * 
   * @param om the offset mapper to use
   * @param newtype  the desired new offset type
   */
  public void fixupOffsets(OffsetMapper om, String newtype) {
    if(!newtype.equals("j") && !newtype.equals("p")) {
      throw new GateRuntimeException("Offset type must be 'j' or 'p'");
    }
    if(offset_type.equals(newtype)) {
      return;
    }    
    for(Map<String,Object> change : changes) {
      if(change.containsKey("start")) {
        if(newtype.equals("j")) {
          change.put("start", om.convertToJava((Integer)change.get("start")));
        } else {
          change.put("start", om.convertToPython((Integer)change.get("start")));
        }
      }
      if(change.containsKey("end")) {
        if(newtype.equals("j")) {
          change.put("end", om.convertToJava((Integer)change.get("end")));
        } else {
          change.put("end", om.convertToPython((Integer)change.get("end")));
        }
      }
    }
  }

  /**
   * Create a ChangeLog from the raw Map we can get from JSON.
   * 
   * @param map the Map object
   * @return ChangeLog instance
   */
  @SuppressWarnings("unchecked")
  public static ChangeLog fromMap(Map<String,Object> map) {
    ChangeLog log = new ChangeLog();
    log.changes = (List<Map<String, Object>>)map.get("changes");
    log.offset_type = (String)map.get("offset_type");
    log.gatenlp_type = "ChangeLog";  // TODO: REMOVE!!
    return log;
  }
  
  /**
   * Type identifier.
   */
  public String gatenlp_type = "ChangeLog"; // TODO: REMOVE!
  
}
