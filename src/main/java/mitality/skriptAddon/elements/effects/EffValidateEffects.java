package mitality.skriptAddon.elements.effects;

import bodyhealth.api.BodyHealthAPI;
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

@Name("Validate Body Health Effects")
@Description("Checks and removes any invalid leftover BodyHealth effects from a player.")
@Examples({
    "validate body health effects of player"
})
@Since("1.0.0")
public class EffValidateEffects extends Effect {

    public static final String[] PATTERNS = {
        "validate [body health] effects (of|for) %players%"
    };

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffValidateEffects.class)
                .addPatterns(PATTERNS)
                .build());
    }

    private Expression<Player> players;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        BodyHealthAPI api = BodyHealthAPI.getInstance();
        for (Player player : players.getArray(event)) {
            api.validateEffects(player);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "validate body health effects of " + players.toString(event, debug);
    }
}
