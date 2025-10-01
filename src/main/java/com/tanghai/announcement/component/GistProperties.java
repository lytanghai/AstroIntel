package com.tanghai.announcement.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gist")
public class GistProperties {
    private String githubToken;
    private String gistId;

    public String getGithubToken() { return githubToken; }
    public void setGithubToken(String githubToken) { this.githubToken = githubToken; }

    public String getGistId() { return gistId; }
    public void setGistId(String gistId) { this.gistId = gistId; }
}
