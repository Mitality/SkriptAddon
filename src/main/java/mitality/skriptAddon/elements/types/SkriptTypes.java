package mitality.skriptAddon.elements.types;

import bodyhealth.core.BodyPart;
import bodyhealth.core.BodyPartState;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.jetbrains.annotations.Nullable;

public class SkriptTypes {

    public static void register() {
        Classes.registerClass(new ClassInfo<>(BodyPart.class, "bodypart")
            .user("body ?parts?")
            .name("Body Part")
            .description("A body part of a player: head, torso, left arm, right arm, left leg, right leg, left foot, right foot.")
            .examples("heal left arm of player by 50", "set body health of head of player to 100")
            .since("1.0.0")
            .parser(new Parser<BodyPart>() {
                @Override
                public @Nullable BodyPart parse(String s, ParseContext context) {
                    return switch (s.toLowerCase().trim()) {
                        case "head" -> BodyPart.HEAD;
                        case "torso" -> BodyPart.TORSO;
                        case "left arm", "arm left" -> BodyPart.ARM_LEFT;
                        case "right arm", "arm right" -> BodyPart.ARM_RIGHT;
                        case "left leg", "leg left" -> BodyPart.LEG_LEFT;
                        case "right leg", "leg right" -> BodyPart.LEG_RIGHT;
                        case "left foot", "foot left" -> BodyPart.FOOT_LEFT;
                        case "right foot", "foot right" -> BodyPart.FOOT_RIGHT;
                        default -> null;
                    };
                }
                @Override
                public boolean canParse(ParseContext context) { return true; }
                @Override
                public String toString(BodyPart part, int flags) {
                    return switch (part) {
                        case HEAD -> "head";
                        case TORSO -> "torso";
                        case ARM_LEFT -> "left arm";
                        case ARM_RIGHT -> "right arm";
                        case LEG_LEFT -> "left leg";
                        case LEG_RIGHT -> "right leg";
                        case FOOT_LEFT -> "left foot";
                        case FOOT_RIGHT -> "right foot";
                    };
                }
                @Override
                public String toVariableNameString(BodyPart part) {
                    return part.name().toLowerCase();
                }
            })
        );

        Classes.registerClass(new ClassInfo<>(BodyPartState.class, "bodypartstate")
            .user("body ?part ?states?")
            .name("Body Part State")
            .description("The condition of a body part: full, nearly full, intermediate, damaged, broken.")
            .examples("if body part state of head of player is broken:")
            .since("1.0.0")
            .parser(new Parser<BodyPartState>() {
                @Override
                public @Nullable BodyPartState parse(String s, ParseContext context) {
                    return switch (s.toLowerCase().trim()) {
                        case "full" -> BodyPartState.FULL;
                        case "nearly full", "nearlyfull" -> BodyPartState.NEARLYFULL;
                        case "intermediate" -> BodyPartState.INTERMEDIATE;
                        case "damaged" -> BodyPartState.DAMAGED;
                        case "broken" -> BodyPartState.BROKEN;
                        default -> null;
                    };
                }
                @Override
                public boolean canParse(ParseContext context) { return true; }
                @Override
                public String toString(BodyPartState state, int flags) {
                    return switch (state) {
                        case FULL -> "full";
                        case NEARLYFULL -> "nearly full";
                        case INTERMEDIATE -> "intermediate";
                        case DAMAGED -> "damaged";
                        case BROKEN -> "broken";
                    };
                }
                @Override
                public String toVariableNameString(BodyPartState state) {
                    return state.name().toLowerCase();
                }
            })
        );
    }
}
