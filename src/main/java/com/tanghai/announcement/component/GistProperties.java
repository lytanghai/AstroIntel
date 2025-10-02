package com.tanghai.announcement.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GistProperties {

    @Value("${GITHUB_TOKEN}")
    private String githubToken;

    @Value("${GIST_ID}")
    private String gistId;

    public String getGithubToken() { return githubToken; }
    public void setGithubToken(String githubToken) { this.githubToken = githubToken; }

    public String getGistId() { return gistId; }
    public void setGistId(String gistId) { this.gistId = gistId; }
}
