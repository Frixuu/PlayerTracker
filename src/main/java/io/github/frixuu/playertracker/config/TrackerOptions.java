package io.github.frixuu.playertracker.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TrackerOptions {
    @SerializedName("trackHidden")
    public boolean trackingHidden;
    @SerializedName("trackInvisible")
    public boolean trackingInvisible;
    @SerializedName("trackSpectators")
    public boolean trackingSpectators;
    @SerializedName("trackTeamScoreboard")
    public boolean trackingTeamScoreboard;
    @SerializedName("trackSameColor")
    public boolean trackingSameColor;
}