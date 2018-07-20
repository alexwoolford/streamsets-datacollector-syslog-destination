package io.woolford.stage.destination.syslog;

import com.cloudbees.syslog.Severity;
import com.streamsets.pipeline.api.base.BaseEnumChooserValues;

public class SyslogSeverityChooserValues extends BaseEnumChooserValues<Severity> {
    public SyslogSeverityChooserValues() {
        super(Severity.class);
    }
}

