package fourSome.model;

import java.util.Date;

public class AssemblyTestReports {

    private Integer reportId;
    private Integer assemblyId;
    private Boolean bootTestPassed;
    private Integer cpuIdleTempC;
    private Integer cpuLoadTempC;
    private Integer gpuIdleTempC;
    private Integer gpuLoadTempC;
    private Integer cinebenchScore;
    private Integer _3dmarkScore;
    private Boolean stabilityTestPassed;
    private Integer stabilityTestMinutes;
    private String note;
    private String finalResult;
    private Date completedAt;
    private String status;

    public AssemblyTestReports() {}

    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }

    public Integer getAssemblyId() { return assemblyId; }
    public void setAssemblyId(Integer assemblyId) { this.assemblyId = assemblyId; }

    public Boolean getBootTestPassed() { return bootTestPassed; }
    public void setBootTestPassed(Boolean bootTestPassed) { this.bootTestPassed = bootTestPassed; }

    public Integer getCpuIdleTempC() { return cpuIdleTempC; }
    public void setCpuIdleTempC(Integer cpuIdleTempC) { this.cpuIdleTempC = cpuIdleTempC; }

    public Integer getCpuLoadTempC() { return cpuLoadTempC; }
    public void setCpuLoadTempC(Integer cpuLoadTempC) { this.cpuLoadTempC = cpuLoadTempC; }

    public Integer getGpuIdleTempC() { return gpuIdleTempC; }
    public void setGpuIdleTempC(Integer gpuIdleTempC) { this.gpuIdleTempC = gpuIdleTempC; }

    public Integer getGpuLoadTempC() { return gpuLoadTempC; }
    public void setGpuLoadTempC(Integer gpuLoadTempC) { this.gpuLoadTempC = gpuLoadTempC; }

    public Integer getCinebenchScore() { return cinebenchScore; }
    public void setCinebenchScore(Integer cinebenchScore) { this.cinebenchScore = cinebenchScore; }

    public Integer get_3dmarkScore() { return _3dmarkScore; }
    public void set_3dmarkScore(Integer _3dmarkScore) { this._3dmarkScore = _3dmarkScore; }

    public Boolean getStabilityTestPassed() { return stabilityTestPassed; }
    public void setStabilityTestPassed(Boolean stabilityTestPassed) { this.stabilityTestPassed = stabilityTestPassed; }

    public Integer getStabilityTestMinutes() { return stabilityTestMinutes; }
    public void setStabilityTestMinutes(Integer stabilityTestMinutes) { this.stabilityTestMinutes = stabilityTestMinutes; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }

    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
