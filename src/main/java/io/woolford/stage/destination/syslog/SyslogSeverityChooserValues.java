package io.woolford.stage.destination.syslog;

import com.streamsets.pipeline.api.base.BaseEnumChooserValues;

public class SyslogSeverityChooserValues extends BaseEnumChooserValues<SyslogSeverityTypes> {
    public SyslogSeverityChooserValues() {
        super(SyslogSeverityTypes.class);
    }
}

