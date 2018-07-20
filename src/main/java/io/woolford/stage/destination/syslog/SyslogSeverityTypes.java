package io.woolford.stage.destination.syslog;


import com.streamsets.pipeline.api.GenerateResourceBundle;
import com.streamsets.pipeline.api.Label;

@GenerateResourceBundle
public enum SyslogSeverityTypes implements Label {

    EMERGENCY("EMERGENCY"),
    ALERT("ALERT"),
    CRITICAL("CRITICAL"),
    ERROR("ERROR"),
    WARNING("WARNING"),
    NOTICE("NOTICE"),
    INFORMATIONAL("INFORMATIONAL"),
    DEBUG("DEBUG");

    private final String label;

    private SyslogSeverityTypes(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}


//import com.streamsets.pipeline.api.GenerateResourceBundle;
//        import com.streamsets.pipeline.api.Label;
//
//@GenerateResourceBundle
//public enum CouchbaseVersionTypes implements Label {
//    VERSION4("Version 4"),
//    VERSION5("Version 5");
//
//    private final String label;
//
//    private CouchbaseVersionTypes(String label) {
//        this.label = label;
//    }
//
//    public String getLabel() {
//        return this.label;
//    }
//}
//
