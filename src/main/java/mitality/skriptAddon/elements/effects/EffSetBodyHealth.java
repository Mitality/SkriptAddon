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

@Name("Set Body Health")
@Description("Sets a player's body part health to a specific percentage (0-100), for all parts or one specific part.")
@Examples({
    "set body health of player to 75",
    "set body health of head of player to 50"
})
@Since("1.0.0")
public class EffSetBodyHealth extends Effect {

    public static final String[] PATTERNS = {
        "set [the] body health of %players% to %number%",
        "set [the] body health of %bodypart% of %players% to %number%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffSetBodyHealth.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private int pattern;
    private Expression<Player> players;
    private Expression<BodyPart> bodyPart;
    private Expression<Number> health;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        if (pattern == 0) {
            players = (Expression<Player>) exprs[0];
            health = (Expression<Number>) exprs[1];
        } else {
            bodyPart = (Expression<BodyPart>) exprs[0];
            players = (Expression<Player>) exprs[1];
            health = (Expression<Number>) exprs[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number h = health.getSingle(event);
        if (h == null) return;
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        for (Player player : players.getArray(event)) {
            if (pattern == 0) {
                api.setHealth(player, h.doubleValue());
            } else {
                BodyPart part = bodyPart.getSingle(event);
                if (part != null) api.setHealth(player, part, h.doubleValue());
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (pattern == 0) {
            return "set body health of " + players.toString(event, debug) + " to " + health.toString(event, debug);
        }
        return "set body health of " + bodyPart.toString(event, debug) + " of " + players.toString(event, debug) + " to " + health.toString(event, debug);
    }
}
