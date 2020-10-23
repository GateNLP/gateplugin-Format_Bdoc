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

import gate.Resource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import gate.creole.AbstractResource;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.GateRuntimeException;

@CreoleResource(
        name = "FormatBdocVersionLogger",
        comment = "Log the version of the plugin",
        tool = true,
        isPrivate = true,
        autoinstances = {@AutoInstance(hidden = true)}
)
public class VersionLogger extends AbstractResource {
    protected boolean versionInfoShown = false;

    /**
     * Our logger instance.
     */
    public transient org.apache.log4j.Logger logger
            = org.apache.log4j.Logger.getLogger(this.getClass());


    /**
     * Initialize resource.
     * @return resource
     */
    @Override
    public Resource init() {
        if (!versionInfoShown) {
            // Show the version of this plugin
            try {
                Properties properties = new Properties();
                InputStream is = getClass().getClassLoader().getResourceAsStream("gateplugin-Format_Bdoc.git.properties");
                if (is != null) {
                    properties.load(is);
                    String buildVersion = properties.getProperty("gitInfo.build.version");
                    String isDirty = properties.getProperty("gitInfo.dirty");
                    if (buildVersion != null && buildVersion.endsWith("-SNAPSHOT")) {
                        logger.info("Plugin Format_Bdoc version=" + buildVersion
                                + " commit="
                                + properties.getProperty("gitInfo.commit.id.abbrev")
                                + " dirty=" + isDirty
                        );
                    }
                } else {
                    logger.error("Could not obtain plugin Format_Bdoc version info");
                }
            } catch (IOException ex) {
                logger.error("Could not obtain plugin Format_Bdoc version info: " + ex.getMessage(), ex);
            }
        
        }
        return this;
    }
}
