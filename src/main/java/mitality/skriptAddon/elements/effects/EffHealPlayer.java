package mitality.skriptAddon.elements.effects;

import bodyhealth.api.BodyHealthAPI;
import bodyhealth.core.BodyPart;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Heal Body Part")
@Description("Heals a player's body parts, either fully or by a specific amount, targeting all parts or one specific part.")
@Examples({
    "fully heal player",
    "heal player by 50",
    "fully heal left arm of player",
    "heal head of player by 25"
})
@Since("1.0.0")
public class EffHealPlayer extends Effect {

    public static final String[] PATTERNS = {
        "fully heal %players%",
        "heal %players% by %number%",
        "fully heal %bodypart% of %players%",
        "heal %bodypart% of %players% by %number%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffHealPlayer.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private int pattern;
    private Expression<Player> players;
    private Expression<BodyPart> bodyPart;
    private Expression<Number> amount;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        switch (pattern) {
            case 0 -> players = (Expression<Player>) exprs[0];
            case 1 -> { players = (Expression<Player>) exprs[0]; amount = (Expression<Number>) exprs[1]; }
            case 2 -> { bodyPart = (Expression<BodyPart>) exprs[0]; players = (Expression<Player>) exprs[1]; }
            case 3 -> { bodyPart = (Expression<BodyPart>) exprs[0]; players = (Expression<Player>) exprs[1]; amount = (Expression<Number>) exprs[2]; }
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        for (Player player : players.getArray(event)) {
            switch (pattern) {
                case 0 -> api.healPlayer(player);
                case 1 -> {
                    Number amt = amount.getSingle(event);
                    if (amt != null) api.healPlayer(player, amt.intValue());
                }
                case 2 -> {
                    BodyPart part = bodyPart.getSingle(event);
                    if (part != null) api.healPlayer(player, part);
                }
                case 3 -> {
                    BodyPart part = bodyPart.getSingle(event);
                    Number amt = amount.getSingle(event);
                    if (part != null && amt != null) api.healPlayer(player, part, amt.intValue());
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return switch (pattern) {
            case 0 -> "fully heal " + players.toString(event, debug);
            case 1 -> "heal " + players.toString(event, debug) + " by " + amount.toString(event, debug);
            case 2 -> "fully heal " + bodyPart.toString(event, debug) + " of " + players.toString(event, debug);
            default -> "heal " + bodyPart.toString(event, debug) + " of " + players.toString(event, debug) + " by " + amount.toString(event, debug);
        };
    }
}
