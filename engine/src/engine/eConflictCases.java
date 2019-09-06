package engine;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public enum eConflictCases
{
    CONFLICT_1(true, true, true, true, true, true) { @Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "ours";}},
    CONFLICT_2(true, true, true, true, false, false) { @Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "their"; }},
    CONFLICT_3(true, true, true, false, true, false) { @Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "ours"; }},
    CONFLICT_4(true, true, true, false, false, false) { @Override public boolean isConflict() { return true; } @Override public String getFileVersionToTake() {return StringFinals.EMPTY_STRING; }},
    CONFLICT_5(true, true, false, true, false, false) {@Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "their";}},
    CONFLICT_6(true, true, false, false, false, false) { @Override public boolean isConflict() { return true; } @Override public String getFileVersionToTake() {return StringFinals.EMPTY_STRING; }},
    CONFLICT_7(true, false, true, false, true, false) { @Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "ours"; }},
    CONFLICT_8(true, false, true, false, false, false) { @Override public boolean isConflict() { return true; } @Override public String getFileVersionToTake() {return StringFinals.EMPTY_STRING; }},
    CONFLICT_9(false, true, true, false, false, false) { @Override public boolean isConflict() { return true; } @Override public String getFileVersionToTake() {return StringFinals.EMPTY_STRING; }},
    CONFLICT_10(true, false, false, false, false, false){ @Override public boolean isConflict() { return true; } @Override public String getFileVersionToTake() {return StringFinals.EMPTY_STRING; }},
    CONFLICT_11(false, true, false, false, false, false) { @Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "ours"; }},
    CONFLICT_12(false, false, true, false, false, false) { @Override public boolean isConflict() { return false; } @Override public String getFileVersionToTake() {return "their"; }};

    private boolean isCondition_0, isCondition_1, isCondition_2,
            isCondition_3, isCondition_4, isCondition_5;


    eConflictCases(boolean i_c0, boolean i_c1, boolean i_c2, boolean i_c3, boolean i_c4, boolean i_c5)
    {
        isCondition_0 = i_c0;
        isCondition_1 = i_c1;
        isCondition_2 = i_c2;
        isCondition_3 = i_c3;
        isCondition_4 = i_c4;
        isCondition_5 = i_c5;
    }

    public static Optional<eConflictCases> getItem(boolean ... values) {
        return Arrays.stream(eConflictCases.values())
                .filter(e -> values[0] == e.isCondition_0)
                .filter(e -> values[1] == e.isCondition_1)
                .filter(e -> values[2] == e.isCondition_2)
                .filter(e -> values[3] == e.isCondition_3)
                .filter(e -> values[4] == e.isCondition_4)
                .filter(e -> values[5] == e.isCondition_5)
                .findFirst();
    }


    public abstract boolean isConflict();
    public abstract String getFileVersionToTake();
}
