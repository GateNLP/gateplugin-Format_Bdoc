/*
 * Copyright (c) 2019 The University of Sheffield.
 *
 * This file is part of gatelib-basicdocument 
 * (see https://github.com/GateNLP/gatelib-basicdocument).
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

import java.util.HashMap;
import java.util.Map;

/**
 * Mainly static helper functions.
 * 
 * @author Johann Petrak johann.petrak@gmail.com
 */
public class BdocUtils {
  /**
   * Add features from the original feature map into the given map or a new map.
   * 
   * This either copies over features from the feature map into the given map
   * or if the map is null, creates a new map that gets the featres.
   * If a feature is of type String, it is copied over as is, otherwise
   * the feature is converted to String using its toString method. 
   * 
   * @param fm  the feature map from which to add features
   * @param map target map, if null will create a new map.
   * @return The given map filled with the features or a new map.
   */
  public static Map<String, Object> featureMap2Map(
          Map<Object, Object> fm, Map<String, Object> map) {
    Map<String, Object> ret;
    if(map == null) {
      ret = new HashMap<>();
    } else {
      ret = map;
    }
    for(Object k : fm.keySet()) {
      if (k == null) {
        // we ignore null keys here, if we have any
        continue;
      }
      ret.put((k instanceof String) ? (String)k : k.toString(), fm.get(k));
    }
    return ret; 
  }
}
