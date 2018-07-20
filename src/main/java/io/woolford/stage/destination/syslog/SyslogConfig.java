package io.woolford.stage.destination.syslog;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;

public class SyslogConfig {

    @ConfigDef(
            required = true,
            label = "Syslog server hostname",
            type = ConfigDef.Type.STRING,
            defaultValue = "localhost",
            description = "Syslog server hostname",
            displayPosition = 10,
            group = "SYSLOG"
    )
    public String syslogServerName;

    @ConfigDef(
            required = true,
            label = "Syslog server port",
            type = ConfigDef.Type.NUMBER,
            defaultValue = "514",
            description = "Syslog server port",
            displayPosition = 20,
            group = "SYSLOG"
    )
    public int syslogServerPort;

    @ConfigDef(
            required = true,
            label = "Syslog severity level",
            type = ConfigDef.Type.MODEL,
            defaultValue = "INFORMATIONAL",
            description = "Syslog severity level",
            displayPosition = 30,
            group = "SYSLOG"
    )
    @ValueChooserModel(SyslogSeverityChooserValues.class)
    public SyslogSeverityTypes syslogServerSeverityType = SyslogSeverityTypes.INFORMATIONAL;



}
