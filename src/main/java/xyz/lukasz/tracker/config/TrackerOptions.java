package xyz.lukasz.tracker.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TrackerOptions {
    @SerializedName("trackHidden")
    public boolean trackingHidden = true;
    @SerializedName("trackInvisible")
    public boolean trackingInvisible = true;
    @SerializedName("trackSpectators")
    public boolean trackingSpectators = true;
    @SerializedName("trackTeamScoreboard")
    public boolean trackingTeamScoreboard = true;
    @SerializedName("trackOtherTeams")
    public boolean trackingOtherTeams = true;
    @SerializedName("trackSameColor")
    public boolean trackingSameColor = true;
    @SerializedName("trackOtherColors")
    public boolean trackingOtherColors = true;
}