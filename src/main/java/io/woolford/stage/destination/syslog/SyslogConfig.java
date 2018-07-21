/*
 * Copyright 2017 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.woolford.stage.destination.syslog;

import _ss_com.streamsets.pipeline.lib.el.RecordEL;
import _ss_com.streamsets.pipeline.lib.el.TimeNowEL;
import com.cloudbees.syslog.MessageFormat;
import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;

public class SyslogConfig {

    @ConfigDef(
            required = true,
            label = "Protocol",
            type = ConfigDef.Type.MODEL,
            defaultValue = "UDP",
            description = "Syslog protocol",
            displayPosition = 10,
            group = "SYSLOG"
    )
    @ValueChooserModel(ProtocolChooserValues.class)
    public String syslogProtocolType;

    @ConfigDef(
            required = true,
            label = "Syslog host",
            type = ConfigDef.Type.STRING,
            defaultValue = "localhost",
            description = "Syslog server hostname",
            displayPosition = 20,
            group = "SYSLOG"
    )
    public String syslogServerName;

    @ConfigDef(
            required = true,
            label = "Syslog port",
            type = ConfigDef.Type.NUMBER,
            defaultValue = "514",
            description = "Syslog port",
            displayPosition = 30,
            group = "SYSLOG"
    )
    public int syslogServerPort;

    @ConfigDef(
            required = true,
            label = "Message format",
            type = ConfigDef.Type.MODEL,
            defaultValue = "RFC_3164",
            description = "Message format",
            displayPosition = 40,
            group = "SYSLOG"
    )
    @ValueChooserModel(MessageFormatChooserValues.class)
    public MessageFormat syslogMessageFormat = MessageFormat.RFC_3164;

    @ConfigDef(
            required = true,
            label = "Message Text",
            type = ConfigDef.Type.STRING,
            description = "Expression to get message text",
            displayPosition = 10,
            group = "MESSAGE",
            elDefs = {RecordEL.class, TimeNowEL.class},
            evaluation = ConfigDef.Evaluation.EXPLICIT
    )
    public String syslogMessageTextEL;

    @ConfigDef(
            required = true,
            label = "Message hostname",
            type = ConfigDef.Type.STRING,
            defaultValue = "localhost",
            description = "Expression to get message hostname",
            displayPosition = 20,
            group = "MESSAGE",
            elDefs = {RecordEL.class, TimeNowEL.class},
            evaluation = ConfigDef.Evaluation.EXPLICIT
    )
    public String syslogMessageHostnameEL;

    @ConfigDef(
            required = true,
            label = "Application name",
            type = ConfigDef.Type.STRING,
            defaultValue = "sdc-syslog",
            description = "Expression to get application name",
            displayPosition = 30,
            group = "MESSAGE",
            elDefs = {RecordEL.class, TimeNowEL.class},
            evaluation = ConfigDef.Evaluation.EXPLICIT
    )
    public String syslogApplicationNameEL;

    @ConfigDef(
            required = true,
            label = "Severity level",
            type = ConfigDef.Type.STRING,
            defaultValue = "1",
            description = "Expression to get severity level integer",
            displayPosition = 40,
            group = "MESSAGE",
            elDefs = {RecordEL.class, TimeNowEL.class},
            evaluation = ConfigDef.Evaluation.EXPLICIT
    )
    public String syslogSeverityEL;

    @ConfigDef(
            required = true,
            label = "Syslog facility",
            type = ConfigDef.Type.STRING,
            defaultValue = "1",
            description = "Expression to get syslog facility integer",
            displayPosition = 50,
            group = "MESSAGE",
            elDefs = {RecordEL.class, TimeNowEL.class},
            evaluation = ConfigDef.Evaluation.EXPLICIT
    )
    public String syslogFacilityEL;
}
