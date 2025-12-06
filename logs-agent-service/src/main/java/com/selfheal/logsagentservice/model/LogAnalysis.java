package com.selfheal.logsagentservice.model;

import java.util.List;

public class LogAnalysis {

    private List<String> rawLogs;
    private String rootCause;
    private List<String> warnings;
    private List<String> errors;

    public List<String> getRawLogs() { return rawLogs; }
    public void setRawLogs(List<String> rawLogs) { this.rawLogs = rawLogs; }

    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
