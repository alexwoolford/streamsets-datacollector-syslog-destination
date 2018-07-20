package io.woolford.stage.destination.syslog;

import com.streamsets.pipeline.api.ConfigDef;

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
            type = ConfigDef.Type.STRING,
            defaultValue = "514",
            description = "Syslog server port",
            displayPosition = 10,
            group = "SYSLOG"
    )
    public String syslogServerPort;

}
