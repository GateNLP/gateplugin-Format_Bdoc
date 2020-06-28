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
package gate.plugin.format.bdoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Work around some annoying things in various libs.
 * 
 * @author Johann Petrak
 */
public class FixJavaAndLibs {
  /**
   * Fake the ability to set the logger level.
   * We assume the backend level is always configured to be INFO.
   */
  public static class FixedSlf4JLogger {
    public static final int DEBUG = 100;
    public static final int INFO = 100;
    public static final int WARN = 100;
    public static final int ERROR = 100;
    private FixedSlf4JLogger() {}
    private Logger logger;
    private int level = INFO;
    public FixedSlf4JLogger(String name) {
      logger = LoggerFactory.getLogger(name);
    }
    public void setLevel(int level) {
      this.level = level;
    }
    public void debug(String msg) {
      if(level >= DEBUG) {
        logger.info(msg);
      }
    }
    public void info(String msg) {
      if(level >= INFO) {
        logger.info(msg);
      }
    }
    public void warn(String msg) {
      if(level >= WARN) {
        logger.warn(msg);
      }
    }
    public void error(String msg) {
      if(level >= ERROR) {
        logger.error(msg);
      }
    }
    public void error(String msg, Throwable thrw) {
      if(level >= ERROR) {
        logger.error(msg, thrw);
      }
    }
    
  }
}
