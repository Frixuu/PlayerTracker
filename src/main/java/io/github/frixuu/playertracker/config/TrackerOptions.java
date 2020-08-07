package io.github.frixuu.playertracker.config;

import lombok.Data;

@Data
public class TrackerOptions {
    public boolean trackHidden;
    public boolean trackInvisible;
    public boolean trackSpectators;
    public boolean trackTeamScoreboard;
    public boolean trackSameColor;
}