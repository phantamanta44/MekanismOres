package io.github.phantamanta44.mekores.ore;

import javax.annotation.Nullable;

public class OreValidity {

    public static final OreValidity VALID = new OreValidity(Reason.VALID);
    public static final OreValidity INVALID_BLACKLIST = new OreValidity(Reason.INVALID_BLACKLIST);
    public static final OreValidity INVALID_NO_ORE = new OreValidity(Reason.INVALID_NO_ORE);
    public static final OreValidity INVALID_NO_RESULT = new OreValidity(Reason.INVALID_NO_RESULT);

    private final Reason reason;
    @Nullable
    private final Object context;

    public OreValidity(Reason reason, @Nullable Object context) {
        this.reason = reason;
        this.context = context;
    }

    public OreValidity(Reason reason) {
        this(reason, null);
    }

    public boolean isValid() {
        return reason == Reason.VALID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(reason);
        if (context != null) {
            sb.append(" {").append(context).append("}");
        }
        return sb.toString();
    }

    public enum Reason {
        VALID, INVALID_BLACKLIST, INVALID_GEM, INVALID_NO_ORE, INVALID_NO_RESULT, INVALID_EXISTING_INTEGRATION
    }

}
