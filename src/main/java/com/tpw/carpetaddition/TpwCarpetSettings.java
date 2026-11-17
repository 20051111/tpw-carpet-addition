package com.tpw.carpetaddition;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;

/**
 * Carpet rules exposed by TPW Carpet Addition under /carpet.
 */
public class TpwCarpetSettings {
    private static final String TPW_CATEGORY = "TPW";

    @Rule(
        categories = {RuleCategory.SURVIVAL, RuleCategory.FEATURE, TPW_CATEGORY}
    )
    public static boolean renewableSpongeInMonument = false;

    @Rule(
        categories = {TPW_CATEGORY},
        options = {"80", "96", "100", "105", "110"},
        strict = false,
        validators = PrismarineThresholdValidator.class
    )
    public static int renewableSpongePrismarineThreshold = 100;

    @Rule(
        categories = {TPW_CATEGORY},
        options = {"30", "50", "60", "80", "100"},
        strict = false,
        validators = PositiveDenominatorValidator.class
    )
    public static int renewableSpongeChanceDenominator = 60;

    @Rule(
        categories = {TPW_CATEGORY},
        options = {"16", "32", "48", "64"},
        strict = false,
        validators = ConduitRadiusValidator.class
    )
    public static int renewableSpongeConduitRadius = 32;

    @Rule(
        categories = {TPW_CATEGORY},
        options = {"1", "3"},
        strict = true,
        validators = MaxSpawnValidator.class
    )
    public static int renewableSpongeMaxSpawn = 3;

    public static class PrismarineThresholdValidator extends Validator<Integer> {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer value, String userInput) {
            return value != null && value >= 1 && value <= 124 ? value : null;
        }

        @Override
        public String description() {
            return "You must choose a value between 1 and 124";
        }
    }

    public static class PositiveDenominatorValidator extends Validator<Integer> {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer value, String userInput) {
            return value != null && value >= 1 ? value : null;
        }

        @Override
        public String description() {
            return "You must choose a positive integer >= 1";
        }
    }

    public static class ConduitRadiusValidator extends Validator<Integer> {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer value, String userInput) {
            return value != null && value >= 0 && value <= 128 ? value : null;
        }

        @Override
        public String description() {
            return "You must choose a value between 0 and 128";
        }
    }

    public static class MaxSpawnValidator extends Validator<Integer> {
        @Override
        public Integer validate(CommandSourceStack source, CarpetRule<Integer> rule, Integer value, String userInput) {
            return value != null && (value == 1 || value == 3) ? value : null;
        }

        @Override
        public String description() {
            return "You must choose either 1 or 3";
        }
    }
}
