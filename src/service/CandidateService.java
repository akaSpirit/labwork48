package service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CandidateService {
    private List<Candidate> candidates;

    public CandidateService() {
        AtomicInteger count = new AtomicInteger(1);
        this.candidates = FileService.readCandidates();
        candidates.forEach(e -> {
            e.setId(count.get());
            count.getAndIncrement();
        });
    }

    public List<Candidate> getAllCandidates() {
        return candidates;
    }

    public Candidate getCandidate(int id) {
        Candidate candidate = candidates.stream().filter(e -> e.getId() == id).findAny().orElseThrow();
        return candidate;
    }
}
