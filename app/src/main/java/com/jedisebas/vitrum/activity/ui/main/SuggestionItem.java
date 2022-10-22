package com.jedisebas.vitrum.activity.ui.main;

public class SuggestionItem {

    private int id;
    private String title;
    // something for images
    private String voteUp;
    private String voteDown;
    private String comments;
    private int status;

    SuggestionItem(final int id, final String title, final String voteUp, final String voteDown, final String comments, final int status) {
        this.id = id;
        this.title = title;
        this.voteUp = voteUp;
        this.voteDown = voteDown;
        this.comments = comments;
        this.status = status;
    }

    SuggestionItem(final SuggestionItem item) {
        this(item.getId(), item.getTitle(), item.getVoteUp(), item.getVoteDown(), item.getComments(), item.getStatus());
    }

    public SuggestionItem() {
        this(0, null, null, null, null, 0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getVoteUp() {
        return voteUp;
    }

    public void setVoteUp(final String voteUp) {
        this.voteUp = voteUp;
    }

    public String getVoteDown() {
        return voteDown;
    }

    public void setVoteDown(final String voteDown) {
        this.voteDown = voteDown;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }
}
