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

import _ss_com.streamsets.pipeline.stage.common.DefaultErrorRecordHandler;
import _ss_com.streamsets.pipeline.stage.common.ErrorRecordHandler;
import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import com.streamsets.pipeline.api.base.RecordTarget;
import io.woolford.stage.lib.sample.Errors;

import com.streamsets.pipeline.api.Batch;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseTarget;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.impl.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * This target is an example and does not actually write to any destination.
 */
public class SyslogTarget extends RecordTarget {

    private static final Logger LOG = LoggerFactory.getLogger(SyslogTarget.class);

    private final SyslogConfig config;
    private ErrorRecordHandler errorRecordHandler;


    public SyslogTarget(SyslogConfig config) {
        this.config = config;
    }


    /** {@inheritDoc} */
    @Override
    protected List<ConfigIssue> init() {
        // Validate configuration values and open any required resources.
        List<ConfigIssue> issues = super.init();
        errorRecordHandler = new DefaultErrorRecordHandler(getContext());
        return issues;
    }

    @Override
    public void destroy() {
        // Clean up any open resources.
        super.destroy();
    }

    @Override
    public void write(Batch batch) throws StageException {
        Iterator<Record> batchIterator = batch.getRecords();

        while (batchIterator.hasNext()) {
            Record record = batchIterator.next();
            try {
                write(record);
            } catch (Exception e) {
                switch (getContext().getOnErrorRecord()) {
                    case DISCARD:
                        break;
                    case TO_ERROR:
                        getContext().toError(record, Errors.SYSLOG_01, e.toString());
                        break;
                    case STOP_PIPELINE:
                        throw new StageException(Errors.SYSLOG_01, e.toString());
                    default:
                        throw new IllegalStateException(
                                Utils.format("Unknown OnError value '{}'", getContext().getOnErrorRecord(), e)
                        );
                }
            }
        }
    }


    public void write(Record record) {

        // TODO: write the records to your final destination

        String message = record.get("/message").getValue().toString();

        // Initialise sender
        UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
        messageSender.setDefaultMessageHostname("myhostname"); // some syslog cloud services may use this field to transmit a secret key
        messageSender.setDefaultAppName("myapp");
        messageSender.setDefaultFacility(Facility.USER);
        messageSender.setDefaultSeverity(config.syslogServerSeverityType);
        messageSender.setSyslogServerHostname(config.syslogServerName);
        messageSender.setSyslogServerPort(config.syslogServerPort);
        messageSender.setMessageFormat(MessageFormat.RFC_3164); // optional, default is RFC 3164

        // send a Syslog message
        try {
            messageSender.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
