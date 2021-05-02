package xyz.lukasz.tracker.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TrackerOptions {
    @SerializedName("trackHidden")
    public boolean trackingHidden = true;
    public boolean shouldExcludeHidden() { return !trackingHidden; }

    @SerializedName("trackInvisible")
    public boolean trackingInvisible = true;
    public boolean shouldExcludeInvisible() { return !trackingInvisible; }

    @SerializedName("trackSpectators")
    public boolean trackingSpectators = true;
    public boolean shouldExcludeSpectators() { return !trackingSpectators; }
    @SerializedName("trackTeamScoreboard")
    public boolean trackingTeamScoreboard = true;
    public boolean shouldExcludeSameTeam() { return !trackingTeamScoreboard; }

    @SerializedName("trackOtherTeams")
    public boolean trackingOtherTeams = true;
    public boolean shouldExcludeOtherTeams() { return !trackingOtherTeams; }

    @SerializedName("trackSameColor")
    public boolean trackingSameColor = true;
    public boolean shouldExcludeSameColor() { return !trackingSameColor; }

    @SerializedName("trackOtherColors")
    public boolean trackingOtherColors = true;
    public boolean shouldExcludeOtherColors() { return !trackingOtherColors; }
}