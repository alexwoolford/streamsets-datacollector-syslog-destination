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

import com.cloudbees.syslog.MessageFormat;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.OnRecordError;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Target;
import com.streamsets.pipeline.sdk.RecordCreator;
import com.streamsets.pipeline.sdk.TargetRunner;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestSyslogTarget {
    private static SyslogDTarget getDefaultConfig() {
        SyslogDTarget dtarget = new SyslogDTarget();
        dtarget.config = new SyslogConfig();
        dtarget.config.syslogServerName = "localhost";
        dtarget.config.syslogServerPort = 2514;
        dtarget.config.syslogMessageFormat = MessageFormat.RFC_3164;
        dtarget.config.syslogProtocolType = "UDP";
        dtarget.config.syslogMessageTextEL = "${record:value('/message')}";
        dtarget.config.syslogMessageHostnameEL = "${record:value('/hostname')}";
        dtarget.config.syslogApplicationNameEL = "${record:value('/application')}";
        dtarget.config.syslogFacilityEL = "${record:value('/facility')}";
        dtarget.config.syslogSeverityEL = "${record:value('/severity')}";
        return dtarget;
    }

    @Test
    public void testWriteSingleRecord() throws Exception {
        SyslogDTarget config = getDefaultConfig();
        Target target = config.createTarget();
        TargetRunner runner = new TargetRunner.Builder(SyslogDTarget.class, target).setOnRecordError(OnRecordError.TO_ERROR).build();

        runner.runInit();

        Record record = RecordCreator.create();
        Map<String, Field> fields = new HashMap<>();
        fields.put("message", Field.create("myTestMessage"));
        fields.put("hostname", Field.create("myTestHost"));
        fields.put("application", Field.create("myTestApp"));
        fields.put("facility", Field.create("1"));
        fields.put("severity", Field.create("1"));
        record.set(Field.create(fields));

        runner.runWrite(Arrays.asList(record));

        runner.runDestroy();

    }
}
