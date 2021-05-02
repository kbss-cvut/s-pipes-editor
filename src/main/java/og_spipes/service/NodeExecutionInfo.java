package og_spipes.service;

import java.util.HashSet;
import java.util.Set;

class NodeExecutionInfo {

    private final Set<String> input = new HashSet<>();
    private final Set<String> output = new HashSet<>();

    public NodeExecutionInfo() {
    }

    public Set<String> getInput() {
        return input;
    }

    public Set<String> getOutput() {
        return output;
    }

    public void merge(NodeExecutionInfo executionInfo) {
        input.addAll(executionInfo.getInput());
        output.addAll(executionInfo.getOutput());
    }

    @Override
    public String toString() {
        return "NodeExecutionInfo{" +
                "input=" + input +
                ", output=" + output +
                '}';
    }
}
