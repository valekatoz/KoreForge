package net.kore.settings;

import com.google.gson.annotations.*;
import java.util.function.*;

public class Setting
{
    @Expose
    @SerializedName("name")
    public String name;
    private Predicate<Boolean> predicate;

    protected Setting(final String name, final Predicate<Boolean> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    protected Setting(final String name) {
        this(name, null);
    }

    public boolean isHidden() {
        return this.predicate != null && this.predicate.test(true);
    }
}
