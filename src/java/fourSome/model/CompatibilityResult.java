package fourSome.model;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityResult {
    private boolean compatible;
    private List<String> errors;
    private List<String> warnings;

    public CompatibilityResult() {
        this.compatible = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public void addError(String message) {
        this.compatible = false;
        this.errors.add(message);
    }

    public void addWarning(String message) {
        this.warnings.add(message);
    }

    public boolean isCompatible() { return compatible; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
}