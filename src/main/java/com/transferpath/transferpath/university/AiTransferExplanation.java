package com.transferpath.transferpath.university;

import java.util.List;

public class AiTransferExplanation {

    private String summary;
    private List<String> strengths;
    private List<String> risks;
    private List<String> nextSteps;

    public AiTransferExplanation(
            String summary,
            List<String> strengths,
            List<String> risks,
            List<String> nextSteps
    ) {
        this.summary = summary;
        this.strengths = strengths;
        this.risks = risks;
        this.nextSteps = nextSteps;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public List<String> getRisks() {
        return risks;
    }

    public List<String> getNextSteps() {
        return nextSteps;
    }
}