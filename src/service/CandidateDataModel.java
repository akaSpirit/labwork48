package service;

import java.util.List;

public class CandidateDataModel {
    private List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public CandidateDataModel(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}

