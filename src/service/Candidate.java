package service;

public class Candidate {
    private Integer id;
    private String name;
    private String photo;
    private int vote;
    private double votePercent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public double getVotePercent() {
        return votePercent;
    }

    public void setVotePercent(double votePercent) {
        this.votePercent = votePercent;
    }
}
