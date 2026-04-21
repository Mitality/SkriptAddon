package mitality.skriptAddon.elements.conditions;

import bodyhealth.api.BodyHealthAPI;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Body Health Is Enabled")
@Description("Checks whether BodyHealth is enabled in a world or at a player's current location.")
@Examples({
    "if body health is enabled in player's world:",
    "if body health is enabled for player:",
    "if body health is not enabled for player:"
})
@Since("1.0.0")
public class CondBodyHealthEnabled extends Condition {

    public static final String[] PATTERNS = {
        "body health is enabled (in|for) %world%",
        "body health is (not enabled|disabled) (in|for) %world%",
        "body health is enabled (in|for) %player%",
        "body health is (not enabled|disabled) (in|for) %player%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.CONDITION, SyntaxInfo.builder(CondBodyHealthEnabled.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private int pattern;
    private Expression<World> world;
    private Expression<Player> player;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        setNegated(pattern == 1 || pattern == 3);
        if (pattern <= 1) {
            world = (Expression<World>) exprs[0];
        } else {
            player = (Expression<Player>) exprs[0];
        }
        return true;
    }

    @Override
    public boolean check(Event event) {
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        boolean enabled;
        if (pattern <= 1) {
            World w = world.getSingle(event);
            if (w == null) return isNegated();
            enabled = api.isSystemEnabled(w);
        } else {
            Player p = player.getSingle(event);
            if (p == null) return isNegated();
            enabled = api.isSystemEnabled(p);
        }
        return enabled ^ isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String target = (pattern <= 1)
            ? world.toString(event, debug)
            : player.toString(event, debug);
        return "body health is " + (isNegated() ? "not enabled" : "enabled") + " for " + target;
    }
}
