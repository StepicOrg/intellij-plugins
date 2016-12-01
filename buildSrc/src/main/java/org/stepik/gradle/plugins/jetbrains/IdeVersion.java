package org.stepik.gradle.plugins.jetbrains;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class IdeVersion {

    private static final String BUILD_NUMBER = "__BUILD_NUMBER__";
    private static final String STAR = "*";
    private static final String SNAPSHOT = "SNAPSHOT";
    private static final String FALLBACK_VERSION = "999.SNAPSHOT";
    private static final int SNAPSHOT_VALUE = Integer.MAX_VALUE;

    private final int[] myComponents;


    private IdeVersion(int... components) {
        myComponents = components;
    }

    @Nullable
    static IdeVersion fromString(@NotNull String version) {
        if (version.trim().isEmpty()) {
            return null;
        }

        if (BUILD_NUMBER.equals(version) || SNAPSHOT.equals(version)) {
            IdeVersion fallback = IdeVersion.fromString(FALLBACK_VERSION);
            if (fallback == null) {
                return null;
            }
            return new IdeVersion(fallback.myComponents);
        }

        String code = version;
        int productSeparator = code.lastIndexOf('-'); //some products have multiple parts, e.g. "FB-IC-143.157"

        if (productSeparator > 0) {
            code = code.substring(productSeparator + 1);
        }

        int baselineVersionSeparator = code.indexOf('.');
        int baselineVersion;
        int buildNumber;

        if (baselineVersionSeparator > 0) {
            String baselineVersionString = code.substring(0, baselineVersionSeparator);
            if (baselineVersionString.trim().isEmpty()) {
                return null;
            }

            String[] components = code.split("\\.");
            List<Integer> intComponentsList = new ArrayList<>();

            for (String component : components) {
                int comp;
                try {
                    comp = parseBuildNumber(component);
                } catch (NumberFormatException e) {
                    return null;
                }
                intComponentsList.add(comp);
                if (comp == SNAPSHOT_VALUE) {
                    break;
                }
            }

            int[] intComponents = new int[intComponentsList.size()];
            for (int i = 0; i < intComponentsList.size(); i++) {
                intComponents[i] = intComponentsList.get(i);
            }

            return new IdeVersion(intComponents);

        } else {
            try {
                buildNumber = parseBuildNumber(code);
            } catch (NumberFormatException e) {
                return null;
            }

            if (buildNumber <= 2000) {
                // it's probably a baseline, not a build number
                return new IdeVersion(buildNumber, 0);
            }

            baselineVersion = getBaseLineForHistoricBuilds(buildNumber);
            return new IdeVersion(baselineVersion, buildNumber);
        }

    }

    private static int parseBuildNumber(@NotNull String code) {
        if (SNAPSHOT.equals(code) || STAR.equals(code) || BUILD_NUMBER.equals(code)) {
            return SNAPSHOT_VALUE;
        }
        return Integer.parseInt(code);
    }

    // See http://www.jetbrains.net/confluence/display/IDEADEV/Build+Number+Ranges for historic build ranges
    private static int getBaseLineForHistoricBuilds(int bn) {
        if (bn >= 10000) {
            return 88; // Maia, 9x builds
        }

        if (bn >= 9500) {
            return 85; // 8.1 builds
        }

        if (bn >= 9100) {
            return 81; // 8.0.x builds
        }

        if (bn >= 8000) {
            return 80; // 8.0, including pre-release builds
        }

        if (bn >= 7500) {
            return 75; // 7.0.2+
        }

        if (bn >= 7200) {
            return 72; // 7.0 final
        }

        if (bn >= 6900) {
            return 69; // 7.0 pre-M2
        }

        if (bn >= 6500) {
            return 65; // 7.0 pre-M1
        }

        if (bn >= 6000) {
            return 60; // 6.0.2+
        }

        if (bn >= 5000) {
            return 55; // 6.0 branch, including all 6.0 EAP builds
        }

        if (bn >= 4000) {
            return 50; // 5.1 branch
        }

        return 40;
    }

    int getBaselineVersion() {
        return myComponents[0];
    }

    int getBuild() {
        return myComponents[1];
    }
}
