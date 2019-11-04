package util;

import org.jetbrains.annotations.NotNull;

public class RegexpPracticeExtendedTest extends util.RegexpReferenceTest {
    @NotNull
    @Override
    public RegexpPracticeInterface getReg() {
        return new RegexpPractice();
    }
}
