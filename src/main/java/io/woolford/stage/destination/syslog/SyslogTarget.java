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
import _ss_com.streamsets.pipeline.stage.common.DefaultErrorRecordHandler;
import _ss_com.streamsets.pipeline.stage.common.ErrorRecordHandler;
import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import com.streamsets.pipeline.api.Batch;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseTarget;
import com.streamsets.pipeline.api.el.ELEval;
import com.streamsets.pipeline.api.el.ELEvalException;
import com.streamsets.pipeline.api.el.ELVars;
import com.streamsets.pipeline.api.impl.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This target is an example and does not actually write to any destination.
 */
public class SyslogTarget extends BaseTarget {

    private static final Logger LOG = LoggerFactory.getLogger(SyslogTarget.class);
    private final SyslogConfig config;
    private ErrorRecordHandler errorRecordHandler;
    private ELEval syslogmessageTextElEval;
    private ELEval syslogMessageHostnameElEval;
    private ELEval syslogApplicationNameElEval;
    private ELEval syslogFacilityEval;
    private ELEval syslogSeverityEval;
    private UdpSyslogMessageSender udpSender = new UdpSyslogMessageSender();
    private TcpSyslogMessageSender tcpSender = new TcpSyslogMessageSender();

    public SyslogTarget(SyslogConfig config) {
        this.config = config;
    }

    private static <T> T resolveEL(ELEval elEval, ELVars elVars, String configValue, Class<T> returnType) throws ELEvalException {
        return elEval.eval(elVars, configValue, returnType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ConfigIssue> init() {
        // Validate configuration values and open any required resources.
        List<ConfigIssue> issues = super.init();
        errorRecordHandler = new DefaultErrorRecordHandler(getContext());

        this.syslogmessageTextElEval = getContext().createELEval("syslogMessageTextEL");
        this.syslogMessageHostnameElEval = getContext().createELEval("syslogMessageHostnameEL");
        this.syslogApplicationNameElEval = getContext().createELEval("syslogApplicationNameEL");
        this.syslogFacilityEval = getContext().createELEval("syslogFacilityEL");
        this.syslogSeverityEval = getContext().createELEval("syslogSeverityEL");


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
        ELVars variables = getContext().createELVars();

        while (batchIterator.hasNext()) {
            Record record = batchIterator.next();
            try {
                if(config.syslogProtocolType == "TCP") {
                    writeTcp(record, this.tcpSender, variables);
                } else {
                    writeUdp(record, this.udpSender, variables);
                }
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


    private void writeUdp(Record record, UdpSyslogMessageSender messageSender, ELVars variables) throws StageException {

        RecordEL.setRecordInContext(variables, record);
        TimeNowEL.setTimeNowInContext(variables, new Date());

        String messageText = resolveEL(syslogmessageTextElEval, variables, config.syslogMessageTextEL, String.class);
        String messageHostname = resolveEL(syslogMessageHostnameElEval, variables, config.syslogMessageHostnameEL, String.class);
        String applicationName = resolveEL(syslogApplicationNameElEval, variables, config.syslogApplicationNameEL, String.class);
        String facilityStr = resolveEL(syslogFacilityEval, variables, config.syslogFacilityEL, String.class);
        String severityStr = resolveEL(syslogSeverityEval, variables, config.syslogSeverityEL, String.class);

        Facility facility = Facility.fromNumericalCode(Integer.parseInt(facilityStr));
        Severity severity = Severity.fromNumericalCode(Integer.parseInt(severityStr));

        messageSender.setSyslogServerHostname(config.syslogServerName);
        messageSender.setSyslogServerPort(config.syslogServerPort);
        messageSender.setMessageFormat(config.syslogMessageFormat);
        messageSender.setDefaultMessageHostname(messageHostname);
        messageSender.setDefaultAppName(applicationName);
        messageSender.setDefaultFacility(facility);
        messageSender.setDefaultSeverity(severity);

        try {
            messageSender.sendMessage(messageText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTcp(Record record, TcpSyslogMessageSender messageSender, ELVars variables) throws StageException {

        RecordEL.setRecordInContext(variables, record);
        TimeNowEL.setTimeNowInContext(variables, new Date());

        String messageText = resolveEL(syslogmessageTextElEval, variables, config.syslogMessageTextEL, String.class);
        String messageHostname = resolveEL(syslogMessageHostnameElEval, variables, config.syslogMessageHostnameEL, String.class);
        String applicationName = resolveEL(syslogApplicationNameElEval, variables, config.syslogApplicationNameEL, String.class);
        String facilityStr = resolveEL(syslogFacilityEval, variables, config.syslogFacilityEL, String.class);
        String severityStr = resolveEL(syslogSeverityEval, variables, config.syslogSeverityEL, String.class);

        Facility facility = Facility.fromNumericalCode(Integer.parseInt(facilityStr));
        Severity severity = Severity.fromNumericalCode(Integer.parseInt(severityStr));

        messageSender.setSyslogServerHostname(config.syslogServerName);
        messageSender.setSyslogServerPort(config.syslogServerPort);
        messageSender.setMessageFormat(config.syslogMessageFormat);
        messageSender.setDefaultMessageHostname(messageHostname);
        messageSender.setDefaultAppName(applicationName);
        messageSender.setDefaultFacility(facility);
        messageSender.setDefaultSeverity(severity);

        try {
            messageSender.sendMessage(messageText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
